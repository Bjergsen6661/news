package com.news.files.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 配置 文件上传至MongoDB的Bucket
 * @create 2022-06-21-12:12
 */
@Component
public class GridFSWebConfig {

    @Value("${spring.data.mongodb.database}")
    private String mongodb;

    @Bean
    public GridFSBucket getGridFSBucket(MongoClient mongoClient){
        //创建 MongoDB 数据库客户端连接
        MongoDatabase database = mongoClient.getDatabase(mongodb);
        //创建 Bucket
        GridFSBucket bucket = GridFSBuckets.create(database);

        return bucket;
    }

}

