package com.dabangvr.home.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.my.activity.MyOrtherActivity;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.util.WXPayUtils;
import com.example.mina.SessionManager;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import Utils.PdUtil;
import config.DyUrl;
import okhttp3.Call;


public class OrtherOKActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imageView;
    private TextView orderStatus;
    private TextView orderTips;
    private TextView tvButtonLeft;
    private TextView tvButtonRight;
    private int errno;
    private PdUtil pdUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_orther_ok;
    }


    @Override
    protected void initView() {
        pdUtil = new PdUtil(this);
        findViewById(R.id.back).setOnClickListener(this);

        tvButtonLeft = findViewById(R.id.tv_see_order);
        tvButtonLeft.setOnClickListener(this);

        tvButtonRight = findViewById(R.id.tv_go_shop);
        tvButtonRight.setOnClickListener(this);

        imageView = findViewById(R.id.errno_img);

        orderStatus = findViewById(R.id.order_status);

        orderTips = findViewById(R.id.order_tips);
    }

    private void send(String tag) {
        JSONObject o = new JSONObject();
        try {
            o.put("sand", "sand");
            o.put("name", getSPKEY(this,"name"));
            o.put("head", getSPKEY(this,"head"));
            o.put("address", tag);
            o.put("type", "100");
            o.put("mess", "下了一单啦~~");
            SessionManager.getInstance().writeToServer(o);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {

        boolean lbroadcast = getIntent().getBooleanExtra("lbroadcast", false);
        if (lbroadcast) {
            //发送购买信息到直播间的所有观看者
            send(getIntent().getStringExtra("tag"));
        }

        //errno == -1 支付失败
        errno = getIntent().getIntExtra("errno", 0);

        if (errno == -1) {
            Bitmap gameStatusBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.s_errno);
            imageView.setImageBitmap(gameStatusBitmap);
            orderStatus.setText("购买失败");
            orderTips.setText("可在“我的订单”中重新购买支付，不要错过了哦");

            tvButtonLeft.setText("返回订单");
            tvButtonRight.setText("重新支付");
        } else {
            Bitmap gameStatusBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.s_successful);
            imageView.setImageBitmap(gameStatusBitmap);
            orderStatus.setText("购买成功");
            orderTips.setText("可在“我的订单”中查看订单状态，请留意");
            tvButtonLeft.setText("查看订单");
            tvButtonRight.setText("继续购物");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_see_order:
                if (errno != -1) {
                    Intent intent = new Intent(this, MyOrtherActivity.class);
                    startActivity(intent);
                    OrderActivity.instants.finish();
                    finish();
                } else {
                    finish();
                }
                break;
            case R.id.tv_go_shop:
                if (errno != -1) {
                    OrderActivity.instants.finish();
                    finish();
                } else {
                    pdUtil.showLoding("重新支付");
                    payment();
                   // prepayOrderAgain();
                }
                break;
        }
    }


//    private void prepayOrderAgain() {
//        HashMap<String, String> map = new HashMap<>();
//        map.put(DyUrl.TOKEN_NAME, getSPKEY(this, "token"));
//        map.put("orderId", getSPKEY(this, "orderId"));
//        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.prepayDeliveryOrderAgain, map, new GsonObjectCallback<String>(DyUrl.BASE) {
//
//            @Override
//            public void onUi(String result) {
//                if (StringUtils.isEmpty(result)) {
//                    return;
//                }
//                try {
//                    JSONObject object = new JSONObject(result);
//                    int errno = object.optInt("errno");
//                    if (errno == 0) {
//                        if (500 == object.optInt("code")) {
//                            return;
//                        }
//                        toWXPay(object.optJSONObject("data"));
//                        finish();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailed(Call call, IOException e) {
//
//            }
//        });
//
//    }

    private void payment() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this,"token"));
        map.put("orderId", getSPKEY(this,"orderId"));
        map.put("payOrderSnType", "orderSn");// 直接购买用orderSnTotal；重新付款用orderSn
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.prepayOrderAgain, map, new GsonObjectCallback<String>(DyUrl.BASE) {
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
                        toWXPay(object.optJSONObject("data"));
                    }else if (errno == 400){
                        ToastUtil.showShort(OrtherOKActivity.this,object.optString("errmsg"));
                    }

                    pdUtil.desLoding();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }


    private void toWXPay(JSONObject object) {
        pdUtil.desLoding();
        setSPKEY(this,"payType","goods");
        WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
        builder.setAppId(object.optString("appid"))
                .setPartnerId(object.optString("partnerid"))
                .setPrepayId(object.optString("prepayid"))
                .setPackageValue(object.optString("package"))
                .setNonceStr(object.optString("noncestr"))
                .setTimeStamp(object.optString("timestamp"))
                .setSign(object.optString("sign"))
                .build().toWXPayNotSign(this);
        ToastUtil.showShort(this, "正在打开微信...");
    }
}
