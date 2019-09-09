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


public class FragmentZhiboCopy extends Fragment implements View.OnClickListener {

    private TabLayout tabLayout;

    private ViewPager viewPager;
    private ImageView tv_my_zb;
    private TabLayoutFragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;
    private ZhiBoPage zhiBoPage;
    private VideoZBFragment videoZBFragment;
    private LinearLayout talRl;
    private int height;
    private VerticalTextview search_edit;

    private ArrayList<String> titleList = new ArrayList<String>();
    private TextView home_type;
    private TextView h_cart;
    private LinearLayout home_search;
    private VideoSonDspFragment videoSonDspFragment;
    private HomeFragment homeFragment;

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
        search_edit = view.findViewById(R.id.search_edit);
        home_type = view.findViewById(R.id.home_type);
        h_cart = view.findViewById(R.id.h_cart);
        home_search = view.findViewById(R.id.home_search);
        home_type.setOnClickListener(this);
        h_cart.setOnClickListener(this);
        home_search.setOnClickListener(this);
        tabLayout = view.findViewById(R.id.tabs);
        talRl = view.findViewById(R.id.rl_tab);
        viewPager = view.findViewById(R.id.viewpager_id);
        view.findViewById(R.id.start_search).setOnClickListener(new View.OnClickListener() {
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

        search_edit.setTextList(titleList);
        search_edit.setText(10, 2, Color.BLACK);//设置属性
        search_edit.setTextStillTime(3000);//设置停留时长间隔
        search_edit.setAnimTime(300);//设置进入和退出的时间间隔
        search_edit.setOnItemClickListener(new VerticalTextview.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent sintent = new Intent(getContext(), SearchActivity.class);
                startActivity(sintent);
            }
        });

        search_edit.startAutoScroll();
    }

    private String TAG = "luhuas";

    public View tab_icon(String name, int iconID) {
        View newtab = LayoutInflater.from(getActivity()).inflate(R.layout.icon_view, null);
        TextView tv = (TextView) newtab.findViewById(R.id.tabtext);
        newtab.setPadding(28, 0, 10, 0);
        tv.setText(name);
        ImageView im = (ImageView) newtab.findViewById(R.id.tabicon);
        if (iconID != 0) {
            im.setImageResource(iconID);
        }
        return newtab;
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
        String[] titles = new String[]{"", "直播", "视频"};

        int leng = titles.length;
        for (int i = 0; i < leng; i++) {
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.getTabAt(i).setText(titles[i]);

        }
        tabLayout.getTabAt(0).setCustomView(tab_icon("同城", R.mipmap.ic_down));


        mFragments = new ArrayList<>(3);
//        zhiBoPage = new ZhiBoPage(0);
        videoZBFragment = new VideoZBFragment();
        videoSonDspFragment = new VideoSonDspFragment();
        homeFragment = new HomeFragment();
        mFragments.add(homeFragment);
        mFragments.add(videoZBFragment);
        mFragments.add(videoSonDspFragment);
        viewPager.setOffscreenPageLimit(mFragments.size());
        mAdapter = new TabLayoutFragmentPagerAdapter(getChildFragmentManager(), mFragments);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        viewPager.setAdapter(mAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { //当前选中
                Log.d(TAG, "onTabSelected: " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_type:        //头部分类
                Intent intent = new Intent(getContext(), HxClassActivity.class);
                startActivity(intent);
                break;
            case R.id.h_cart:           //头部消息
                // TODO: 2019/8/6 头部消息
                Intent cintent = new Intent(getContext(), CartActivity.class);
                startActivity(cintent);
                break;
            case R.id.home_search:          //头部搜索
                Intent sintent = new Intent(getContext(), SearchActivity.class);
                startActivity(sintent);
                break;
            default:
                break;
        }
    }
}
