package com.dabangvr.model;


import java.util.List;

/**
 * 商品列表的主要信息类
 */
public class Goods {
    private String id;

    private String categoryId;

    private String name;

    private String title;

    private String describe;

    private String listUrl;

    private String sellingPrice;

    private String marketPrice;

    private String salesVolume;

    private String assembleId;//拼团商品特有
    private String assembleNumber;//拼团商品特有
    private String startTime;//拼团商品特有
    private String endTime;//拼团商品特有

    private String secondsStartTime;//秒杀
    private String secondsEndTime;//秒杀结束时间
    private String goodsListUrl;

    private List<ActivityBean> activitys;

    public List<ActivityBean> getActivitys() {
        return activitys;
    }

    public void setActivitys(List<ActivityBean> activitys) {
        this.activitys = activitys;
    }

    public Goods() {
    }

    public String getSalesVolume() {
        return salesVolume;
    }

    public void setSalesVolume(String salesVolume) {
        this.salesVolume = salesVolume;
    }

    public String getSecondsStartTime() {
        return secondsStartTime;
    }

    public void setSecondsStartTime(String secondsStartTime) {
        this.secondsStartTime = secondsStartTime;
    }

    public String getSecondsEndTime() {
        return secondsEndTime;
    }

    public void setSecondsEndTime(String secondsEndTime) {
        this.secondsEndTime = secondsEndTime;
    }

    public String getGoodsListUrl() {
        return goodsListUrl;
    }

    public void setGoodsListUrl(String goodsListUrl) {
        this.goodsListUrl = goodsListUrl;
    }

    public String getAssembleNumber() {
        return assembleNumber;
    }

    public void setAssembleNumber(String assembleNumber) {
        this.assembleNumber = assembleNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAssembleId() {
        return assembleId;
    }

    public void setAssembleId(String assembleId) {
        this.assembleId = assembleId;
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

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getListUrl() {
        return listUrl;
    }

    public void setListUrl(String listUrl) {
        this.listUrl = listUrl;
    }
}