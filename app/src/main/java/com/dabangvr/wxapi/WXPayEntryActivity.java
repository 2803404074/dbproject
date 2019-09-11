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
        int key = (int) SPUtils2.instance(this).getkey(PayType.PAY_KEY,0);
        Intent intent = new Intent(WXPayEntryActivity.this, OrtherOKActivity.class);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            //说明从订单页面支付的，需要结束
            if (key !=0 && key == PayType.ORDER_PAGE){
                AppManager.getAppManager().finishActivity(OrderActivity.class);
            }
            intent.putExtra("code",resp.errCode==0?200:400);//200成功   400失败
        }
        startActivity(intent);
        finish();
    }
}
