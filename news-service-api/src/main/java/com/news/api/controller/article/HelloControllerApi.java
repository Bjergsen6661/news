package com.news.api.controller.article;

import com.news.common.jsonres.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-06-22-21:55
 */
@Api(value = "test", tags = {"test"})
@RequestMapping("/article")
public interface HelloControllerApi {

    @ApiOperation(value = "test", notes = "test", httpMethod = "GET")
    @GetMapping("/test")
    public R test();
}
