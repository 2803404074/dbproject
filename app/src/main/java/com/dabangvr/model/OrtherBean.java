package com.dabangvr.model;

import java.util.List;

/**
 * 订单实体类
 */
public class OrtherBean {
    private String deptLogo;
    private String deptName;
    private String deptTotalPrice;//总费用
    private String deptLogisticsTotalPrice;//快递费
    private String deptId;
    private List<OrtherGoods>goodsList;

    public OrtherBean() {
    }

    public String getDeptLogo() {
        return deptLogo;
    }

    public void setDeptLogo(String deptLogo) {
        this.deptLogo = deptLogo;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptTotalPrice() {
        return deptTotalPrice;
    }

    public void setDeptTotalPrice(String deptTotalPrice) {
        this.deptTotalPrice = deptTotalPrice;
    }

    public String getDeptLogisticsTotalPrice() {
        return deptLogisticsTotalPrice;
    }

    public void setDeptLogisticsTotalPrice(String deptLogisticsTotalPrice) {
        this.deptLogisticsTotalPrice = deptLogisticsTotalPrice;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public List<OrtherGoods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<OrtherGoods> goodsList) {
        this.goodsList = goodsList;
    }
}
