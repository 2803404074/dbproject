package com.dabangvr.my.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.main.MainActivity;
import com.dabangvr.util.DateUtil;
import com.dabangvr.util.LoadingDialog;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.SPUtils2;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.wxapi.AppManager;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

import Utils.OkHttpManager;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import config.DyUrl;

/**
 * 手机登陆页面
 * create by 黄仕豪
 * create date 2018.11.19
 */
public class PhoneActivity extends AppCompatActivity {
    private LoadingDialog loadingDialog;
    private EditText etPhone;
    private EditText etCode;
    private String phone;
    private Context context;
    private String uuid;
    private SPUtils spUtils;
    private TextView tv_sendCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_phone);
        initView();
    }

    private void initView() {
        spUtils = new SPUtils(this,"db_user");
        uuid = (String) spUtils.getkey("uuid","");
        if(StringUtils.isEmpty(uuid)){
            uuid = UUID.randomUUID().toString();
        }
        loadingDialog = new LoadingDialog(this);
        EventHandler eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eh);
        //初始化输入框左边的图标
        etPhone = findViewById(R.id.et_phone);
        etCode = findViewById(R.id.et_code);
        initImg(etPhone, R.drawable.po_account);
        initImg(etCode, R.drawable.po_password);
        final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000,1000);
        //获取验证码按钮监听
        tv_sendCode = findViewById(R.id.tv_sendCode);
        tv_sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DateUtil.isPhoneNumber(etPhone.getText().toString())){
                    SMSSDK.getVerificationCode("86",etPhone.getText().toString());
                    myCountDownTimer.start();
                }else {
                    ToastUtil.showShort(context,"手机号输入有误,请从新输入");
                }
            }
        });

        //登陆按钮监听
        findViewById(R.id.bt_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                phone = etPhone.getText().toString();
                SMSSDK.submitVerificationCode("86", phone, etCode.getText().toString());
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                System.out.println("--------result" + event);
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
                    Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
                    loginMyServer(etPhone.getText().toString());
                    loadingDialog.dismiss();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //已经验证
                    Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //已经验证
                    Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
//                    textV.setText(data.toString());
                }

            } else {
//				((Throwable) data).printStackTrace();
//				Toast.makeText(MainActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
//					Toast.makeText(MainActivity.this, "123", Toast.LENGTH_SHORT).show();
                int status = 0;
                try {
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");
                    status = object.optInt("status");
                    if (!TextUtils.isEmpty(des)) {
                        loadingDialog.dismiss();
                        Toast.makeText(PhoneActivity.this, des, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    SMSLog.getInstance().w(e);
                    loadingDialog.dismiss();
                }
            }
        }
    };
    private void loginMyServer(String uName) {
        HashMap<String,String> map = new HashMap<>();
        map.put("nickName", uName);
        map.put("loginType", "phone");
        map.put("phone", uName);
        map.put("openId", uName);
        OkHttpManager okHttpManager=OkHttpManager.getInstance(context,DyUrl.BASE);
        okHttpManager.requestAsyn(DyUrl.LOGIN, "",2, map, new OkHttpManager.ReqCallBack<String>() {
            @Override
            public void onReqSuccess(String result) {
                if(StringUtils.isEmpty(result)){
                    Toast.makeText(context,"登陆失败",Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(500 == jsonObject.optInt("code")){
                        return;
                    }
                    JSONObject data = jsonObject.optJSONObject("data");
                    String userStr = data.optString("user");
                    SPUtils2.instance(PhoneActivity.this).put("user",userStr);
                    spUtils.put("token",data.optString("token"));
                    spUtils.put("isLogin","true");
                    Intent intent = new Intent(PhoneActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    AppManager.getAppManager().finishActivity(LoginActivity.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onReqFailed(String errorMsg) {
                Toast.makeText(PhoneActivity.this, "登陆异常"+errorMsg, Toast.LENGTH_LONG).show();
                loadingDialog.dismiss();
            }
        });
    }

    /**
     * 账号和密码编辑框的图标样式
     *
     * @param editText
     * @param drawable
     */
    private void initImg(EditText editText, int drawable) {
        Drawable drawableFirst = getResources().getDrawable(drawable);
        drawableFirst.setBounds(0, 0, 60, 60);//第一0是距左右边距离，第二0是距上下边距离，第三长度,第四宽度
        editText.setCompoundDrawables(drawableFirst, null, null, null);//只放上面
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    /**
     * 倒计时函数
     */
    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            tv_sendCode.setClickable(false);
            tv_sendCode.setText(l/1000+"秒");

        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            tv_sendCode.setText("重新获取");
            //设置可点击
            tv_sendCode.setClickable(true);
        }
    }

}
