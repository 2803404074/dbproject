package com.dabangvr.model;

/**
 * 规格实体类
 */
public class Specifications {
    private String id;//规格id
    private String value;//规格名称
    private String specId;//
    public String getId() {
        return id;
    }

    public String getSpecId() {
        return specId;
    }

    public void setSpecId(String specId) {
        this.specId = specId;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
