package com.news.article.service;

import com.news.common.jsonres.R;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.bo.NewArticleBO;
import com.news.model.user.pojo.Category;
import org.springframework.validation.BindingResult;

import java.util.Date;
import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 文章入库业务
 * @create 2022-06-23-10:58
 */
public interface ArticleService {

    //发布文章
    public void createArticle(NewArticleBO newArticleBO);

    //判断传入的分类id是否符合
    public boolean isCategoryIdVaild(List<Category> categoryList, Integer categoryId);

    //更新定时发布为即时发布
    public void updateAppointToPublish();

    //分页查询用户文章
    PagedGridResult queryMyArticleList(String userId, String keyword, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize);

    //更改文章状态
    public void updateArticleStatus(String articleId, Integer pendingStatus);

    //管理员分页查询用户文章
    PagedGridResult queryAllArticleList(Integer status, Integer page, Integer pageSize);

    //用户撤销文章 —— 状态改为5
    void withdrawArticle(String userId, String articleId);

    //用户删除文章
    void deleteArticle(String userId, String articleId);

    //根据文章id更新定时发布为即时发布
    void updateArticleToPublish(String articleId);
}
