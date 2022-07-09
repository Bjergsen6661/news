package com.news.api.controller.user;

import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.model.user.bo.UpdateUserInfoBo;
import com.news.model.user.vo.UserBaseInfoVo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 调用端降级处理
 * @create 2022-06-30-11:34
 */
@Component
public class UserFactoryFallback implements FallbackFactory<UserControllerApi> {

    @Override
    public UserControllerApi create(Throwable throwable) {
        return new UserControllerApi() {
            @Override
            public R getUserInfo(String userId) {
                return R.errorCustom(ResponseStatusEnum.SYSTEM_ERROR_FEIGN);
            }

            @Override
            public R getAccountInfo(String userId) {
                return R.errorCustom(ResponseStatusEnum.SYSTEM_ERROR_FEIGN);
            }

            @Override
            public R updateUserInfo(@Valid UpdateUserInfoBo updateUserInfoBo) {
                return R.errorCustom(ResponseStatusEnum.SYSTEM_ERROR_FEIGN);
            }

            @Override
            public R queryByIds(String userIds) {
                System.out.println("进入调用端的服务降级...");

                //用户服务出故障，返回空用户数据
                List<UserBaseInfoVo> publisherList = new ArrayList<>();
                return R.ok(publisherList);
            }
        };
    }
}
