package com.news.model.user.vo;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 传给前端 粉丝性别分布数量信息
 * @create 2022-06-25-23:21
 */
public class FansCountsVo {

    private Integer manCounts;
    private Integer womanCounts;

    public Integer getManCounts() {
        return manCounts;
    }

    public void setManCounts(Integer manCounts) {
        this.manCounts = manCounts;
    }

    public Integer getWomanCounts() {
        return womanCounts;
    }

    public void setWomanCounts(Integer womanCounts) {
        this.womanCounts = womanCounts;
    }
}
