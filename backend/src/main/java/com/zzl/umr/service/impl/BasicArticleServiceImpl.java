package com.zzl.umr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.umr.mapper.BasicArticleMapper;
import com.zzl.umr.mapper.BasicUserMapper;
import com.zzl.umr.model.BasicArticle;
import com.zzl.umr.model.BasicUser;
import com.zzl.umr.model.cdn.ArticleViewCdn;
import com.zzl.umr.model.cdn.BaseQueryCdn;
import com.zzl.umr.model.dto.HttpResult;
import com.zzl.umr.model.dto.ViewCountUpdateDTO;
import com.zzl.umr.service.BasicArticleService;
import com.zzl.umr.service.RedisService;
import com.zzl.umr.service.ArticleViewCountProducerService;
import com.zzl.umr.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.zzl.umr.constants.MessageConstant.DATE_FORMATTER;
import static com.zzl.umr.constants.MessageConstant.HOT_THRESHOLD;
import static com.zzl.umr.constants.MessageConstant.USER_LOGOUT_ERROR_MSG;
import static com.zzl.umr.constants.RedisKeyFixConstant.DAILY_VIEW_KEY_PREFIX;
import static com.zzl.umr.constants.RedisKeyFixConstant.VIEW_COUNT_KEY_PREFIX;

@Slf4j
@Service("basicArticleService")
public class BasicArticleServiceImpl extends ServiceImpl<BasicArticleMapper, BasicArticle> implements BasicArticleService {

    @Resource
    private BasicArticleMapper basicArticleMapper;

    @Resource
    private BasicUserMapper basicUserMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ArticleViewCountProducerService articleViewCountProducerService;

    @Override
    public Page<BasicArticle> queryListPage(BaseQueryCdn queryCdn) {
        Page<BasicArticle> page = new Page<>(queryCdn.getPageNum(), queryCdn.getPageSize());
        LambdaQueryWrapper<BasicArticle> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(queryCdn.getKeyword())) {
            wrapper.like(BasicArticle::getTitle, queryCdn.getKeyword())
                    .or()
                    .like(BasicArticle::getSummary, queryCdn.getKeyword());
        }

        wrapper.orderByDesc(BasicArticle::getIsTop)
                .orderByDesc(BasicArticle::getIsHot)
                .orderByDesc(BasicArticle::getCreateTime);

        Page<BasicArticle> pageResult = basicArticleMapper.selectPage(page, wrapper);
        List<BasicArticle> records = pageResult.getRecords();

        if (!records.isEmpty()) {
            List<String> authorIds = records.stream()
                    .map(BasicArticle::getAuthorId)
                    .distinct()
                    .collect(Collectors.toList());

            if (!authorIds.isEmpty()) {
                List<BasicUser> authors = basicUserMapper.selectBatchIds(authorIds);
                Map<String, String> authorMap = authors.stream()
                        .collect(
                                Collectors.toMap(BasicUser::getId,
                                        user -> StringUtils.isNotBlank(user.getNickName())

                                                ? user.getNickName() : user.getUserName()));

                records.forEach(article ->
                        article.setAuthorName(
                                authorMap.getOrDefault(article.getAuthorId(), USER_LOGOUT_ERROR_MSG)));
            }
        }
        return pageResult;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(BasicArticle basicArticle) {
        if (basicArticle == null) {
            return false;
        }
        if (StringUtils.isBlank(basicArticle.getTitle()) || StringUtils.isBlank(basicArticle.getAuthorId())) {
            return false;
        }

        basicArticle.setId(SnowflakeIdWorker.newId());

        if (StringUtils.isBlank(basicArticle.getSummary()) && StringUtils.isNotBlank(basicArticle.getContent())) {
            String content = basicArticle.getContent().trim();
            // 取前100 字
            basicArticle.setSummary(content.length() > 100 ? content.substring(0, 100) : content);
        }

        return basicArticleMapper.insert(basicArticle) > 0;
    }

    @Override
    public HttpResult queryById(String id) {
        BasicArticle article = basicArticleMapper.selectById(id);
        if (article == null) {
            return HttpResult.success("文章已被删除");
        }
        if (StringUtils.isNotBlank(article.getAuthorId())) {
            BasicUser author = basicUserMapper.selectById(article.getAuthorId());
            if (author != null) {
                article.setAuthorName(StringUtils.isNotBlank(author.getNickName()) ? author.getNickName() : author.getUserName());
            } else {
                article.setAuthorName(USER_LOGOUT_ERROR_MSG);
            }
        }
        return HttpResult.success(article);
    }

    @Override
    public HttpResult addViewCount(ArticleViewCdn basicArticle) {
        if (basicArticle == null || StringUtils.isEmpty(basicArticle.getArticleId())) {
            return HttpResult.fail("文章ID不能为空");
        }

        String articleId = basicArticle.getArticleId();
        String totalKey = VIEW_COUNT_KEY_PREFIX + articleId;
        String dailyKey = buildDailyKey(articleId);

        // 1. Redis 原子递增总浏览量
        Long totalViewCount = redisService.increment(totalKey, 1L);

        // 2. 递增当日浏览量，并设置过期时间
        Long dailyViewCount = redisService.increment(dailyKey, 1L);

        // 设置过期时间，防止长期占用内存（例如2天后自动删除）
        if (dailyViewCount == 1) {
            redisService.expire(dailyKey, 2, TimeUnit.DAYS);
        }

        // 检查是否达到热帖阈值
        if (dailyViewCount % HOT_THRESHOLD == 0) {
            try {
                // 尝试将文章设为热帖
                LambdaUpdateWrapper<BasicArticle> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(BasicArticle::getId, articleId);

                updateWrapper.set(BasicArticle::getIsHot, true);
                updateWrapper.set(BasicArticle::getUpdateTime, new Date());
                int updated = basicArticleMapper.update(null, updateWrapper);

                if (updated > 0) {
                    log.info("文章 {} 已达到热帖标准，已设置为热帖", articleId);
                }
            } catch (Exception e) {
                log.error("更新文章热帖状态失败, articleId: {}", articleId, e);
            }
        }

        // 发送异步消息，批量更新数据库的总浏览量
        ViewCountUpdateDTO dto = new ViewCountUpdateDTO();
        dto.setArticleId(articleId);
        dto.setUserId(basicArticle.getUserId());
        dto.setIncrement(1);
        articleViewCountProducerService.sendViewCountUpdate(dto);

        return HttpResult.success(totalViewCount);
    }


    /**
     * 构建每日浏览量的 Redis key
     *
     * @param articleId 文章ID
     * @return Redis key
     */
    private String buildDailyKey(String articleId) {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        return DAILY_VIEW_KEY_PREFIX + dateStr + ":" + articleId;
    }
}
