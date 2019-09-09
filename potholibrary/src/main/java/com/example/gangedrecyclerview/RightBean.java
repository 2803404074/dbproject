package com.example.gangedrecyclerview;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by fatchao
 * 日期  2017-07-24.
 * 邮箱  fat_chao@163.com
 */

public class RightBean implements Parcelable {
    private String name;
    private String titleName;
    private String tag;
    private boolean isTitle;
    private String imgsrc;

    private String id;
    private String listUrl;

    private String deliverPrice;//外卖价
    private String packagingPrice;//打包费
    private String startingPrice;//起送价
    private String salesVolume;//销量
    private String sellingPrice;//销售价
    private String remainingInventory; //库存
    private String marketPrice;   //市场价


    private List<GoodsDetails.GoodsSpecVoList> deliveryGoodsSpecVoList;
    private List<GoodsDetails.ProductInfoVoList> deliveryProductInfoVoList;


    public RightBean(String name) {
        this.name = name;
    }


    public RightBean(String id, String name, String listUrl) {
        this.id = id;
        this.name = name;
        this.listUrl = listUrl;
    }

    public RightBean(String name, String id, String listUrl, List<GoodsDetails.GoodsSpecVoList> deliveryGoodsSpecVoList, List<GoodsDetails.ProductInfoVoList> deliveryProductInfoVoList, String marketPrice) {
        this.name = name;
        this.id = id;
        this.listUrl = listUrl;
        this.deliveryGoodsSpecVoList = deliveryGoodsSpecVoList;
        this.deliveryProductInfoVoList = deliveryProductInfoVoList;
        this.marketPrice = marketPrice;
    }

    protected RightBean(Parcel in) {
        name = in.readString();
        titleName = in.readString();
        tag = in.readString();
        isTitle = in.readByte() != 0;
        imgsrc = in.readString();

        id = in.readString();
        listUrl = in.readString();

        deliverPrice = in.readString();
        packagingPrice = in.readString();
        startingPrice = in.readString();
        salesVolume = in.readString();
        sellingPrice = in.readString();
        remainingInventory = in.readString();
        marketPrice = in.readString();
    }

    public static final Creator<RightBean> CREATOR = new Creator<RightBean>() {
        @Override
        public RightBean createFromParcel(Parcel in) {
            return new RightBean(in);
        }

        @Override
        public RightBean[] newArray(int size) {
            return new RightBean[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public int describeContents() {
        return 0;
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

    public String getSalesVolume() {
        return salesVolume;
    }

    public void setSalesVolume(String salesVolume) {
        this.salesVolume = salesVolume;
    }

    public String getRemainingInventory() {
        return remainingInventory;
    }

    public void setRemainingInventory(String remainingInventory) {
        this.remainingInventory = remainingInventory;
    }

    public String getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(titleName);
        dest.writeString(tag);
        dest.writeByte((byte) (isTitle ? 1 : 0));
        dest.writeString(imgsrc);
        dest.writeString(id);
        dest.writeString(listUrl);

        dest.writeString(deliverPrice);
        dest.writeString(packagingPrice);
        dest.writeString(startingPrice);
        dest.writeString(salesVolume);
        dest.writeString(sellingPrice);
        dest.writeString(remainingInventory);
        dest.writeString(marketPrice);
    }
}
