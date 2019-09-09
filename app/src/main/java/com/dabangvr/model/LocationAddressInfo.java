package com.dabangvr.model;

public class LocationAddressInfo {
    private String lon;
    private String lat;
    private String title;
    private String text;

    private String province;
    private String city;
    private String county;

    public LocationAddressInfo() {
    }

    public LocationAddressInfo(String lon, String lat, String title, String text) {
        this.lon = lon;
        this.lat = lat;
        this.title = title;
        this.text = text;
    }

    public LocationAddressInfo(String lon, String lat, String title, String text, String province, String city, String county) {
        this.lon = lon;
        this.lat = lat;
        this.title = title;
        this.text = text;
        this.province = province;
        this.city = city;
        this.county = county;
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

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
