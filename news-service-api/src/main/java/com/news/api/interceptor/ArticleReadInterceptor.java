package com.news.api.interceptor;

import com.news.common.utils.IPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.news.api.BaseController.ARTICLE_ALREADY_READ;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 文章阅读量防刷拦截
 * @create 2022-06-26-10:50
 */
public class ArticleReadInterceptor implements HandlerInterceptor {

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userIP = IPUtil.getRequestIp(request);
        String articleId = request.getParameter("articleId");

        boolean keyIsExist = redisTemplate.hasKey(ARTICLE_ALREADY_READ + ":" + articleId + ":" + userIP);

        if (keyIsExist) {
            //存在用户ip记录，表示读过
            return false;
        }
        return true;
    }
}
