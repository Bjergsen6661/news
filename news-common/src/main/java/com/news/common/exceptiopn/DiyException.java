package com.news.common.exceptiopn;

import com.news.common.jsonres.ResponseStatusEnum;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 自定义异常类
 * @create 2022-06-16-10:30
 */
public class DiyException extends RuntimeException{
    //属性
    static final long serialVersionUID = -7034897190746766938L;

    private ResponseStatusEnum responseStatus; //枚举异常

    //构造器
    public DiyException(ResponseStatusEnum responseStatus){
        super("异常状态码: " +  responseStatus.status() +
                "; 异常信息: " + responseStatus.msg());

        this.responseStatus = responseStatus;
    }

    public ResponseStatusEnum getResponseStatus() {
        return responseStatus;
    }
}
