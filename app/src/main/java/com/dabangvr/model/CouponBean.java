package com.dabangvr.model;


import java.util.List;

/**
 * 优惠卷bean
 */
public class CouponBean {
    private String name;  //优惠卷名字
    private String favorablePrice;  //优惠价格
    private String details; //优惠卷详情
    private String limit; //优惠卷限制
    private String limit_two; //优惠卷限制
    private String state; //优惠卷领取状态
    private String startDate;
    private String endDate;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFavorablePrice() {
        return favorablePrice;
    }

    public void setFavorablePrice(String favorablePrice) {
        this.favorablePrice = favorablePrice;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLimit_two() {
        return limit_two;
    }

    public void setLimit_two(String limit_two) {
        this.limit_two = limit_two;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}