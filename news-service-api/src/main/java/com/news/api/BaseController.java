package com.news.api;

import com.github.pagehelper.PageInfo;
import com.news.common.exceptiopn.GraceException;
import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.common.utils.JsonUtils;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.vo.UserBaseInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 统一业务处理
 * @create 2022-06-16-11:48
 */

@Slf4j
@Component
public class BaseController {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RestTemplate restTemplate;

    public static final String MOBILE_SMSCODE = "smsCode"; //验证码
    public static final String REDIS_USER_TOKEN = "redis_user_token"; //用户登录token
    public static final String REDIS_USER_INFO = "redis_user_info"; //用户信息
    public static final String REDIS_ADMIN_TOKEN = "redis_admin_token"; //管理员登录token
    public static final String TEMP_PATH = "./workspaces/temp_face"; //管理员人脸零时文件存储

    @Value("${website.domain-name}")
    public String DOMAIN_NAME;
    public static final Integer COOKIE_MONTH = 30 * 24 * 60 * 60;
    public static final Integer COOKIE_DELETE = 0;

    public static final String REDIS_ALL_CATEGORY = "redis_all_category"; //缓存所有文章分类

    public static final String REDIS_WRITER_FANS_COUNTS = "redis_fans_counts"; //用户粉丝数
    public static final String REDIS_MY_FOLLOW_COUNTS = "redis_my_follow_counts"; //用户关注数

    public static final String REDIS_ARTICLE_READ_COUNTS = "redis_article_read_counts"; //文章阅读量
    public static final String ARTICLE_ALREADY_READ = "article_already_read"; //已读文章的用户记录
    public static final String REDIS_ARTICLE_COMMENT_COUNTS = "article_comment_counts"; //文章评论数

    public static final String REDIS_ARTICLE_READ_COUNTS_ZSETS = "zset_read_counts"; //文章阅读量


    /**
     * 获取Bo中的错误信息
     * @param result 字段绑定的校验信息
     */
    public Map<String, String> getErrors(BindingResult result){
        Map<String, String> map = new HashMap<>();

        List<FieldError> fieldError = result.getFieldErrors();
        for(FieldError error : fieldError){
            String errorField = error.getField();
            String errorMessage = error.getDefaultMessage();
            map.put(errorField, errorMessage);
        }

        return map;
    }


    /**
     * 保存cookie信息 —— 带转码功能
     * @param cooikeName cookie名
     * @param cookieValue cookie值
     * @param ttl cookie存活时间
     */
    public void setCookie(HttpServletRequest request, HttpServletResponse response,
                          String cooikeName, String cookieValue, Integer ttl){
        try {
            cookieValue = URLEncoder.encode(cookieValue, "utf-8"); //转码处理
            setCookieValue(request, response, cooikeName, cookieValue, ttl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    //设置cookie
    public void setCookieValue(HttpServletRequest request, HttpServletResponse response,
                          String cooikeName, String cookieValue, Integer ttl){
        Cookie cookie = new Cookie(cooikeName, cookieValue);
        cookie.setMaxAge(ttl);
        cookie.setDomain(DOMAIN_NAME);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    //删除cookie
    public void deleteCookie(String cookieName,
                             HttpServletRequest request,
                             HttpServletResponse response) {

        try {
            String deleteValue = URLEncoder.encode("", "utf-8");
            setCookieValue(request, response, cookieName, deleteValue, COOKIE_DELETE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 封装分页信息
     * @param list 查询出来的数据列表
     * @param page 当前页码
     */
    public PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);

        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages()); //总页数
        grid.setRecords(pageList.getTotal()); //总记录数

        return grid;
    }

    /**
     * 返回redis中对应key[粉丝数、关注数]的value
     * @param key 传入对应redis中的key
     * @return
     */
    public Integer getCountsFromRedis(String key) {
        Object o = redisTemplate.opsForValue().get(key);
        String countsStr = JsonUtils.objectToJson(o);

        if (StringUtils.isBlank(countsStr) || o == null) {
            countsStr = "0";
        }
//        Integer counts = Integer.valueOf(countsStr);
        int counts = Integer.parseInt(countsStr);

        return counts;
    }

    /**
     * restTemplate 将用户id远程调用，查询用户基本信息列表
     * @param idSet 去重的id列表
     */
    public List<UserBaseInfoVo> getPublisherList(Set<String> idSet){

        /******************************* 使用RestTemplate远程调用 *************************************/
        String url = "http://win.news.com:8003/user/queryByIds?userIds=" + JsonUtils.objectToJson(idSet);
        log.info("url:" + url);
        ResponseEntity<R> userBaseInfoEntity = restTemplate.getForEntity(url, R.class);
        R body = userBaseInfoEntity.getBody();

        //构建用户基本信息列表
        /******************************* 使用Feign远程调用 *************************************/
//        R body = userClient.queryByIds(JsonUtils.objectToJson(idSet));

        List<UserBaseInfoVo> userBaseInfoVoList = null;
        if(body.getStatus() == 200){
            String userlistsJson = JsonUtils.objectToJson(body.getData());
            userBaseInfoVoList = JsonUtils.jsonToList(userlistsJson, UserBaseInfoVo.class);
        }else{
            GraceException.display(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        }

        return userBaseInfoVoList;
    }


}
