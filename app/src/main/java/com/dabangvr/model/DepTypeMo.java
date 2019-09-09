package com.dabangvr.model;

/**
 * 店铺类型
 */
public class DepTypeMo {
    private int id;
    private String name;
    private String categoryImg;

    public DepTypeMo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryImg() {
        return categoryImg;
    }

    public void setCategoryImg(String categoryImg) {
        this.categoryImg = categoryImg;
    }
}
