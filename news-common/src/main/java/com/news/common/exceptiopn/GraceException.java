package com.news.common.exceptiopn;

import com.news.common.jsonres.ResponseStatusEnum;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 优雅处理异常，统一封装
 * @create 2022-06-16-10:52
 */
public class GraceException {

    public static void display(ResponseStatusEnum responseStatus){
        throw new DiyException(responseStatus);
    }
}
