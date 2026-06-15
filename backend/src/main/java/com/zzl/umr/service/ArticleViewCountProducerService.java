package com.zzl.umr.service;

import com.zzl.umr.config.rabbitmq.RabbitMQConfig;
import com.zzl.umr.model.dto.ViewCountUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhangzl
 * @description
 * @date 2026/03/16 14:50:35
 */
@Service
@Slf4j
public class ArticleViewCountProducerService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendViewCountUpdate(ViewCountUpdateDTO dto) {
        try {
            dto.setTimestamp(System.currentTimeMillis());
            log.debug("发送浏览量更新消息: {}", dto);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.VIEW_COUNT_EXCHANGE,
                    RabbitMQConfig.VIEW_COUNT_ROUTING_KEY,
                    dto
            );
        } catch (Exception e) {
            log.error("发送浏览量更新消息失败", e);
        }
    }
}