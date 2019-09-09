package com.dabangvr.home.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.view.home.TabLayoutFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * 9.9秒杀fragment
 */

public class NineMsFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private TabLayoutFragmentPagerAdapter adapter;
    private NineMsPagerFragment nineMsPagerFragment;
    private TextView tev_nine;
    private TextView tev_two_nine;
    private TextView tev_three_nine;
    private ViewPager vp_pager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("tag", "onCreateView()方法执行");
        context = NineMsFragment.this.getContext();
        View view = inflater.inflate(R.layout.ms_nine_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        vp_pager = view.findViewById(R.id.m_viewpager);

        tev_nine = view.findViewById(R.id.tev_nine);
        tev_two_nine = view.findViewById(R.id.tev_two_nine);
        tev_three_nine = view.findViewById(R.id.tev_three_nine);
        tev_nine.setOnClickListener(this);
        tev_two_nine.setOnClickListener(this);
        tev_three_nine.setOnClickListener(this);
        tev_nine.setSelected(true);
        tev_two_nine.setSelected(false);
        tev_three_nine.setSelected(false);

        List<Fragment> mFragments = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            nineMsPagerFragment = new NineMsPagerFragment(0);
            nineMsPagerFragment.setTabPos(i, "00");//设置第几页，以及每页的id
            mFragments.add(nineMsPagerFragment);

        }
        adapter = new TabLayoutFragmentPagerAdapter(getActivity().getSupportFragmentManager(), mFragments);
        vp_pager.setAdapter(adapter);
        //设置当前显示哪个标签页
        vp_pager.setCurrentItem(0);
        vp_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //滑动监听加载数据，一次只加载一个标签页                刷新标志     排序
                ((NineMsPagerFragment) adapter.getItem(position)).sendMessage(0, "1");
                setSelecl(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tev_nine:
                vp_pager.setCurrentItem(0);
                tev_nine.setSelected(true);
                tev_two_nine.setSelected(false);
                tev_three_nine.setSelected(false);
                break;
            case R.id.tev_two_nine:
                vp_pager.setCurrentItem(1);
                tev_nine.setSelected(false);
                tev_two_nine.setSelected(true);
                tev_three_nine.setSelected(false);
                break;
            case R.id.tev_three_nine:
                vp_pager.setCurrentItem(2);
                tev_nine.setSelected(false);
                tev_two_nine.setSelected(false);
                tev_three_nine.setSelected(true);
                break;
        }
    }

    private void setSelecl(int i) {
        switch (i) {
            case 0:
                tev_nine.setSelected(true);
                tev_two_nine.setSelected(false);
                tev_three_nine.setSelected(false);
                break;
            case 1:
                tev_nine.setSelected(false);
                tev_two_nine.setSelected(true);
                tev_three_nine.setSelected(false);
                break;
            case 2:
                tev_nine.setSelected(false);
                tev_two_nine.setSelected(false);
                tev_three_nine.setSelected(true);
                break;
        }

    }
}
