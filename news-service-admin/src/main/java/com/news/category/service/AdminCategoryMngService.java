package com.news.category.service;

import com.news.common.jsonres.R;
import com.news.model.user.bo.SaveCategoryBo;
import com.news.model.user.pojo.Category;
import org.springframework.validation.BindingResult;

import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 文章分类业务
 * @create 2022-06-22-10:06
 */
public interface AdminCategoryMngService {

    //新增文章分类
    public void addCategory(Category category);

    //更新文章分类
    public void updateCategory(Category newCategory);

    //获取文章分类列表
    public List<Category> getCategoryList();

    //查询当前分类名与标签颜色是否存在
    boolean queryCategoryIfExist(String newName, String tagColor);

    //用户端查询分类分页数据
    public R UserGetCategoryList();

}
