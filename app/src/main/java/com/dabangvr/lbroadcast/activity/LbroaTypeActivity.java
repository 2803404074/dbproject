package com.dabangvr.lbroadcast.activity;

import android.app.Activity;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.dabangvr.R;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.home.fragment.HxFragment;
import com.dabangvr.lbroadcast.fragment.page.FragmentGw;
import com.dabangvr.lbroadcast.fragment.page.ZhiBoTypePage;
import com.dabangvr.lbroadcast.widget.MyTabLayout;
import com.dabangvr.util.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LbroaTypeActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;

    @BindView(R.id.my_tabLayout)
    MyTabLayout myTabLayout;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private int position = 0;

    private List<FragmentGw.ZhiboPageMo> typeData = new ArrayList<>();
    private String [] title;
    private List<Fragment> fragments = new ArrayList<>();
    private SimpleFragmentPagerAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStatusBarColor();
        setAndroidNativeLightStatusBar(true);

        setContentView(R.layout.activity_ibroa_type);
        ButterKnife.bind(this);
        initView();
    }
    private  void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = LbroaTypeActivity.this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorWhite));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //使用SystemBarTint库使4.4版本状态栏变色，需要先将状态栏设置为透明
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(getResources().getColor(R.color.colorWhite));
        }
    }
    private void setAndroidNativeLightStatusBar(boolean dark) {
        View decor = getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    private void initView() {
        position = getIntent().getIntExtra("position",0);

        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(0),"全部",R.drawable.button_style_black));
        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(1),"精选",R.drawable.button_style_green));
        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(2),"关注",R.drawable.button_style_red));
        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(3),"娱乐",R.drawable.button_style_green));
        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(4),"海滩",R.drawable.button_style_black));

        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(5),"户外",R.drawable.button_style_red));
        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(6),"交友",R.drawable.button_style_black));
        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(7),"女神",R.drawable.button_style_green));
        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(8),"男神",R.drawable.button_style_red));
        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(9),"门店",R.drawable.button_style_black));

        title = new String[10];

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        for (int i = 0; i < typeData.size(); i++) {
            ZhiBoTypePage fragment = new ZhiBoTypePage(position);
            fragment.setTabPos(i,typeData.get(i).getId());//设置第几页，以及每页的id
            fragments.add(fragment);
            title[i] = typeData.get(i).getName();
        }
        mAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), fragments, title);
        viewPager.setAdapter(mAdapter);
        myTabLayout.setupWithViewPager(viewPager);
//        mTabLayout.setSmoothScrollingEnabled(true);
//        //mTabLayout.setTabMode(TabLayout.MODE_FIXED);//全部放下，不滑动
        myTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//超过长度可滑动
        //设置当前显示哪个标签页
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //滑动监听加载数据，一次只加载一个标签页
                ((ZhiBoTypePage) mAdapter.getItem(position)).sendMessage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



}
