package com.news.article.task;

import com.news.api.config.RabbitMQDelayConfig;
import com.news.api.controller.elasticsearch.EsArticleControllerApi;
import com.news.article.service.ArticleService;
import com.news.common.jsonres.R;
import com.news.common.utils.JsonUtils;
import com.news.model.user.eo.ArticleEo;
import com.news.model.user.pojo.Article;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 消费延迟消息
 * @create 2022-06-27-22:49
 */
@Component
public class RabbitMQConsumer {

    @Autowired
    ArticleService articleService;

    @Autowired
    EsArticleControllerApi esClient;

    /**
     * 监听消息队列 修改队列状态
     * @param payload 消息内容
     * @param message 消息对象
     */
    @RabbitListener(queues = {RabbitMQDelayConfig.QUEUE_DELAY})
    public void watchQueue(String payload, Message message) {

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        System.out.println("消费者接受延迟消息：" + new Date());

        if (routingKey.equalsIgnoreCase("publish.delay.display")) {
            // 消费者接收到定时发布的延迟消息（文章id），修改状态为`即时发布`
            String articleId = payload;
            articleService.updateArticleToPublish(articleId);
        }
    }


    /**
     * 监听消息队列 创建es文章的文档信息
     * @param payload 消息内容
     * @param message 消息对象
     */
    @RabbitListener(queues = {RabbitMQDelayConfig.QUEUE_DELAY})
    public void eswatchQueue(String payload, Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        System.out.println("消费者接受延迟消息：" + new Date());

        if (routingKey.equalsIgnoreCase("espublish.delay.display")) {
            Article article = JsonUtils.jsonToPojo(payload, Article.class);
            // 消费者接收到定时发布的延迟消息（文章实体），在es中创建文档
            ArticleEo eo = new ArticleEo();
            eo.setId(article.getId());
            eo.setTitle(article.getTitle());
            eo.setCategoryId(article.getCategoryId());
            eo.setArticleType(article.getArticleType().longValue());
            eo.setArticleCover(article.getArticleCover());
            eo.setPublishTime(article.getPublishTime());

            //创建es文档
            esClient.addDocument(eo);
        }
    }
}
