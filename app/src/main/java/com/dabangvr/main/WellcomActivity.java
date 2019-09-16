package com.dabangvr.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.my.activity.LoginActivity;
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
import Utils.TObjectCallback;
import bean.UserMess;
import config.DyUrl;
import okhttp3.Call;

public class WellcomActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcom);

        ImageView imageView = findViewById(R.id.application_head);
        //网络图片
        Glide.with(WellcomActivity.this).load("http://test.fuxingsc.com/upload/20190325/110257757795a7.png").into(imageView);

        //新用户,直接跳到登陆页
        String token = (String) SPUtils2.instance(this).getkey("token","");
        if (StringUtils.isEmpty(token)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WellcomActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            },1500);
            return;
        }
        //获取用户信息
        initUserData(token);

        //获取首页数据
        initHomeData();

    }


    /**
     * 获取用户信息
     */
    private void initUserData(String token) {
        HashMap<String, Object> map = new HashMap<>();
        OkHttp3Utils.getInstance(DyUrl.BASE).doPostJson(DyUrl.USER_INFO, map, token, new TObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                UserMess userMess = JsonUtil.string2Obj(result, UserMess.class);
                if (userMess!=null){
                    SPUtils2.instance(WellcomActivity.this).put("user",result);
                    SPUtils2.instance(WellcomActivity.this).put("token",userMess.getToken());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(WellcomActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    },1500);
                }
            }
            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(WellcomActivity.this,"登录失败,请重新登录");
                SPUtils2.instance(WellcomActivity.this).remove("user");
                SPUtils2.instance(WellcomActivity.this).remove("token");
                Intent intent = new Intent(WellcomActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * 获取首页数据
     */
    private void initHomeData() {

    }
}
