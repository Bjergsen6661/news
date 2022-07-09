package com.news.model.user.vo;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 传给前端 粉丝地域分布数量信息
 * @create 2022-06-25-23:28
 */
public class RegionRatioVo {

    private String name; //地域名
    private Integer value; //粉丝数

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RegionRatioVo{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
