package com.dabangvr.model;

import com.example.model.CommentListMo;

import java.util.List;

import main.CommentMo;

public class DynamicMo {
    private String id;
    private String content;
    private String nickName;
    private String headUrl;
    private String sendTime;
    private List<String> picUrl;//说说图片
    private String anchorId;
    private String userId;
    private String praisedNumber;//点赞量
    private String commentNumber;//评论量
    private List<DynamicGoodsMo> goodsVoList;

    private List<CommentListMo> commentVoList;
    private String greatTag;//1是已 -1是未
    private String followTag;//1是已 -1是未

    public DynamicMo() {
    }

    public List<CommentListMo> getCommentVoList() {
        return commentVoList;
    }

    public void setCommentVoList(List<CommentListMo> commentVoList) {
        this.commentVoList = commentVoList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGreatTag() {
        return greatTag;
    }

    public void setGreatTag(String greatTag) {
        this.greatTag = greatTag;
    }

    public String getFollowTag() {
        return followTag;
    }

    public void setFollowTag(String followTag) {
        this.followTag = followTag;
    }

    public String getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(String commentNumber) {
        this.commentNumber = commentNumber;
    }

    public String getPraisedNumber() {
        return praisedNumber;
    }

    public void setPraisedNumber(String praisedNumber) {
        this.praisedNumber = praisedNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
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

    public List<String> getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(List<String> picUrl) {
        this.picUrl = picUrl;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public List<DynamicGoodsMo> getGoodsVoList() {
        return goodsVoList;
    }

    public void setGoodsVoList(List<DynamicGoodsMo> goodsVoList) {
        this.goodsVoList = goodsVoList;
    }
}
