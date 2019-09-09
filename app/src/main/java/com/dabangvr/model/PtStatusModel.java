package com.dabangvr.model;

/**
 * 拼团状态模型
 */
public class PtStatusModel {
    private String id;
    private String title;

    public PtStatusModel() {
    }

    public PtStatusModel(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
