package com.news.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.news.api.BaseController;
import com.news.api.controller.elasticsearch.EsFansControllerApi;
import com.news.common.enums.Sex;
import com.news.common.jsonres.R;
import com.news.common.utils.JsonUtils;
import com.news.common.utils.PagedGridResult;
import com.news.model.user.eo.FansEo;
import com.news.model.user.eo.MyPageResult;
import com.news.model.user.pojo.AppUser;
import com.news.model.user.pojo.Fans;
import com.news.model.user.vo.FansCountsVo;
import com.news.model.user.vo.RegionRatioVo;
import com.news.user.mapper.FansMapper;
import com.news.user.service.MyFansService;
import com.news.user.service.UserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.news.api.BaseController.REDIS_MY_FOLLOW_COUNTS;
import static com.news.api.BaseController.REDIS_WRITER_FANS_COUNTS;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 粉丝管理业务实现
 * @create 2022-06-25-21:37
 */
@Service
public class MyFansServiceImpl implements MyFansService {

    @Autowired
    FansMapper fansMapper;

    @Autowired
    UserService userService;

    @Autowired
    Sid sid;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    BaseController baseController;

    @Autowired
    EsFansControllerApi esFansClient;

    //需要注意：文字要匹配地图，否则会失败，比如地图中为[江苏], 但是传入的是[江苏省], 那么地图上是不会显示具体的数值的
    public static final String[] regions = {"北京", "天津", "上海", "重庆",
            "河北", "山西", "辽宁", "吉林", "黑龙江", "江苏", "浙江", "安徽", "福建", "江西", "山东",
            "河南", "湖北", "湖南", "广东", "海南", "四川", "贵州", "云南", "陕西", "甘肃", "青海", "台湾",
            "内蒙古", "广西", "西藏", "宁夏", "新疆",
            "香港", "澳门"};

    @Override
    public boolean isMeFollowThisWriter(String writerId, String fanId) {

        /***********************基于数据库的查找**************************/
        Fans fan = new Fans();
        fan.setFanId(fanId);
        fan.setWriterId(writerId);
        int count = fansMapper.selectCount(fan); //查询用户与作家是否存在关注

        /***********************基于es的查找**************************/

        return count > 0 ? true : false;
    }

    @Transactional
    @Override
    public void follow(String writerId, String fanId) {
        // 获得粉丝用户信息
        AppUser fanInfo = userService.getUser(fanId);

        String fanPkId = sid.nextShort(); //构建全局唯一主键

        // 保存作家粉丝关联关系，字段冗余便于统计分析，并且只认成为第一次成为粉丝的数据
        Fans fan = new Fans();
        fan.setId(fanPkId);
        fan.setFanId(fanId);
        fan.setFace(fanInfo.getFace());
        fan.setWriterId(writerId);
        fan.setFanNickname(fanInfo.getNickname());
        fan.setProvince(fanInfo.getProvince());
        fan.setSex(fanInfo.getSex());

        //保存作家粉丝关联信息
        fansMapper.insert(fan);

        // redis 作家粉丝数累加
        redisTemplate.opsForValue().increment(REDIS_WRITER_FANS_COUNTS + ":" + writerId, 1);
        // redis 我的关注数累加
        redisTemplate.opsForValue().increment(REDIS_MY_FOLLOW_COUNTS + ":" + fanId, 1);

        //保存粉丝关系到es中
        FansEo fansEo = new FansEo();
        BeanUtils.copyProperties(fan, fansEo);
        //创建文档
        esFansClient.addDocument(fansEo);

    }

    @Transactional
    @Override
    public void unfollow(String writerId, String fanId) {
        // 删除作家粉丝的关联关系
        Fans fan = new Fans();
        fan.setFanId(fanId);
        fan.setWriterId(writerId);
        fansMapper.delete(fan);

        // redis 作家粉丝数累减
        redisTemplate.opsForValue().decrement(REDIS_WRITER_FANS_COUNTS + ":" + writerId, 1);
        // redis 我的关注数累减
        redisTemplate.opsForValue().decrement(REDIS_MY_FOLLOW_COUNTS + ":" + fanId, 1);

        //删除es中的粉丝关系文档
        esFansClient.deleteDocument(writerId, fanId);
    }

