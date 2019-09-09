package com.dabangvr.model;

public class CheckMo {
    private String goodsId;
    private String listUrl;
    private String name;
    private String price;

    public CheckMo(String goodsId, String listUrl, String name, String price) {
        this.goodsId = goodsId;
        this.listUrl = listUrl;
        this.name = name;
        this.price = price;
    }

    public CheckMo() {
    }

    public String getGoodsId() {
        return goodsId;
    }

    public String getListUrl() {
        return listUrl;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }
}
