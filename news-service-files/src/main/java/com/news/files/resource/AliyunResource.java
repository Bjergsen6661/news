package com.news.files.resource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description OSS文件上传前缀路径配置绑定类
 * @create 2022-06-19-22:21
 */
@Component
@PropertySource("classpath:file-${spring.profiles.active}.properties")
@ConfigurationProperties(prefix = "alioss")
public class AliyunResource {

    private String endpoint;

    // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。
    // 强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
    private String accesskeyid;
    private String accesskeysecret;

    // 填写Bucket名称，例如examplebucket。
    private String bucketName;

    // 填写Object完整路径（Bucket下的目录路径），完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
    private String objectName;

    //host路径
    private String host;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccesskeyid() {
        return accesskeyid;
    }

    public void setAccesskeyid(String accesskeyid) {
        this.accesskeyid = accesskeyid;
    }

    public String getAccesskeysecret() {
        return accesskeysecret;
    }

    public void setAccesskeysecret(String accesskeysecret) {
        this.accesskeysecret = accesskeysecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
