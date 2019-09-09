package com.dabangvr.model;

import java.util.List;

/**
 * 直播购物每个item实体类
 */
public class GwBean {
    private String tag;
    private String coverUrl;//历史直播的封面图
    private String rtmpPlayURL;
    private String hlsPlayURL;
    private String hdlPlayURL;
    private String liveTitle;
    private String headUrl;
    private String snapshotPlayURL;
    private String anchorId;
    private String userId;
    private List<ZBGoods> liveGoodsList;
    private String anchorName;
    private String fans;
    private String lookCount;//观看数量

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public GwBean() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getLookCount() {
        return lookCount;
    }

    public void setLookCount(String lookCount) {
        this.lookCount = lookCount;
    }

    public String getRtmpPlayURL() {
        return rtmpPlayURL;
    }

    public void setRtmpPlayURL(String rtmpPlayURL) {
        this.rtmpPlayURL = rtmpPlayURL;
    }

    public String getHlsPlayURL() {
        return hlsPlayURL;
    }

    public void setHlsPlayURL(String hlsPlayURL) {
        this.hlsPlayURL = hlsPlayURL;
    }

    public String getHdlPlayURL() {
        return hdlPlayURL;
    }

    public void setHdlPlayURL(String hdlPlayURL) {
        this.hdlPlayURL = hdlPlayURL;
    }

    public String getLiveTitle() {
        return liveTitle;
    }

    public void setLiveTitle(String liveTitle) {
        this.liveTitle = liveTitle;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getSnapshotPlayURL() {
        return snapshotPlayURL;
    }

    public void setSnapshotPlayURL(String snapshotPlayURL) {
        this.snapshotPlayURL = snapshotPlayURL;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public List<ZBGoods> getLiveGoodsList() {
        return liveGoodsList;
    }

    public void setLiveGoodsList(List<ZBGoods> liveGoodsList) {
        this.liveGoodsList = liveGoodsList;
    }

    public String getAnchorName() {
        return anchorName;
    }

    public void setAnchorName(String anchorName) {
        this.anchorName = anchorName;
    }

    public String getFans() {
        return fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }
}
