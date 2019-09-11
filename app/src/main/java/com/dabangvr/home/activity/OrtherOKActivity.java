package com.dabangvr.home.activity;

import android.os.Bundle;
import android.view.View;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.util.StatusBarUtil;
import com.example.mina.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;


public class OrtherOKActivity extends BaseNewActivity implements View.OnClickListener {

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
    public void initView() {
        int code = getIntent().getIntExtra("code",0);
        //支付成功
        if (code == 200){
            //设置支付成功的图片
        }else {
            //设置支付失败的图片
        }
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
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }
}
