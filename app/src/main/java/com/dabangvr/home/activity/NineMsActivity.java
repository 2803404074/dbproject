package com.dabangvr.home.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.home.fragment.NineMsFragment;
import com.dabangvr.home.fragment.NineMsPagerFragment;
import com.dabangvr.my.StatusBarUtil;
import com.dabangvr.view.home.TabLayoutFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class NineMsActivity extends BaseNewActivity implements View.OnClickListener {

    private ImageView title;

    private TextView tev_nine;
    private TextView tev_two_nine;
    private TextView tev_three_nine;
    private ViewPager vp_pager;
    private TabLayoutFragmentPagerAdapter adapter;
    private NineMsPagerFragment nineMsPagerFragment;
    private String[] tabString = {"9.9", "19.9", "39.9"};

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
        return R.layout.activity_nine_ms;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.ms_title);
        vp_pager = findViewById(R.id.m_viewpager);
        tev_nine = findViewById(R.id.tev_nine);
        tev_two_nine = findViewById(R.id.tev_two_nine);
        tev_three_nine = findViewById(R.id.tev_three_nine);
        tev_nine.setOnClickListener(this);
        tev_two_nine.setOnClickListener(this);
        tev_three_nine.setOnClickListener(this);
        tev_nine.setSelected(true);
        tev_two_nine.setSelected(false);
        tev_three_nine.setSelected(false);


        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initFragment();


    }

    private void initFragment() {
        List<Fragment> mFragments = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            nineMsPagerFragment = new NineMsPagerFragment(0);
            nineMsPagerFragment.setTabPos(i, tabString[i]);//设置第几页，以及每页的id
            mFragments.add(nineMsPagerFragment);

        }
        adapter = new TabLayoutFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
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
    public void initData() {

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
