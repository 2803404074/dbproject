package config;

public class DyUrl {

    public static String TOKEN_NAME = "DABANG-TOKEN";
    //根地址http://www.vrzbgw.com/dabang

    //public static String BASE = "";
    public static String BASE = "http://www.vrzbgw.com/dabang";//远程
//    public static String BASE = "http://admin.vrzbgw.com:8080";//远程
//    public static String BASE = "http://192.168.200.111:8080";//
//    public static String BASE = "http://192.168.200.103:8080";//
    //public static String BASE = "http://192.168.1.103:8080";//强
     //public static String BASE = "http://192.168.0.108:8080";//邓
    //public static String BASE = "http://192.168.1.110:8080";//桃

    //public static String BASE = "http://www.vrzbgw.com/dabang";

    //--------------------------资源图片视频----------------

    //app图标
    public static String APP_LOGO = "http://image.vrzbgw.com/upload/20190522/174149610e4144.png";

    //登录背景视频
    public static String APP_LOGIN_VIDEO = "http://pili-clickplay.vrzbgw.com/login_video.mp4";

    //秒杀顶部图片
    public static String MS_IMG = "http://pili-clickplay.vrzbgw.com/ms_img.jpg";

    //门店顶部图片
    public static String MD_TOP_IMG = "http://pili-clickplay.vrzbgw.com/dep_top_img.jfif";

    //---------------------------------------------


    //首页-渠道列表
    public static String getChannelMenuList = "/api/index/getChannelMenuList";

    //首页-分类列表
    public static String getGoodsCategoryList = "/api/index/getGoodsCategoryList";

    //获取分类列表（含二级分类）
    public static String getIndexCategoryList = "/api/index/getIndexCategoryList";

    //----------------------------------首页---------------------------------------

    //搜索
    public static String getSearchGoodsList = "/api/index/getSearchGoodsList";

    //所有商品列表
    public static String getGoodsLists = "/api/goods/getGoodsLists";


    //首页-海鲜-类型列表
    public static String CategoryList = "/api/index/getGoodsCategoryList";

    //首页-海鲜-商品列表
    public static String getGoodsList = "/api/goods/getGoodsList";

    //首页-海鲜-商品详情
    public static String getGoodsDetails = "/api/goods/getGoodsDetails";

    //首页轮播图列表
    public static String ROTA = "/api/index/getGoodsRotationList";

    //新品首发列表
    //public static String NEWS = "/api/goods/getNewGoodsList";
    public static String NEWS = "/api/goods/newGoodsList";

    //全球购商品列表
    public static String getGlobalList = "/api/goods/getGlobalList";

    //评论列表
    public static String getCommentListTwo = "/api/goods/getCommentListTwo";

    //添加订单评论
    public static String getCommentSave = "/api/goods/getCommentSave";

    //--------------------------------拼团,秒杀---------------------------------
    //获取拼团商品列表
    public static String getGroupGoodsList = "/api/goods/getGroupGoodsList";

    //秒杀列表
    public static String getSecondsKillGoodsList = "/api/goods/getSecondsKillGoodsList";

    //----------------------------订单----------------------------------
    //确认订单
    public static String confirmGoods = "/api/buygoods/confirmGoods2Buy";

    //秒杀确认订单
    public static String confirmGoods2seconds = "/api/buygoods/confirmGoods2seconds";

    //拼团确认订单
    public static String confirmGoods2groupbuy = "/api/buygoods/confirmGoods2groupbuy";

    //获取确认订单
    public static String getConfirmGoods = "/api/buygoods/getConfirmGoods";

    //我的订单列表
    public static String getOrderList = "/api/order/getOrderList";

    //订单详情
    public static String getOrderDetails = "/api/order/getOrderDetails";

    //购物车获取确认订单
    public static String confirmGoods2Cart = "/api/buygoods/confirmGoods2Cart";

    //提交订单
    public static String submitOrder = "/api/buygoods/submitOrder";

    //重新支付
    public static String prepayOrderAgain = "/api/payorder/prepayOrderAgain";

    //微信支付统一入口，获取订单号
    public static String prepayOrder = "/api/payorder/prepayOrder";

    //支付成功/失败后将状态通知服务器的接口
    public static String notifyApp = "/api/payorder/notifyApp";


