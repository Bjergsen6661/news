package com.news.api.config;

import com.news.api.handler.MyRestErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 基于OkHttp3配置RestTemplate
 * @create 2022-06-24-21:52
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {

        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }

}

