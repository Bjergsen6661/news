package com.news.user.controller;

import com.news.api.controller.user.AppUserMngControllerApi;
import com.news.common.enums.UserStatus;
import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.pojo.AppUser;
import com.news.user.service.AppUserMngService;
import com.news.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 用户管理接口实现
 * @create 2022-06-22-16:22
 */
@Slf4j
@RestController
public class AppUserMngController implements AppUserMngControllerApi {

    @Autowired
    AppUserMngService appUserMngService;

    @Autowired
    UserService userService;

    @Override
    public R queryAll(String nickname, Integer status,
                      Date startDate, Date endDate,
                      Integer page, Integer pageSize) {

        //日期范围分页查询用户列表
        PagedGridResult userList = appUserMngService.queryAllUserList(nickname, status, startDate, endDate, page, pageSize);

        return R.ok(userList);
    }

    @Override
    public R userDetail(String userId) {
        //通过用户id查询用户信息
        AppUser user = userService.getUser(userId);

        return R.ok(user);
    }

    @Override
    public R freezeUserOrNot(String userId, Integer doStatus) {

        if (!UserStatus.isUserStatusValid(doStatus)) {
            //用户状态参数出错
            return R.errorCustom(ResponseStatusEnum.USER_STATUS_ERROR);
        }

        //冻结用户账号，或解除封号操作
        appUserMngService.freezeUserOrNot(userId, doStatus);

        return R.ok();
    }
}
