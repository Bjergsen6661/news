package com.news.user.controller;

import com.news.api.BaseController;
import com.news.api.controller.user.MyFansControllerApi;
import com.news.common.enums.Sex;
import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.eo.FansEo;
import com.news.model.user.vo.FansCountsVo;
import com.news.model.user.vo.RegionRatioVo;
import com.news.user.service.MyFansService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 粉丝管理接口实现
 * @create 2022-06-25-21:36
 */
@RestController
public class MyFansController extends BaseController implements MyFansControllerApi {

    @Autowired
    public MyFansService myFansService;

    @Override
    public R isMeFollowThisWriter(String writerId, String fanId) {
        //合法性校验
        if(StringUtils.isBlank(writerId) || StringUtils.isBlank(fanId)){
            return R.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }

        //判断当前用户是否关注该作家
        boolean result = myFansService.isMeFollowThisWriter(writerId,fanId);

        return R.ok(result);
    }

    @Override
    public R follow(String writerId, String fanId) {
        //合法性校验
        if(StringUtils.isBlank(writerId) || StringUtils.isBlank(fanId)){
            return R.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }

        //关注作者，成为粉丝，保存粉丝关系到es中
        myFansService.follow(writerId, fanId);

        return R.ok();
    }

    @Override
    public R unfollow(String writerId, String fanId) {
        //合法性校验
        if(StringUtils.isBlank(writerId) || StringUtils.isBlank(fanId)){
            return R.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }

        //取关作者，取消粉丝，删除es中的粉丝关系
        myFansService.unfollow(writerId, fanId);

        return R.ok();
    }

    @Override
    public R queryAll(String writerId, Integer page, Integer pageSize) {
        //分页查询当前用户的所有粉丝列表
        PagedGridResult gridResult = myFansService.queryMyFansList(writerId, page, pageSize);

        System.out.println("当前页数:" + gridResult.getPage());
        System.out.println("总页数:" + gridResult.getTotal());
        System.out.println("总记录数:" + gridResult.getRecords());

        return R.ok(gridResult);
    }

    @Override
    public R queryRatio(String writerId) {
        //合法性校验
        if(StringUtils.isBlank(writerId)){
            return R.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }

        /*************************基于数据库查询男女粉丝数****************************/
//        // 查询男性粉丝数
//        int manCounts = myFansService.queryFansCounts(writerId, Sex.man);
//        // 查询女性粉丝数
//        int womanCounts = myFansService.queryFansCounts(writerId, Sex.woman);
//
//        FansCountsVo fansCountsVo = new FansCountsVo();
//        fansCountsVo.setManCounts(manCounts);
//        fansCountsVo.setWomanCounts(womanCounts);
//
//        return R.ok(fansCountsVo);

        /*************************基于es查询男女粉丝数****************************/
        FansCountsVo fansCountsVo = myFansService.queryFansCountsByEs(writerId);

        System.out.println("男粉丝数：" + fansCountsVo.getManCounts());
        System.out.println("女粉丝数：" + fansCountsVo.getWomanCounts());
        return R.ok(fansCountsVo);
    }

    @Override
    public R queryRatioByRegion(String writerId) {

        /*************************基于数据库查询粉丝地域分布****************************/
//        //查询每个地域的粉丝数量
//        List<RegionRatioVo> regionRatioVoList = myFansService.queryRatioByRegion(writerId);

        /*************************基于es查询粉丝地域分布****************************/
        //查询每个地域的粉丝数量
        List<RegionRatioVo> regionRatioVoList = myFansService.queryRatioByRegionByEs(writerId);

        return R.ok(regionRatioVoList);
    }

    @Override
    public R forceUpdateFanInfo(String relationId, String writerId, String fanId) {
        //被动更新粉丝数据信息
        myFansService.forceUpdateFanInfo(relationId, writerId, fanId);

        return R.ok();
    }
}

