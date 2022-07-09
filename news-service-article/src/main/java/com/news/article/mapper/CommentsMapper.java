package com.news.article.mapper;


import com.news.api.my.mapper.MyMapper;
import com.news.model.user.pojo.Comments;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsMapper extends MyMapper<Comments> {

}