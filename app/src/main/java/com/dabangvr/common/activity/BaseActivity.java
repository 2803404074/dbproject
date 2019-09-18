package com.dabangvr.common.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
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
import com.dabangvr.my.activity.SbActivity;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.SPUtils2;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.TextUtil;
import com.dabangvr.util.ToastUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import bean.UserMess;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import config.DyUrl;
import okhttp3.Call;

public abstract class BaseActivity extends Activity {
    private Unbinder mUnbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTheme(R.style.activityTheme);
        setContentView(setLayout());
        mUnbinder = ButterKnife.bind(this);

        initView();
        initData();
        initPermissions();
    }
    public String getSPKEY(Activity activity,String key){
        return (String) SPUtils2.instance(activity).getkey(key,"");
    }
    public void setSPKEY(Activity activity,String key,String values){
        SPUtils2.instance(activity).put(key,values);
    }
    public static void removeSPKEY(Activity activity,String key){
        SPUtils2.instance(activity).remove(key);
    }

    // 设置布局
    public abstract int setLayout();

    // 初始化组件
    protected abstract void initView();

    // 初始化数据
    protected abstract void initData();

    public <T extends View> T bindView(int id){
        return (T)findViewById(id);
    }

    public static void setColor(Activity activity, int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 生成一个状态栏大小的矩形
            View StatusView = createStatusView(activity,color);
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
     * @param activity 需要设置的activity
     * @param color 状态栏的颜色值
     * @return 状态栏矩形条
     */
    private static View createStatusView(Activity activity, int color) {
        // 获得状态栏的高度
        int resourceId = activity.getResources()
                .getIdentifier("status_bar_height","dimen","android");
        int statusBarHeight = activity.getResources()
                .getDimensionPixelSize(resourceId);

        // 绘制一个和状态栏一样高度的矩形
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);

        return statusView;
    }





    /***********************我是分割线***************************/

    /**
     * 使状态栏透明
     * 适用于图片作为背景的界面，此时需要图片填充到状态栏
     * @param activity 需要设置的activity
     */
    public static void setTranslucent(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup)((ViewGroup)activity
                    .findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    private void initPermissions() {
        //申请存储权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //没有权限则申请权限
                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        //申请相机权限
        if (ContextCompat.checkSelfPermission(BaseActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) BaseActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    2);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttp3Utils.desInstance();
        mUnbinder.unbind();
    }
}