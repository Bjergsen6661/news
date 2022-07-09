package com.news.api.controller.elasticsearch;

import com.news.api.config.MyServiceLists;
import com.news.common.jsonres.R;
import com.news.model.user.eo.ArticleEo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description es文章远程调用接口
 * @create 2022-07-02-14:36
 */
@Api(value = "es文章业务", tags = {"es文章业务controller"})
@RequestMapping("/esArticle")
@FeignClient(value = MyServiceLists.SERVICE_ELASTICSEARCH, contextId = "article") //开启feign
public interface EsArticleControllerApi {

    @ApiOperation(value = "创建索引库", notes = "创建索引库请求", httpMethod = "GET")
    @GetMapping("/createIndex")
    public R createIndex();


    @ApiOperation(value = "创建文档", notes = "创建文档请求", httpMethod = "POST")
    @PostMapping("/addDoc")
    public R addDocument(@RequestBody ArticleEo articleEo);


    @ApiOperation(value = "删除文档", notes = "删除文档请求", httpMethod = "GET")
    @GetMapping("/delDoc")
    public R deleteDocument(@RequestParam String articleId);


    @ApiOperation(value = "文章首页检索", notes = "文章首页检索请求", httpMethod = "GET")
    @GetMapping("/list")
    public R search(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Integer category,
                                  @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "15") Integer pageSize);


    @ApiOperation(value = "文章首页热文检索", notes = "文章首页热文检索请求", httpMethod = "POST")
    @PostMapping("/hotlist")
    public R hotList(@RequestBody Set<String> setId);


    @ApiOperation(value = "查询作家所有文章", notes = "查询作家所有文章请求", httpMethod = "GET")
    @GetMapping("/getWriterArticles")
    public R getWriterArticles(@RequestParam String writerId);

}
