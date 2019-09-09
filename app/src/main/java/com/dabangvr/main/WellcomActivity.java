package com.dabangvr.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.my.activity.LoginActivity;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.StatusBarUtil;

import org.apache.commons.lang.StringUtils;

public class WellcomActivity extends AppCompatActivity {

    private SPUtils spUtils;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
//        StatusBarUtil.setTranslucentStatus(this);

//        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
//        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
//        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
//            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
//            //这样半透明+白=灰, 状态栏的文字能看得清
//            StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.white));
//        }
        setContentView(R.layout.activity_wellcom);

        spUtils = new SPUtils(this,"db_user");
        ImageView imageView = findViewById(R.id.application_head);
        Glide.with(WellcomActivity.this).load("http://test.fuxingsc.com/upload/20190325/110257757795a7.png").into(imageView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String isLogin = (String) spUtils.getkey("isLogin","");
                if (StringUtils.isEmpty(isLogin)){
                    Intent intent = new Intent(WellcomActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(WellcomActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },1500);
    }
}
