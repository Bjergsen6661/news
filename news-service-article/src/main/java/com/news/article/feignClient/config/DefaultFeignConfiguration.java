package com.news.article.feignClient.config;

import com.news.article.feignClient.fallback.UserClientFallbackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description feign降级配置
 * @create 2022-07-08-13:25
 */
@Configuration
public class DefaultFeignConfiguration {

    @Bean
    public UserClientFallbackFactory userClientFallbackFactory(){
        return new UserClientFallbackFactory();
    }
}