    //重新支付
    public static String prepayDeliveryOrderAgain = "/api/delivery/prepayDeliveryOrderAgain";


    //微信退款
    public static String refundRequest = "/api/payorder/refundRequest";

    //收藏商品
    public static String getGoodsCollectSave = "/api/my/getGoodsCollectSave";

    //商品收藏列表
    public static String getGoodsCollectList = "/api/my/getGoodsCollectList";

    //加入购物车
    public static String addToCart = "/api/buygoods/addToCart";

    //删除购物车
    public static String delete2Cart = "/api/buygoods/delete2Cart";

    //购物车列表
    public static String getGoods2CartList = "/api/goods/getGoods2CartList";

    //修改购物车数量
    public static String updateNumber2Cart = "/api/buygoods/updateNumber2Cart";

    //appid=e85fa22a3c63fdd916380b63526053f4&city=%E5%8C%97%E4%BA%AC
    //神箭手，搜索城市返回是否正确或存在
    public static String getCity = "https://search.heweather.net/find?parameters";

    //获取七牛云对象存储token
    public static String getQiNiuToken = "/api/config/getUploadConfigToken";





    //----------------------用户信息---------------------
    //登陆
    public static String LOGIN="/api/auth/login";

    //退出登陆
    public static String LOGOUT = "/api/auth/logout";

    //获取用户信息
    public static String USER_INFO="/api/auth/getUserInfo";
    //public static String USER_INFO="/api/auth/getUserInfoByToken";

    //根据国家获取省份
    public static String proviceList = "/api/my/proviceList";

    //根据省份id，获取城市列表
    public static String cityList = "/api/my/cityList";

    //添加收货地址
    public static String AddressAdd = "/api/my/addressAdd";

    //查询收货地址列表
    public static String addressList = "/api/my/addressList";

    //修改地址
    public static String addressUpdate = "/api/my/addressUpdate";

    //删除地址
    public static String addressDelete = "/api/my/addressDelete";

    //修改用户信息
    public static String update = "/api/auth/update";

    //获取商家列表
    public static String getNearbyDeptList = "/api/delivery/getNearbyDeptList";





    //------------------------社区----------------------------
    //发说说
    public static String say = "/api/my/say";

    //获取说说列表
    public static String getSayList = "/api/my/getSayList";

    //----------------------------认证-------------------------

    //商家入驻
    public static String addDept = "/api/delivery/addDept";

    //申请主播
    public static String appAnchor = "/api/delivery/appAnchor";

    //用户申请列表
    public static String deptOrAnchor = "/api/my/deptOrAnchor";

    //获取店铺类型
    public static String getFoodType = "/api/delivery/getFoodType";


    //---------------------------门店----------------------
    public static String searchDeptList = "/api/delivery/searchDeptList";

    //外卖商品列表
    public static String getDeptGoodsList = "/api/delivery/getDeptGoodsList";

    //外卖商品详情
    public static String getDeliveryGoodsDetails = "/api/delivery/getDeliveryGoodsDetails";

    //外卖确认订单
    public static String confirmGoods2Delivery = "/api/delivery/confirmGoods2Delivery";

    //获取订单信息
    public static String getConfirmGoods2Delivery = "/api/delivery/getConfirmGoods2Delivery";

    //外卖提交订单接口
    public static String submitGoods2Delivery = "/api/delivery/submitGoods2Delivery";

    //外卖订单列表
    public static String getDeliveryOrderList = "/api/delivery/getDeliveryOrderList";

    //外卖订单详情
    public static String getDeliveryOrderDetails = "/api/delivery/getDeliveryOrderDetails";

    //修改订单状态
    public static String updateOrderState = "/api/order/updateOrderState";

    //---------------------------直播------------------------

    //获取推流地址
    public static String createStream = "/api/pili/create";

    //获取可直播的商品列表
    public static String getLiveGoodgsList = "/api/pili/getLiveGoodgsList";

    //获取主播所推的商品
    public static String getAnchorLiveGoodsList = "/api/pili/getAnchorLiveGoodsList";

    //关注主播
    public static String updateFans = "/api/my/updateFans";

    //主播信息
    public static String getAnchor = "/api/my/getAnchor";
}
