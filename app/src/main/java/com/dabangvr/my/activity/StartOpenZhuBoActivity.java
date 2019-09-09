package com.dabangvr.my.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.StatusBarUtil;

/**
 * 主播申请入口
 * 娱乐主播和购物主播
 *
 */
public class StartOpenZhuBoActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        initView();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_start_open_zhu_bo;
    }

    @Override
    protected void initView() {
        removeSPKEY(this,"zbrz");
        findViewById(R.id.start_zb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartOpenZhuBoActivity.this,ApplyAnchorActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }
}
