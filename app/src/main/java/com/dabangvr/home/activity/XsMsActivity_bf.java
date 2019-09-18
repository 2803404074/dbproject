package com.dabangvr.home.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.home.fragment.MsFragmentMy;
import com.dabangvr.home.fragment.MsFragmentNow;
import com.dabangvr.my.StatusBarUtil;

public class XsMsActivity_bf extends BaseNewActivity implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup radioGroup;
    private FragmentManager fragmentManager;
    private RadioButton radioButton_00, radioButton_01, radioButton_02;
    private MsFragmentNow msFragmentNow;
    private MsFragmentMy msFragmentMy;

    private ImageView title;

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
        title = findViewById(R.id.ms_title);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fragmentManager = getSupportFragmentManager();
        radioGroup = findViewById(R.id.ms_radiogrop);
        radioGroup.setOnCheckedChangeListener(this);
        radioButton_00 = findViewById(R.id.ms_now);
        radioButton_01 = findViewById(R.id.ms_host);
        radioButton_02 = findViewById(R.id.ms_my);
        radioButton_00.setChecked(true);
        radioButton_00.setTextColor(this.getResources().getColor(R.color.colorDb));
        changeImageSize();
    }

    @Override
    public void initData() {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.ms_now: {
                changeFragment(0);
                radioButton_00.setTextColor(this.getResources().getColor(R.color.colorDb));
                radioButton_01.setTextColor(this.getResources().getColor(R.color.black));
                radioButton_02.setTextColor(this.getResources().getColor(R.color.black));
//                title.setText("限时秒杀");
                break;
            }
            case R.id.ms_host: {
                changeFragment(0);
                radioButton_01.setTextColor(this.getResources().getColor(R.color.colorDb));
                radioButton_00.setTextColor(this.getResources().getColor(R.color.black));
                radioButton_02.setTextColor(this.getResources().getColor(R.color.black));

//                title.setText("秒杀排行");
                //广播通知fragment更新数据
                Intent intent = new Intent("android.intent.action.MS_HOST");
                LocalBroadcastManager.getInstance(XsMsActivity_bf.this).sendBroadcast(intent);
                break;
            }
            case R.id.ms_my: {
                changeFragment(3);
                radioButton_02.setTextColor(this.getResources().getColor(R.color.colorDb));
                radioButton_00.setTextColor(this.getResources().getColor(R.color.black));
                radioButton_01.setTextColor(this.getResources().getColor(R.color.black));
//                title.setText("我的秒杀");
                break;
            }
        }
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
            case 3:
                if (msFragmentMy == null) {
                    msFragmentMy = new MsFragmentMy();
                    beginTransaction.add(R.id.ms_fragment, msFragmentMy);
                } else {
                    beginTransaction.show(msFragmentMy);
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
        if (msFragmentMy != null) {
            transaction.hide(msFragmentMy);
        }
    }


    //定义底部标签图片大小
    private void changeImageSize() {
        Drawable drawableFirst = getResources().getDrawable(R.drawable.pt_naoz);
        drawableFirst.setBounds(0, 0, 69, 69);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        radioButton_00.setCompoundDrawables(null, drawableFirst, null, null);//只放上面

        Drawable drawableSearch = getResources().getDrawable(R.drawable.pt_host);
        drawableSearch.setBounds(0, 0, 69, 69);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        radioButton_01.setCompoundDrawables(null, drawableSearch, null, null);//只放上面

        Drawable drawableMe = getResources().getDrawable(R.drawable.pt_my_pt);
        drawableMe.setBounds(5, 5, 90, 90);
        radioButton_02.setCompoundDrawables(null, drawableMe, null, null);
    }
}
