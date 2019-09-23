package com.dabangvr.my.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.home.fragment.NineMsPagerFragment;
import com.dabangvr.my.StatusBarUtil;
import com.dabangvr.my.fragment.MyYhjRceordPagerFragment;
import com.dabangvr.view.home.TabLayoutFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的优惠-历史记录
 */
public class MyYhjRecordActivity extends BaseNewActivity {

    @BindView(R.id.tab_viewpager)
    ViewPager vp_pager;
    @BindView(R.id.already_used)
    TextView already_used;
    @BindView(R.id.expired)
    TextView expired;
    private String[] tabString = {"0", "1"};
    private TabLayoutFragmentPagerAdapter adapter;
    private MyYhjRceordPagerFragment myYhjRceordPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            //解决Android5.0以上，状态栏设置颜色后变灰的问题
            StatusBarUtil.setTransparentForWindow(this);
        }
    }

    @Override
    public int setLayout() {
        return R.layout.activity_yhj_record;
    }

    @Override
    public void initView() {
        initFragment();
    }

    private void initFragment() {
        List<Fragment> mFragments = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            myYhjRceordPagerFragment = new MyYhjRceordPagerFragment(0);
            myYhjRceordPagerFragment.setTabPos(i, tabString[i]);//设置第几页，以及每页的id
            mFragments.add(myYhjRceordPagerFragment);

        }
        adapter = new TabLayoutFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        vp_pager.setAdapter(adapter);
        //设置当前显示哪个标签页
        vp_pager.setCurrentItem(0);
        already_used.setSelected(true);
        vp_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //滑动监听加载数据，一次只加载一个标签页                刷新标志     排序
                ((MyYhjRceordPagerFragment) adapter.getItem(position)).sendMessage(0, "1");
                setSelecl(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.back, R.id.already_used, R.id.expired})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.already_used:
                vp_pager.setCurrentItem(0);
                already_used.setSelected(true);
                expired.setSelected(false);
                break;
            case R.id.expired:
                vp_pager.setCurrentItem(1);
                already_used.setSelected(false);
                expired.setSelected(true);
                break;
        }
    }

    private void setSelecl(int i) {
        switch (i) {
            case 0:
                already_used.setSelected(true);
                expired.setSelected(false);

                break;
            case 1:
                already_used.setSelected(false);
                expired.setSelected(true);
                break;

        }

    }
}
