package com.dabangvr.dynamic.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dabangvr.R;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.dynamic.activity.DynamicActivity;
import com.dabangvr.dynamic.page.DynamicPage;
import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;


public class FragmentDynamic extends Fragment {

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private List<DynamicTypeMo> mData = new ArrayList<>();
    private SimpleFragmentPagerAdapter mAdapter;

    public static FragmentDynamic newInstance(int index) {
        FragmentDynamic dynamic = new FragmentDynamic();
        Bundle args = new Bundle();
        args.putInt("index", index);
        dynamic.setArguments(args);
        return dynamic;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dynamic, container, false);
        //发说说
        view.findViewById(R.id.iv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FragmentDynamic.this.getContext(), DynamicActivity.class);
                startActivity(intent);
            }
        });
        SlidingTabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        setTabPage(tabLayout,viewPager);
        return view;
    }

    private void setTabPage(SlidingTabLayout tabLayout ,ViewPager viewPager) {
        mData.add(new DynamicTypeMo(String.valueOf(0),"全部"));
        mData.add(new DynamicTypeMo(String.valueOf(1),"热门"));
        mData.add(new DynamicTypeMo(String.valueOf(2),"关注"));


        String tabTitle[] = new String[mData.size()];

        for (int i = 0; i < mData.size(); i++) {
            DynamicPage fragment = new DynamicPage(0);
            fragment.setTabPos(i,mData.get(i).getId());//设置第几页，以及每页的id
            mFragments.add(fragment);
            tabTitle[i] = mData.get(i).getName();
        }
        mAdapter = new SimpleFragmentPagerAdapter(getFragmentManager(), mFragments, tabTitle);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(0);
        tabLayout.setViewPager(viewPager, tabTitle, FragmentDynamic.this.getActivity(), mFragments);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //滑动监听加载数据，一次只加载一个标签页
                ((DynamicPage) mAdapter.getItem(position)).sendMessage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    private class DynamicTypeMo{
        private String id;
        private String name;

        public DynamicTypeMo(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public DynamicTypeMo() {
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
