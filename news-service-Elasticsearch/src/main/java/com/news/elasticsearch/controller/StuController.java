package com.news.elasticsearch.controller;

import com.news.common.jsonres.R;
import com.news.common.utils.JsonUtils;
import com.news.elasticsearch.constants.StuConstants;
import com.news.elasticsearch.pojo.Stu;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description stu 索引库相关操作
 * @create 2022-07-02-12:30
 */
@Slf4j
@RestController
@RequestMapping("/stu")
public class StuController {

    @Autowired
    RestHighLevelClient client;

    /**
     * 创建索引库
     */
    @GetMapping("/createIndex")
    public R createIndex() throws IOException {
        //1.创建Request对象
        CreateIndexRequest request = new CreateIndexRequest("stu");

        //2.准备请求的参数，DSL语句
        request.source(StuConstants.MAPPING_STU, XContentType.JSON);

        //3.发送请求
        client.indices().create(request, RequestOptions.DEFAULT);

        log.info("Stu 创建索引库...");
        return R.ok("Stu创建索引库成功...");
    }


    /**
     * 新增文档
     */
    @GetMapping("/addDoc")
    public R addDocument() throws IOException {
        // 准备Source
        Stu stu = new Stu();
        stu.setStuId(1001L);
        stu.setName("imooc");
        stu.setAge(21);
        stu.setMoney(100.2f);
        stu.setDescription("一位男生");
        String stuJson = JsonUtils.objectToJson(stu);

        // 1.创建request对象
        IndexRequest request = new IndexRequest("stu").id("1");

        // 2.准备JSON数据
        request.source(stuJson, XContentType.JSON);

        // 3.发送请求
        client.index(request, RequestOptions.DEFAULT);

        log.info("Stu 新增文档...");
        return R.ok("Stu新增文档成功...");
    }


    /**
     * 查询文档
     */
    @GetMapping("/getDoc/{id}")
    public R getDocument(@PathVariable String id) throws IOException {
        //1.准备request
        GetRequest request = new GetRequest("stu", id);

        //2.发起请求，得到响应
        GetResponse response = client.get(request, RequestOptions.DEFAULT);

        //3.解析响应结果
        String stuJson = response.getSourceAsString();
        Stu stu = JsonUtils.jsonToPojo(stuJson, Stu.class);
        System.out.println(stu.toString());

        log.info("查询文档_" + id + stu.toString());
        return R.ok(stu);
    }


    /**
     * 删除文档
     */
    @GetMapping("/delDoc/{id}")
    public R deleteDocument(@PathVariable String id) throws IOException {
        // 1.准备Request
        DeleteRequest request = new DeleteRequest("stu", id);

        // 2.发送请求
        client.delete(request, RequestOptions.DEFAULT);

        log.info("删除文档_" + id + "成功...");
        return R.ok("删除文档_" + id);
    }


    /**
     * 修改文档
     */
    @GetMapping("/updateDoc/{id}")
    public R updateDocument(@PathVariable String id) throws IOException {
        //准备Source
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("description", "hello world");
        updateMap.put("age", 12);

        //1.准备request
        UpdateRequest request = new UpdateRequest("stu", id);

        //2.准备请求参数
        request.doc(updateMap);

        //3.发送请求
        client.update(request, RequestOptions.DEFAULT);

        log.info("修改文档_" + id + "成功...");
        return R.ok("修改文档_" + id);
    }
}
