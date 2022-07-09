package com.news.article.controller;

import com.news.api.BaseController;
import com.news.api.controller.article.CommentControllerApi;
import com.news.article.service.CommentPortalService;
import com.news.common.jsonres.R;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.bo.CommentReplyBO;
import com.news.model.user.vo.UserBaseInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 文章评论接口实现
 * @create 2022-06-26-16:01
 */
@RestController
public class CommentController extends BaseController implements CommentControllerApi {

    @Autowired
    private CommentPortalService commentPortalService;

    @Autowired
    BaseController baseController;

    @Override
    public R createComment(@Valid CommentReplyBO commentReplyBO) {

        //0.校验result合法性

        //1.根据留言用户id查询他的昵称，用于冗余存入留言记录，避免多表管理查询的性能开支
        String userId = commentReplyBO.getCommentUserId();
        //发起restTemplate调用获得用户基本信息，获取用户名
        Set<String> idSet = new HashSet<>();
        idSet.add(userId);
        List<UserBaseInfoVo> publisherList = commentPortalService.getPublisherList(idSet);
        String nickName = publisherList.get(0).getNickname();
        String userFace = publisherList.get(0).getFace();

        //2.保存评论信息
        commentPortalService.createComment(commentReplyBO.getArticleId(),
                                            commentReplyBO.getFatherId(),
                                            commentReplyBO.getContent(),
                                            userId,
                                            nickName,
                                            userFace);

        return R.ok();
    }

    @Override
    public R commentCounts(String articleId) {
        //获取文章的评论数
        Integer counts = getCountsFromRedis(REDIS_ARTICLE_COMMENT_COUNTS + ":" + articleId);

        return R.ok(counts);
    }

    @Override
    public R list(String articleId, Integer page, Integer pageSize) {
        //分页查询文章的所有评论
        PagedGridResult gridResult = commentPortalService.queryArticleComments(articleId, page, pageSize);

        return R.ok(gridResult);
    }

    @Override
    public R queryWriterCommentsMng(String writerId, Integer page, Integer pageSize) {
        //评论管理分页查询评论
        PagedGridResult gridResult = commentPortalService.queryWriterCommentsMng(writerId, page, pageSize);

        return R.ok(gridResult);
    }

    @Override
    public R deleteComment(String writerId, String commentId) {
        //评论管理删除评论
        commentPortalService.deleteComment(writerId, commentId);

        return R.ok();
    }

}

