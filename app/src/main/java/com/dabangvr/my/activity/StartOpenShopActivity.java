package com.dabangvr.my.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.util.StatusBarUtil;
import com.example.mylibrary.ViewPagerForScrollView;
import com.dabangvr.my.fragment.IntroduceFragment;
import com.dabangvr.util.BannerStart;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

public class StartOpenShopActivity extends BaseNewActivity {

    private TabLayout tabLayout;
    private ViewPagerForScrollView vp_pager;
    private List<Fragment> mFragments;
    private SimpleFragmentPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_start_open_shop;
    }

    @Override
    public void initView() {

        Banner banner = findViewById(R.id.ms_banner);
        BannerStart.starBanner(StartOpenShopActivity.this,banner,"7");

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tabLayout = findViewById(R.id.tablayout);
        vp_pager = (ViewPagerForScrollView) findViewById(R.id.tab_viewpager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mFragments = new ArrayList<>();

        String[] title = {"平台介绍","入驻流程","入驻要求","资费查询"};
        for (int i = 0; i < title.length; i++) {
            IntroduceFragment fragment = new IntroduceFragment();
            fragment.setTabPos(i);
            mFragments.add(fragment);
        }

        adapter = new SimpleFragmentPagerAdapter(this.getSupportFragmentManager(), mFragments, title);
        vp_pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(vp_pager);
        //设置当前显示哪个标签页
        vp_pager.setCurrentItem(0);
    }

    @Override
    public void initData() {

    }
}
