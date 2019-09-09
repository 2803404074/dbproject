package com.dabangvr.model;

/**
 * 规格产品实体
 * 选择规格后，与该实体类的goodsSpecIds对比，一样的话有该规格选择
 */
public class ProductInfoVoList {
    private int id;
    private String name;
    private String goodsSpecIds;
    private String retailPrice;
    private String number;//库存
    private String groupPrice;//拼团规格的价钱
    private String secondsPrice;//秒杀规格的价钱
    public ProductInfoVoList() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public String getRetailPrice() {
        return retailPrice;
    }

    public String getGroupPrice() {
        return groupPrice;
    }

    public void setGroupPrice(String groupPrice) {
        this.groupPrice = groupPrice;
    }

    public String getSecondsPrice() {
        return secondsPrice;
    }

    public void setSecondsPrice(String secondsPrice) {
        this.secondsPrice = secondsPrice;
    }

    public void setRetailPrice(String retailPrice) {
        this.retailPrice = retailPrice;
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

    public String getGoodsSpecIds() {
        return goodsSpecIds;
    }

    public void setGoodsSpecIds(String goodsSpecIds) {
        this.goodsSpecIds = goodsSpecIds;
    }
}
