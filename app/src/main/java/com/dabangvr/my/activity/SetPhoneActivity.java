package com.dabangvr.my.activity;

import android.content.Intent;
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
import com.dabangvr.util.DateUtil;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.wxapi.WXEntryActivity;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import config.DyUrl;
import okhttp3.Call;

public class SetPhoneActivity extends AppCompatActivity {

    private String status;//判断是登陆时候的跳转，还是更换手机号码的跳转
    private String token;
    private EditText etNumber;
    private EditText etCode;
    private String number;
    private SPUtils spUtils;
    private String uName, icon, openID, type;
    private TextView getCode;
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
                    number = etNumber.getText().toString();
                    if(status.equals("edit")){
                        updata(number);
                    }else {
                        senMyServer();
                    }
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //已经验证
                    Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //已经验证
                    //Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
//                    textV.setText(data.toString());
                }

            } else {
                int status = 0;
                try {
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");
                    status = object.optInt("status");
                    if (!TextUtils.isEmpty(des)) {
                        Toast.makeText(SetPhoneActivity.this, des, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    SMSLog.getInstance().w(e);
                }
            }
        }
    };

    /**
     * 修改手机
     */
    public void updata(String number) {
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME,token);
        map.put("mobile",number);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.update, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if(StringUtils.isEmpty(result)){
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    if(0 == object.optInt("errno")){
                        finish();
                        ToastUtil.showShort(SetPhoneActivity.this,"修改成功");
//                        if(null!=textView){
//                            textView.setText(editValue);
//                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }


    /**
     * 登陆需要绑定手机
     * @param
     * @param
     */
    private void senMyServer() {
        HashMap<String, String> map = new HashMap<>();
        map.put("nickName", uName);
        map.put("loginType", type);
        map.put("icon", icon);
        map.put("phone", number);//number
        map.put("openId", openID);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.LOGIN, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int code = object.optInt("code");
                    if (code == 0) {
                        Intent intent = new Intent("android.intent.action.USER_LOGIN");
                        JSONObject data = object.optJSONObject("data");

                        //获取touken
                        String token = data.optString("token");
                        spUtils.put("token", token);
                        //获取用户信息
                        String user = data.optString("user");
                        intent.putExtra("user_info", user);
                        //UserMess mess = JsonUtil.string2Obj(user,UserMess.class);
                        LocalBroadcastManager.getInstance(SetPhoneActivity.this).sendBroadcast(intent);
//                        loadingDialog.dismiss();
                        finish();//结束当前页面
                        if(WXEntryActivity.instants != null){
                            WXEntryActivity.instants.finish();
                        }
                        if(LoginActivity.instant != null){//结束登陆页面
                            LoginActivity.instant.finish();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(Call call, IOException e) {
                Toast.makeText(SetPhoneActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
                //loadingDialog.dismiss();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_phone);
        initPhone();
        initView();
    }

    private void initPhone() {
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
    }


    private void initView() {
        spUtils = new SPUtils(this, "db_user");
        token = (String)spUtils.getkey("token","");
        Intent intent = getIntent();
        status = intent.getStringExtra("status");
        if(status.equals("edit")){

        }else {
            uName = intent.getStringExtra("uName");
            icon = intent.getStringExtra("icon");
            openID = intent.getStringExtra("openID");
            type = intent.getStringExtra("type");
        }

        etNumber = findViewById(R.id.et_number);
        etCode = findViewById(R.id.et_code);
        final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000, 1000);
        //获取验证码
        getCode = findViewById(R.id.get_code);
        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DateUtil.isPhoneNumber(etNumber.getText().toString())) {
                    SMSSDK.getVerificationCode("86", etNumber.getText().toString());
                    myCountDownTimer.start();
                } else {
                    ToastUtil.showShort(SetPhoneActivity.this, "手机号输入有误,请从新输入");
                }
            }
        });
        //判断验证码、手机号
        findViewById(R.id.set_phone_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSSDK.submitVerificationCode("86", etNumber.getText().toString(), etCode.getText().toString());
            }
        });

        findViewById(R.id.ph_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if(WXEntryActivity.instants != null){
                    WXEntryActivity.instants.finish();
                }
            }
        });
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
            getCode.setClickable(false);
            getCode.setText(l / 1000 + "秒");

        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            getCode.setText("重新获取");
            //设置可点击
            getCode.setClickable(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (WXEntryActivity.instants != null) {
            WXEntryActivity.instants.finish();
        }
    }
}
