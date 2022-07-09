package com.news.model.user.eo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description article 索引库字段
 *  创建文档时，文档id要对应文章id
 * @create 2022-07-02-14:23
 */
public class ArticleEo {

    private String id;
    private String title;
    private Integer categoryId;
    private Long articleType;
    private String articleCover;
    private String publishUserId;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date publishTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Long getArticleType() {
        return articleType;
    }

    public void setArticleType(Long articleType) {
        this.articleType = articleType;
    }

    public String getArticleCover() {
        return articleCover;
    }

    public void setArticleCover(String articleCover) {
        this.articleCover = articleCover;
    }

    public String getPublishUserId() {
        return publishUserId;
    }

    public void setPublishUserId(String publishUserId) {
        this.publishUserId = publishUserId;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    @Override
    public String toString() {
        return "ArticleEo{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", categoryId=" + categoryId +
                ", articleType=" + articleType +
                ", articleCover='" + articleCover + '\'' +
                ", publishUserId='" + publishUserId + '\'' +
                ", publishTime=" + publishTime +
                '}';
    }
}
