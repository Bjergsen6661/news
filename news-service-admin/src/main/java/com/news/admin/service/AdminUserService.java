package com.news.admin.service;

import com.news.common.jsonres.R;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.bo.AdminLoginBO;
import com.news.model.user.bo.NewAdminBo;
import com.news.model.user.pojo.AdminUser;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-06-20-10:34
 */
public interface AdminUserService {

    //根据用户名查询管理员信息
    public AdminUser queryAdminByUsername(String username);

    //获得管理员用户信息
    public R getAdminR(AdminLoginBO adminLoginBO, HttpServletRequest request1, HttpServletResponse response);

    //校验管理员用户是否存在
    public R ifAdminExist(String username);

    //新增管理员用户信息
    public R addNewAdmin(NewAdminBo newAdminBo, HttpServletRequest request, HttpServletResponse response);

    //分页查询管理员列表信息
    public PagedGridResult getAdminLists(Integer page, Integer pageSize);

    //用户退出请求
    public R adminLogout(String adminId, HttpServletRequest request, HttpServletResponse response);
}
