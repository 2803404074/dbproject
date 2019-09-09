package com.example.model;

import java.io.File;
import java.util.List;

/**
 * 商家入驻提交信息
 */
public class BusinessMo {
    private String name;//入驻名称
    private String userName;//法人姓名
    private String phone;//联系电话
    private String idcard;//身份证号
    private String productionProvince;//省
    private String productionCity;//市
    private String productionCounty;//县
    private String productionAddress;//详细地址
    private String lon;//经度
    private String lat;//纬度
    private String foodType;//店铺分类id
    private File idcartFacial;//身份证正面
    private File idcartBehind;//身份证反面
    private List<File> threeCertificates;//营业执照/餐饮许可
    private String agreedAgreement;//是否同意
    private String email;//
    private List<File> storeImgs;//门店照
    private String synopsis;//入驻商简介


    public File getIdcartFacial() {
        return idcartFacial;
    }

    public void setIdcartFacial(File idcartFacial) {
        this.idcartFacial = idcartFacial;
    }

    public File getIdcartBehind() {
        return idcartBehind;
    }

    public void setIdcartBehind(File idcartBehind) {
        this.idcartBehind = idcartBehind;
    }

    public List<File> getThreeCertificates() {
        return threeCertificates;
    }

    public void setThreeCertificates(List<File> threeCertificates) {
        this.threeCertificates = threeCertificates;
    }

    public List<File> getStoreImgs() {
        return storeImgs;
    }

    public void setStoreImgs(List<File> storeImgs) {
        this.storeImgs = storeImgs;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getProductionProvince() {
        return productionProvince;
    }

    public void setProductionProvince(String productionProvince) {
        this.productionProvince = productionProvince;
    }

    public String getProductionCity() {
        return productionCity;
    }

    public void setProductionCity(String productionCity) {
        this.productionCity = productionCity;
    }

    public String getProductionCounty() {
        return productionCounty;
    }

    public void setProductionCounty(String productionCounty) {
        this.productionCounty = productionCounty;
    }

    public String getProductionAddress() {
        return productionAddress;
    }

    public void setProductionAddress(String productionAddress) {
        this.productionAddress = productionAddress;
    }


    public String getAgreedAgreement() {
        return agreedAgreement;
    }

    public void setAgreedAgreement(String agreedAgreement) {
        this.agreedAgreement = agreedAgreement;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
}
