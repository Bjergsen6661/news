package com.news.model.user.bo;

import javax.validation.constraints.NotBlank;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description '/adminMng/addNewAdmin'请求携带的对象
 * @create 2022-06-20-21:00
 */
public class NewAdminBo {

    @NotBlank(message = "登录名不能为空！")
    private String username;
    @NotBlank(message = "负责人名不能为空！")
    private String adminName;

    //方式一：密码登录
    private String password;
    private String confirmPassword;

    //方式二：人脸识别登录
    private String img64;
    private String faceId;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getImg64() {
        return img64;
    }

    public void setImg64(String img64) {
        this.img64 = img64;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }
}
