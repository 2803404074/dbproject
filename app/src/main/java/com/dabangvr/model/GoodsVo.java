package com.dabangvr.model;

import java.io.Serializable;

/**
 * 商品
 */
public class GoodsVo implements Serializable {

    //商品id
    private String id;
    //商品类型id
    private String categoryId;
    //商品类型name
    private String categoryName;
    //商品名称
    private String name;
    //名称标题
    private String title;
    //商品描述
    private String describe;
    //列表图rul
    private String listUrl;
    //商品销售价
    private String sellingPrice;
    //商品市场价
    private String marketPrice;
    //团购价格
    private String groupPrice;

    //库存
    private String remainingInventory;

    //商家id
    private String deptId;

    //销售量
    private String salesVolume;

    //商品详情
    private String goodsDesc;

    //是否热销
    private String isHot;

    private String deptName;

    private String deptLogo;


    //团购基础信息
    private String assembleId;
    private String assemblePriec;
    private String assembleNumber;
    private String startTime;
    private String endTime;
    private String restrictionVolume;
    private String activeState;
    private String remainingGroupNumber;//拼团库存

    //秒杀基础信息
    private String secondsStartTime;
    private String secondsEndTime;
    private String secondsState;
    private String secondsNumber;
    private String secondsPrice;
    private String remainingSecondsNumber;//拼团库存


    public GoodsVo() {
    }

    public String getRemainingGroupNumber() {
        return remainingGroupNumber;
    }

    public void setRemainingGroupNumber(String remainingGroupNumber) {
        this.remainingGroupNumber = remainingGroupNumber;
    }

    public String getRemainingSecondsNumber() {
        return remainingSecondsNumber;
    }

    public void setRemainingSecondsNumber(String remainingSecondsNumber) {
        this.remainingSecondsNumber = remainingSecondsNumber;
    }

    public String getSecondsPrice() {
        return secondsPrice;
    }

    public void setSecondsPrice(String secondsPrice) {
        this.secondsPrice = secondsPrice;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public String getGroupPrice() {
        return groupPrice;
    }

    public void setGroupPrice(String groupPrice) {
        this.groupPrice = groupPrice;
    }

    public String getRemainingInventory() {
        return remainingInventory;
    }

    public void setRemainingInventory(String remainingInventory) {
        this.remainingInventory = remainingInventory;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getSalesVolume() {
        return salesVolume;
    }

    public void setSalesVolume(String salesVolume) {
        this.salesVolume = salesVolume;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public String getIsHot() {
        return isHot;
    }

    public void setIsHot(String isHot) {
        this.isHot = isHot;
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

    public String getAssembleId() {
        return assembleId;
    }

    public void setAssembleId(String assembleId) {
        this.assembleId = assembleId;
    }

    public String getAssemblePriec() {
        return assemblePriec;
    }

    public void setAssemblePriec(String assemblePriec) {
        this.assemblePriec = assemblePriec;
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

    public String getRestrictionVolume() {
        return restrictionVolume;
    }

    public void setRestrictionVolume(String restrictionVolume) {
        this.restrictionVolume = restrictionVolume;
    }

    public String getActiveState() {
        return activeState;
    }

    public void setActiveState(String activeState) {
        this.activeState = activeState;
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

    public String getSecondsState() {
        return secondsState;
    }

    public void setSecondsState(String secondsState) {
        this.secondsState = secondsState;
    }

    public String getSecondsNumber() {
        return secondsNumber;
    }

    public void setSecondsNumber(String secondsNumber) {
        this.secondsNumber = secondsNumber;
    }
}
