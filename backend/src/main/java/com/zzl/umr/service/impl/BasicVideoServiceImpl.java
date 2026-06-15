package com.zzl.umr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.umr.mapper.BasicUserMapper;
import com.zzl.umr.mapper.BasicVideoInfoMapper;
import com.zzl.umr.mapper.BasicVideoMapper;
import com.zzl.umr.model.BasicUser;
import com.zzl.umr.model.BasicVideo;
import com.zzl.umr.model.BasicVideoInfo;
import com.zzl.umr.model.cdn.BaseQueryCdn;
import com.zzl.umr.model.dto.HttpResult;
import com.zzl.umr.service.BasicVideoService;
import com.zzl.umr.utils.SnowflakeIdWorker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zzl.umr.constants.MessageConstant.USER_LOGOUT_ERROR_MSG;

@Service("basicVideoService")
public class BasicVideoServiceImpl extends ServiceImpl<BasicVideoMapper, BasicVideo> implements BasicVideoService {
    private static final long NEW_WINDOW_MS = 3L * 24 * 60 * 60 * 1000;
    @Resource
    private BasicVideoMapper basicVideoMapper;

    @Resource
    private BasicVideoInfoMapper basicVideoInfoMapper;

    @Resource
    private BasicUserMapper basicUserMapper;

    @Override
    public Page<BasicVideo> queryListPage(BaseQueryCdn queryCdn) {
        Page<BasicVideo> page = new Page<>(queryCdn.getPageNum(), queryCdn.getPageSize());
        LambdaQueryWrapper<BasicVideo> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(queryCdn.getKeyword())) {
            wrapper.like(BasicVideo::getTitle, queryCdn.getKeyword()).or().like(BasicVideo::getIntro, queryCdn.getKeyword());
        }

        wrapper.orderByDesc(BasicVideo::getIsHot).orderByDesc(BasicVideo::getCreateTime);

        Page<BasicVideo> pageResult = basicVideoMapper.selectPage(page, wrapper);
        List<BasicVideo> records = pageResult.getRecords();
        if (records == null || records.isEmpty()) {
            return pageResult;
        }
        long now = System.currentTimeMillis();
        records.forEach(v -> v.setIsNew(v.getCreateTime() != null && (now - v.getCreateTime().getTime()) <= NEW_WINDOW_MS ? 1 : 0));

        List<String> authorIds = records.stream().map(BasicVideo::getAuthorId).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        if (!authorIds.isEmpty()) {
            List<BasicUser> authors = basicUserMapper.selectBatchIds(authorIds);
            Map<String, String> authorMap = authors.stream().collect(Collectors.toMap(BasicUser::getId, user -> StringUtils.isNotBlank(user.getNickName()) ? user.getNickName() : user.getUserName(), (a, b) -> a));
            records.forEach(video -> video.setAuthorName(authorMap.getOrDefault(video.getAuthorId(), USER_LOGOUT_ERROR_MSG)));
        }

        List<String> videoIds = records.stream().map(BasicVideo::getId).collect(Collectors.toList());
        if (!videoIds.isEmpty()) {
            List<BasicVideoInfo> infoList = basicVideoInfoMapper.selectList(new LambdaQueryWrapper<BasicVideoInfo>().in(BasicVideoInfo::getVideoId, videoIds));
            Map<String, BasicVideoInfo> infoMap = infoList.stream().collect(Collectors.toMap(BasicVideoInfo::getVideoId, v -> v, (a, b) -> a));
            records.forEach(video -> {
                BasicVideoInfo info = infoMap.get(video.getId());
                video.setLikeCount(info != null && info.getLikeCount() != null ? info.getLikeCount() : 0L);
                video.setCollectCount(info != null && info.getCollectCount() != null ? info.getCollectCount() : 0L);
                video.setCoinCount(info != null && info.getCoinCount() != null ? info.getCoinCount() : 0L);
            });
        }

        return pageResult;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(BasicVideo basicVideo) {
        if (basicVideo == null) {
            return false;
        }
        if (StringUtils.isBlank(basicVideo.getTitle()) || StringUtils.isBlank(basicVideo.getAuthorId())) {
            return false;
        }
        basicVideo.setId(SnowflakeIdWorker.newId());

        boolean inserted = basicVideoMapper.insert(basicVideo) > 0;
        if (!inserted) {
            return false;
        }
        // 初始化
        BasicVideoInfo info = new BasicVideoInfo();
        info.setId(SnowflakeIdWorker.newId());
        info.setVideoId(basicVideo.getId());
        info.setLikeCount(0L);
        info.setCollectCount(0L);
        info.setCoinCount(0L);
        basicVideoInfoMapper.insert(info);
        return true;
    }

    @Override
    public HttpResult queryById(String id) {

        BasicVideo video = basicVideoMapper.selectById(id);
        if (video == null) {
            return HttpResult.success("视频不见了呢~~");
        }
        if (StringUtils.isNotBlank(video.getAuthorId())) {
            BasicUser author = basicUserMapper.selectById(video.getAuthorId());
            long now = System.currentTimeMillis();
            video.setIsNew(video.getCreateTime() != null && (now - video.getCreateTime().getTime()) <= NEW_WINDOW_MS ? 1 : 0);
            if (author != null) {
                video.setAuthorName(StringUtils.isNotBlank(author.getNickName()) ? author.getNickName() : author.getUserName());
            } else {
                video.setAuthorName(USER_LOGOUT_ERROR_MSG);
            }
        }
        BasicVideoInfo info = basicVideoInfoMapper.selectOne(new LambdaQueryWrapper<BasicVideoInfo>().eq(BasicVideoInfo::getVideoId, id).last("limit 1"));

        video.setLikeCount(info != null && info.getLikeCount() != null ? info.getLikeCount() : 0L);
        video.setCollectCount(info != null && info.getCollectCount() != null ? info.getCollectCount() : 0L);
        video.setCoinCount(info != null && info.getCoinCount() != null ? info.getCoinCount() : 0L);

        return HttpResult.success(video);
    }
}
