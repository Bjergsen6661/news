package com.news.user.service.impl;

import com.news.api.BaseController;
import com.news.common.enums.UserStatus;
import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.common.utils.IPUtil;
import com.news.common.utils.JsonUtils;
import com.news.common.utils.ValidateCodeUtils;
import com.news.model.user.bo.RegisterLoginBo;
import com.news.model.user.pojo.AppUser;
import com.news.user.service.PassportService;
import com.news.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.news.api.BaseController.*;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 登录验证实现类
 * @create 2022-06-15-21:59
 */
@Slf4j
@Service
public class PassportServiceImpl implements PassportService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    BaseController baseController;

    @Override
    public void sentCode(String phoneNum,
                         HttpServletRequest request) {

        //生成随机的6位验证码
        String code = ValidateCodeUtils.generateValidateCode(6).toString();

        /************************ 使用腾讯云发送短信验证码 **************************/

//        //调用第三方API短信服务 —— 前100次免费
//        SMSUtils smsUtils = new SMSUtils();
//        //您的验证码为：{1}，{2}分钟内有效，如非本人操作，请忽略本短信！
//        smsUtils.sendSms(phoneNum, code, "1");

        /*********************** 模拟发送短信验证码 redis接收 *************************/

        //记录用户ip 限制用户在60s内只能发一次
        String userIp = IPUtil.getRequestIp(request);
        String keyIp = userIp.replace(':', '.');
        log.info("用户ip：" + keyIp);
        redisTemplate.opsForValue().setIfAbsent("userIp" + "_" + keyIp, userIp, 60, TimeUnit.SECONDS);

        //将获取到的验证码保存到redis，设置过期时间60s
        redisTemplate.opsForValue().setIfAbsent(MOBILE_SMSCODE + "_" + phoneNum, code, 60, TimeUnit.SECONDS);

    }

    /**
     * @param registerLoginBo 前端传来的对象
     */
    @Override
    public R getUserR(RegisterLoginBo registerLoginBo,
                      HttpServletRequest request,
                      HttpServletResponse response) {

        //1.判断BindingResult中是否保存了错误的验证信息，如果有则需返回

        //2.校验验证码 —— 去redis中比对
        String mobile = registerLoginBo.getMobile();   //键入的mobile
        String smsCode = registerLoginBo.getSmsCode(); //键入的code

        String RedisCode = (String) redisTemplate.opsForValue().get(MOBILE_SMSCODE + "_" + mobile);
        if(StringUtils.isEmpty(RedisCode) || !RedisCode.equals(smsCode)){
            //校验失败
            return R.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        //3.校验成功进行注册\登录
        AppUser user = userService.queryByMobile(mobile);
        if(user != null && user.getActiveStatus() == UserStatus.FROZEN.type) {
            //若该用户注册过但是被冻结，抛出异常
            return R.errorCustom(ResponseStatusEnum.USER_FROZEN);
        }else if(user == null){
            //若该用户未注册过，需要进行注册入库
            user = userService.createByMobile(mobile);
        }

        //4.保存用户分布式会话的相关操作
        int userActiveStatus = user.getActiveStatus();
        if(userActiveStatus != UserStatus.FROZEN.type){
            //保存信息到redis、cookie中
            doLoginSettings(user, request, response);
        }

        //5.用户登录或注册成功后，删除短信验证码（验证码只能使用一次）
        redisTemplate.delete(MOBILE_SMSCODE + "_" + mobile);

        //返回用户状态给前端
        return R.ok(userActiveStatus);
    }

    @Override
    public R logout(String userId,
                    HttpServletRequest request,
                    HttpServletResponse response) {

        //删除redis中的token数据
        redisTemplate.delete(REDIS_USER_TOKEN + ":" + userId);

        //删除cookie
        baseController.deleteCookie("utoken", request, response);
        baseController.deleteCookie("uid", request, response);

        return R.ok();
    }

    /**
     * 保存信息到redis、cookie中
     *
     * @param user 校验成功与注册成功的用户信息
     */
    private void doLoginSettings(AppUser user,
                                 HttpServletRequest request,
                                 HttpServletResponse response){

        //信息准备
        String userId = user.getId();
        String uToken = UUID.randomUUID().toString().trim();

        //保存当前用户对应的token、用户信息到redis，保存时间30天
        redisTemplate.opsForValue().set(REDIS_USER_TOKEN + ":" + userId, uToken, 30, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user), 30, TimeUnit.DAYS);

        //保存用户token和id到cookie —— "uid"、"utoken" 与前端对应
        baseController.setCookie(request, response, "utoken", uToken, COOKIE_MONTH);
        baseController.setCookie(request, response, "uid", userId, COOKIE_MONTH);
    }

}
