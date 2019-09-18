package com.dabangvr.my.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.common.adapter.PagerAdapter;
import com.dabangvr.my.fragment.CommitOk;
import com.dabangvr.my.fragment.FragmentBusinessIden;
import com.dabangvr.my.fragment.FragmentBusinessMess;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.StatusBarUtil;
import com.example.camera.CameraActivity;
import com.example.mylibrary.ViewPagerScroller;
import com.example.mylibrary.ViewPagerSlide;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

import static com.dabangvr.my.fragment.FragmentBusinessMess.textView;

public class BusinessActivity extends BaseNewActivity implements CommitOk.OnOkClike {

    private ArrayList<Fragment> fragments;
    private PagerAdapter adapter;
    private ViewPagerSlide viewPager;
    private SPUtils spUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_business;
    }

    @Override
    public void initView() {

        spUtils = new SPUtils(this,"db_user");
        spUtils.remove("SJRZ");

        viewPager = (ViewPagerSlide)findViewById(R.id.view_pager);
        viewPager.setScroll(false);
        fragments = new ArrayList<>();
        fragments.add(new FragmentBusinessMess(viewPager));
        fragments.add(new FragmentBusinessIden(viewPager));
        fragments.add(new CommitOk(0,this));

        adapter = new PagerAdapter(this.getSupportFragmentManager(),fragments);

        ViewPagerScroller mPagerScroller=new ViewPagerScroller(this);
        mPagerScroller.setScrollDuration(2000);
        mPagerScroller.initViewPagerScroll(viewPager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onOkclike(boolean isOk) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FragmentBusinessIden.province = null;
        FragmentBusinessIden.city = null;
        FragmentBusinessIden.county = null;
        FragmentBusinessIden.lat = null;
        FragmentBusinessIden.lon = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentBusinessIden.province = null;
        FragmentBusinessIden.city = null;
        FragmentBusinessIden.county = null;
        FragmentBusinessIden.lat = null;
        FragmentBusinessIden.lon = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CameraActivity.REQUEST_CODE:
                if (data != null) {
                    String path = data.getStringExtra("result");
                    if (!TextUtils.isEmpty(path)) {
                        FragmentBusinessMess.setImg(0,path);
                    }
                }

                break;
            case CameraActivity.REQUEST_BACK:
                if (data != null) {
                    String path = data.getStringExtra("result");
                    if (!TextUtils.isEmpty(path)) {
                        FragmentBusinessMess.setImg(1,path);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

        if (StringUtils.isEmpty(FragmentBusinessMess.path01) && StringUtils.isEmpty(FragmentBusinessMess.path02)){
            textView.setText("请上传身份证");
        }else if (!StringUtils.isEmpty(FragmentBusinessMess.path01) && StringUtils.isEmpty(FragmentBusinessMess.path02)){
            textView.setText("请上传身份证反面");
        }else if (StringUtils.isEmpty(FragmentBusinessMess.path01) && !StringUtils.isEmpty(FragmentBusinessMess.path02)){
            textView.setText("请上传身份证正面");
        }else {
            textView.setText("身份证已上传");
            textView.setTextColor(getResources().getColor(R.color.colorDb3));
        }

    }
}