package com.dabangvr.my.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.adapter.PagerAdapter;
import com.dabangvr.my.fragment.CommitOk;
import com.dabangvr.my.fragment.FragmentApplyAnMess;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.StatusBarUtil;
import com.example.mylibrary.ViewPagerScroller;
import com.example.mylibrary.ViewPagerSlide;

import java.util.ArrayList;

/**
 * 申请主播页面
 */
public class ApplyAnchorActivity extends BaseActivity implements CommitOk.OnOkClike {

    private ArrayList<Fragment> fragments;
    private PagerAdapter adapter;
    private ViewPagerSlide viewPager;
    private SPUtils spUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_apply_anchor;
    }

    @Override
    protected void initView() {
        spUtils = new SPUtils(this, "db_user");
        spUtils.remove("ZBRZ");

        viewPager = findViewById(R.id.view_pager);
        viewPager.setScroll(false);
        fragments = new ArrayList<>();
        fragments.add(new FragmentApplyAnMess(viewPager));
        fragments.add(new CommitOk(1,this));//type=1,表示直播申请;0表示商家入驻

        adapter = new PagerAdapter(this.getSupportFragmentManager(), fragments);

        ViewPagerScroller mPagerScroller = new ViewPagerScroller(this);
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
    protected void initData() {

    }

    @Override
    public void onOkclike(boolean isOk) {
        finish();
    }
}
