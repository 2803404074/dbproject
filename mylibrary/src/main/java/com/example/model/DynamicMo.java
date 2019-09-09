package com.example.model;

import java.util.List;

/**
 * 动态bean
 */
public class DynamicMo {
    private String id;
    private String authName;
    private String authHead;
    private String data;
    private String content;
    private List<String> contentImg;
    private String dzNum;
    private String commentNum;
    private String shareNum;
    private List<CommentListMo> commentList;

    public DynamicMo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    public String getAuthHead() {
        return authHead;
    }

    public void setAuthHead(String authHead) {
        this.authHead = authHead;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getContentImg() {
        return contentImg;
    }

    public void setContentImg(List<String> contentImg) {
        this.contentImg = contentImg;
    }

    public String getDzNum() {
        return dzNum;
    }

    public void setDzNum(String dzNum) {
        this.dzNum = dzNum;
    }

    public String getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    public String getShareNum() {
        return shareNum;
    }

    public void setShareNum(String shareNum) {
        this.shareNum = shareNum;
    }

    public List<CommentListMo> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentListMo> commentList) {
        this.commentList = commentList;
    }
}
