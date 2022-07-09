package com.news.api.controller.article;

import com.news.common.jsonres.R;
import com.news.model.user.bo.NewArticleBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 文章入库接口
 * @create 2022-06-23-10:40
 */

@Api(value = "文章业务", tags = {"文章业务controller"})
@RequestMapping("/article")
public interface ArticleControllerApi {

    @ApiOperation(value = "用户发文", notes = "用户发文请求", httpMethod = "POST")
    @PostMapping("/createArticle")
    public R createArticle(@RequestBody @Valid NewArticleBO newArticleBO);


    @ApiOperation(value = "用户查询的所有文章列表", notes = "用户查询的所有文章列表请求", httpMethod = "POST")
    @PostMapping("/queryMyList")
    public R queryMyList(@RequestParam String userId,
                         @RequestParam String keyword,
                         @RequestParam Integer status,
                         @RequestParam Date startDate,
                         @RequestParam Date endDate,
                         @ApiParam(name = "page", value = "当前页码", required = false)
                             @RequestParam(defaultValue = "1") Integer page,
                         @ApiParam(name = "pageSize", value = "每一页显示的条数", required = false)
                             @RequestParam(defaultValue = "10") Integer pageSize);


    @ApiOperation(value = "管理员查询用户的所有文章列表", notes = "管理员查询用户的所有文章列表请求", httpMethod = "POST")
    @PostMapping("/queryAllList")
    public R queryAllList(@RequestParam Integer status,
                          @ApiParam(name = "page", value = "当前页码", required = false)
                              @RequestParam(defaultValue = "1") Integer page,
                          @ApiParam(name = "pageSize", value = "每一页显示的条数", required = false)
                              @RequestParam(defaultValue = "10") Integer pageSize);


    @ApiOperation(value = "管理员修改用户的文章状态", notes = "管理员修改用户的文章状态请求 1通过 0不通过", httpMethod = "POST")
    @PostMapping("/doReview")
    public R doReview(@RequestParam String articleId,
                      @RequestParam Integer passOrNot);


    @ApiOperation(value = "用户撤销文章", notes = "用户撤销文章请求", httpMethod = "POST")
    @PostMapping("/withdraw")
    public R withdraw(@RequestParam String userId,
                      @RequestParam String articleId);


    @ApiOperation(value = "用户删除文章", notes = "用户删除文章", httpMethod = "POST")
    @PostMapping("/delete")
    public R delete(@RequestParam String userId,
                    @RequestParam String articleId);

}