    @Override
    public PagedGridResult queryMyFansList(String writerId, Integer page, Integer pageSize) {

        /********************基于数据库查询所有粉丝***********************/
//        Fans fan = new Fans();
//        fan.setWriterId(writerId);
//
//        //构建分页数据
//        PageHelper.startPage(page, pageSize);
//        List<Fans> list = fansMapper.select(fan);

//        return baseController.setterPagedGrid(list, page);

        /***********************基于es查询所有粉丝***********************/
        R search = esFansClient.search(writerId, page, pageSize);
        MyPageResult res = null;
        if(search.getStatus() == 200){
            String searchJson = JsonUtils.objectToJson(search.getData());
            res = JsonUtils.jsonToPojo(searchJson, MyPageResult.class);
        }else{
            res = new MyPageResult();
        }

        //构建分页数据
        long allRecords = res.getTotal();
        long allPages = allRecords % pageSize == 0 ? allRecords / pageSize : allRecords / pageSize + 1;
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page); //当前页码
        grid.setRows(res.getFansEos()); //详情数据
        grid.setTotal(allPages); //总页数
        grid.setRecords(allRecords); //总记录数

        return grid;
    }

    @Override
    public int queryFansCounts(String writerId, Sex sex) {

        Fans fan = new Fans();
        fan.setWriterId(writerId);

        if (sex == Sex.man) {
            fan.setSex(Sex.man.type);
        } else if (sex == Sex.woman) {
            fan.setSex(Sex.woman.type);
        } else {
            return 0;
        }

        int count = fansMapper.selectCount(fan);
        return count;
    }

    @Override
    public FansCountsVo queryFansCountsByEs(String writerId) {
        //通过es聚合，查询划分男女分布数
        R search = esFansClient.queryFansSex(writerId);
        FansCountsVo res = null;
        if(search.getStatus() == 200){
            String searchJson = JsonUtils.objectToJson(search.getData());
            res = JsonUtils.jsonToPojo(searchJson, FansCountsVo.class);
        }else {
            res = new FansCountsVo();
        }

        return res;
    }

    @Override
    public List<RegionRatioVo> queryRatioByRegion(String writerId) {
        Fans fan = new Fans();
        fan.setWriterId(writerId);

        List<RegionRatioVo> regionRatioVoList = new ArrayList<>();
        //遍历每一个地域，查询所属该地域的粉丝数
        for (String region : regions) {
            fan.setProvince(region);
            int count = fansMapper.selectCount(fan);

            RegionRatioVo regionRatioVO = new RegionRatioVo();
            regionRatioVO.setName(region);
            regionRatioVO.setValue(count);

            regionRatioVoList.add(regionRatioVO);
        }

        return regionRatioVoList;
    }

    @Override
    public List<RegionRatioVo> queryRatioByRegionByEs(String writerId) {
        //通过es聚合，查询粉丝地域分布
        R search = esFansClient.queryFansRegion(writerId);
        Map<String, Integer> map = null;
        if(search.getStatus() == 200){
            String searchJson = JsonUtils.objectToJson(search.getData());
            map = JsonUtils.jsonToPojo(searchJson, Map.class);
        }else {
            map = new HashMap<String, Integer>();
        }

        System.out.println("es传来的map：" + map.toString());

        //遍历每一个地域，查询所属该地域的粉丝数
        List<RegionRatioVo> regionRatioVoList = new ArrayList<>();
        //将聚合统计的map合并到list中
        for (String region : regions) {
            RegionRatioVo regionRatioVO = new RegionRatioVo();
            regionRatioVO.setName(region);
            if(map.containsKey(region)){
                Integer counts = map.get(region);
                regionRatioVO.setValue(counts);
            }else{
                regionRatioVO.setValue(null);
            }

            regionRatioVoList.add(regionRatioVO);
        }

        return regionRatioVoList;
    }

    @Override
    public void forceUpdateFanInfo(String relationId, String writerId, String fanId) {
        // 1. 根据fanId查询用户信息
        AppUser user = userService.getUser(fanId);

        // 2. 更新用户信息到db和es中
        Fans fans = new Fans();
        fans.setId(relationId);
        fans.setFace(user.getFace());
        fans.setFanNickname(user.getNickname());
        fans.setSex(user.getSex());
        fans.setProvince(user.getProvince());
        //2.1、更新db
        fansMapper.updateByPrimaryKeySelective(fans);
        //2.2、更新es
        FansEo fansEo = new FansEo();
        BeanUtils.copyProperties(fans, fansEo);
        fansEo.setWriterId(writerId);
        fansEo.setFanId(fanId);
        esFansClient.updateDocument(fansEo);
    }
}
