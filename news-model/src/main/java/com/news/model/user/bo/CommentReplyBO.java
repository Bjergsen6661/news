package com.news.model.user.bo;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description '/comment/createComment'请求携带的对象
 * @create 2022-06-26-15:57
 */
public class CommentReplyBO {

    @NotBlank(message = "留言信息不完整")
    private String articleId;

    @NotBlank(message = "留言信息不完整")
    private String fatherId;

    @NotBlank(message = "当前用户信息不正确，请尝试重新登录")
    private String commentUserId;

    @NotBlank(message = "留言内容不能为空")
    @Length(max = 50, message = "文章内容长度不能超过50")
    private String content;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(String commentUserId) {
        this.commentUserId = commentUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
