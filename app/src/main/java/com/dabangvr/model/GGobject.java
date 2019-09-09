package com.dabangvr.model;

import java.util.List;

/**
 * 规格类型实体类
 */
public class GGobject {
    private int id;//规格类型id
    private String name;//规格类型名称
    private List<Specifications>goodsSpecList;//规格列表

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

    public List<Specifications> getGoodsSpecList() {
        return goodsSpecList;
    }

    public void setGoodsSpecList(List<Specifications> goodsSpecList) {
        this.goodsSpecList = goodsSpecList;
    }
}
