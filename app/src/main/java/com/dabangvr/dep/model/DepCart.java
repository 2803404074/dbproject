package com.dabangvr.dep.model;

public class DepCart {
    private String goodsId;
    private String name;
    private String productId;
    private String number;
    private String price;
    private String url;

    public DepCart(String goodsId, String name, String productId, String number, String price, String url) {
        this.goodsId = goodsId;
        this.name = name;
        this.productId = productId;
        this.number = number;
        this.price = price;
        this.url = url;
    }

    public DepCart() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
