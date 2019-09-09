package com.dabangvr.my.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.lbroadcast.activity.PlayActivity;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.util.WXPayUtils;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.DyUrl;
import config.GiftUrl;
import okhttp3.Call;

public class SbActivity extends BaseActivity implements View.OnClickListener {

    //自定义输入的金额
    private EditText etMoney;
    private TextView tvAuthMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_sb;
    }

    @Override
    protected void initView() {
        TextView tvSb = findViewById(R.id.tv_sb);
        tvSb.setText(getIntent().getStringExtra("diamond"));
        findViewById(R.id.tv_cz).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            if (!StringUtils.isEmpty(etMoney.getText().toString())){
                tvAuthMoney.setText(String.valueOf(Integer.parseInt(etMoney.getText().toString())/10));
            }else {
                tvAuthMoney.setText("0");
            }

        }
    };

    @Override
    protected void initData() {

    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    /**
     * 充值
     */
    private void startCz(String money) {
        Map<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this, "token"));
        map.put("price", money);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(GiftUrl.investDiamond, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (errno == 0) {
                        if (500 == object.optInt("code")) {
                            return;
                        }
                        //吊起微信;
                        toWXPay(object.optJSONObject("data"));
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
     * 微信支付
     *
     * @param object
     */
    private void toWXPay(JSONObject object) {
        setSPKEY(this,"payType","Recharge");
        JSONObject dataObj = object.optJSONObject("data");

        WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
        builder.setAppId(dataObj.optString("appid"))
                .setPartnerId(dataObj.optString("partnerid"))
                .setPrepayId(dataObj.optString("prepayid"))
                .setPackageValue(dataObj.optString("package"))
                .setNonceStr(dataObj.optString("noncestr"))
                .setTimeStamp(dataObj.optString("timestamp"))
                .setSign(dataObj.optString("sign"))
                .build().toWXPayNotSign(SbActivity.this);
        ToastUtil.showShort(this, "正在打开微信...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cz:
                ShowView();
                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 充值弹窗
     */
    private void ShowView() {
        final Dialog dialog = new Dialog(SbActivity.this, R.style.BottomDialogStyle);
        View viewM = View.inflate(SbActivity.this, R.layout.play_cz_dialog, null);
        //20充值
//                viewM.findViewById(R.id.tv_money_01).setOnClickListener(this);

        viewM.findViewById(R.id.tv_money_01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCz("1");
            }
        });

        //50充值
        viewM.findViewById(R.id.tv_money_02).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCz("50");
            }
        });
        //100充值
        viewM.findViewById(R.id.tv_money_03).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCz("100");
            }
        });
        //200充值
        viewM.findViewById(R.id.tv_money_04).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCz("200");
            }
        });
        //自定义充值
        tvAuthMoney = viewM.findViewById(R.id.tv_money_05);
        tvAuthMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(etMoney.getText().toString())) {
                    ToastUtil.showShort(SbActivity.this, "请输入金额");
                    return;
                }

                if (Integer.parseInt(etMoney.getText().toString()) < 10) {
                    ToastUtil.showShort(SbActivity.this, "至少充值100个鲜币哦~~");
                    return;
                }
                startCz(tvAuthMoney.getText().toString());
            }
        });

        etMoney = viewM.findViewById(R.id.et_money);

        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (delayRun != null) {
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
                //延迟800ms，如果不再输入字符，则执行该线程的run方法
                handler.postDelayed(delayRun, 500);
            }
        });

        //自定义充值
        tvAuthMoney = viewM.findViewById(R.id.tv_money_05);
        tvAuthMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(etMoney.getText().toString())) {
                    ToastUtil.showShort(SbActivity.this, "请输入金额");
                    return;
                }

                if (Integer.parseInt(etMoney.getText().toString()) < 10) {
                    ToastUtil.showShort(SbActivity.this, "至少充值100个鲜币哦~~");
                    return;
                }
                startCz(tvAuthMoney.getText().toString());
            }
        });

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
}
