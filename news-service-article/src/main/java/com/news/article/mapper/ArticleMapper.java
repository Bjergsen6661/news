package com.news.article.mapper;

import com.news.api.my.mapper.MyMapper;
import com.news.model.user.pojo.Article;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleMapper extends MyMapper<Article> {
}