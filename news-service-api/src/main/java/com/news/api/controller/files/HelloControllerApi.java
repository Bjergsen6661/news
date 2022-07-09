package com.news.api.controller.files;

import com.news.common.jsonres.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-06-19-10:34
 */

@Api(value = "test", tags = {"test"})
@RequestMapping("/test")
public interface HelloControllerApi {

    @ApiOperation(value = "test", notes = "test", httpMethod = "GET")
    @GetMapping
    public R test();

}
