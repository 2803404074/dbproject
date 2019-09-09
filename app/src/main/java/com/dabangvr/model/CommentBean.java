package com.dabangvr.model;

/**
 * 评论实体类
 */
public class CommentBean {
    private String id;//评论id

    private String userId;//用户id

    private String goodsId;//商品id

    private String nickName;//评论人

    private String headUrl;//评论人头像

    private String commentContent;//评论内容

    private String commentData;//评论时间

    private String commentDZ;//点赞量

    private String commentRecall;//回复量

    public CommentBean() {
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentDZ() {
        return commentDZ;
    }

    public void setCommentDZ(String commentDZ) {
        this.commentDZ = commentDZ;
    }

    public String getCommentRecall() {
        return commentRecall;
    }

    public void setCommentRecall(String commentRecall) {
        this.commentRecall = commentRecall;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }


    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentData() {
        return commentData;
    }

    public void setCommentData(String commentData) {
        this.commentData = commentData;
    }
}
