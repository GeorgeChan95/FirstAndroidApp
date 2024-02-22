package com.george.chapter07_client.entity;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1l;

    private Integer id;

    private String name;

    private Integer age;

    private Long height;

    private Float weight;

    private Boolean married;

    public UserInfo() {
    }

    public UserInfo(String name, Integer age, Long height, Float weight, Boolean married) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.married = married;
    }

    public UserInfo(Integer id, String name, Integer age, Long height, Float weight, Boolean married) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.married = married;
    }

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Boolean getMarried() {
        return married;
    }

    public void setMarried(Boolean married) {
        this.married = married;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", married=" + married +
                '}';
    }
}
