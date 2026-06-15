package com.zzl.umr.service;

import com.zzl.umr.config.rabbitmq.RabbitMQConfig;
import com.zzl.umr.model.dto.SysMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhangzl
 * @description 消息生产者服务
 * @date 2026/01/30 14:27:31
 */
@Service
@Slf4j
public class MessageProducerService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendInsectMessage(SysMessageDTO message) {
        try {
            message.setTimestamp(System.currentTimeMillis());
            log.info("发送消息: {}", message);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.INSECT_MESSAGE_EXCHANGE,
                    RabbitMQConfig.INSECT_MESSAGE_ROUTING_KEY,
                    message
            );
        } catch (Exception e) {
            log.error("发送消息失败", e);
        }
    }
}