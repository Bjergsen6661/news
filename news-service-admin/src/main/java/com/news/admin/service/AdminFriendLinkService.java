package com.news.admin.service;

import com.news.common.jsonres.R;
import com.news.model.user.bo.SaveFriendLinkBO;
import com.news.model.user.mo.FriendLinkMO;
import org.springframework.validation.BindingResult;

import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 友情链接业务
 * @create 2022-06-21-23:05
 */
public interface AdminFriendLinkService {

    //新增或修改友情链接
    public R saveFriendLink(SaveFriendLinkBO saveFriendLinkBO);

    //获取友情链接列表
    public List<FriendLinkMO> getFriendLinkList();

    //删除友情链接
    public R deleteFriendLink(String linkId);

    //用户首页获取'保留'的友情链接列表
    public List<FriendLinkMO> queryPortalFriendLinkList();

}
