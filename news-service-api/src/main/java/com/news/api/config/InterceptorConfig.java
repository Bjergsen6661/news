package com.news.api.config;

import com.news.api.interceptor.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 拦截器配置
 * @create 2022-06-16-10:22
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public PassportInterceptor passportInterceptor() {
        return new PassportInterceptor();
    }

    @Bean
    public UserTokenInterceptor userTokenInterceptor() {
        return new UserTokenInterceptor();
    }

    @Bean
    public UserActiveInterceptor userActiveInterceptor() {
        return new UserActiveInterceptor();
    }

    @Bean
    public AdminTokenInterceptor adminTokenInterceptor() {
        return new AdminTokenInterceptor();
    }

    @Bean
    public ArticleReadInterceptor articleReadInterceptor() {
        return new ArticleReadInterceptor();
    }

    /**
     * 注册拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //验证码防刷
        registry.addInterceptor(passportInterceptor())
                .addPathPatterns("/passport/getSMSCode");

        //阅读量防刷
        registry.addInterceptor(articleReadInterceptor())
                .addPathPatterns("/portal/article/readArticle");

        //用户登录才能执行
        registry.addInterceptor(userTokenInterceptor())
                .addPathPatterns("/user/getAccountInfo")
                .addPathPatterns("/fs/uploadFace")
                .addPathPatterns("/user/updateUserInfo")
                .addPathPatterns("/fs/uploadSomeFiles")
                .addPathPatterns("/article/createArticle")
                .addPathPatterns("/article/queryMyList")
                .addPathPatterns("/article/withdraw")
                .addPathPatterns("/article/delete")
                .addPathPatterns("/fans/unfollow")
                .addPathPatterns("/fans/queryAll");

        //用户处于激活状态才能执行
        registry.addInterceptor(userActiveInterceptor())
                .addPathPatterns("/fs/uploadSomeFiles")
                .addPathPatterns("/fans/unfollow")
                .addPathPatterns("/fans/queryAll");

        //管理员登录才能执行
        registry.addInterceptor(adminTokenInterceptor())
                .addPathPatterns("/adminMng/adminIsExist")
                .addPathPatterns("/adminMng/addNewAdmin")
                .addPathPatterns("/adminMng/getAdminList")
                .addPathPatterns("/fs/uploadToGridFS")
                .addPathPatterns("/fs/readInGridFS")
                .addPathPatterns("/friendLinkMng/saveOrUpdateFriendLink")
                .addPathPatterns("/friendLinkMng/getFriendLinkList")
                .addPathPatterns("/friendLinkMng/deleteFriendLinkList")
                .addPathPatterns("/categoryMng/saveOrUpdateCategory")
                .addPathPatterns("/categoryMng/getCatList")
                .addPathPatterns("/appUser/queryAll")
                .addPathPatterns("/appUser/userDetail")
                .addPathPatterns("/appUser/freezeUserOrNot")
                .addPathPatterns("/article/queryAllList")
                .addPathPatterns("/article/doReview");

    }


}

