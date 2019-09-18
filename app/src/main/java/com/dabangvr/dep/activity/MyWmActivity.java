package com.dabangvr.dep.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.dep.fragment.MyOrderWmFragment;
import com.dabangvr.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class MyWmActivity extends BaseNewActivity {
    public static final String[] tabTitles = new String[]{"全部订单", "待评价", "退款"};
    public static final String[] tabTitlesId = new String[]{"", "301", "400"};
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SimpleFragmentPagerAdapter adapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_my_wm;
    }

    @Override
    public void initView() {

        mTabLayout = findViewById(R.id.tablayout);
        mViewPager = findViewById(R.id.viewpager);


        for (int i = 0; i < tabTitles.length; i++) {
            MyOrderWmFragment fragment = new MyOrderWmFragment(0);
            fragment.setTabPos(i, tabTitlesId[i]);//设置第几页，以及每页的id
            mFragments.add(fragment);
        }

        adapter = new SimpleFragmentPagerAdapter(this.getSupportFragmentManager(), mFragments, tabTitles);
        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);
        //设置当前显示哪个标签页
        mViewPager.setCurrentItem(0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //滑动监听加载数据，一次只加载一个标签页
                ((MyOrderWmFragment) adapter.getItem(position)).sendMessage(0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void initData() {

    }
}
