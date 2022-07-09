package com.news.elasticsearch.pojo;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description stu索引库字段
 * @create 2022-07-02-12:50
 */
public class Stu {

    private Long stuId;
    private String name;
    private Integer age;
    private float money;
    private String description;


    public Long getStuId() {
        return stuId;
    }

    public void setStuId(Long stuId) {
        this.stuId = stuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Stu{" +
                "stuId=" + stuId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", money=" + money +
                ", description='" + description + '\'' +
                '}';
    }
}
