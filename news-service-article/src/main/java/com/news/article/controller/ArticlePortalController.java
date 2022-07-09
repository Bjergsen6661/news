package com.news.article.controller;

import com.news.api.BaseController;
import com.news.api.controller.article.ArticlePortalControllerApi;
import com.news.article.service.ArticlePortalService;
import com.news.common.jsonres.R;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.eo.ArticleEo;
import com.news.model.user.pojo.Article;
import com.news.model.user.vo.ArticleDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 用户首页文章接口实现
 * @create 2022-06-24-11:56
 */
@Slf4j
@RestController
public class ArticlePortalController extends BaseController implements ArticlePortalControllerApi {

    @Autowired
    ArticlePortalService articlePortalService;

    @Override
    public R list(String keyword, Integer category, Integer page, Integer pageSize) {

        /******************************* 基于mysql数据库查询 *********************************/
//        //用户首页分页查询文章列表
//        PagedGridResult articleLists = articlePortalService.queryIndexArticleList(keyword, category, page, pageSize);
//
//        //重构文章响应实体，加上用户基本数据
//        articleLists = articlePortalService.rebuildArticleGrid(articleLists);

        /******************************* 基于es搜索引擎查询 *********************************/
        //用户首页分页查询文章列表
        PagedGridResult articleLists = articlePortalService.queryIndexArticleListByEs(keyword, category, page, pageSize);

        //重构文章响应实体，加上用户基本数据
        articleLists = articlePortalService.rebuildArticleGridEo(articleLists);

        System.out.println("当前页数:" + articleLists.getPage());
        System.out.println("总页数:" + articleLists.getTotal());
        System.out.println("总记录数:" + articleLists.getRecords());

        return R.ok(articleLists);
    }

    @Override
    public R hotList() {
        //首页查询热文列表
        List<Article> hotList = articlePortalService.queryHotArticleList();

        return R.ok(hotList);
    }

    @Override
    public R queryArticleListOfWriter(String writerId, Integer page, Integer pageSize) {
        //作家页面查询文章列表
        PagedGridResult gridResult = articlePortalService.queryArticleListOfWriter(writerId, page, pageSize);

        //重构文章响应实体，加上作家基本数据
        gridResult = articlePortalService.rebuildArticleGrid(gridResult);
        return R.ok(gridResult);
    }

    @Override
    public R queryGoodArticleListOfWriter(String writerId) {
        //作家页面查询最近佳文列表
        PagedGridResult gridResult = articlePortalService.queryGoodArticleListOfWriter(writerId);

        return R.ok(gridResult);
    }

    @Override
    public R detail(String articleId) {
        // 查询文章内容，并且加上文章作者名
        ArticleDetailVo article = articlePortalService.detail(articleId);

        return R.ok(article);
    }

    @Override
    public R readArticle(String articleId, HttpServletRequest request) {
        // redis 文章阅读数累加1，并处理防刷
        articlePortalService.increaseReads(articleId, request);

        return R.ok();
    }

    @Override
    public Integer readCounts(String articleId) {

        return getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS + ":" + articleId);
    }
}
