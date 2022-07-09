package com.news.api.controller.article;

import com.news.common.jsonres.R;
import com.news.model.user.bo.CommentReplyBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 文章评论接口
 * @create 2022-06-26-15:56
 */
@Api(value = "文章详情页的评论业务", tags = {"文章详情页的评论业务controller"})
@RequestMapping("/comment")
public interface CommentControllerApi {

    @ApiOperation(value = "用户留言，或回复留言", notes = "用户留言，或回复留言请求", httpMethod = "POST")
    @PostMapping("/createComment")
    public R createComment(@RequestBody @Valid CommentReplyBO commentReplyBO);


    @ApiOperation(value = "用户评论数查询", notes = "用户评论数查询请求", httpMethod = "GET")
    @GetMapping("/counts")
    public R commentCounts(@RequestParam String articleId);


    @ApiOperation(value = "分页查询文章的所有评论列表", notes = "分页查询文章的所有评论列表请求", httpMethod = "GET")
    @GetMapping("/list")
    public R list(@RequestParam String articleId,
                  @RequestParam(defaultValue = "1") Integer page,
                  @RequestParam(defaultValue = "10") Integer pageSize);


    @ApiOperation(value = "用户评论管理分页查询评论", notes = "用户评论管理分页查询评论请求", httpMethod = "POST")
    @PostMapping("/mng")
    public R queryWriterCommentsMng(@RequestParam String writerId,
                           @RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "10") Integer pageSize);


    @ApiOperation(value = "用户评论管理删除评论", notes = "用户评论管理删除评论请求", httpMethod = "POST")
    @PostMapping("/delete")
    public R deleteComment(@RequestParam String writerId,
                           @RequestParam String commentId);

}

