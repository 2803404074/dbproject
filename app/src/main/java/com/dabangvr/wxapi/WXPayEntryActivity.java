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
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if(resp.errCode == 0){
                //充值
                if (null == OrderActivity.instants && null == OrderDepActivity.instants){
                    finish();
                    return;
                }

                Intent intent = new Intent(WXPayEntryActivity.this,OrtherOKActivity.class);
                if (null != OrderActivity.instants){
                    intent.putExtra("lbroadcast",OrderActivity.isLbroad);
                    intent.putExtra("tag",OrderActivity.tag);
                }
                startActivity(intent);

                if (null!=OrderActivity.instants){
                    OrderActivity.instants.finish();
                }
                if (null!= OrderDepActivity.instants){
                    OrderDepActivity.instants.finish();
                }

                finish();
            }else {
                //充值
                if (null == OrderActivity.instants && null == OrderDepActivity.instants){
                    finish();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(WXPayEntryActivity.this);
                builder.setTitle("提示");
                builder.setMessage("支付失败,"+resp.errCode);
                builder.setPositiveButton("返回商家", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (null!=OrderActivity.instants){
                            OrderActivity.instants.finish();
                            Intent intent = new Intent(WXPayEntryActivity.this,OrtherOKActivity.class);
                            intent.putExtra("errno",-1);
                            startActivity(intent);
                        }else if (null!= OrderDepActivity.instants){
                            OrderDepActivity.instants.finish();
                            Intent intent = new Intent(WXPayEntryActivity.this,OrtherOKActivity.class);
                            intent.putExtra("errno",-1);
                            startActivity(intent);
                        }else {

                        }
                        finish();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }


        }
    }

    /**
     * 支付成功与否都通知服务端
     * 不需要做任何回调处理
     * @param code 微信支付失败成功标志
     */
    private void senMessServer(int code) {
        Intent intent = new Intent("android.intent.action.WXRESULT");
        intent.putExtra("status",String.valueOf(code));
        LocalBroadcastManager.getInstance(WXPayEntryActivity.this).sendBroadcast(intent);
    }

}
