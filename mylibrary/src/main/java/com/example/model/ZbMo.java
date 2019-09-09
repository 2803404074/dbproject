package com.example.model;

import java.util.List;

/**
 * 申请主播提交的资料
 */
public class ZbMo {
    //必须
    private String name;  //真实姓名
    private String phone; //电话
    private String idcard;//身份证号
    private String idcardFace;//身份证图片地址正
    private String idcardBack;//身份证图片地址反
    private String provinceId;
    private String cityId;
    private String countyId;
    private String address;
    private String agreed_agreement;//主播协议：0不同意 1同意

    //非必须
    private List<String>userImg;//生活照图片地址
    private String email; //邮箱
    private String workEx;//工作经验
    private String hobby;//兴趣爱好
    private String synopsis;//个人介绍

    public ZbMo() {
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCountyId() {
        return countyId;
    }

    public void setCountyId(String countyId) {
        this.countyId = countyId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAgreed_agreement() {
        return agreed_agreement;
    }

    public void setAgreed_agreement(String agreed_agreement) {
        this.agreed_agreement = agreed_agreement;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getIdcardFace() {
        return idcardFace;
    }

    public void setIdcardFace(String idcardFace) {
        this.idcardFace = idcardFace;
    }

    public String getIdcardBack() {
        return idcardBack;
    }

    public void setIdcardBack(String idcardBack) {
        this.idcardBack = idcardBack;
    }

    public List<String> getUserImg() {
        return userImg;
    }

    public void setUserImg(List<String> userImg) {
        this.userImg = userImg;
    }

    public String getWorkEx() {
        return workEx;
    }

    public void setWorkEx(String workEx) {
        this.workEx = workEx;
    }


    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
}
