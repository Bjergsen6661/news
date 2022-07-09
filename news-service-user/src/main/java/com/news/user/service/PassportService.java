package com.news.user.service;

import com.news.common.jsonres.R;
import com.news.model.user.bo.RegisterLoginBo;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.HttpCookie;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-06-15-21:59
 */
public interface PassportService {
    //为手机号发送验证码
    void sentCode(String phoneNum, HttpServletRequest request);

    //登录验证，并获取当前对象
    R getUserR(RegisterLoginBo registerLoginBo, HttpServletRequest request, HttpServletResponse response);

    //用户退出登录
    R logout(String userId, HttpServletRequest request, HttpServletResponse response);
}
