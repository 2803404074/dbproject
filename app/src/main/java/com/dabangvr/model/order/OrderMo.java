package com.dabangvr.model.order;

import com.dabangvr.model.AddressBean;

import java.util.List;

public class OrderMo {
    private String productId;
    private String orderGoodsTotalPrice;
    private String orderTotalPrice;
    private String goodsId;
    private String integralTag;

    private List<DepGoods> deptGoodsList;

    private boolean addressStatu;

    private AddressBean receivingAddress;

    private String deductPrice;//折扣的价钱
    private String deductOrderPrice;//折扣后的订单总价

    private String anchorId;
    private String orderLogisticsTotalPrice;
    private String number;
    private String integral;
    private String buyType;
    private String totalIntegral;

    public OrderMo() {
    }

    public boolean isAddressStatu() {
        return addressStatu;
    }

    public void setAddressStatu(boolean addressStatu) {
        this.addressStatu = addressStatu;
    }

    public String getProductId() {
        return productId;
    }

    public String getDeductOrderPrice() {
        return deductOrderPrice;
    }

    public void setDeductOrderPrice(String deductOrderPrice) {
        this.deductOrderPrice = deductOrderPrice;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getOrderGoodsTotalPrice() {
        return orderGoodsTotalPrice;
    }

    public void setOrderGoodsTotalPrice(String orderGoodsTotalPrice) {
        this.orderGoodsTotalPrice = orderGoodsTotalPrice;
    }

    public String getOrderTotalPrice() {
        return orderTotalPrice;
    }

    public void setOrderTotalPrice(String orderTotalPrice) {
        this.orderTotalPrice = orderTotalPrice;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getIntegralTag() {
        return integralTag;
    }

    public void setIntegralTag(String integralTag) {
        this.integralTag = integralTag;
    }

    public List<DepGoods> getDeptGoodsList() {
        return deptGoodsList;
    }

    public void setDeptGoodsList(List<DepGoods> deptGoodsList) {
        this.deptGoodsList = deptGoodsList;
    }

    public AddressBean getReceivingAddress() {
        return receivingAddress;
    }

    public void setReceivingAddress(AddressBean receivingAddress) {
        this.receivingAddress = receivingAddress;
    }

    public String getDeductPrice() {
        return deductPrice;
    }

    public void setDeductPrice(String deductPrice) {
        this.deductPrice = deductPrice;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public String getOrderLogisticsTotalPrice() {
        return orderLogisticsTotalPrice;
    }

    public void setOrderLogisticsTotalPrice(String orderLogisticsTotalPrice) {
        this.orderLogisticsTotalPrice = orderLogisticsTotalPrice;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIntegral() {
        return integral;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public String getBuyType() {
        return buyType;
    }

    public void setBuyType(String buyType) {
        this.buyType = buyType;
    }

    public String getTotalIntegral() {
        return totalIntegral;
    }

    public void setTotalIntegral(String totalIntegral) {
        this.totalIntegral = totalIntegral;
    }
}


