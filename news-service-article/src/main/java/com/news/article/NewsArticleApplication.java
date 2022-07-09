package com.news.article;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.news.article.mapper")
@ComponentScan(basePackages = {"com.news", "org.n3r.idworker"})
@EnableFeignClients({"com.news"})
//@EnableHystrix
public class NewsArticleApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsArticleApplication.class, args);
    }

}
