package com.dabangvr.model;

import java.util.List;

import me.yokeyword.indexablerv.IndexableEntity;

public class CityBean {
    private String name;
    private double log;
    private double lat;
    private List<ChildrenCity> children;

    public CityBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLog() {
        return log;
    }

    public void setLog(double log) {
        this.log = log;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public List<ChildrenCity> getChildren() {
        return children;
    }

    public void setChildren(List<ChildrenCity> children) {
        this.children = children;
    }


}
