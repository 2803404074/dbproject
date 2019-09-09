package com.dabangvr.model.goods;

import java.util.List;

/**
 * 外卖商品
 */
public class DeliveGoodsDetails {
    private String id;
    private String name;
    private String listUrl;
    private String sellingPrice;
    private String marketPrice;
    private String deliverPrice;
    private String packagingPrice;
    private String startingPrice;
    private String remainingInventory;
    private String salesVolume;

    private List<GoodsDetails.GoodsSpecVoList> deliveryGoodsSpecVoList;
    private List<GoodsDetails.ProductInfoVoList> deliveryProductInfoVoList;


    public DeliveGoodsDetails() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(String startingPrice) {
        this.startingPrice = startingPrice;
    }

    public String getRemainingInventory() {
        return remainingInventory;
    }

    public void setRemainingInventory(String remainingInventory) {
        this.remainingInventory = remainingInventory;
    }

    public String getSalesVolume() {
        return salesVolume;
    }

    public void setSalesVolume(String salesVolume) {
        this.salesVolume = salesVolume;
    }

    public List<GoodsDetails.GoodsSpecVoList> getDeliveryGoodsSpecVoList() {
        return deliveryGoodsSpecVoList;
    }

    public void setDeliveryGoodsSpecVoList(List<GoodsDetails.GoodsSpecVoList> deliveryGoodsSpecVoList) {
        this.deliveryGoodsSpecVoList = deliveryGoodsSpecVoList;
    }

    public List<GoodsDetails.ProductInfoVoList> getDeliveryProductInfoVoList() {
        return deliveryProductInfoVoList;
    }

    public void setDeliveryProductInfoVoList(List<GoodsDetails.ProductInfoVoList> deliveryProductInfoVoList) {
        this.deliveryProductInfoVoList = deliveryProductInfoVoList;
    }
}
