package com.news.api.controller.user;

import com.news.api.config.MyServiceLists;
import com.news.common.jsonres.R;
import com.news.model.user.bo.UpdateUserInfoBo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 用户服务接口
 * @create 2022-06-17-20:58
 */
@Api(value = "用户信息", tags = "用户信息相关的Controller")
@RequestMapping("/user")
public interface UserControllerApi {

    @ApiOperation(value = "获取用户基础信息请求", notes = "获取用户基础信息请求", httpMethod = "POST")
    @PostMapping("/getUserInfo")
    public R getUserInfo(@RequestParam String userId);


    @ApiOperation(value = "获取账户信息请求", notes = "获取账户信息请求", httpMethod = "POST")
    @PostMapping("/getAccountInfo")
    public R getAccountInfo(@RequestParam String userId);


    @ApiOperation(value = "完善/修改用户信息请求", notes = "完善/修改用户信息请求", httpMethod = "POST")
    @PostMapping("/updateUserInfo")
    public R updateUserInfo(@RequestBody @Valid UpdateUserInfoBo updateUserInfoBo);


    @ApiOperation(value = "根据用户id查询用户", notes = "根据用户id查询用户请求", httpMethod = "GET")
    @GetMapping("/queryByIds")
    public R queryByIds(@RequestParam String userIds);

}
