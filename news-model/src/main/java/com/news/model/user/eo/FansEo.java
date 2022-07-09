package com.news.model.user.eo;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description fans 索引库字段
 *  创建文档时，文档id要对应粉丝id
 * @create 2022-07-04-13:18
 */
public class FansEo {

    private String id;
    private String writerId;
    private String fanId;
    private String face;
    private String fanNickname;
    private Integer sex;
    private String province;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWriterId() {
        return writerId;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public String getFanId() {
        return fanId;
    }

    public void setFanId(String fanId) {
        this.fanId = fanId;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getFanNickname() {
        return fanNickname;
    }

    public void setFanNickname(String fanNickname) {
        this.fanNickname = fanNickname;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
