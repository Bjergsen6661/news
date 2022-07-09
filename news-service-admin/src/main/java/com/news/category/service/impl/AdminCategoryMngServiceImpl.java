package com.news.category.service.impl;

import com.news.category.mapper.CategoryMapper;
import com.news.category.service.AdminCategoryMngService;
import com.news.api.BaseController;
import com.news.common.exceptiopn.GraceException;
import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.common.utils.JsonUtils;
import com.news.model.user.pojo.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

import static com.news.api.BaseController.REDIS_ALL_CATEGORY;


/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 文章分类业务实现
 * @create 2022-06-22-10:08
 */
@Service
public class AdminCategoryMngServiceImpl implements AdminCategoryMngService {

    @Autowired
    BaseController baseController;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void addCategory(Category category) {
        //将信息保存数据库
        int res = categoryMapper.insert(category);
        if(res != 1){
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }

        // 直接使用redis删除缓存即可，用户端在查询的时候会直接查库，再把最新的数据放入到缓存中
        redisTemplate.delete(REDIS_ALL_CATEGORY);
    }

    @Override
    @Transactional
    public void updateCategory(Category newCategory) {
        //将信息保存数据库
        int res = categoryMapper.updateByPrimaryKey(newCategory);
        if(res != 1){
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }

        // 直接使用redis删除缓存即可，用户端在查询的时候会直接查库，再把最新的数据放入到缓存中
        redisTemplate.delete(REDIS_ALL_CATEGORY);
    }

    @Override
    public List<Category> getCategoryList() {

        return categoryMapper.selectAll();
    }

    @Override
    public boolean queryCategoryIfExist(String newName, String tagColor) {

        List<Category> categories = null;

        //查询当前新分类名的信息
        Example example = new Example(Category.class);
        Example.Criteria categoryCriteria = example.createCriteria();
        categoryCriteria.andEqualTo("name", newName);
        categoryCriteria.andEqualTo("tagColor", tagColor);

        categoryMapper.selectByExample(example);

        //若有查询结果，说明存在重复
        if(categories != null && !categories.isEmpty() && categories.size() > 0){
            return true;
        }

        //反之没重复
        return false;
    }

    @Override
    public R UserGetCategoryList() {
        //获取缓存中的值
        String allCatJson = (String) redisTemplate.opsForValue().get(REDIS_ALL_CATEGORY);
        List<Category> categoryList = null;

        if (StringUtils.isBlank(allCatJson)) {
            //缓存中没有去数据库中查，后在放入缓存
            categoryList = this.getCategoryList();
            redisTemplate.opsForValue().set(REDIS_ALL_CATEGORY, JsonUtils.objectToJson(categoryList));
        } else {
            //缓存中有,直接从缓存中获取
            categoryList = JsonUtils.jsonToList(allCatJson, Category.class);
        }

        return R.ok(categoryList);
    }

}
