package com.george.chapter13.entity;

public class UserInfo {
    public Integer id;
    public String name;
    public Integer age;

    @Override
    public String toString() {
        return "UserInfo: " + "name=" + name + ", age=" + age;
    }
}
