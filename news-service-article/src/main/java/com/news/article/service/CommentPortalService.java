package com.news.article.service;

import com.news.common.utils.PagedGridResult;
import com.news.model.user.vo.UserBaseInfoVo;

import java.util.List;
import java.util.Set;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 文章评论业务
 * @create 2022-06-26-16:02
 */
public interface CommentPortalService {

    //保存评论信息
    void createComment(String articleId, String fatherId,
                       String content, String userId,
                       String nickname, String userFace);

    //分页查询文章的所有评论
    PagedGridResult queryArticleComments(String articleId, Integer page, Integer pageSize);

    //用户评论管理分页查询评论
    PagedGridResult queryWriterCommentsMng(String writerId, Integer page, Integer pageSize);

    //评论管理删除评论
    void deleteComment(String writerId, String commentId);

    //远程调用获取用户基本信息
    List<UserBaseInfoVo> getPublisherList(Set<String> idSet);
}
