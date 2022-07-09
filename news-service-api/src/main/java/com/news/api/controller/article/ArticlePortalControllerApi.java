package com.news.api.controller.article;

import com.news.common.jsonres.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 用户首页文章接口
 * @create 2022-06-24-11:52
 */
@Api(value = "用户首页文章业务", tags = {"用户首页文章业务controller"})
@RequestMapping("/portal/article")
public interface ArticlePortalControllerApi {

    @ApiOperation(value = "首页查询文章列表", notes = "首页查询文章列表请求", httpMethod = "GET")
    @GetMapping("/list")
    public R list(@RequestParam(required = false) String keyword,
                  @RequestParam(required = false) Integer category,
                  @RequestParam(defaultValue = "1") Integer page,
                  @RequestParam(defaultValue = "15") Integer pageSize);


    @ApiOperation(value = "首页查询热闻列表", notes = "首页查询热闻列表请求", httpMethod = "GET")
    @GetMapping("/hotList")
    public R hotList();


    @ApiOperation(value = "作家页面查询文章列表", notes = "作家页面查询文章列表请求", httpMethod = "GET")
    @GetMapping("/queryArticleListOfWriter")
    public R queryArticleListOfWriter(@RequestParam String writerId,
                                      @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize);


    @ApiOperation(value = "作家页面查询近期佳文", notes = "作家页面查询近期佳文请求", httpMethod = "GET")
    @GetMapping("/queryGoodArticleListOfWriter")
    public R queryGoodArticleListOfWriter(@RequestParam String writerId);


    @ApiOperation(value = "首页查询文章详情", notes = "首页查询文章详情请求", httpMethod = "GET")
    @GetMapping("/detail")
    public R detail(@RequestParam String articleId);


    @ApiOperation(value = "阅读文章，累加阅读量", notes = "阅读文章，累加阅读量", httpMethod = "POST")
    @PostMapping("/readArticle")
    public R readArticle(@RequestParam String articleId,
                         HttpServletRequest request);

    @ApiOperation(value = "获得文章阅读量", notes = "获得文章阅读量", httpMethod = "GET")
    @GetMapping("/readCounts")
    public Integer readCounts(@RequestParam String articleId);


}
