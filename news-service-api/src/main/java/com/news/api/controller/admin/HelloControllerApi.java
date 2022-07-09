package com.news.api.controller.admin;

import com.news.common.jsonres.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-06-19-10:34
 */

@Api(value = "test", tags = {"test"})
@RequestMapping("/admin")
public interface HelloControllerApi {

    @ApiOperation(value = "test", notes = "test", httpMethod = "GET")
    @GetMapping("/test")
    public R test();

}
