package com.news.user.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.news.api.BaseController;
import com.news.api.controller.user.UserControllerApi;
import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.common.utils.JsonUtils;
import com.news.model.user.bo.UpdateUserInfoBo;
import com.news.model.user.pojo.AppUser;
import com.news.model.user.vo.UserAccountInfoVo;
import com.news.model.user.vo.UserBaseInfoVo;
import com.news.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description User接口
 * @create 2022-06-17-21:08
 */
@Slf4j
@RestController
public class UserController extends BaseController implements UserControllerApi {

    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public R getUserInfo(String userId) {
        if(StringUtils.isEmpty(userId)){
            log.info("用户未登录...");
            return R.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }

        //获得用户基本信息
        UserBaseInfoVo userBaseInfoVo = getBasicUserInfo(userId);

        //查询redis中用户的粉丝数与关注数，放入vo
        String fansKey = REDIS_WRITER_FANS_COUNTS + ":" + userId;
        String followKey = REDIS_MY_FOLLOW_COUNTS + ":" + userId;
        userBaseInfoVo.setMyFansCounts(getCountsFromRedis(fansKey));
        userBaseInfoVo.setMyFollowCounts(getCountsFromRedis(followKey));

        return R.ok(userBaseInfoVo);
    }

    @Override
    public R getAccountInfo(String userId) {

        if(StringUtils.isEmpty(userId)){
            log.info("用户未登录...");
            return R.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }

        //根据userId查询用户信息
        AppUser user = getUser(userId);

        //返回用户信息Vo —— 对象相同信息拷贝
        UserAccountInfoVo userAccountInfoVo = new UserAccountInfoVo();
        BeanUtils.copyProperties(user, userAccountInfoVo);

        return R.ok(userAccountInfoVo);
    }

    @Override
    public R updateUserInfo(@Valid UpdateUserInfoBo updateUserInfoBo) {

        //完善\更新用户信息，并且激活用户状态
        return userService.updateInfo(updateUserInfoBo);
    }

//    @HystrixCommand(fallbackMethod = "queryByIdsFallback") //熔断使用备用方法
    @SentinelResource(value = "queryByIdsFallback", fallback = "queryByIdsFallback")
    @Override
    public R queryByIds(String userIds) {

        //模拟异常
//        int a = 1 / 0;

        //1.合法性校验
        if (StringUtils.isBlank(userIds)) {
            return R.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        //2.查询并记录用户的基本信息
        List<UserBaseInfoVo> publisherList = new ArrayList<>();
        List<String> userIdList = JsonUtils.jsonToList(userIds, String.class);
        for (String uid : userIdList) {
            // 通过id获得用户基本信息
            UserBaseInfoVo userVO = getBasicUserInfo(uid);
            // 添加到发布者list
            publisherList.add(userVO);
        }

        return R.ok(publisherList);
    }

    /**
     * 用于降级的方法
     */
    public R queryByIdsFallback(String userIds, Throwable e) {
        log.warn("进入降级方法：queryByIdsFallback...");

        List<UserBaseInfoVo> publisherList = new ArrayList<>();

        List<String> userIdList = JsonUtils.jsonToList(userIds, String.class);
        for (String userId : userIdList) {
            // 手动构建空对象，详情页所展示的用户信息可有可无
            UserBaseInfoVo userVO = new UserBaseInfoVo();

            // 添加到publisherList
            publisherList.add(userVO);
        }

        return R.ok(publisherList);
    }


    //公用私有方法 —— 由于用户信息变动频率不大，可以存入redis
    private AppUser getUser(String userId){
        String key = REDIS_USER_INFO + ":" + userId;
        AppUser user = null;

        //先查询缓存
        String userJson = (String) redisTemplate.opsForValue().get(key);
        if(!StringUtils.isEmpty(userJson)){
            log.info("缓存中获取用户信息...");
            user = JsonUtils.jsonToPojo(userJson, AppUser.class);
        }else{
            //缓存中没有，去查数据库并存入缓存
            log.info("数据库中查询用户信息...");
            user = userService.getUser(userId);

            //保存30天
            redisTemplate.opsForValue().set(key, JsonUtils.objectToJson(user), 30, TimeUnit.DAYS);
        }

        return user;
    }


    //获得用户基本信息
    private UserBaseInfoVo getBasicUserInfo(String userId) {
        // 1. 根据userId查询用户的信息
        AppUser user = getUser(userId);

        // 2. 返回用户信息
        UserBaseInfoVo userVO = new UserBaseInfoVo();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }

}
