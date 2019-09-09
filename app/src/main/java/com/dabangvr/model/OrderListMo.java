package com.dabangvr.model;

import java.util.List;

/**
 * 订单列表mo
 */
public class OrderListMo {
    private String id;//订单id
    private int  orderState;
    private String orderStateValue;
    private String deptId;//商家id
    private String deptName;//商家名称
    private String deptLogo;//商家logo
    private String orderTotalPrice;//订单总价
    private List<OrderGoodsList> orderGoodslist;//订单的商品列表


    public OrderListMo() {
    }

    public String getOrderTotalPrice() {
        return orderTotalPrice;
    }

    public void setOrderTotalPrice(String orderTotalPrice) {
        this.orderTotalPrice = orderTotalPrice;
    }

    public String getOrderStateValue() {
        return orderStateValue;
    }

    public void setOrderStateValue(String orderStateValue) {
        this.orderStateValue = orderStateValue;
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptLogo() {
        return deptLogo;
    }

    public void setDeptLogo(String deptLogo) {
        this.deptLogo = deptLogo;
    }

    public List<OrderGoodsList> getOrderGoodslist() {
        return orderGoodslist;
    }

    public void setOrderGoodslist(List<OrderGoodsList> orderGoodslist) {
        this.orderGoodslist = orderGoodslist;
    }
}
