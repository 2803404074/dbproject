package com.dabangvr.my.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.model.OrtherStatic;
import com.dabangvr.model.TabAndViewPagerMo;
import com.dabangvr.my.fragment.MyOrtherPageFragment;
import com.dabangvr.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人中心-我的订单
 */
public class MyOrtherActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager vp_pager;
    private List<Fragment> mFragments;
    private SimpleFragmentPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_my_orther;
    }

    @Override
    protected void initView() {
        findViewById(R.id.backe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tabLayout = findViewById(R.id.tablayout);
        vp_pager = (ViewPager) findViewById(R.id.tab_viewpager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mFragments = new ArrayList<>();
    }

    @Override
    protected void initData() {
        List<TabAndViewPagerMo> list = OrtherStatic.setData();
        String[] title = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            MyOrtherPageFragment fragment = new MyOrtherPageFragment(0);
            fragment.setTabPos(i, list.get(i).getId());//设置第几页，以及每页的id
            mFragments.add(fragment);
            title[i] = list.get(i).getTitle();
        }
        adapter = new SimpleFragmentPagerAdapter(this.getSupportFragmentManager(), mFragments, title);
        vp_pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(vp_pager);
        //设置当前显示哪个标签页
        vp_pager.setCurrentItem(0);

        vp_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //滑动监听加载数据，一次只加载一个标签页
                ((MyOrtherPageFragment) adapter.getItem(position)).sendMessage(0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
