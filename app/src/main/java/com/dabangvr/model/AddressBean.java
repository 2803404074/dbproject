package com.dabangvr.model;

import java.io.Serializable;

public class AddressBean implements Serializable {
    private String id;
    private String receivingCountry;//国家
    private String province;//省代码
    private String city;//城市代码
    private String area;//县区代码

    private String provinceOne;//省名称
    private String cityOne;//城市名称
    private String areaOne;//县区名称


    private String address;//详细地址
    private String zipCode;//邮编
    private String consigneeName;//收货人
    private String consigneePhone;//电话
    private int isDefault;//默认 1默认,0非默认
    private boolean isCheck;

    public AddressBean() {
    }

    public String getProvinceOne() {
        return provinceOne;
    }

    public void setProvinceOne(String provinceOne) {
        this.provinceOne = provinceOne;
    }

    public String getCityOne() {
        return cityOne;
    }

    public void setCityOne(String cityOne) {
        this.cityOne = cityOne;
    }

    public String getAreaOne() {
        return areaOne;
    }

    public void setAreaOne(String areaOne) {
        this.areaOne = areaOne;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReceivingCountry() {
        return receivingCountry;
    }

    public void setReceivingCountry(String receivingCountry) {
        this.receivingCountry = receivingCountry;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
