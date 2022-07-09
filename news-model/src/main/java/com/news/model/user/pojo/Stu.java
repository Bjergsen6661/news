package com.news.model.user.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 测试freemarker
 * @create 2022-06-27-10:13
 */
public class Stu implements Serializable {

    private String uid;
    private String username;
    private Integer age;
    private Date birthday;
    private Float amount;
    private boolean haveChild;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public boolean isHaveChild() {
        return haveChild;
    }

    public void setHaveChild(boolean haveChild) {
        this.haveChild = haveChild;
    }
}

