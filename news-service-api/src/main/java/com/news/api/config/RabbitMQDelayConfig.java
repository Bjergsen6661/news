package com.news.api.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 实现延时队列
 * @create 2022-06-27-17:12
 */
@Configuration
public class RabbitMQDelayConfig {

    // 定义交换机的名称
    public static final String EXCHANGE_DELAY = "exchange_delay";

    // 定义队列的名称
    public static final String QUEUE_DELAY = "queue_delay";

    // 创建交换机，放入springboot容器
    @Bean(EXCHANGE_DELAY)
    public Exchange exchange() {
        return ExchangeBuilder
                .topicExchange(EXCHANGE_DELAY)    // 使用topic类型，并定义交换机的名称。
                .durable(true)
                .delayed()                      // 设置延迟
                .build();
    }

    // 创建队列
    @Bean(QUEUE_DELAY)
    public Queue queue() {
        return new Queue(QUEUE_DELAY);
    }

    /******************************* 修改文章发布状态 ***************************************/
    // 队列绑定交换机
    @Bean
    public Binding delayBinding(@Qualifier(QUEUE_DELAY) Queue queue,
                                @Qualifier(EXCHANGE_DELAY) Exchange exchange) {
        return BindingBuilder               // 定义绑定关系
                .bind(queue)                // 绑定队列
                .to(exchange)               // 到交换机
                .with("publish.delay.#")   // 定义路由规则（requestMapping映射）
                .noargs();                  // 执行绑定
    }


    /******************************** 创建es文章的文档信息 ***************************************/
    // 队列绑定交换机
    @Bean
    public Binding esdelayBinding(@Qualifier(QUEUE_DELAY) Queue queue,
                                @Qualifier(EXCHANGE_DELAY) Exchange exchange) {
        return BindingBuilder               // 定义绑定关系
                .bind(queue)                // 绑定队列
                .to(exchange)               // 到交换机
                .with("espublish.delay.#")   // 定义路由规则（requestMapping映射）
                .noargs();                  // 执行绑定
    }

}
