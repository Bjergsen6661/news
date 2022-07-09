package com.news.article.service.impl;

import com.github.pagehelper.PageHelper;
import com.news.api.BaseController;
import com.news.api.controller.user.UserControllerApi;
import com.news.article.feignClient.UserClient;
import com.news.article.mapper.CommentsMapper;
import com.news.article.mapper.DiyCommentsMapper;
import com.news.article.service.ArticlePortalService;
import com.news.article.service.CommentPortalService;
import com.news.common.exceptiopn.GraceException;
import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.common.utils.JsonUtils;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.pojo.Comments;
import com.news.model.user.vo.ArticleDetailVo;
import com.news.model.user.vo.CommentsVo;
import com.news.model.user.vo.UserBaseInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.news.api.BaseController.REDIS_ARTICLE_COMMENT_COUNTS;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 文章评论业务实现
 * @create 2022-06-26-16:02
 */
@Service
public class CommentPortalServiceImpl implements CommentPortalService {

    @Autowired
    Sid sid;

    @Autowired
    CommentsMapper commentsMapper;

    @Autowired
    DiyCommentsMapper diyCommentsMapper;

    @Autowired
    ArticlePortalService articlePortalService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    BaseController baseController;

    @Autowired
    UserClient userClient;

    @Transactional
    @Override
    public void createComment(String articleId, String fatherId,
                              String content, String userId,
                              String nickname, String userFace) {

        // 查询文章内容
        ArticleDetailVo article  = articlePortalService.detail(articleId);

        //生成全局唯一主键
        String commentId = sid.nextShort();

        //属性赋值
        Comments newComments = new Comments();
        newComments.setId(commentId);

        newComments.setWriterId(article.getPublishUserId());
        newComments.setFatherId(fatherId);
        newComments.setArticleId(articleId);
        newComments.setArticleTitle(article.getTitle());
        newComments.setArticleCover(article.getCover());

        newComments.setCommentUserId(userId);
        newComments.setCommentUserNickname(nickname);
        newComments.setCommentUserFace(userFace);
        newComments.setContent(content);
        newComments.setCreateTime(new Date());

        //数据库持久化保存
        commentsMapper.insert(newComments);

        // redsi中评论数累加1
        redisTemplate.opsForValue().increment(REDIS_ARTICLE_COMMENT_COUNTS + ":" + articleId, 1);
    }

    @Override
    public PagedGridResult queryArticleComments(String articleId,
                                                Integer page,
                                                Integer pageSize) {

        //设置参数
        Map<String, Object> map = new HashMap<>();
        map.put("articleId", articleId);

        //构建分页数据
        PageHelper.startPage(page, pageSize);
        List<CommentsVo> list = diyCommentsMapper.queryArticleCommentList(map);

        return baseController.setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult queryWriterCommentsMng(String writerId,
                                                  Integer page,
                                                  Integer pageSize) {

        //查询该用户下所属的评论
        Comments comments = new Comments();
        comments.setWriterId(writerId);

        //设置分页数据
        PageHelper.startPage(page, pageSize);
        List<Comments> list = commentsMapper.select(comments);

        return baseController.setterPagedGrid(list, page);
    }

    @Override
    public void deleteComment(String writerId, String commentId) {
        //校验参数合法性
        if(StringUtils.isNotBlank(writerId) && StringUtils.isNotBlank(commentId)){
            Comments comments = new Comments();
            comments.setWriterId(writerId);
            comments.setId(commentId);

            //删除处理
            commentsMapper.delete(comments);
        }
    }

    @Override
    public List<UserBaseInfoVo> getPublisherList(Set<String> idSet) {

        return getPublisherListByFeign(idSet);
    }

    /**
     * @param idSet 去重的id列表
     */
    private List<UserBaseInfoVo> getPublisherListByFeign(Set<String> idSet){
        //构建用户基本信息列表
        /******************************* 使用Feign远程调用 *************************************/
        R body = userClient.queryByIds(JsonUtils.objectToJson(idSet));

        List<UserBaseInfoVo> userBaseInfoVoList = null;
        if(body.getStatus() == 200){
            String userlistsJson = JsonUtils.objectToJson(body.getData());
            userBaseInfoVoList = JsonUtils.jsonToList(userlistsJson, UserBaseInfoVo.class);
        }else{
            GraceException.display(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        }

        return userBaseInfoVoList;
    }
}
