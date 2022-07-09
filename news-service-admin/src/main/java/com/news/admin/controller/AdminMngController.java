package com.news.admin.controller;

import com.news.admin.service.AdminUserService;
import com.news.api.controller.admin.AdminMngControllerApi;
import com.news.common.jsonres.R;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.bo.AdminLoginBO;
import com.news.model.user.bo.NewAdminBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 管理员管理接口实现
 * @create 2022-06-20-10:49
 */

@RestController
public class AdminMngController implements AdminMngControllerApi {

    @Autowired
    AdminUserService adminUserService;

    @Override
    public R adminLogin(AdminLoginBO adminLoginBO,
                        HttpServletRequest request,
                        HttpServletResponse response) {


        //获得管理员用户信息
        return adminUserService.getAdminR(adminLoginBO, request, response);
    }

    @Override
    public R adminIsExist(String username) {

        //校验管理员用户是否存在
        return adminUserService.ifAdminExist(username);
    }

    @Override
    public R addNewAdmin(NewAdminBo newAdminBo,
                         HttpServletRequest request,
                         HttpServletResponse response) {

        //新增管理员用户信息
        return adminUserService.addNewAdmin(newAdminBo, request, response);
    }

    @Override
    public R getAdminList(Integer page, Integer pageSize) {

        //分页查询管理员列表信息
        PagedGridResult gridResult = adminUserService.getAdminLists(page, pageSize);
        return R.ok(gridResult);
    }

    @Override
    public R adminLogout(String adminId,
                         HttpServletRequest request,
                         HttpServletResponse response) {

        //用户退出请求
        return adminUserService.adminLogout(adminId, request, response);
    }
}
