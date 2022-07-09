package com.news.article.mapper;

import com.news.model.user.vo.CommentsVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DiyCommentsMapper{

    //查询文章的所有评论
    public List<CommentsVo> queryArticleCommentList(@Param("paramMap") Map<String, Object> map);
}
