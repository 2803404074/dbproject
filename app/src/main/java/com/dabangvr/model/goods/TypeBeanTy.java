package com.dabangvr.model.goods;

import com.dabangvr.model.TypeBean;

import java.util.List;

/**
 * 分类图片
 */
public class TypeBeanTy {
    private String id;
    private String name;
    private String categoryImg;
    private List<TypeBean> typeList;

    public TypeBeanTy() {
    }

    public String getCategoryImg() {
        return categoryImg;
    }

    public void setCategoryImg(String categoryImg) {
        this.categoryImg = categoryImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TypeBean> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<TypeBean> typeList) {
        this.typeList = typeList;
    }
}
