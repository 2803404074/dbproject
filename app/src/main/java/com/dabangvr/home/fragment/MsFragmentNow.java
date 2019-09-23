package com.dabangvr.home.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.model.MsDataMo;
import com.dabangvr.view.home.TabLayoutFragmentPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import config.DyUrl;


/**
 * 正在秒杀fragment
 */

public class MsFragmentNow extends Fragment {

    private Context context;
    private TabLayoutFragmentPagerAdapter adapter;
    private MsPager fragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("tag", "onCreateView()方法执行");
        context = MsFragmentNow.this.getContext();
        View view = inflater.inflate(R.layout.ms_now_fragment, container, false);
        initView(view);
        return view;
    }

    public View tab_icon(String time, String state) {
        View newtab = LayoutInflater.from(getActivity()).inflate(R.layout.tab_icon_view, null);

        TextView tabtv = (TextView) newtab.findViewById(R.id.tab_time);
        TextView tabim = (TextView) newtab.findViewById(R.id.tab_state);
        newtab.setPadding(28, 0, 10, 0);
        if (!TextUtils.isEmpty(state) && !TextUtils.isEmpty(time)) {
            try {
                int intstate = Integer.parseInt(state);
                String times = time.substring(0, 2);
                int anInttimes = Integer.parseInt(times);
                if (intstate == anInttimes) {
                    tabtv.setTextSize(22);
                    tabim.setTextColor(getActivity().getResources().getColor(R.color.white));
                    tabtv.setTextColor(getActivity().getResources().getColor(R.color.white));
                    tabim.setTextSize(15);
                    tabim.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    tabim.setText("抢购进行中");
                } else if (intstate < anInttimes) {
                    tabtv.setTextSize(16);
                    tabim.setTextColor(getActivity().getResources().getColor(R.color.divider));
                    tabtv.setTextColor(getActivity().getResources().getColor(R.color.divider));
                    tabim.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    tabim.setTextSize(15);
                    tabim.setText("即将开始");
                } else {
                    tabtv.setTextSize(16);
                    tabim.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    tabim.setTextColor(getActivity().getResources().getColor(R.color.divider));
                    tabtv.setTextColor(getActivity().getResources().getColor(R.color.divider));
                    tabim.setTextSize(15);
                    tabim.setText("已开抢");
                }


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        tabtv.setText(time);


        return newtab;
    }

    private void initView(View view) {

        TabLayout tabLayout = view.findViewById(R.id.m_tablayout);
        ViewPager vp_pager = view.findViewById(R.id.m_viewpager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        //判断当前几点中，就默认设置几点的秒杀
        SimpleDateFormat formatter = new SimpleDateFormat("HH");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);


        List<Fragment> mFragments = new ArrayList<>();
        final List<MsDataMo> list = MsDataMo.getMs();
        String[] title = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            fragment = new MsPager(0);
            fragment.setTabPos(i, list.get(i).getId());//设置第几页，以及每页的id
            mFragments.add(fragment);
            title[i] = list.get(i).getName();
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.getTabAt(i).setCustomView(tab_icon(list.get(i).getName(), str));
        }

        adapter = new TabLayoutFragmentPagerAdapter(getActivity().getSupportFragmentManager(), mFragments);
        vp_pager.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vp_pager));
        vp_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        //设置当前显示哪个标签页
        vp_pager.setCurrentItem(0);


        tabLayout.getTabAt(preDataNow(str)).select();


        ((MsPager) adapter.getItem(preDataNow(str))).sendMessage(0, "1");
        vp_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //滑动监听加载数据，一次只加载一个标签页                刷新标志     排序
                ((MsPager) adapter.getItem(position)).sendMessage(0, "1");

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                TextView tabtv = tab.getCustomView().findViewById(R.id.tab_time);
                TextView tabim = tab.getCustomView().findViewById(R.id.tab_state);
                tabtv.setTextSize(22);
                tabim.setTextSize(15);
                tabtv.setTextColor(getActivity().getResources().getColor(R.color.white));
                tabim.setTextColor(getActivity().getResources().getColor(R.color.white));
                tabim.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabtv = tab.getCustomView().findViewById(R.id.tab_time);
                TextView tabim = tab.getCustomView().findViewById(R.id.tab_state);
                tabtv.setTextSize(15);
                tabim.setTextSize(15);
                tabim.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tabtv.setTextColor(getActivity().getResources().getColor(R.color.divider));
                tabim.setTextColor(getActivity().getResources().getColor(R.color.divider));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private int preDataNow(String str) {
        switch (str) {
            case "00":
                return 0;
            case "01":
                return 1;
            case "02":
                return 2;
            case "03":
                return 3;
            case "04":
                return 4;
            case "05":
                return 5;
            case "06":
                return 6;
            case "07":
                return 7;
            case "08":
                return 8;
            case "09":
                return 9;
            case "10":
                return 10;
            case "11":
                return 11;
            case "12":
                return 12;
            case "13":
                return 13;
            case "14":
                return 14;
            case "15":
                return 15;
            case "16":
                return 16;
            case "17":
                return 17;
            case "18":
                return 18;
            case "19":
                return 19;
            case "20":
                return 20;
            case "21":
                return 21;
            case "22":
                return 22;
            case "23":
                return 23;
        }
        return 0;
    }
}
