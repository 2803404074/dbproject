package com.dabangvr.main;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.dabangvr.common.network.NetWorkStateReceiver;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.mob.MobSDK;
import com.qiniu.pili.droid.streaming.StreamingEnv;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application {
    private static Context mContext;
    public static String APP_ID="wx2351c48134140a3c";
    public static IWXAPI api;

    NetWorkStateReceiver netWorkStateReceiver;
    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        MobSDK.init(this);//分享、短信功能初始化

        Fresco.initialize(this);//第三方加载图片

        //七牛云推流端
        StreamingEnv.init(getApplicationContext());

        //微信
        api = WXAPIFactory.createWXAPI(this,APP_ID,true);
        api.registerApp(APP_ID);

        //极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        //网络变化
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
//        System.out.println("注册");



    }

    public static Context getInstance() {
        return mContext;
    }
}