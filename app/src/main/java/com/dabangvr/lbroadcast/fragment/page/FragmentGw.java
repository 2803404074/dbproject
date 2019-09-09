package com.dabangvr.lbroadcast.fragment.page;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dabangvr.R;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.home.fragment.HxFragment;
import com.dabangvr.lbroadcast.activity.MyZbActivity;
import com.dabangvr.lbroadcast.activity.ZbSearchActivity;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播购物页面
 */
public class FragmentGw extends Fragment implements OnTabSelectListener {
    private Context mContext = FragmentGw.this.getContext();
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private List<ZhiboPageMo> mData = new ArrayList<>();
    private SimpleFragmentPagerAdapter mAdapter;
    public static FragmentGw newInstance() {
        FragmentGw fragment = new FragmentGw();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.zb_fragment_gw,null);

        view.findViewById(R.id.tv_my_zb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FragmentGw.this.getContext(), MyZbActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.home_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FragmentGw.this.getContext(), ZbSearchActivity.class);
                startActivity(intent);
            }
        });
        SlidingTabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        setTabPage(tabLayout,viewPager);
        return view;
    }


    private void setTabPage(SlidingTabLayout tabLayout,ViewPager viewPager) {
//        mData.add(new ZhiboPageMo(String.valueOf(0),"全部"));
//        mData.add(new ZhiboPageMo(String.valueOf(1),"精选"));
//        mData.add(new ZhiboPageMo(String.valueOf(2),"关注"));
//        mData.add(new ZhiboPageMo(String.valueOf(3),"娱乐"));
//        mData.add(new ZhiboPageMo(String.valueOf(4),"海滩"));
        String tabTitle[] = new String[mData.size()];

        for (int i = 0; i < mData.size(); i++) {
            ZhiBoPage fragment = new ZhiBoPage(0);
            fragment.setTabPos(i,mData.get(i).getId());//设置第几页，以及每页的id
            mFragments.add(fragment);
            tabTitle[i] = mData.get(i).getName();
        }
        mAdapter = new SimpleFragmentPagerAdapter(getFragmentManager(), mFragments, tabTitle);
        viewPager.setAdapter(mAdapter);
        tabLayout.setViewPager(viewPager, tabTitle, FragmentGw.this.getActivity(), mFragments);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //滑动监听加载数据，一次只加载一个标签页
                ((ZhiBoPage) mAdapter.getItem(position)).sendMessage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }

    public static class ZhiboPageMo{
        private String id;
        private String name;
        private int background;

        public ZhiboPageMo(String id, String name, int background) {
            this.id = id;
            this.name = name;
            this.background = background;
        }

        public ZhiboPageMo() {
        }

        public int getBackground() {
            return background;
        }

        public void setBackground(int background) {
            this.background = background;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
