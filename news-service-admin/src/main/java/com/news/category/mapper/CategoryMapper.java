package com.news.category.mapper;


import com.news.api.my.mapper.MyMapper;
import com.news.model.user.pojo.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMapper extends MyMapper<Category> {
}