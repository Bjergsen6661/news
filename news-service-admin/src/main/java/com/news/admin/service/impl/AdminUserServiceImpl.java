package com.news.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.news.admin.mapper.AdminUserMapper;
import com.news.admin.service.AdminUserService;
import com.news.api.BaseController;
import com.news.common.exceptiopn.GraceException;
import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.bo.AdminLoginBO;
import com.news.model.user.bo.NewAdminBo;
import com.news.model.user.pojo.AdminUser;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.news.api.BaseController.COOKIE_MONTH;
import static com.news.api.BaseController.REDIS_ADMIN_TOKEN;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-06-20-10:35
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    public AdminUserMapper adminUserMapper;

    @Autowired
    BaseController baseController;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    Sid sid;

    @Override
    public AdminUser queryAdminByUsername(String username) {

        Example example = new Example(AdminUser.class);
        Example.Criteria adminUserCriteria = example.createCriteria();
        adminUserCriteria.andEqualTo("username", username);

        return adminUserMapper.selectOneByExample(example);
    }

    @Override
    public R getAdminR(AdminLoginBO adminLoginBO,
                       HttpServletRequest request,
                       HttpServletResponse response) {

        //1.判断BindingResult中是否保存了错误的验证信息，如果有则需返回

        //2.通过用户键入的用户名查询管理员信息
        String userName = adminLoginBO.getUsername();
        AdminUser adminUser = this.queryAdminByUsername(userName);

        //3.校验登录信息
        if(adminUser == null ||
           !BCrypt.checkpw(adminLoginBO.getPassword(), adminUser.getPassword())
        ){
            //不存在管理员用户信息，校验失败
            //解密数据库中密码进行比对，账号密码不匹配，校验失败
            return R.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }

        //4.保存用户分布式会话的相关操作
        doLoginSettings(adminUser, request, response);

        return R.ok();
    }

    @Override
    public R ifAdminExist(String username) {
        // 根据用户名查询用户信息，验证管理人用户名必须唯一
        checkAdminExist(username);

        return R.ok();
    }

    @Override
    public R addNewAdmin(NewAdminBo newAdminBo,
                         HttpServletRequest request,
                         HttpServletResponse response) {

        //1.判断BindingResult中是否保存了错误的验证信息，如果有则需返回

        //2.校验存储合法性
        //base64为空，必须使用密码注册
        if(StringUtils.isBlank(newAdminBo.getImg64())){
            //键入密码为空
            if(StringUtils.isBlank(newAdminBo.getPassword()) ||
                    StringUtils.isBlank(newAdminBo.getConfirmPassword())){
                return R.errorCustom(ResponseStatusEnum.ADMIN_PASSWORD_NULL_ERROR);
            }
            //密码两次输入不一致
            if(!newAdminBo.getPassword().equals(newAdminBo.getConfirmPassword())){
                return R.errorCustom(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);
            }
        }

        //3.校验注册的用户名唯一性
        String username = newAdminBo.getUsername();
        checkAdminExist(username);

        //4.将当前通过校验的注册信息进行存储
        String adminId = sid.nextShort(); //设置全局唯一主键
        AdminUser adminUser = new AdminUser();
        adminUser.setId(adminId);
        adminUser.setUsername(newAdminBo.getUsername());
        adminUser.setAdminName(newAdminBo.getAdminName());
        //密码登录 —— 加密存储
        if(StringUtils.isNotBlank(newAdminBo.getPassword())){
            String pwd = BCrypt.hashpw(newAdminBo.getPassword(), BCrypt.gensalt());
            adminUser.setPassword(pwd);
        }
        //人脸登录
        if(StringUtils.isNotBlank(newAdminBo.getFaceId())){
            adminUser.setFaceId(newAdminBo.getFaceId());
        }
        adminUser.setCreatedTime(new Date());
        adminUser.setUpdatedTime(new Date());
        //存储到数据库中
        int res = adminUserMapper.insert(adminUser);

        if(res != 1){
            GraceException.display(ResponseStatusEnum.ADMIN_CREATE_ERROR);
        }
        return R.ok();
    }


    @Override
    public PagedGridResult getAdminLists(Integer page, Integer pageSize) {
        Example adminExample = new Example(AdminUser.class);
        adminExample.orderBy("createdTime").desc();

        //开启分页功能并设置相关属性
        PageHelper.startPage(page, pageSize);
        List<AdminUser> adminUserList = adminUserMapper.selectByExample(adminExample);

        return baseController.setterPagedGrid(adminUserList, page);
    }

    @Override
    public R adminLogout(String adminId,
                         HttpServletRequest request,
                         HttpServletResponse response) {

        //删除redis中的token数据
        redisTemplate.delete(REDIS_ADMIN_TOKEN + ":" + adminId);

        //删除cookie中的数据
        baseController.deleteCookie("atoken", request, response);
        baseController.deleteCookie("aid", request, response);
        baseController.deleteCookie("aname", request, response);

        return R.ok();
    }

    /**
     *  保存信息到redis、cookie中
     *
     * @param admin 信息校验成功的管理员信息
     */
    private void doLoginSettings(AdminUser admin,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        //信息准备
        String adminId = admin.getId();
        String aToken = UUID.randomUUID().toString().trim();
        String adminName = admin.getAdminName();

        //保存当前用户对应的token、用户信息到redis，保存30天
        redisTemplate.opsForValue().set(REDIS_ADMIN_TOKEN + ":" + adminId, aToken, 30, TimeUnit.DAYS);

        //保存用户token、id 和 name到cookie —— "atoken"、"aid"、"aname" 与前端对应
        baseController.setCookie(request, response, "atoken", aToken, COOKIE_MONTH);
        baseController.setCookie(request, response, "aid", adminId, COOKIE_MONTH);
        baseController.setCookie(request, response, "aname", adminName, COOKIE_MONTH);
    }

    /**
     * 根据用户名查询用户信息，验证管理人用户名必须唯一
     * @param username 用户名
     */
    private void checkAdminExist(String username){
        // 根据用户名查询用户信息，验证管理人用户名必须唯一
        AdminUser admin = this.queryAdminByUsername(username);

        if (admin != null) {
            GraceException.display(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);
        }
    }
}
