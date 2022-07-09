package com.news.user.service;

import com.news.common.utils.PagedGridResult;

import java.util.Date;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 管理用户业务
 * @create 2022-06-22-16:45
 */
public interface AppUserMngService {

    //日期范围分页查询用户列表
    public PagedGridResult queryAllUserList(String nickname, Integer status,
                                            Date startDate, Date endDate,
                                            Integer page, Integer pageSize);

    //冻结用户账号，或解除封号操作
    public void freezeUserOrNot(String userId, Integer doStatus);
}
