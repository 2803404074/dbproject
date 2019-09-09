package com.dabangvr.model;

import java.util.List;

public class GoodsAll {
    private String id;
    private String categoryId;
    private String name;
    private String title;
    private String listUrl;
    private String sellingPrice;
    private String marketPrice;
    private String remainingInventory;
    private List<GGobject> specList;//规格列表
    private List<ProductInfoVoList> productInfoList;//产品列表

    public GoodsAll() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getListUrl() {
        return listUrl;
    }

    public void setListUrl(String listUrl) {
        this.listUrl = listUrl;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getRemainingInventory() {
        return remainingInventory;
    }

    public void setRemainingInventory(String remainingInventory) {
        this.remainingInventory = remainingInventory;
    }

    public List<GGobject> getSpecList() {
        return specList;
    }

    public void setSpecList(List<GGobject> specList) {
        this.specList = specList;
    }

    public List<ProductInfoVoList> getProductInfoList() {
        return productInfoList;
    }

    public void setProductInfoList(List<ProductInfoVoList> productInfoList) {
        this.productInfoList = productInfoList;
    }
}
