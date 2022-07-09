package com.news.api.controller.user;

import com.news.common.jsonres.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 用户管理接口
 * @create 2022-06-22-16:16
 */
@Api(value = "用户管理维护", tags = {"用户管理维护controller"})
@RequestMapping("/appUser")
public interface AppUserMngControllerApi {

    @ApiOperation(value = "查询所有网站用户", notes = "查询所有网站用户请求", httpMethod = "POST")
    @PostMapping("/queryAll")
    public R queryAll(@RequestParam String nickname,
                      @RequestParam Integer status,
                      @RequestParam Date startDate,
                      @RequestParam Date endDate,
                      @ApiParam(name = "page", value = "当前页", required = false)
                          @RequestParam(defaultValue = "1") Integer page,
                      @ApiParam(name = "pageSize", value = "每一页显示的条数", required = false)
                          @RequestParam(defaultValue = "8") Integer pageSize);


    @ApiOperation(value = "查看用户详情信息", notes = "查看用户详情信息", httpMethod = "POST")
    @PostMapping("/userDetail")
    public R userDetail(@RequestParam String userId);


    @ApiOperation(value = "冻结用户、解除封号", notes = "冻结用户，解除封号请求", httpMethod = "POST")
    @PostMapping("/freezeUserOrNot")
    public R freezeUserOrNot(@RequestParam String userId,
                             @RequestParam Integer doStatus);

}
