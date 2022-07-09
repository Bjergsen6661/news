package com.news.api.interceptor;

import com.news.common.exceptiopn.DiyException;
import com.news.common.exceptiopn.GraceException;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.common.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 验证码发送拦截器 —— 防多次发生
 * @create 2022-06-16-9:37
 */
@Slf4j
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 目标方法执行之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        /**
         * 获取用户ip，去redis中查
         *  - 存在，表示在60内发送过，限制发送次数不允许发
         *  - 不存在，表示可以发送
         */
        String userIp = IPUtil.getRequestIp(request);
        String keyIp = userIp.replace(':', '.');
        String o = (String) redisTemplate.opsForValue().get("userIp" + "_" + keyIp);
        if(!StringUtils.isEmpty(o)){
            //封装异常
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
//            throw new DiyException(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            return false;
        }

        return true;
    }


    /**
     * 目标方法执行完成之后
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }


    /**
     * 页面渲染以后
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
