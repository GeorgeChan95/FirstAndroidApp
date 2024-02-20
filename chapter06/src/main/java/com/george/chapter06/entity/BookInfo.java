package com.george.chapter06.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Types;

// “@Entity”注解，表示该类是Room专用的数据类型，对应的表名称也叫BookInfo
@Entity(tableName="book_info")
public class BookInfo {

    @PrimaryKey(autoGenerate = true) // 表示该字段为主键,且自增
    @NonNull // 字段不能为空(主键都不能为空)
    private int id;

    @ColumnInfo(typeAffinity = Types.VARCHAR)
    private String name; // 书籍名称

    @ColumnInfo(typeAffinity = Types.VARCHAR)
    private String author; // 作者

    @ColumnInfo(typeAffinity = Types.VARCHAR)
    private String press; // 出版社

    @Nullable
    @ColumnInfo(name = "price", defaultValue = "NULL", typeAffinity = Types.DOUBLE)
    private Double price; // 价格

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "BookInfo=[" +
                "name: " + name +
                ", author: " + author +
                ", press: " + press +
                ", price: " + price +
                "]";
    }
}

