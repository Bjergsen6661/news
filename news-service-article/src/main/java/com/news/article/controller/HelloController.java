package com.news.article.controller;

import com.news.api.config.RabbitMQDelayConfig;
import com.news.api.controller.article.HelloControllerApi;
import com.news.common.jsonres.R;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 测试
 * @create 2022-06-22-21:54
 */
@RestController
public class HelloController implements HelloControllerApi {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public R test() {

//        MessagePostProcessor messagePostProcessor = message -> {
//            // 设置持久
//            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
//            // 设置延迟的时间，单位毫秒
//            message.getMessageProperties().setDelay(5000);
//
//            return message;
//        };
//
//        // 发送延迟消息
//        rabbitTemplate.convertAndSend(
//                RabbitMQDelayConfig.EXCHANGE_DELAY,
//                "delay.demo",
//                "这是一条延迟消息~",
//                messagePostProcessor);
//
//        System.out.println("生产者发送延迟消息：" + new Date());

        return R.ok("hello world !");
    }
}
