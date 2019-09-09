package com.dabangvr.model;

public class VedioBean {
    private String urlVedio;//全屏视频地址
    private String urlHead;//用户头像地址
    private String vDz;//点赞数量
    private String vShaer;//转发数量
    private String vComment;//评论数量
    private String vedioTitle;//视频标题
    public VedioBean() {
    }

    public String getUrlVedio() {
        return urlVedio;
    }

    public String getVedioTitle() {
        return vedioTitle;
    }

    public void setVedioTitle(String vedioTitle) {
        this.vedioTitle = vedioTitle;
    }

    public void setUrlVedio(String urlVedio) {
        this.urlVedio = urlVedio;
    }

    public String getUrlHead() {
        return urlHead;
    }

    public void setUrlHead(String urlHead) {
        this.urlHead = urlHead;
    }

    public String getvDz() {
        return vDz;
    }

    public void setvDz(String vDz) {
        this.vDz = vDz;
    }

    public String getvShaer() {
        return vShaer;
    }

    public void setvShaer(String vShaer) {
        this.vShaer = vShaer;
    }

    public String getvComment() {
        return vComment;
    }

    public void setvComment(String vComment) {
        this.vComment = vComment;
    }
}
