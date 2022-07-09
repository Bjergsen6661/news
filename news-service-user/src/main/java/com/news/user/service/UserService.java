package com.news.user.service;

import com.news.common.jsonres.R;
import com.news.model.user.bo.UpdateUserInfoBo;
import com.news.model.user.pojo.AppUser;
import org.springframework.validation.BindingResult;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 用户信息业务
 * @create 2022-06-16-20:56
 */
public interface UserService {

    //根据手机号查询用户是否存在
    public AppUser queryByMobile(String mobile);

    //根据手机号创建用户记录
    public AppUser createByMobile(String mobile);

    //根据userId查询用户信息
    AppUser getUser(String userId);

    //完善\更新用户信息，并且激活用户状态
    R updateInfo(UpdateUserInfoBo updateUserInfoBo);

}