package com.dabangvr.model;

/**
 * 首页顶部每个主播头像信息
 */
public class HomeTopBean {
    private String zImg;
    private String zName;
    private String zNum;

    public HomeTopBean() {
    }

    public String getzImg() {
        return zImg;
    }

    public void setzImg(String zImg) {
        this.zImg = zImg;
    }

    public String getzName() {
        return zName;
    }

    public void setzName(String zName) {
        this.zName = zName;
    }

    public String getzNum() {
        return zNum;
    }

    public void setzNum(String zNum) {
        this.zNum = zNum;
    }
}
