package com.news.model.user.bo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description `/categoryMng/saveOrUpdateCategory`请求携带的对象
 * @create 2022-06-22-10:57
 */
public class SaveCategoryBo {

    //分类id
    private Integer id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    //原来的分类名
    private String oldName;

    //是否在页面显示
    boolean show;

    @NotBlank(message = "分类颜色不能为空")
    private String tagColor;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getTagColor() {
        return tagColor;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }
}
