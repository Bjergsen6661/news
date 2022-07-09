package com.news.elasticsearch.controller;

import com.news.api.controller.elasticsearch.EsArticleControllerApi;
import com.news.common.jsonres.R;
import com.news.common.utils.PagedGridResult;
import com.news.elasticsearch.service.EsArticleService;
import com.news.model.user.eo.ArticleEo;
import com.news.model.user.eo.MyPageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description article 索引库相关操作
 * @create 2022-07-02-13:48
 */
@Slf4j
@RestController
public class EsArticleController implements EsArticleControllerApi {

    @Autowired
    EsArticleService esArticleService;

    @Override
    public R createIndex() {
        //创建索引库
        esArticleService.createIndex();

        log.info("Article 创建索引库...");
        return R.ok("Article索引库创建成功...");
    }

    @Override
    public R addDocument(ArticleEo articleEo) {
        //新增文档信息
        esArticleService.addDoc(articleEo);

        String id = articleEo.getId();
        log.info("article_" + id + "新增文档...");
        return R.ok("article_" + id + "新增文档成功...");
    }

    @Override
    public R deleteDocument(String articleId) {
        //删除文档信息
        esArticleService.delDoc(articleId);

        log.info("article_" + articleId + "删除文档...");
        return R.ok("article_" + articleId + "删除文档成功...");
    }

    @Override
    public R search(String keyword, Integer category, Integer page, Integer pageSize) {
        //获取es检索分页信息
        MyPageResult myPageResult = esArticleService.search(keyword, category, page, pageSize);

        return R.ok(myPageResult);
    }

    @Override
    public R hotList(Set<String> setId) {
        //通过传来的Setid集合，查询es中的文章信息
        List<ArticleEo> list = esArticleService.hotList(setId);

        return R.ok(list);
    }

    @Override
    public R getWriterArticles(String writerId) {
        //通过作家id查询所有所属文章
        List<ArticleEo> list = esArticleService.getWriterArticles(writerId);

        return R.ok(list);
    }

}
