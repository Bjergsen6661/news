package com.news.elasticsearch.controller;

import com.news.api.controller.elasticsearch.EsFansControllerApi;
import com.news.common.jsonres.R;
import com.news.elasticsearch.service.EsFansService;
import com.news.model.user.eo.FansEo;
import com.news.model.user.eo.MyPageResult;
import com.news.model.user.vo.FansCountsVo;
import com.news.model.user.vo.RegionRatioVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description fans 索引库相关操作
 * @create 2022-07-04-13:05
 */
@Slf4j
@RestController
public class EsFansController implements EsFansControllerApi {

    @Autowired
    EsFansService esFansService;

    @Override
    public R createIndex() {
        //创建索引库
        esFansService.createIndex();

        log.info("fans 创建索引库...");
        return R.ok("fans索引库创建成功...");
    }

    @Override
    public R addDocument(FansEo fansEo) {
        //新增文档信息
        esFansService.addDoc(fansEo);

        String docId = fansEo.getWriterId() + "_" + fansEo.getFanId();

        log.info("fans_" + docId + "新增文档...");
        return R.ok("fans_" + docId + "新增文档成功...");
    }

    @Override
    public R deleteDocument(String writerId, String fansId) {
        //删除文档信息
        esFansService.delDoc(writerId, fansId);

        log.info("fans_" + writerId + "_" + fansId + "删除文档...");
        return R.ok("fans_" +  writerId + "_" + fansId + "删除文档成功...");
    }

    @Override
    public R updateDocument(FansEo fansEo) {
        //修改文档信息
        esFansService.updateDoc(fansEo);

        String docId = fansEo.getWriterId() + "_" + fansEo.getFanId();

        log.info("fans_" + docId + "修改文档...");
        return R.ok("fans_" + docId + "修改文档成功...");
    }

    @Override
    public R search(String writerId, Integer page, Integer pageSize) {
        //获取es检索分页信息
        MyPageResult myPageResult = esFansService.search(writerId, page, pageSize);

        return R.ok(myPageResult);
    }

    @Override
    public R queryFansSex(String writerId) {
        //查询文档粉丝性别分布信息
        FansCountsVo fansCountsVo = esFansService.getSexCounts(writerId);


        log.info("writer_" + writerId + "查询粉丝性别分布...");
        return R.ok(fansCountsVo);
    }

    @Override
    public R queryFansRegion(String writerId) {
        //查询文档粉丝地域分布信息
        Map<String, Integer> map  =  esFansService.getRegionCounts(writerId);

        log.info("writer_" + writerId + "查询粉丝地域分布...");
        return R.ok(map);
    }


}
