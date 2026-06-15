package com.zzl.umr.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 系统消息提示
    public static final String INSECT_MESSAGE_QUEUE = "insect.message.queue";
    public static final String INSECT_MESSAGE_EXCHANGE = "insect.message.exchange";
    public static final String INSECT_MESSAGE_ROUTING_KEY = "insect.message.routing";

    @Bean
    public Queue insectMessageQueue() {
        return new Queue(INSECT_MESSAGE_QUEUE, true);
    }

    @Bean
    public DirectExchange insectMessageExchange() {
        return new DirectExchange(INSECT_MESSAGE_EXCHANGE, true, false);
    }

    @Bean
    public Binding insectMessageBinding() {
        return BindingBuilder
                .bind(insectMessageQueue())
                .to(insectMessageExchange())
                .with(INSECT_MESSAGE_ROUTING_KEY);
    }


    // 文章浏览量更新
    public static final String VIEW_COUNT_QUEUE = "view.count.queue";
    public static final String VIEW_COUNT_EXCHANGE = "view.count.exchange";
    public static final String VIEW_COUNT_ROUTING_KEY = "view.count.routing";

    @Bean
    public Queue viewCountQueue() {
        return QueueBuilder.durable(VIEW_COUNT_QUEUE).build();
    }

    @Bean
    public TopicExchange viewCountExchange() {
        return ExchangeBuilder.topicExchange(VIEW_COUNT_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding viewCountBinding() {
        return BindingBuilder.bind(viewCountQueue())
                .to(viewCountExchange())
                .with(VIEW_COUNT_ROUTING_KEY);
    }



    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }


    @Bean
    public SimpleRabbitListenerContainerFactory viewCountBatchContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setConcurrentConsumers(2);                 // 可根据需要调整
        factory.setMaxConcurrentConsumers(5);
        factory.setBatchListener(true);                     // 开启批量监听
        factory.setConsumerBatchEnabled(true);              // 启用批量消费
        factory.setBatchSize(20);                            // 每次最多拉取20条消息
        factory.setReceiveTimeout(3000L);                    // 接收超时（毫秒）
        // 如果希望手动确认消息，可添加 factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
}