package com.dabangvr.video.zb;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.dabangvr.R;
import com.dabangvr.base.BaseFragment;
import com.dabangvr.common.activity.MyFragment;
import com.dabangvr.video.adapter.AddtabAdapter;
import com.dabangvr.video.adapter.TextOnClickListener;
import com.dabangvr.video.zb.NoScrollViewPager;
import com.dabangvr.video.zb.VideoSonZBragment;
import com.dabangvr.view.home.TabLayoutFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class VideoZBFragment extends BaseFragment {

    private AddtabAdapter addtabAdapter;
    private List<Fragment> mFragments = new ArrayList<>();
    private TabLayoutFragmentPagerAdapter mAdapter;
    private String type;
    private String TAG = "luhuas";

    @BindView(R.id.recy)
    RecyclerView recyclerView;
    @BindView(R.id.viewpager_id)
    NoScrollViewPager viewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceState = getArguments();
        if (savedInstanceState != null) {
            type = savedInstanceState.getString("type");
        }
    }

    @Override
    public int layoutId() {
        return R.layout.recy_vido_zb;
    }


    private void initTablayout() {


        String[] titles = new String[]{"推荐", "美女", "搞笑", "内衣", "游戏", "美女", "搞笑", "内衣", "游戏", "美女", "搞笑", "内衣", "游戏"};
        List<String> tab = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            tab.add(titles[i]);
        }

        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        addtabAdapter = new AddtabAdapter(tab, getContext());
        recyclerView.setAdapter(addtabAdapter);


        for (int i = 0; i < titles.length; i++) {
            VideoSonZBragment videoSonZBragment = new VideoSonZBragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", titles[i]);
            videoSonZBragment.setArguments(bundle);
            mFragments.add(videoSonZBragment);
        }

        mAdapter = new TabLayoutFragmentPagerAdapter(getChildFragmentManager(), mFragments);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.d(TAG, "onPageSelected: " + i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        addtabAdapter.setTextOnClickListener(new TextOnClickListener() {
            @Override
            public void onClickListener(int position) {
                viewPager.setCurrentItem(position);
            }
        });
    }


    @Override
    public void initView() {
        initTablayout();
    }

    @Override
    public void initData() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

}
