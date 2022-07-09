package com.news.user.service;

import com.news.common.enums.Sex;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.vo.FansCountsVo;
import com.news.model.user.vo.RegionRatioVo;

import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 粉丝管理业务
 * @create 2022-06-25-21:37
 */
public interface MyFansService {

    //判断当前用户是否关注该作家
    boolean isMeFollowThisWriter(String writerId, String fanId);

    //关注作者，成为粉丝，数量记录保存在redis
    void follow(String writerId, String fanId);

    //取关作者，取消粉丝，删除es中的粉丝关系
    void unfollow(String writerId, String fanId);

    //分页查询当前用户的所有粉丝列表
    PagedGridResult queryMyFansList(String writerId, Integer page, Integer pageSize);

    //查询男、女粉丝数 —— 基于数据库
    int queryFansCounts(String writerId, Sex sex);

    //查询男、女粉丝数 —— 基于es
    FansCountsVo queryFansCountsByEs(String writerId);

    //查询查询每个地域的粉丝数量 —— 基于数据库
    List<RegionRatioVo> queryRatioByRegion(String writerId);

    //查询查询每个地域的粉丝数量 —— 基于es
    List<RegionRatioVo> queryRatioByRegionByEs(String writerId);

    //被动更新粉丝数据信息
    void forceUpdateFanInfo(String relationId, String writerId, String fanId);
}
