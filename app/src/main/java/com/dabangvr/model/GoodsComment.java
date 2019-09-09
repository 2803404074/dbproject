package com.dabangvr.model;

import java.util.List;

public class GoodsComment {
    private String userId;
    private String commentContent;
    private String commentData;
    private List<String> commentImg;
    private String grade;
    private String nickName;
    private String headUrl;
    private String commentSize;//评论总数


    public GoodsComment() {
    }

    public String getCommentSize() {
        return commentSize;
    }

    public void setCommentSize(String commentSize) {
        this.commentSize = commentSize;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentData() {
        return commentData;
    }

    public void setCommentData(String commentData) {
        this.commentData = commentData;
    }

    public List<String> getCommentImg() {
        return commentImg;
    }

    public void setCommentImg(List<String> commentImg) {
        this.commentImg = commentImg;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }
}
