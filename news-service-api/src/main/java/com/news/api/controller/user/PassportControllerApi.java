package com.news.api.controller.user;

import com.news.common.jsonres.R;
import com.news.model.user.bo.RegisterLoginBo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 统一调度管理user服务的controller接口，实现在各自微服务中具体实现
 */
@Api(value = "用户注册登录", tags = {"用户注册登录的Controller"})
@RequestMapping("/passport")
public interface PassportControllerApi {

    @ApiOperation(value = "发送验证码", notes = "发送验证码请求", httpMethod = "GET")
    @GetMapping("/getSMSCode")
    public R getSMSCode(@RequestParam String mobile, HttpServletRequest request);

    @ApiOperation(value = "注册/登录", notes = "注册/登录请求", httpMethod = "POST")
    @PostMapping("/doLogin")
    public R doLogin(@RequestBody @Valid RegisterLoginBo registerLoginBo,
                     HttpServletRequest request, HttpServletResponse response);

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录请求", httpMethod = "POST")
    @PostMapping("/logout")
    public R logout(@RequestParam String userId, HttpServletRequest request, HttpServletResponse response);
}
