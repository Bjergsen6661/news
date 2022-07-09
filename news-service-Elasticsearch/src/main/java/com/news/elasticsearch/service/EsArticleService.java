package com.news.elasticsearch.service;

import com.news.model.user.eo.ArticleEo;
import com.news.model.user.eo.MyPageResult;

import java.util.List;
import java.util.Set;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description article 索引库相关业务
 * @create 2022-07-03-12:31
 */
public interface EsArticleService {

    //创建索引库
    void createIndex();

    //新增文档信息
    void addDoc(ArticleEo articleEo);

    //删除文档信息
    void delDoc(String articleId);

    //获取es检索分页信息 json格式
    MyPageResult search(String keyword, Integer category, Integer page, Integer pageSize);

    //通过传来的Setid集合，查询es中的文章信息
    List<ArticleEo> hotList(Set<String> articleIds);

    //通过作家id查询所有所属文章
    List<ArticleEo> getWriterArticles(String writerId);

}
