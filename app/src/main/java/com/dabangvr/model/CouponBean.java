package com.dabangvr.model;


import java.util.List;

/**
 * 优惠卷bean
 */
public class CouponBean {
    private String name;  //优惠卷名字
    private String details; //优惠卷详情
    private String limit; //优惠卷限制
    private String limit_two; //优惠卷限制
    private String state; //优惠卷领取状态

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