package com.news.elasticsearch.service;

import com.news.model.user.eo.FansEo;
import com.news.model.user.eo.MyPageResult;
import com.news.model.user.vo.FansCountsVo;
import com.news.model.user.vo.RegionRatioVo;

import java.util.List;
import java.util.Map;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description fans 索引库相关业务
 * @create 2022-07-04-13:06
 */
public interface EsFansService {

    //创建索引库
    void createIndex();

    //新增文档信息
    void addDoc(FansEo fansEo);

    //删除文档信息
    void delDoc(String writerId, String fansId);

    //获取es检索分页所有粉丝信息
    MyPageResult search(String writerId, Integer page, Integer pageSize);

    //修改文档信息
    void updateDoc(FansEo fansEo);

    //查询该作家所属的粉丝性别分布
    FansCountsVo getSexCounts(String writerId);

    //查询该作家所属的粉丝地域分布
    Map<String, Integer> getRegionCounts(String writerId);
}
