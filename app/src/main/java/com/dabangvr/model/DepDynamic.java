package com.dabangvr.model;

/**
 * 商家列表-活动
 */
public class DepDynamic {
    private String deptId;
    private String name;
    private String foodType;
    private String logo;
    private String deptSalesVolume;//销量
    private String reachTime;//送达时间
    private String distance;//配送范围 km
    private String deliveryPrice;//配送价
    private String juli;//距离m
    private String activityName;//活动

    public DepDynamic() {
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDeptSalesVolume() {
        return deptSalesVolume;
    }

    public void setDeptSalesVolume(String deptSalesVolume) {
        this.deptSalesVolume = deptSalesVolume;
    }

    public String getReachTime() {
        return reachTime;
    }

    public void setReachTime(String reachTime) {
        this.reachTime = reachTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public String getJuli() {
        return juli;
    }

    public void setJuli(String juli) {
        this.juli = juli;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
