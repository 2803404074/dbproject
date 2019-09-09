package com.dabangvr.model;

public class DynamicCommentMo {
    private String authName;//评论人
    private String mess;//评论信息
    private String data;//时间

    public DynamicCommentMo() {
    }

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
