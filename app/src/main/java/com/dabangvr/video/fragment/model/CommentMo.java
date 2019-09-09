package com.dabangvr.video.fragment.model;

import java.util.List;

public class CommentMo {
    private String id;
    private String headUrl;
    private String nickName;
    private String addTime;
    private String content;
    private String praseCount;//点赞数量
    private List<CommentMo> children;

    private boolean isClick;//是否点赞过

    public CommentMo() {
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPraseCount() {
        return praseCount;
    }

    public void setPraseCount(String praseCount) {
        this.praseCount = praseCount;
    }

    public List<CommentMo> getChildren() {
        return children;
    }

    public void setChildren(List<CommentMo> children) {
        this.children = children;
    }
}
