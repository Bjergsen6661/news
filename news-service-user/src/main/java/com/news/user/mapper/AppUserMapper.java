package com.news.user.mapper;


import com.news.api.my.mapper.MyMapper;
import com.news.model.user.pojo.AppUser;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserMapper extends MyMapper<AppUser> {
}