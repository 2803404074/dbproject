package com.dabangvr.model;

public class ZhiboMo {
    private String id;
    private String rtmpPlayURL;//播放地址1
    private String hlsPlayURL;//播放地址2
    private String hdlPlayURL;//播放地址3
    private String snapshotPlayURL;//封面地址
    private String title;//标题
    private String head;//主播头像
    private String name;//主播名字
    private String fanseNumber;

    public ZhiboMo() {
    }

    public String getFanseNumber() {
        return fanseNumber;
    }

    public void setFanseNumber(String fanseNumber) {
        this.fanseNumber = fanseNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSnapshotPlayURL() {
        return snapshotPlayURL;
    }

    public void setSnapshotPlayURL(String snapshotPlayURL) {
        this.snapshotPlayURL = snapshotPlayURL;
    }
}
