package com.dabangvr.my.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.home.activity.HxActivity;
import com.dabangvr.home.activity.HxActivityType;
import com.dabangvr.lbroadcast.activity.PlayZhiBoActivity;
import com.dabangvr.main.MainActivity;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.TextUtil;

import bean.UserMess;

public class JfActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_money;
    }

    @Override
    protected void initView() {
        TextView tvJf = findViewById(R.id.tv_jf);
        tvJf.setText(TextUtil.isNull(getUser().getIntegral()));

        findViewById(R.id.tv_goods).setOnClickListener(this);
        findViewById(R.id.tv_live).setOnClickListener(this);
        findViewById(R.id.tv_short).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    private UserMess getUser(){
        return JsonUtil.string2Obj(getSPKEY(this,"user"), UserMess.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_live:
                if (isAnch()){
                    Intent intent = new Intent(JfActivity.this, PlayZhiBoActivity.class);
                    startActivity(intent);
                }else {
                    show(JfActivity.this, 1, "只有通过实名认证才能开直播哦", "去认证");
                }
                break;
            case R.id.tv_short:
                if (isAnch()){
                    Intent intent = new Intent(JfActivity.this, PlayZhiBoActivity.class);
                    startActivity(intent);
                }else {
                    show(JfActivity.this, 1, "只有通过实名认证才能发视频哦", "去认证");
                }
                break;
            case R.id.tv_goods:
                Intent intent = new Intent(JfActivity.this, HxActivity.class);
                startActivity(intent);
                break;
                default:break;
        }
    }
}
