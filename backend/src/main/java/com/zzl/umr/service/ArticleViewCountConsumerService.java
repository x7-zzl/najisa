package com.zzl.umr.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zzl.umr.config.rabbitmq.RabbitMQConfig;
import com.zzl.umr.mapper.BasicArticleMapper;
import com.zzl.umr.model.BasicArticle;
import com.zzl.umr.model.dto.ViewCountUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangzl
 * @description 浏览量消费者服务
 * @date 2026/03/16 14:51:59
 */
@Slf4j
@Service
public class ArticleViewCountConsumerService {

    @Resource
    private BasicArticleMapper basicArticleMapper;

    /**
     * 批量处理浏览量更新消息
     *
     * @param queueList 消息列表
     */
    @RabbitListener(queues = RabbitMQConfig.VIEW_COUNT_QUEUE,
            containerFactory = "viewCountBatchContainerFactory")
    public void handleBatchViewCountUpdate(List<ViewCountUpdateDTO> queueList) {
        if (queueList == null || queueList.isEmpty()) {
            return;
        }

        log.info("接收到 {} 条浏览量更新消息", queueList.size());

        // 按文章 ID 聚合增量
        Map<String, Integer> incrementMap = queueList.stream()
                .collect(Collectors.groupingBy(
                        ViewCountUpdateDTO::getArticleId,
                        Collectors.summingInt(ViewCountUpdateDTO::getIncrement)
                ));

        // 批量更新数据库（逐条更新，但已大幅减少更新次数）
        int successCount = 0;
        for (Map.Entry<String, Integer> entry : incrementMap.entrySet()) {
            String articleId = entry.getKey();
            int increment = entry.getValue();

            LambdaUpdateWrapper<BasicArticle> wrapper = new LambdaUpdateWrapper<>();
            // 原子递增
            wrapper.eq(BasicArticle::getId, articleId)
                    .setSql("view_count = view_count + " + increment);
            int updated = basicArticleMapper.update(null, wrapper);
            if (updated > 0) {
                successCount++;
            } else {
                log.warn("文章不存在，id: {}", articleId);
            }

            // TODO:更新用户浏览记录
        }

        log.info("批量更新完成，成功更新 {} 篇文章", successCount);
    }
}