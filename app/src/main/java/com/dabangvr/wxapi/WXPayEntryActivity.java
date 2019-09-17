package com.dabangvr.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.dabangvr.dep.activity.OrderDepActivity;
import com.dabangvr.home.activity.OrderActivity;
import com.dabangvr.home.activity.OrtherOKActivity;
import com.dabangvr.lbroadcast.activity.PlayActivity;
import com.dabangvr.my.activity.OrderDetailedActivity;
import com.dabangvr.util.SPUtils2;
import com.example.mina.SessionManager;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.pay_result);
        api = WXAPIFactory.createWXAPI(this, "wx2351c48134140a3c");
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onResp(BaseResp resp) {
        //如果存在订单页面，结束掉
        AppManager.getAppManager().finishActivity(OrderActivity.class);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0){
                //跳转支付成功页面
                Intent intent = new Intent(WXPayEntryActivity.this, OrtherOKActivity.class);
                startActivity(intent);
            }else {
                Intent intent = new Intent(WXPayEntryActivity.this, OrderDetailedActivity.class);
                intent.putExtra("orderId",(String)SPUtils2.instance(this).getkey("payOrderId",""));
                startActivity(intent);
            }
        }

        finish();
    }
}
