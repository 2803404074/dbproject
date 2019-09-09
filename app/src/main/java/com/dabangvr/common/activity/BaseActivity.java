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

public abstract class BaseActivity extends AppCompatActivity {

    private static SPUtils spUtils;
    private static String isAnch; //1是主播
    private static String anchId;

    private static int REQUEST_PERMISSION_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.RECORD_AUDIO};

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(setLayout());
        mUnbinder = ButterKnife.bind(this);


        //沉浸式代码配置
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
//        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
//        //设置状态栏透明
//        StatusBarUtil.setTranslucentStatus(this);
//
//        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
//        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
//        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
//            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
//            //这样半透明+白=灰, 状态栏的文字能看得清
//            StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.white));
//        }

        initView();
        initData();
        initPermissions();


    }

    public static String getSPKEY(Activity activity,String key){
        if(null == spUtils){
            spUtils = new SPUtils(activity,"db_user");
        }
        return (String) spUtils.getkey(key,"");
    }

    public static void setSPKEY(Activity activity,String key,String values){
        if(null == spUtils){
            spUtils = new SPUtils(activity,"db_user");
        }
        spUtils.put(key,values);
    }
    public static void removeSPKEY(Activity activity,String key){
        if(null == spUtils){
            spUtils = new SPUtils(activity,"db_user");
        }
        spUtils.remove(key);
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
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
//            }
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
//            }
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
//            }
//        }
//
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



    public static UserMess getUserInfo (final Activity activity){
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME,getSPKEY(activity,"token"));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.USER_INFO, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (errno == 0) {
                        if (500 == object.optInt("code")) {
                            removeSPKEY(activity,"token");
                            removeSPKEY(activity,"user");
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                            ToastUtil.showShort(activity,"登录失败,请重新登录");
                            activity.finish();
                            return ;
                        }
                        String data = object.optString("data");
                        UserMess userMess = JsonUtil.string2Obj(data, UserMess.class);
                        if (null != userMess){
                            setSPKEY(activity,"user",data);
                            setSPKEY(activity,"userId",String.valueOf(userMess.getId()));
                            setSPKEY(activity,"isAnchor",String.valueOf(userMess.getIsAnchor()));
                            setSPKEY(activity,"token",userMess.getToken());
                            //setSPKEY(activity,"anchorId",userMess.getAnchorId());
                            anchId = userMess.getAnchorId();
                            isAnch = String.valueOf(userMess.getIsAnchor());
                            setSPKEY(activity,"head",TextUtil.isNull2Url(userMess.getHeadUrl()));
                            setSPKEY(activity,"name",userMess.getNickName());
                            setSPKEY(activity,"integral",userMess.getIntegral());
                            if (TextUtil.isNullFor(userMess.getIsNew()) || userMess.getIsNew().equals("true")){//新人弹窗
                                final Dialog dialog = new Dialog(activity, R.style.BottomDialogStyle);
                                View viewM = View.inflate(activity, R.layout.user_new_dialog, null);
                                viewM.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.setContentView(viewM);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                Window dialogWindow = dialog.getWindow();
                                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                                lp.gravity = Gravity.CENTER;
                                dialogWindow.setAttributes(lp);
                                dialog.show();
                            }
                        }else {
                            removeSPKEY(activity,"token");
                            removeSPKEY(activity,"user");
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                            ToastUtil.showShort(activity,"登录失败，请重新登录");
                            activity.finish();
                        }
                    }else {
                        removeSPKEY(activity,"token");
                        removeSPKEY(activity,"user");
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                        ToastUtil.showShort(activity,"登录已失效，请重新登录");
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
        mUnbinder.unbind();
    }


    public static boolean isAnch(){
        if (isAnch.equals("1")){
            return true;
        }
        return false;
    }

    public String getAnchId(){
        return anchId;
    }
}