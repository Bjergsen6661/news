package com.news.model.user.bo;

import javax.validation.constraints.NotBlank;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description `/passport/doLogin`请求携带的对象
 * @create 2022-06-16-11:08
 */
public class RegisterLoginBo {

    @NotBlank(message = "手机号不能为空！")
    private String mobile;

    @NotBlank(message = "短信验证码不能为空！")
    private String smsCode;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    @Override
    public String toString() {
        return "RegisterLoginBo{" +
                "mobile='" + mobile + '\'' +
                ", smsCode='" + smsCode + '\'' +
                '}';
    }
}
