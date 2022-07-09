package com.news.article.service;

import com.news.common.utils.PagedGridResult;
import com.news.model.user.eo.ArticleEo;
import com.news.model.user.pojo.Article;
import com.news.model.user.vo.ArticleDetailVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 用户首页文章业务
 * @create 2022-06-24-11:57
 */
public interface ArticlePortalService {

    //用户首页分页查询文章列表
    public PagedGridResult queryIndexArticleList(String keyword, Integer category,
                                                 Integer page, Integer pageSize);

    //es 用户首页分页查询文章列表
    PagedGridResult queryIndexArticleListByEs(String keyword, Integer category,
                                              Integer page, Integer pageSize);

    //首页查询热文列表
    public List<Article> queryHotArticleList();

    //作家页面查询文章列表
    public PagedGridResult queryArticleListOfWriter(String writerId, Integer page, Integer pageSize);

    //作家页面查询最近佳文列表
    public PagedGridResult queryGoodArticleListOfWriter(String writerId);

    // 查询文章内容
    public ArticleDetailVo detail(String articleId);

    //重构文章响应实体，加上用户基本数据
    public PagedGridResult rebuildArticleGrid(PagedGridResult articleLists);
    public PagedGridResult rebuildArticleGridEo(PagedGridResult articleLists);

    // redis 文章阅读数累加1，并根据ip防刷
    public void increaseReads(String articleId, HttpServletRequest request);
}
