package com.dabangvr.dep.model;

import java.util.List;

public class WmDetailsMo {
    private String id;
    private String orderSn;
    private int orderState;
    private String payState;
    private String totalPrice;
    private String actualPayPrice;
    private String deliverPrice;
    private String packagingPrice;
    private String address;
    private String consigneeName;
    private String consigneePhone;
    private String orderTime;
    private String deliveryTime;
    private String leaveMessage;
    private String payType;//1微信
    private String deptId;
    private String deptName;
    private List<WmGoods>orderDetailsVoList;

    public WmDetailsMo() {
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    public String getPayState() {
        return payState;
    }

    public void setPayState(String payState) {
        this.payState = payState;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getActualPayPrice() {
        return actualPayPrice;
    }

    public void setActualPayPrice(String actualPayPrice) {
        this.actualPayPrice = actualPayPrice;
    }

    public String getDeliverPrice() {
        return deliverPrice;
    }

    public void setDeliverPrice(String deliverPrice) {
        this.deliverPrice = deliverPrice;
    }

    public String getPackagingPrice() {
        return packagingPrice;
    }

    public void setPackagingPrice(String packagingPrice) {
        this.packagingPrice = packagingPrice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getConsigneePhone() {
        return consigneePhone;
    }

    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public List<WmGoods> getOrderDetailsVoList() {
        return orderDetailsVoList;
    }

    public void setOrderDetailsVoList(List<WmGoods> orderDetailsVoList) {
        this.orderDetailsVoList = orderDetailsVoList;
    }

    public static class WmGoods{
        private String id;
        private String goodsId;
        private String goodsName;
        private String goodsNumber;
        private String sellingPrice;
        private String marketPrice;
        private String logisticsPrice;
        private String packagingPrice;
        private String chartUrl;

        public WmGoods() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(String goodsId) {
            this.goodsId = goodsId;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public String getGoodsNumber() {
            return goodsNumber;
        }

        public void setGoodsNumber(String goodsNumber) {
            this.goodsNumber = goodsNumber;
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

        public String getLogisticsPrice() {
            return logisticsPrice;
        }

        public void setLogisticsPrice(String logisticsPrice) {
            this.logisticsPrice = logisticsPrice;
        }

        public String getPackagingPrice() {
            return packagingPrice;
        }

        public void setPackagingPrice(String packagingPrice) {
            this.packagingPrice = packagingPrice;
        }

        public String getChartUrl() {
            return chartUrl;
        }

        public void setChartUrl(String chartUrl) {
            this.chartUrl = chartUrl;
        }
    }
}
