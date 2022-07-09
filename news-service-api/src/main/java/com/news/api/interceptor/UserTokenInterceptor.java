package com.news.api.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 用户拦截器 —— 验证是否登录
 * @create 2022-06-18-22:06
 */
public class UserTokenInterceptor extends BaseInterceptor implements HandlerInterceptor {

    /**
     * 目标方法执行之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //在header中获取比直接从cookie中获取，拓展性高
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        //判断是否放行
        boolean run = verifyUserIdToken(userId, userToken, REDIS_USER_TOKEN);
        return run;
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
