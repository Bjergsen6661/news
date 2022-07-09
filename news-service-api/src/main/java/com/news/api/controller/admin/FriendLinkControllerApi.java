package com.news.api.controller.admin;

import com.news.common.jsonres.R;
import com.news.model.user.bo.SaveFriendLinkBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 友情链接相关接口
 * @create 2022-06-21-22:40
 */
@Api(value = "管理友情链接维护", tags = {"管理友情链接维护controller"})
@RequestMapping("/friendLinkMng")
public interface FriendLinkControllerApi {

    @PostMapping("/saveOrUpdateFriendLink")
    @ApiOperation(value = "新增或修改友情链接", notes = "新增或修改友情链接请求", httpMethod = "POST")
    public R saveOrUpdateFriendLink(@RequestBody @Valid SaveFriendLinkBO saveFriendLinkBO);


    @ApiOperation(value = "获取友情链接列表", notes = "获取友情链接列表请求", httpMethod = "POST")
    @PostMapping("/getFriendLinkList")
    public R getFriendLinkList();


    @ApiOperation(value = "删除友情链接", notes = "删除友情链接请求", httpMethod = "POST")
    @PostMapping("/delete")
    public R delete(@RequestParam String linkId);


    @ApiOperation(value = "用户首页查询友情链接列表", notes = "用户首页查询友情链接列表请求", httpMethod = "GET")
    @GetMapping("/portal/list")
    public R getPortalFriendLinkList();



}


