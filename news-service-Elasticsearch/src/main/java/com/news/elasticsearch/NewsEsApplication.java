package com.news.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
                                    MongoAutoConfiguration.class,
                                    RabbitAutoConfiguration.class})
@ComponentScan(basePackages = {"com.news", "org.n3r.idworker"})
public class NewsEsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsEsApplication.class, args);
    }

}
