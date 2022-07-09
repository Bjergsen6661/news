package com.news.admin.controller;

import com.news.admin.service.AdminFriendLinkService;
import com.news.api.controller.admin.FriendLinkControllerApi;
import com.news.common.jsonres.R;
import com.news.model.user.bo.SaveFriendLinkBO;
import com.news.model.user.mo.FriendLinkMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 友情链接相关接口实现
 * @create 2022-06-21-22:59
 */
@RestController
public class FriendLinkMngController implements FriendLinkControllerApi {

    @Autowired
    AdminFriendLinkService adminFriendLinkService;

    @Override
    public R saveOrUpdateFriendLink(@Valid SaveFriendLinkBO saveFriendLinkBO) {

        //新增或修改友情链接
        return adminFriendLinkService.saveFriendLink(saveFriendLinkBO);
    }

    @Override
    public R getFriendLinkList() {

        //获取友情链接列表 —— 数量不多无需分页
         List<FriendLinkMO> list =  adminFriendLinkService.getFriendLinkList();

         return R.ok(list);
    }

    @Override
    public R delete(String linkId) {

        //删除友情链接
        return adminFriendLinkService.deleteFriendLink(linkId);
    }

    @Override
    public R getPortalFriendLinkList() {

        //用户首页获取'保留'的友情链接列表
        List<FriendLinkMO> friendLinkMOS = adminFriendLinkService.queryPortalFriendLinkList();
        return R.ok(friendLinkMOS);
    }
}
