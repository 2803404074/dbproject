package com.dabangvr.wxapi;

import android.widget.Switch;

import com.dabangvr.home.activity.OrderActivity;

/**
 * 购买商品类支付类型
 * 1):从订单页面支付
 * 2):从订单详情页支付
 * 3):从订单列表支付
 * <p>
 * 充值金币（礼物）
 * 4):从鲜币中心充值
 * 5):从观看直播的页面充值
 * <p>
 * 共 5 种支付
 * 这里采用SP存储 区分
 */
public class PayType {
    public final static String PAY_KEY = "payType";
    public final static int ORDER_PAGE = 100;//从订单页面支付
    public final static int ORDER_PAGE_DETAILS = 200;//从订单详情页支付
    public final static int ORDER_PAGE_LIST = 300;//从订单列表支付
    public final static int GOLD_CORE = 400;//从鲜币中心充值
    public final static int GOLD_PAGE = 500;//从观看直播的页面充值
}
