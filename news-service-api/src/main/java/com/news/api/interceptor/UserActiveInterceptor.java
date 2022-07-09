package com.news.api.interceptor;

import com.news.common.enums.UserStatus;
import com.news.common.exceptiopn.GraceException;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.common.utils.JsonUtils;
import com.news.model.user.pojo.AppUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 用户激活状态检查拦截器 —— 发/修/删文章、发/查评论等功能需要用户激活以后才能进行
 * @create 2022-06-18-22:38
 */
public class UserActiveInterceptor
        extends BaseInterceptor
        implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //缓存中查询用户信息
        String userId = request.getHeader("headerUserId");
        String redisUser = (String) redisTemplate.opsForValue().get(REDIS_USER_INFO + ":" + userId);

        AppUser user = null;
        if (StringUtils.isNotBlank(redisUser)) {
            user = JsonUtils.jsonToPojo(redisUser, AppUser.class);
        } else {
            //redis查不到用户信息 -> 未登录，不放行
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            return false;
        }

        // 如果不是激活状态则不能执行后续操作
        if (user.getActiveStatus() == null || user.getActiveStatus() != UserStatus.ACTIVE.type) {
            //用户未激活，不能进行相关操作
            GraceException.display(ResponseStatusEnum.USER_INACTIVE_ERROR);
            return false;
        }

        return true;
    }
}
