package com.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude = RabbitAutoConfiguration.class)
@MapperScan(value = {"com.news.admin.mapper", "com.news.category.mapper"})
@ComponentScan(basePackages = {"com.news", "org.n3r.idworker"})
public class NewsAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsAdminApplication.class, args);
    }
}
