package com.news.admin.controller;


import com.news.api.controller.admin.HelloControllerApi;
import com.news.common.jsonres.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 测试
 * @create 2022-06-19-10:33
 */
@Slf4j
@RestController
 public class HelloController implements HelloControllerApi {

    @Override
    public R test() {

        log.info("hello");
        log.error("hello");
        log.warn("hello");
        log.debug("hello");

        return R.ok("hello world !");
    }
}
