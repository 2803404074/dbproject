package com.dabangvr.main;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.dabangvr.common.network.NetWorkStateReceiver;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.mob.MobSDK;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.Iterator;
import java.util.List;

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

        //微信
        api = WXAPIFactory.createWXAPI(this,APP_ID,true);
        api.registerApp(APP_ID);

        //极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        // 初始化环信sdk
        init(this);


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

    public void init(Context context) {
        // 第一步
        EMOptions options = initChatOptions();
        // 第二步
        boolean success = initSDK(context, options);
        if (success) {
            // 设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
            EMClient.getInstance().setDebugMode(true);

            // 初始化数据库
            //initDbDao(context);
        }
    }

    private EMOptions initChatOptions() {
        // 获取到EMChatOptions对象
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 设置是否需要已读回执
        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(false);
        return options;
    }
    private boolean sdkInited = false;

    public synchronized boolean initSDK(Context context, EMOptions options) {
        if (sdkInited) {
            return true;
        }

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);

        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process
        // name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(getPackageName())) {

            // 则此application::onCreate 是被service 调用的，直接返回
            return false;
        }
        if (options == null) {
            EMClient.getInstance().init(context, initChatOptions());
        } else {
            EMClient.getInstance().init(context, options);
        }
        sdkInited = true;
        return true;
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {

                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
    }
}