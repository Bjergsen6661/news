package com.news.elasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 配置连接es
 * @create 2022-07-02-12:05
 */
@Configuration
public class ClientConfiguration {

    @Bean
    public RestHighLevelClient client(){
        return new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.200.130:9200")
        ));
    }
}
