package com.news.article.feignClient.fallback;

import com.news.article.feignClient.UserClient;
import com.news.common.jsonres.R;
import com.news.common.utils.JsonUtils;
import com.news.model.user.vo.UserBaseInfoVo;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 远程调用user的异常处理
 * @create 2022-07-08-13:21
 */
@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable throwable) {
        return new UserClient() {
            @Override
            public R queryByIds(String userIds) {
                log.warn("用户服务故障，进入客户端的服务降级");

                List<UserBaseInfoVo> publisherList = new ArrayList<>();
                return R.ok(publisherList);
            }
        };
    }
}
