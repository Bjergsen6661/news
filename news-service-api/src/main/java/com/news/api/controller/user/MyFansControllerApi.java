package com.news.api.controller.user;

import com.news.common.jsonres.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 粉丝管理接口
 * @create 2022-06-25-21:34
 */
@Api(value = "粉丝管理", tags = {"粉丝管理的controller"})
@RequestMapping("/fans")
public interface MyFansControllerApi {

    @PostMapping("/isMeFollowThisWriter")
    @ApiOperation(value = "查询当前用户是否关注作家", notes = "查询当前用户是否关注作家", httpMethod = "POST")
    public R isMeFollowThisWriter(@RequestParam String writerId,
                                  @RequestParam String fanId);


    @ApiOperation(value = "关注作家，成为粉丝", notes = "关注作家，成为粉丝请求", httpMethod = "POST")
    @PostMapping("/follow")
    public R follow(@RequestParam String writerId,
                    @RequestParam String fanId);


    @ApiOperation(value = "取消关注，作家损失粉丝", notes = "取消关注，作家损失粉丝请求", httpMethod = "POST")
    @PostMapping("/unfollow")
    public R unfollow(@RequestParam String writerId,
                      @RequestParam String fanId);


    @ApiOperation(value = "用户查询自己的所有粉丝", notes = "用户查询自己的所有粉丝请求", httpMethod = "POST")
    @PostMapping("/queryAll")
    public R queryAll(@RequestParam String writerId,
                      @RequestParam(defaultValue = "1") Integer page,
                      @RequestParam(defaultValue = "20") Integer pageSize);


    @ApiOperation(value = "查询粉丝男女比例", notes = "查询粉丝男女比例请求", httpMethod = "POST")
    @PostMapping("/queryRatio")
    public R queryRatio(@RequestParam String writerId);


    @ApiOperation(value = "查询粉丝地域比例", notes = "查询粉丝地域比例请求", httpMethod = "POST")
    @PostMapping("/queryRatioByRegion")
    public R queryRatioByRegion(@RequestParam String writerId);


    @ApiOperation(value = "被动更新粉丝相关信息", notes = "被动更新粉丝相关信息请求", httpMethod = "POST")
    @PostMapping("/forceUpdateFanInfo")
    public R forceUpdateFanInfo(@RequestParam String relationId,
                                @RequestParam String writerId,
                                @RequestParam String fanId);

}

