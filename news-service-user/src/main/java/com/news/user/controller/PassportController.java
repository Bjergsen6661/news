package com.news.user.controller;

import com.news.api.controller.user.PassportControllerApi;
import com.news.common.jsonres.R;
import com.news.model.user.bo.RegisterLoginBo;
import com.news.user.service.PassportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-06-15-22:00
 */

@Slf4j
@RestController
public class PassportController implements PassportControllerApi {

    @Autowired
    private PassportService passportService;


    @Override
    public R getSMSCode(String mobile, HttpServletRequest request) {
        if(!StringUtils.isEmpty(mobile)){
            log.info("进行短信发送...");

            passportService.sentCode(mobile, request);
            return R.ok();
        }

        return R.error();
    }

    @Override
    public R doLogin(@Valid RegisterLoginBo registerLoginBo,
                     HttpServletRequest request, HttpServletResponse response) {

        //登录验证，并获取当前对象
        return passportService.getUserR(registerLoginBo, request, response);
    }

    @Override
    public R logout(String userId,
                    HttpServletRequest request,
                    HttpServletResponse response) {

        //用户退出登录
        return passportService.logout(userId, request, response);
    }
}
