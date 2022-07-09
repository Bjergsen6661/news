package com.news.article.task;

import com.news.article.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description mq实现定时任务 —— 发布文章
 * @create 2022-06-23-16:47
 */
@Slf4j
//@Configuration
//@EnableScheduling  // 开启定时任务
public class TaskPublishArticle {

    @Autowired
    ArticleService articleService;

    //添加定时任务
    @Scheduled(cron = "0/10 * * * * ?")
    private void publishArticle() {

        log.info("执行定时任务：" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));

        // 修改文章定时状态改为即时状态
        articleService.updateAppointToPublish();

    }
}
