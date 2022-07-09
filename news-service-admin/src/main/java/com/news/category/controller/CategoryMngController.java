package com.news.category.controller;

import com.news.api.BaseController;
import com.news.category.service.AdminCategoryMngService;
import com.news.api.controller.admin.CategoryMngControllerApi;
import com.news.common.exceptiopn.GraceException;
import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.common.utils.JsonUtils;
import com.news.model.user.bo.SaveCategoryBo;
import com.news.model.user.pojo.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import sun.security.provider.certpath.OCSPResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 管理文章分类接口实现
 * @create 2022-06-22-10:05
 */
@RestController
public class CategoryMngController extends BaseController implements CategoryMngControllerApi {

    @Autowired
    AdminCategoryMngService adminCategoryMngService;

    @Override
    public R saveOrUpdateCategory(@Valid SaveCategoryBo saveCategoryBo) {

        //1.判断BindingResult中是否保存了错误的验证信息，如果有则需返回

        //2.执行新增或修改分类操作
        Category newCategory = new Category();
        BeanUtils.copyProperties(saveCategoryBo, newCategory);

        //若id为空则实现新增；反之实现更新
        if(saveCategoryBo.getId() == null){
            //首先查询当前分类名是否存在
            boolean isExist = adminCategoryMngService.queryCategoryIfExist(newCategory.getName(), newCategory.getTagColor());
            if(isExist == false){
                //不存在重复，才能新增
                adminCategoryMngService.addCategory(newCategory);
            }else{
                return R.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);
            }
        }else{
            //分类名与标签颜色有所更改才能更新
            boolean isExist = adminCategoryMngService.queryCategoryIfExist(newCategory.getName(), newCategory.getTagColor());

            if(isExist == false){
                //不存在重复，才能进行更新
                adminCategoryMngService.updateCategory(newCategory);
            }else{
                return R.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);
            }
        }

        return R.ok();
    }

    @Override
    public R getCatList() {
        //获取友情链接列表 —— 数量不多无需分页
        List<Category> categoryList = adminCategoryMngService.getCategoryList();

        return R.ok(categoryList);
    }

    @Override
    public R getCats() {

        //用户端查询分类列表
        return adminCategoryMngService.UserGetCategoryList();
    }
}
