package com.news.user.service.impl;

import com.news.api.BaseController;
import com.news.common.enums.Sex;
import com.news.common.enums.UserStatus;
import com.news.common.exceptiopn.GraceException;
import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.common.utils.DateUtil;
import com.news.common.utils.DesensitizationUtil;
import com.news.common.utils.JsonUtils;
import com.news.model.user.bo.UpdateUserInfoBo;
import com.news.model.user.pojo.AppUser;
import com.news.user.mapper.AppUserMapper;
import com.news.user.service.UserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.news.api.BaseController.REDIS_USER_INFO;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 用户信息业务实现类
 * @create 2022-06-16-20:56
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    AppUserMapper appUserMapper;

    @Autowired
    Sid sid;

    @Autowired
    BaseController baseController;

    @Autowired
    RedisTemplate redisTemplate;

//    @Autowired
//    RedisHandler redisHandler;

    private static final String USER_FACE0 = "http://122.152.205.72:88/group1/M00/00/05/CpoxxFw_8_qAIlFXAAAcIhVPdSg994.png";

    @Override
    public AppUser queryByMobile(String mobile) {

        Example example = new Example(AppUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("mobile", mobile);
        AppUser user = appUserMapper.selectOneByExample(example);

        return user;
    }

    @Transactional
    @Override
    public AppUser createByMobile(String mobile) {
        //使用第三方组件Sid构建全库唯一主键
        String userId = sid.nextShort();

        AppUser user = new AppUser();

        user.setId(userId);
        user.setMobile(mobile);
        user.setNickname("用户：" + DesensitizationUtil.commonDisplay(mobile)); //默认用户名（手机号脱敏处理）
        user.setFace(USER_FACE0);
        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        user.setSex(Sex.secret.type);
        user.setActiveStatus(UserStatus.INACTIVE.type); //用户状态
        user.setTotalIncome(0);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        //将封装好的对象数据存入数据库
        appUserMapper.insert(user);

        return user;
    }

    @Override
    public AppUser getUser(String userId) {

        return appUserMapper.selectByPrimaryKey(userId);
    }

    @Override
    public R updateInfo(UpdateUserInfoBo updateUserInfoBo) {

        //1.判断BindingResult中是否保存了错误的验证信息，如果有则需返回
        //设置全局BindingResult处理

        //保证双写一致，先删除redis中的数据，后更新数据库
        String userId = updateUserInfoBo.getId();
        String key = REDIS_USER_INFO + ":" + userId;
        redisTemplate.delete(key);

        //2.执行更新操作
        AppUser userInfo = new AppUser();
        BeanUtils.copyProperties(updateUserInfoBo, userInfo);
        userInfo.setUpdatedTime(new Date());
        userInfo.setActiveStatus(UserStatus.ACTIVE.type); //激活状态
        int resNums = appUserMapper.updateByPrimaryKeySelective(userInfo);

        if(resNums != 1){
            //修改失败，抛异常
            GraceException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
        }

        //最新用户数据同步redis缓存,保存时间30天
        AppUser newUser = getUser(userId);
        redisTemplate.opsForValue().set(key, JsonUtils.objectToJson(newUser), 30, TimeUnit.DAYS);

        //缓存双删策略
        try {
            Thread.sleep(100);
            //再次删除
            redisTemplate.delete(key);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return R.ok();
    }
}
