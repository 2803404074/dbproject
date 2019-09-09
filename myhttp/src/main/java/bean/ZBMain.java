package bean;

import java.util.List;

public class ZBMain {
    private String anchorId;
    private String coverUrl;//封面地址
    private String rtmpPlayURL;//播放地址
    private String tag;//房间标识
    private String liveTitle;//标题
    private String headUrl;//主播头像
    private String anchorName;//主播名
    private String fans;//主播粉丝
    private String fansTag; //是否已经关注
    private int roundurl; //外框地址
    private List<Goods>liveGoodsList;//购物直播商品列表

    public ZBMain() {
    }

    public String getFansTag() {
        return fansTag;
    }

    public void setFansTag(String fansTag) {
        this.fansTag = fansTag;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
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

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getRtmpPlayURL() {
        return rtmpPlayURL;
    }

    public void setRtmpPlayURL(String rtmpPlayURL) {
        this.rtmpPlayURL = rtmpPlayURL;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public List<Goods> getLiveGoodsList() {
        return liveGoodsList;
    }

    public void setLiveGoodsList(List<Goods> liveGoodsList) {
        this.liveGoodsList = liveGoodsList;
    }

    public int getRoundurl() {
        return roundurl;
    }

    public void setRoundurl(int roundurl) {
        this.roundurl = roundurl;
    }
}
