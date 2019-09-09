package com.dabangvr.lbroadcast.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.home.activity.HxActivity;
import com.dabangvr.lbroadcast.fragment.HxFragmentCheck;
import com.dabangvr.model.CheckMo;
import com.dabangvr.model.TypeBean;
import com.dabangvr.util.JsonUtil;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播选择商品
 */
public class HxActivityCheck extends AppCompatActivity implements HxFragmentCheck.OnSelect, View.OnClickListener {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private SimpleFragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private int position = 0;

    private TextView tvGoodsNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_hx_check);
        initView();
    }

    private void initView() {
        String str = getIntent().getStringExtra("list");
        position = getIntent().getIntExtra("position", 0);
        List<TypeBean> list = JsonUtil.string2Obj(str, List.class, TypeBean.class);

        //获取已经选过的商品
        String data = getIntent().getStringExtra("data");
        if (!StringUtils.isEmpty(data)) {
            checkList = JsonUtil.string2Obj(data, List.class, CheckMo.class);
        }
        mTabLayout = findViewById(R.id.m_tablayout);
        mViewPager = findViewById(R.id.m_viewpager);
        setTab(list);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HxActivityCheck.this, HxActivityChoose.class);
                intent.putExtra("data", JsonUtil.obj2String(checkList));
                setResult(88, intent);
                finish();
            }
        });
        tvGoodsNum = findViewById(R.id.check_goods_num);
        findViewById(R.id.check_goods).setOnClickListener(this);

        if (null != checkList)
            tvGoodsNum.setText("(" + checkList.size() + ")");
    }

    private void setTab(List<TypeBean> list) {
        String[] tabTitle = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            HxFragmentCheck fragment = new HxFragmentCheck(position);
            fragment.setOnSelect(this, checkList);
            fragment.setTabPos(i, list.get(i).getId());//设置第几页，以及每页的id
            mFragments.add(fragment);
            tabTitle[i] = list.get(i).getName();
        }
        mAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), mFragments, tabTitle);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
//        mTabLayout.setSmoothScrollingEnabled(true);
//        //mTabLayout.setTabMode(TabLayout.MODE_FIXED);//全部放下，不滑动
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//超过长度可滑动
        //设置当前显示哪个标签页
        mViewPager.setCurrentItem(position);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //滑动监听加载数据，一次只加载一个标签页
                ((HxFragmentCheck) mAdapter.getItem(position)).sendMessage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void select(String goodsId, String listUrl, String name, String price) {
        if (null == checkList) {
            checkList = new ArrayList<>();
        }
        checkList.add(new CheckMo(goodsId, listUrl, name, price));
        tvGoodsNum.setText("(" + checkList.size() + ")");
    }

    private List<CheckMo> checkList;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_goods:
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
                View view = View.inflate(getApplication(), R.layout.recy_demo_check, null);
                RecyclerView recyclerView = view.findViewById(R.id.ms_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                BaseLoadMoreHeaderAdapter adapter = new BaseLoadMoreHeaderAdapter<CheckMo>(
                        HxActivityCheck.this, recyclerView, checkList, R.layout.sc_recy_item) {
                    @Override
                    public void convert(Context mContext, BaseRecyclerHolder holder, CheckMo o) {
                        holder.setImageByUrl(R.id.sc_img, o.getListUrl());
                        holder.setText(R.id.sc_title, o.getName());
                        holder.setText(R.id.sc_price, o.getPrice());
                    }
                };
                recyclerView.setAdapter(adapter);

                builder.setView(view);
                final AlertDialog dialog = builder.create();
                view.findViewById(R.id.keep_check).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                view.findViewById(R.id.check_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HxActivityCheck.this, HxActivityChoose.class);
                        intent.putExtra("data", JsonUtil.obj2String(checkList));
                        setResult(88, intent);
                        finish();
                    }
                });
                dialog.show();
                //弹窗
                break;
            default:
                break;
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HxActivityCheck.this, HxActivityChoose.class);
        intent.putExtra("data", JsonUtil.obj2String(checkList));
        setResult(88, intent);
        finish();
    }
}
