package com.dabangvr.model;

/**
 * 商家模型
 */
public class DepMo {
    private String deptId;
    private String name;
    private String productionProvince;
    private String productionCity;
    private String productionCounty;
    private String productionAddress;
    private String logo;
    private String email;

    public String getDeptId() {
        return deptId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
