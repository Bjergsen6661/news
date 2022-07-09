package com.news.article.mapper;

import com.news.api.my.mapper.MyMapper;
import com.news.model.user.pojo.Article;
import org.springframework.stereotype.Repository;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 自定义mapper，做定时文章发布
 * @create 2022-06-23-17:13
 */
@Repository
public interface DiyArticleMapper extends MyMapper<Article> {

    //把文章发布状态由1变为0
    public void updateAppointToPublish();
}
