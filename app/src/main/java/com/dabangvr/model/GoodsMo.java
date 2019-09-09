package com.dabangvr.model;

import org.json.JSONArray;

import java.util.List;

/**
 * 商品详情信息
 */
public class GoodsMo {

    private String id;
    private String depId;
    private String name;
    private String title;
    private String describe;
    private String sellingPrice;
    private String marketPrice;//市场价
    private JSONArray imgList;//商品资源
    private String goodsDesc;//详细资料图片
    private String groupPrice;//团购价
    private String commentListSize;
    private List<CommentBean> commentList;//评论列表

    public GoodsMo() {
    }

    public String getCommentListSize() {
        return commentListSize;
    }

    public void setCommentListSize(String commentListSize) {
        this.commentListSize = commentListSize;
    }

    public String getGroupPrice() {
        return groupPrice;
    }

    public void setGroupPrice(String groupPrice) {
        this.groupPrice = groupPrice;
    }

    public String getDepId() {
        return depId;
    }

    public List<CommentBean> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentBean> commentList) {
        this.commentList = commentList;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public void setDepId(String depId) {
        this.depId = depId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    public JSONArray getImgList() {
        return imgList;
    }

    public void setImgList(JSONArray imgList) {
        this.imgList = imgList;
    }
}
