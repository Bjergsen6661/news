package com.news.article.feignClient;

import com.news.api.config.MyServiceLists;
import com.news.article.feignClient.fallback.UserClientFallbackFactory;
import com.news.common.jsonres.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 远程调用user服务
 * @create 2022-07-04-14:53
 */
@FeignClient(value = MyServiceLists.SERVICE_USER,
                path ="user", //使用path代替RequestMapping
                fallbackFactory = UserClientFallbackFactory.class) //开启feign
//@RequestMapping("/user")
public interface UserClient {

    @GetMapping("/queryByIds")
    public R queryByIds(@RequestParam String userIds);
}
