package com.dabangvr.my.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.model.OrtherStatic;
import com.dabangvr.model.TabAndViewPagerMo;
import com.dabangvr.my.fragment.MyOrtherPageFragment;
import com.dabangvr.util.LoadingDialog;
import com.dabangvr.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人中心-我的订单
 */
public class MyOrtherActivity extends BaseNewActivity implements MyOrtherPageFragment.LoadingCallBack {
    private TabLayout tabLayout;
    private ViewPager vp_pager;
    private List<Fragment> mFragments;
    private SimpleFragmentPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.colorGray1));
        }
    }

    @Override
    public int setLayout() {
        return R.layout.activity_my_orther;
    }

    @Override
    public void initView() {
        findViewById(R.id.backe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tabLayout = findViewById(R.id.tablayout);
        vp_pager = (ViewPager) findViewById(R.id.tab_viewpager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mFragments = new ArrayList<>();
    }

    @Override
    public void initData() {
        List<TabAndViewPagerMo> list = OrtherStatic.setData();
        String[] title = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            MyOrtherPageFragment fragment = new MyOrtherPageFragment();
            fragment.setOrderStatus(list.get(i).getId());
            fragment.setCallBack(this);
            mFragments.add(fragment);
            title[i] = list.get(i).getTitle();
        }
        adapter = new SimpleFragmentPagerAdapter(this.getSupportFragmentManager(), mFragments, title);
        vp_pager.setAdapter(adapter);
        int position = getIntent().getIntExtra("position",0);
        vp_pager.setCurrentItem(position);
        tabLayout.setupWithViewPager(vp_pager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//超过长度可滑动
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void show() {
        setLoaddingView(true);
    }

    @Override
    public void hide() {
        setLoaddingView(false);
    }
}
