package config;

public class GiftUrl {

    //获取礼物列表
    public static String getLiveGiftList  = "/api/livegift/getLiveGiftList";

    //直播音乐列表
    public static String getMusicList = "/api/pili/getMusicList";


    //充值金币
    public static String investDiamond = "/api/livegift/investDiamond";


    //打赏
    public static String rewardGift = "/api/livegift/rewardGift";


    //短视频上传
    public static String upLoadShortVideo = "/api/liveShortVideo/saveVideo";

    //短视频列表
    public static String getLiveShortVideoList = "/api/liveShortVideo/getLiveShortVideoList";

    //点赞视频
    public static String praseShortVideo = "/api/liveShortVideo/praseShortVideo";

    //评论短视频
    public static String commentShortVideo = "/api/liveShortVideo/commentShortVideo";

    //短视频评论列表
    public static String getShortVideoComment = "/api/liveShortVideo/getShortVideoComment";



    //评论说说
    public static String commentSay = "/api/my/commentSay";

    //点赞说说
    public static String praisedSay = "/api/my/praisedSay";


    //我的粉丝和我关注的用户列表
    public static String myFans = "/api/my/myFans";


    //获取直播列表(0全部，1精选，2关注)
    public static String getFocusLiveRecords = "/api/pili/getFocusLiveRecords";

    //获取某主播直播历史
    public static String getActivityRecords = "/api/pili/getActivityRecords";

}
