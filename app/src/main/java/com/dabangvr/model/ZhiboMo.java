package com.dabangvr.model;

import java.util.List;

public class ZhiboMo {

    /**
     * errno : 0
     * data : [{"id":124,"userId":209,"anchorId":60,"streamName":"recreation_209","liveTitle":"ggggg","coverUrl":"live-covera7db87d4-3fcb-46d6-9d95-adce516b955a","state":1,"addTime":1569395638000,"headUrl":"http://qzapp.qlogo.cn/qzapp/1109644507/44EA28650286AB1537AD5D86B357B7E1/100","nickName":"音乐队长－阿木木","liveType":"recreation"},{"id":93,"userId":205,"anchorId":63,"streamName":"shopping_205","start":"1565427840","end":"1565427866","fname":"\r\nhttp://161.0.157.9/PLTV/88888888/224/3221226800/03.m3u8","liveTitle":"啦啦啦啦","coverUrl":"http://image.vrzbgw.com/upload/20190810/170355908f5794.gif","goodsIds":"265","state":1,"addTime":1565427838000,"headUrl":"http://thirdwx.qlogo.cn/mmopen/vi_32/WyBFlUaDAQ1ud2tpXVqmYp2vuTBky8MMEIbOibjZThW5zKPIqJTIWwyDZ61VDJXEHFJEeaRIZ7SLXuSMOicibrYJQ/132","nickName":"莫爱前朝公主，只恋今世红颜","liveType":"shopping"},{"id":92,"userId":209,"anchorId":63,"streamName":"shopping_205","start":"1565295814","end":"1565295891","fname":"http://pili-clickplay.vrzbgw.com/recordings/z1.db-live-broadcast.shopping_205/1565295814_1565295891.m3u8","liveTitle":"哟哟哟","coverUrl":"http://image.vrzbgw.com/upload/20190809/04232579483509.jpg","goodsIds":"265","state":1,"addTime":1565295812000,"headUrl":"http://qzapp.qlogo.cn/qzapp/1109644507/44EA28650286AB1537AD5D86B357B7E1/100","nickName":"音乐队长－阿木木","liveType":"shopping"},{"id":91,"userId":174,"anchorId":57,"streamName":"shopping_174","start":"1565174362","end":"1565174936","fname":"http://pili-clickplay.vrzbgw.com/recordings/z1.db-live-broadcast.shopping_174/1565174362_1565174936.m3u8","liveTitle":"哈哈哈","coverUrl":"http://image.vrzbgw.com/upload/20190807/18365854093576.jpg","goodsIds":"397,396,398","state":1,"addTime":1565174223000,"headUrl":"http://thirdwx.qlogo.cn/mmopen/vi_32/eiaB3BceHABQ39VuKzhHYOaTfIWnNjKrCib5ldFMRAicicEticId0p6mWb9S8MftFHmwWPb5Ek6MI5M5hZblFs1pQicg/132","nickName":":-）","liveType":"shopping"}]
     * errmsg : 执行成功
     */


    /**
     * id : 124
     * userId : 209
     * anchorId : 60
     * streamName : recreation_209
     * liveTitle : ggggg
     * coverUrl : live-covera7db87d4-3fcb-46d6-9d95-adce516b955a
     * state : 1
     * addTime : 1569395638000
     * headUrl : http://qzapp.qlogo.cn/qzapp/1109644507/44EA28650286AB1537AD5D86B357B7E1/100
     * nickName : 音乐队长－阿木木
     * liveType : recreation
     * start : 1565427840
     * end : 1565427866
     * fname :
     * http://161.0.157.9/PLTV/88888888/224/3221226800/03.m3u8
     * goodsIds : 265
     */

    private int id;
    private int userId;
    private int anchorId;
    private String streamName;
    private String liveTitle;
    private String coverUrl;
    private int state;
    private long addTime;
    private String headUrl;
    private String nickName;
    private String liveType;
    private String start;
    private String end;
    private String fname;
    private String goodsIds;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(int anchorId) {
        this.anchorId = anchorId;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public String getLiveTitle() {
        return liveTitle;
    }

    public void setLiveTitle(String liveTitle) {
        this.liveTitle = liveTitle;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
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

    public String getLiveType() {
        return liveType;
    }

    public void setLiveType(String liveType) {
        this.liveType = liveType;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(String goodsIds) {
        this.goodsIds = goodsIds;
    }

}
