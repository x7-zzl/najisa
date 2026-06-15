package com.zzl.umr.service;


import com.rabbitmq.client.Channel;
import com.zzl.umr.config.rabbitmq.RabbitMQConfig;
import com.zzl.umr.model.SysMessage;
import com.zzl.umr.model.dto.SysMessageDTO;
import com.zzl.umr.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static ch.qos.logback.core.CoreConstants.EMPTY_STRING;
import static com.zzl.umr.constants.MessageConstant.ADD_STRING;
import static com.zzl.umr.constants.MessageConstant.DELETE_STRING;

/**
 * @author zhangzl
 * @description 消息消费者服务
 * @date 2026/01/30 14:28:26
 */
@Service
@Slf4j
public class MessageConsumerService {

    @Resource
    private SysMessageService sysMessageService;

    @Resource
    private WebSocketServer webSocketServer;

    @RabbitListener(queues = RabbitMQConfig.INSECT_MESSAGE_QUEUE)
    public void handleInsectMessage(SysMessageDTO messageDTO, Message message, Channel channel) {
        try {
            log.info("收到消息: {}", messageDTO);

            String content = EMPTY_STRING;
            String title = EMPTY_STRING;

            if (ADD_STRING.equals(messageDTO.getOperationType())) {
                title = "蛊虫诞生";
                content = String.format("蛊界天道更迭,%s流派产生新蛊虫【%s】",
                        messageDTO.getGenreName(), messageDTO.getName());
            } else if (DELETE_STRING.equals(messageDTO.getOperationType())) {
                title = "蛊虫消逝";
                content = String.format("蛊界天道更迭,%s流派蛊虫【%s】消逝",
                        messageDTO.getGenreName(), messageDTO.getName());
            }

            SysMessage sysMessage = new SysMessage();
            sysMessage.setId(SnowflakeIdWorker.newId());
            sysMessage.setTitle(title);
            sysMessage.setContent(content);
            sysMessage.setMessageType(1);
            sysMessage.setRelatedId(messageDTO.getBusinessId());

            sysMessageService.save(sysMessage);

            webSocketServer.sendMessageToAll(sysMessage);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("消息处理成功");
        } catch (Exception e) {
            log.error("处理消息失败", e);
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (Exception ex) {
                log.error("消息确认失败", ex);
            }
        }
    }
}