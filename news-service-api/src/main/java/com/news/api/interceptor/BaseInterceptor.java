package com.news.api.interceptor;

import com.news.common.exceptiopn.GraceException;
import com.news.common.jsonres.ResponseStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 验证utoken、uid
 * @create 2022-06-18-22:10
 */
public class BaseInterceptor {

    @Autowired
    public RedisTemplate redisTemplate;

    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_ADMIN_TOKEN = "redis_admin_token";
    public static final String REDIS_USER_INFO = "redis_user_info";

    public static final String REDIS_ZUUL_IP_TIMES = "zuul_ip:";
    public static final String REDIS_ZUUL_IP_LIMITS = "zuul_ip_limit:";

    public boolean verifyUserIdToken(String uId, String uToken, String redisKeyPrefix) {

        if (StringUtils.isNotBlank(uId) && StringUtils.isNotBlank(uToken)) {
            //获取reids中存储的token
            String redisToken = (String) redisTemplate.opsForValue().get(redisKeyPrefix + ":" + uId);

            //校验token有效性
            if (StringUtils.isBlank(redisToken)) {
                //redis中存储的token为空（过期）
                GraceException.display(ResponseStatusEnum.UN_LOGIN);
                return false;
            } else {
                if (!redisToken.equals(uToken)) {
                    //token不一致，不放行
                    GraceException.display(ResponseStatusEnum.TICKET_INVALID);
                    return false;
                }
            }
        } else {
            //获取不到utoken、uid，未登录，不放行
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            return false;
        }

        /**
         * false: 请求被拦截，被驳回，验证出现问题
         * true: 请求在经过验证校验以后，是OK的，是可以放行的
         */
        return true;
    }

}

