package com.dabangvr.base.plush;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import com.dabangvr.R;
import com.dabangvr.common.activity.CartActivity;
import com.dabangvr.home.activity.HxxqLastActivity;
import com.dabangvr.main.WellcomActivity;
import com.dabangvr.my.activity.OrderDetailedActivity;
import org.json.JSONException;
import org.json.JSONObject;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.dabangvr.base.plush.SPTAG.goodsType;
import static com.dabangvr.base.plush.SPTAG.orderType;

public class JPushReceiver extends JPushMessageReceiver {
    private static final String TAG = "JIGUANG";

    /**
     * TODO 连接极光服务器
     */
    @Override
    public void onConnected(Context context, boolean b) {
        super.onConnected(context, b);
        Log.e(TAG, "onConnected");
    }

    /**
     * TODO 注册极光时的回调
     */
    @Override
    public void onRegister(Context context, String s) {
        super.onRegister(context, s);
        Log.e(TAG, "onRegister" + s);
    }

    /**
     * TODO 注册以及解除注册别名时回调
     */
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
        Log.e(TAG, jPushMessage.toString());
    }

    /**
     * TODO 接收到推送下来的通知
     *      可以利用附加字段（notificationMessage.notificationExtras）来区别Notication,指定不同的动作,附加字段是个json字符串
     *      通知（Notification），指在手机的通知栏（状态栏）上会显示的一条通知信息
     */
    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);
        Log.e(TAG, "消息进入"+notificationMessage.toString());
        //processCustomMessage(context, notificationMessage.notificationTitle,notificationMessage.notificationContent);
    }

    /**
     * TODO 打开了通知
     *      notificationMessage.notificationExtras(附加字段)的内容处理代码
     *      打开新的Activity， 打开一个网页等..
     */
    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
        try {
            JSONObject object = new JSONObject(notificationMessage.notificationExtras);
            JSONObject object1 = object.optJSONObject("data");

            //消息类型
            int code = object1.optInt("code");

            //跳转id
            String jumpId = object1.optString("jumpId");


            //是否在前台
            //是：直接跳转到相应的activity
            //否：判断应用是否
            if (isAppForeground(context)){
                //直接跳转到相应的activity
                Intent intent = null;
                if (code == goodsType){
                    intent = new Intent(context, HxxqLastActivity.class);
                    intent.putExtra("goodsId",jumpId);
                }else if (code == orderType){
                    intent = new Intent(context, OrderDetailedActivity.class);
                    intent.putExtra("orderId",jumpId);
                }else {
                    //其他页面
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else{
                Intent intent = new Intent(context, WellcomActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * TODO 接收到推送下来的自定义消息
     *      自定义消息不是通知，默认不会被SDK展示到通知栏上，极光推送仅负责透传给SDK。其内容和展示形式完全由开发者自己定义。
     *      自定义消息主要用于应用的内部业务逻辑和特殊展示需求
     *
     *      CustomMessage{messageId='29273426906431070', extra='{"url":"1024"}', message='订单已发货', contentType='', title='订单已发货',senderId='380956f57f80a3cb9b1b1fbe', appId='com.dabangvr'}
     */
    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);
        Log.e(TAG, "消息进入onMessage");
    }



    /**
     * 判断是否在前台
     * @return
     */
    private boolean isAppForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals("com.dabangvr")) {
            return true;
        }
        return false;
    }
}
