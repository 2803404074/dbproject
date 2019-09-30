package com.dabangvr.lbroadcast.fragment.page;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.CartActivity;
import com.dabangvr.contens.ParameterString;
import com.dabangvr.home.activity.HxClassActivity;
import com.dabangvr.home.activity.SearchActivity;
import com.dabangvr.lbroadcast.activity.ZbSearchActivity;
import com.dabangvr.video.dsp.VideoSonDspFragment;
import com.dabangvr.video.zb.VideoZBFragment;
import com.dabangvr.view.home.HomeFragment;
import com.dabangvr.view.home.TabLayoutFragmentPagerAdapter;
import com.paradoxie.autoscrolltextview.VerticalTextview;

import java.util.ArrayList;
import java.util.List;


public class FragmentZhiboCopy extends Fragment {

    private TabLayout tabLayout;

    private ViewPager viewPager;
    private TabLayoutFragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;
    private VideoZBFragment videoZBFragment;
    private LinearLayout talRl;
    private int height;

    private ArrayList<String> titleList = new ArrayList<String>();
    private VideoSonDspFragment videoSonDspFragment;
    private HomeFragment homeFragment;
    private VideoSonDspFragment videoSonDspFragment1;

    public static FragmentZhiboCopy newInstance(int index) {
        FragmentZhiboCopy zhibo = new FragmentZhiboCopy();
        Bundle args = new Bundle();
        args.putInt("index", index);
        zhibo.setArguments(args);
        return zhibo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_zhibo_copy, null);
        initView(view);
        return view;
    }


    private void initView(View view) {
        tabLayout = view.findViewById(R.id.tabs);
        talRl = view.findViewById(R.id.rl_tab);
        viewPager = view.findViewById(R.id.viewpager_id);
        ((ImageView)view.findViewById(R.id.start_search)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ZbSearchActivity.class);
                startActivity(intent);
            }
        });
        try {
            initTablayout();
            initSearch();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initSearch() {
        titleList.add("鱼类");
        titleList.add("虾类");
        titleList.add("蟹类");
        titleList.add("9.9包邮");
        titleList.add("外卖");
        titleList.add("美食");
        titleList.add("海产品批发");
        titleList.add("海鲜干货");

    }




    private void initTablayout() {
        ViewTreeObserver viewTreeObserver = talRl.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                talRl.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                height = talRl.getMeasuredHeight();
                viewPager.setPadding(0, height, 0, 0);
            }
        });
        String[] titles = new String[]{"关注", "发现", "跳跳","推荐"};

        int leng = titles.length;
        for (int i = 0; i < leng; i++) {
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.getTabAt(i).setText(titles[i]);

        }


        mFragments = new ArrayList<>(4);
//        zhiBoPage = new ZhiBoPage(0);
        videoZBFragment = new VideoZBFragment();
        videoSonDspFragment = new VideoSonDspFragment();
        videoSonDspFragment1 = new VideoSonDspFragment();
        homeFragment = new HomeFragment();
        mFragments.add(videoSonDspFragment1);
        mFragments.add(homeFragment);
        mFragments.add(videoZBFragment);
        mFragments.add(videoSonDspFragment);
        viewPager.setOffscreenPageLimit(mFragments.size());
        mAdapter = new TabLayoutFragmentPagerAdapter(getChildFragmentManager(), mFragments);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(1);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { //当前选中
                Log.d(ParameterString.TAG, "onTabSelected: " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

}
