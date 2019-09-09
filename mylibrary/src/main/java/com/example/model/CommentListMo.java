package com.example.model;

/**
 * 评论列表
 */
public class CommentListMo {
    private String id;
    private String nickName;//评论人
    private String content;//评论内容


    public CommentListMo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
