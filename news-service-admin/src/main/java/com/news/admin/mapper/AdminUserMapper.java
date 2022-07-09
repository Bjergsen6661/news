package com.news.admin.mapper;

import com.news.api.my.mapper.MyMapper;
import com.news.model.user.pojo.AdminUser;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserMapper extends MyMapper<AdminUser> {
}