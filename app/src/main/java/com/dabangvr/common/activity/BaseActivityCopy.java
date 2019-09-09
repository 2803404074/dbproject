package com.dabangvr.common.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.main.MainActivity;
import com.dabangvr.my.activity.ApplyAnchorActivity;
import com.dabangvr.my.activity.LoginActivity;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.ToastUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import bean.UserMess;
import config.DyUrl;
import okhttp3.Call;

public abstract class BaseActivityCopy extends AppCompatActivity {

    private static SPUtils spUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //沉浸式代码配置
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);

        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.white));
        }

          initView();
//        initData();
//        initPermissions();
    }

    public static String getSPKEY(Activity activity, String key) {
        if (null == spUtils) {
            spUtils = new SPUtils(activity, "db_user");
        }
        return (String) spUtils.getkey(key, "");
    }

    public static void setSPKEY(Activity activity, String key, String values) {
        if (null == spUtils) {
            spUtils = new SPUtils(activity, "db_user");
        }
        spUtils.put(key, values);
    }

    public static void removeSPKEY(Activity activity, String key) {
        if (null == spUtils) {
            spUtils = new SPUtils(activity, "db_user");
        }
        spUtils.remove(key);
    }

    // 设置布局
    public abstract int setLayout();

    public void setLayoutLine(Activity activity,int layout) {
        if (haveLin(activity)){
            setContentView(layout);
            //return layout;
        }else {
            setContentView(R.layout.noline);
            //return R.layout.noline;
        }
    }

    // 初始化组件
    protected abstract void initView();

    // 初始化数据
    protected abstract void initData();

    public <T extends View> T bindView(int id) {
        return (T) findViewById(id);
    }

    public static void setColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 生成一个状态栏大小的矩形
            View StatusView = createStatusView(activity, color);
            // 添加statusView到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(StatusView);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content))
                    .getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    /**
     * 生成一个和状态栏大小相同的矩形条
     *
     * @param activity 需要设置的activity
     * @param color    状态栏的颜色值
     * @return 状态栏矩形条
     */
    private static View createStatusView(Activity activity, int color) {
        // 获得状态栏的高度
        int resourceId = activity.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources()
                .getDimensionPixelSize(resourceId);

        // 绘制一个和状态栏一样高度的矩形
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);

        return statusView;
    }


    public static void show(final Activity activity, final int index, String mess, String btnStr) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle("海风暴")
                .setMessage(mess)
                .setIcon(R.mipmap.application)
                .setPositiveButton(btnStr, new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (index == 0) {
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                        }
                        if (index == 1) {
                            Intent intent = new Intent(activity, ApplyAnchorActivity.class);
                            activity.startActivity(intent);
                        }
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {//添加普通按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
        alertDialog.show();
    }

    /***********************我是分割线***************************/

    /**
     * 使状态栏透明
     * 适用于图片作为背景的界面，此时需要图片填充到状态栏
     *
     * @param activity 需要设置的activity
     */
    public static void setTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity
                    .findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    private void initPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
//            }
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
//            }
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
//            }
        }

//        if (Build.VERSION.SDK_INT >= 23) {
//            String[] permissions = {
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.READ_PHONE_STATE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//            };
//            if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_DENIED) {
//                requestPermissions(permissions, 0);
//            }
//        }

        //申请存储权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(BaseActivityCopy.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //没有权限则申请权限
                ActivityCompat.requestPermissions(BaseActivityCopy.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        //申请相机权限
        if (ContextCompat.checkSelfPermission(BaseActivityCopy.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) BaseActivityCopy.this,
                    new String[]{Manifest.permission.CAMERA},
                    2);
        }
    }


    public static UserMess getUserInfo(final Activity activity) {
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(activity, "token"));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.USER_INFO, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (errno == 400) {
                        removeSPKEY(activity, "token");
                        removeSPKEY(activity, "user");
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                        ToastUtil.showShort(activity, "登录已失效，请重新登录");
                        activity.finish();
                        return;
                    }
                    if (errno == 0) {
                        if (500 == object.optInt("code")) {
                            removeSPKEY(activity, "token");
                            removeSPKEY(activity, "user");
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                            ToastUtil.showShort(activity, "登录已失效，请重新登录");
                            activity.finish();
                            return;
                        }
                        String data = object.optString("data");
                        if (StringUtils.isEmpty(data)) {
                            removeSPKEY(activity, "token");
                            removeSPKEY(activity, "user");
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                            ToastUtil.showShort(activity, "登录已失效，请重新登录");
                            activity.finish();
                            return;
                        }
                        setSPKEY(activity, "user", data);

                        UserMess userMess = JsonUtil.string2Obj(data, UserMess.class);
                        if (null != userMess) {
                            if (1 == userMess.getIsAnchor()) {
                                MainActivity.isAnchor = true;
                            }
                            if (null != userMess.getToken()) {
                                setSPKEY(activity, "token", userMess.getToken());
                            }
                        } else {
                            removeSPKEY(activity, "token");
                            removeSPKEY(activity, "user");
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                            ToastUtil.showShort(activity, "登录已失效，请重新登录");
                            activity.finish();
                        }
                    }
                    if (errno == 401) {
                        removeSPKEY(activity, "token");
                        removeSPKEY(activity, "user");
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                        ToastUtil.showShort(activity, "登录已失效，请重新登录");
                        activity.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                Toast.makeText(activity, "网络无法达到哦", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                //Toast.makeText(MainActivity.this, "网络无法达到哦", Toast.LENGTH_LONG).show();
            }
        });
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttp3Utils.desInstance();
        spUtils = null;
    }


    public boolean haveLin(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                Toast.makeText(activity, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show();
                return true;
            } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                Toast.makeText(activity, "WIFI已连接,移动数据已断开", Toast.LENGTH_SHORT).show();
                return true;
            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                Toast.makeText(activity, "WIFI已断开,移动数据已连接", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(activity, "WIFI已断开,移动数据已断开", Toast.LENGTH_SHORT).show();
                return false;
            }
//API大于23时使用下面的方式进行网络监听
        } else {
            ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                int type2 = networkInfo.getType();

                switch (type2) {
                    case 0://移动 网络    2G 3G 4G 都是一样的 实测 mix2s 联通卡
                        Toast.makeText(activity, "当前使用移动网络,注意流量哦", Toast.LENGTH_SHORT).show();
                        return true;
                    case 1: //wifi网络
                        //Toast.makeText(context, "当前使用WIFI网络", Toast.LENGTH_SHORT).show();
                        return true;
                    case 9:  //网线连接
                        Toast.makeText(activity, "网线连接", Toast.LENGTH_SHORT).show();
                        return true;
                }

            } else {// 无网络
                Toast.makeText(activity, "无网络", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }
}