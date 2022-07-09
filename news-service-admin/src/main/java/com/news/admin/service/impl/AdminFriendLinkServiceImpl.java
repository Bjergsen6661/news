package com.news.admin.service.impl;

import com.news.admin.repository.FriendLinkRepository;
import com.news.admin.service.AdminFriendLinkService;
import com.news.api.BaseController;
import com.news.common.enums.YesOrNo;
import com.news.common.jsonres.R;
import com.news.model.user.bo.SaveFriendLinkBO;
import com.news.model.user.mo.FriendLinkMO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 友情链接业务实现
 * @create 2022-06-21-23:05
 */
@Slf4j
@Service
public class AdminFriendLinkServiceImpl implements AdminFriendLinkService {

    @Autowired
    BaseController baseController;

    @Autowired
    private FriendLinkRepository friendLinkRepository;

    @Override
    public R saveFriendLink(SaveFriendLinkBO saveFriendLinkBO) {

        //1.判断BindingResult中是否保存了错误的验证信息，如果有则需返回

        //2.将友情链接保存到MongoDB中
        FriendLinkMO friendLinkMO = new FriendLinkMO();
        BeanUtils.copyProperties(saveFriendLinkBO, friendLinkMO);
        friendLinkMO.setCreateTime(new Date());
        friendLinkMO.setUpdateTime(new Date());

        friendLinkRepository.save(friendLinkMO);
        log.info("保存友情链接成功..." );

        return R.ok();
    }

    @Override
    public List<FriendLinkMO> getFriendLinkList() {

        //获取所有友情链接
        return friendLinkRepository.findAll();
    }

    @Override
    public R deleteFriendLink(String linkId) {

        //删除友情链接
        friendLinkRepository.deleteById(linkId);
        log.info("删除友情链接成功..." );

        return R.ok();
    }

    @Override
    public List<FriendLinkMO> queryPortalFriendLinkList() {

        //用户首页获取'保留'的友情链接列表
        return friendLinkRepository.getAllByIsDelete(YesOrNo.NO.type);
    }
}
