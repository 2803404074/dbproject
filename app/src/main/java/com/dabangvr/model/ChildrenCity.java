package com.dabangvr.model;

import me.yokeyword.indexablerv.IndexableEntity;

public class ChildrenCity implements IndexableEntity {
    private String name;
    private double log;
    private double lat;

    public ChildrenCity() {
    }

    public ChildrenCity(String name, double log, double lat) {
        this.name = name;
        this.log = log;
        this.lat = lat;
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

    @Override
    public String getFieldIndexBy() {
        return name;
    }

    @Override
    public void setFieldIndexBy(String indexField) {
        this.name = indexField;
    }

    @Override
    public void setFieldPinyinIndexBy(String pinyin) {

    }
}
