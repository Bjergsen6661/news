package com.news.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, RabbitAutoConfiguration.class})
@MapperScan("com.news.user.mapper")
@ComponentScan(basePackages = {"com.news", "org.n3r.idworker"})
//@EnableCircuitBreaker //开启hystrix的熔断机制
@EnableFeignClients({"com.news"})
public class NewsUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsUserApplication.class, args);
    }
}
