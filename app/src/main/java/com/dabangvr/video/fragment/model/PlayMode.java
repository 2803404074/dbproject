package com.dabangvr.video.fragment.model;

public class PlayMode {
    private String id;
    private String userId;
    private String title;
    private String videoUrl;
    private String videoSeconds;
    private String coverPath;
    private String likeCounts;
    private String praseCount;//点赞量
    private String commentCount;//评论量
    private String headUrl;//
    private String nickName;
    private String addTime;//
    private boolean isDz;


    public PlayMode() {
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public PlayMode(String title, String url) {
        this.title = title;
        this.videoUrl = url;
    }

    public String getPraseCount() {
        return praseCount;
    }

    public void setPraseCount(String praseCount) {
        this.praseCount = praseCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
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

    public boolean isDz() {
        return isDz;
    }

    public void setDz(boolean dz) {
        isDz = dz;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoSeconds() {
        return videoSeconds;
    }

    public void setVideoSeconds(String videoSeconds) {
        this.videoSeconds = videoSeconds;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getLikeCounts() {
        return likeCounts;
    }

    public void setLikeCounts(String likeCounts) {
        this.likeCounts = likeCounts;
    }
}
