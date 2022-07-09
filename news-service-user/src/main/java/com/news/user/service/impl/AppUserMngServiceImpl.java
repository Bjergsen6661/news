package com.news.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.news.api.BaseController;
import com.news.common.enums.UserStatus;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.pojo.AppUser;
import com.news.user.mapper.AppUserMapper;
import com.news.user.service.AppUserMngService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

import static com.news.api.BaseController.REDIS_USER_INFO;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 管理用户业务实现
 * @create 2022-06-22-16:46
 */
@Service
public class AppUserMngServiceImpl implements AppUserMngService {

    @Autowired
    AppUserMapper appUserMapper;

    @Autowired
    BaseController baseController;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public PagedGridResult queryAllUserList(String nickname, Integer status,
                                            Date startDate, Date endDate,
                                            Integer page, Integer pageSize) {

        //构建查询 criteria
        Example userExample = new Example(AppUser.class);
        userExample.orderBy("createdTime").desc();
        Example.Criteria criteria = userExample.createCriteria();

        //进行条件查询
        if (StringUtils.isNotBlank(nickname)) {
            criteria.andLike("nickname", "%" + nickname + "%");
        }
        if (UserStatus.isUserStatusValid(status)) {
            criteria.andEqualTo("activeStatus", status);
        }
        if (startDate != null) {
            criteria.andGreaterThanOrEqualTo("createdTime", startDate);
        }
        if (endDate != null) {
            criteria.andLessThanOrEqualTo("createdTime", endDate);
        }

        //分页设置
        PageHelper.startPage(page, pageSize);
        List<AppUser> list = appUserMapper.selectByExample(userExample);

        return baseController.setterPagedGrid(list, page);
    }

    @Override
    public void freezeUserOrNot(String userId, Integer status) {
        //更改用户状态
        AppUser temp = new AppUser();
        temp.setId(userId);
        temp.setActiveStatus(status);

        //更新数据库数据
        appUserMapper.updateByPrimaryKeySelective(temp);

        // 同步reids用户状态：
        // 方式：删除用户会话，使得用户需要重新登录来刷新他的状态，
        // admin不干预，让用户重新登录系统再去其他的操作，目的就是重置会话信息
        redisTemplate.delete(REDIS_USER_INFO + ":" + userId);
    }

}
