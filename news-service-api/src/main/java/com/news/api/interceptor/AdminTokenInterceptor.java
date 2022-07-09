package com.news.api.interceptor;

import com.news.api.interceptor.BaseInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 管理员拦截器 —— 验证是否登录
 * @create 2022-06-20-12:03
 */
public class AdminTokenInterceptor extends BaseInterceptor implements HandlerInterceptor {

    /**
     * 目标方法执行之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //前端把信息写进了header
        //在header中获取比直接从cookie中获取，拓展性高 (adminUserId、adminUserToken 与前端对应)
        String aId = request.getHeader("adminUserId");
        String aToken = request.getHeader("adminUserToken");

        //判断是否放行 —— 校验token有效性
        boolean run = verifyUserIdToken(aId, aToken, REDIS_ADMIN_TOKEN);

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
