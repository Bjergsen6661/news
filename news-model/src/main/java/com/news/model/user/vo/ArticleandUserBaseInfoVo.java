package com.news.model.user.vo;

import com.news.model.user.pojo.Article;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 用户首页获取文章信息(阅读量)，加上用户头像与用户名信息
 * @create 2022-06-24-22:11
 */

public class ArticleandUserBaseInfoVo extends Article {

    //继承Article的属性信息

    //外加上用户头像与用户名信息，直接放vo
    private UserBaseInfoVo publisherVo;

    public UserBaseInfoVo getPublisherVo() {
        return publisherVo;
    }

    public void setPublisherVo(UserBaseInfoVo publisherVo) {
        this.publisherVo = publisherVo;
    }


    @Override
    public String toString() {
        return "ArticleandUserBaseInfoVo{" +
                "publisherVo=" + publisherVo +
                '}';
    }
}
