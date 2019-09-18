package com.dabangvr.home.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.home.fragment.MsFragmentNow;
import com.dabangvr.my.StatusBarUtil;

public class XsMsActivity extends BaseNewActivity {


    private FragmentManager fragmentManager;

    private MsFragmentNow msFragmentNow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            StatusBarUtil.setTransparentForWindow(this);
        }
    }

    @Override
    public int setLayout() {
        return R.layout.activity_ms;
    }

    @Override
    public void initView() {


        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    public void initData() {
        changeFragment(0);
    }



    public void changeFragment(int index) {

        // 调用FragmentTransaction中的方法来处理这个transaction
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        hideFragments(beginTransaction);

        // 根据不同的标签页执行不同的操作
        switch (index) {
            case 0:
                if (msFragmentNow == null) {
                    msFragmentNow = new MsFragmentNow();
                    beginTransaction.add(R.id.ms_fragment, msFragmentNow);
                } else {
                    beginTransaction.show(msFragmentNow);
                }
                break;
            default:
                break;
        }

        //需要提交事务
        beginTransaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (msFragmentNow != null) {
            transaction.hide(msFragmentNow);
        }
    }

}
