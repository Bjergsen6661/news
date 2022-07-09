package com.news.api.controller.admin;

import com.news.common.jsonres.R;
import com.news.model.user.bo.SaveCategoryBo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 管理文章分类接口
 * @create 2022-06-22-10:03
 */
@Api(value = "管理文章分类维护", tags = {"管理文章分类维护controller"})
@RequestMapping("/categoryMng")
public interface CategoryMngControllerApi {

    @ApiOperation(value = "新增或修改分类", notes = "新增或修改分类请求", httpMethod = "POST")
    @PostMapping("/saveOrUpdateCategory")
    public R saveOrUpdateCategory(@RequestBody @Valid SaveCategoryBo saveCategoryBo);

    @ApiOperation(value = "获取文章分类列表", notes = "获取文章分类列表请求", httpMethod = "POST")
    @PostMapping("/getCatList")
    public R getCatList();

    @ApiOperation(value = "用户端查询文章分类列表", notes = "用户端查询文章分类列表请求", httpMethod = "GET")
    @GetMapping("/getCats")
    public R getCats();
}
