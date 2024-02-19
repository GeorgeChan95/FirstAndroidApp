package com.george.logintest.entity;

import java.io.Serializable;

public class LoginInfo implements Serializable {
    private Integer id;
    private String phone;
    private String password;
    /**
     * 是否记住密码：0-否 1-是
     */
    private Integer remember;

    public LoginInfo() {
    }

    public LoginInfo(String phone, String password, Integer remember) {
        this.phone = phone;
        this.password = password;
        this.remember = remember;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRemember() {
        return remember;
    }

    public void setRemember(Integer remember) {
        this.remember = remember;
    }
}
