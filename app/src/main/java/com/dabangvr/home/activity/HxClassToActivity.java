package com.dabangvr.home.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.home.fragment.HxFragment;
import com.dabangvr.model.TypeBean;
import com.paradoxie.autoscrolltextview.VerticalTextview;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.OkHttp3Utils;
import Utils.TObjectCallback;
import butterknife.BindView;
import butterknife.OnClick;
import config.DyUrl;
import config.JsonUtil;

/**
 * 海鲜跳转的页面
 */
public class HxClassToActivity extends BaseNewActivity {

    @BindView(R.id.search_edit)
    VerticalTextview verticalTextview;

    @BindView(R.id.m_tablayout)
    TabLayout mTabLayout;

    @BindView(R.id.m_viewpager)
    ViewPager mViewPager;

    private ArrayList<String> titleList = new ArrayList<String>();//搜索框轮播文字数据

    private SimpleFragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();


    @Override
    public int setLayout() {
        return R.layout.activity_hx_class_to;
    }

    @OnClick({R.id.back})
    public void onTextClick(View view) {
        if (view.getId() == R.id.back) {
            finish();
        }
    }

    @Override
    public void initView() {
        titleList.add("新鲜海螺");
        titleList.add("出海收货");
        titleList.add("海鲜干货");
        titleList.add("9.9包邮");
        titleList.add("优惠活动");
        titleList.add("即使发货");
        titleList.add("海产品批发");
        titleList.add("海鲜干货");

        verticalTextview.setTextList(titleList);
        verticalTextview.setText(10, 2, Color.BLACK);//设置属性
        verticalTextview.setTextStillTime(3000);//设置停留时长间隔
        verticalTextview.setAnimTime(300);//设置进入和退出的时间间隔
        verticalTextview.setOnItemClickListener(new VerticalTextview.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent sintent = new Intent(getContext(), SearchActivity.class);
                startActivity(sintent);
            }
        });
        verticalTextview.startAutoScroll();
    }

    /**
     * 获取顶部分类
     */
    @Override
    public void initData() {
        //加载时动画,true 开启
        setLoaddingView(true);
        Map<String,Object>map = new HashMap<>();
        map.put("parentId",getIntent().getStringExtra("typeId"));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPostJson(DyUrl.getGoodsCategoryList, map, getToken(this), new TObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    String str = object.optString("goodsCategoryList");
                    List<TypeBean> list = JsonUtil.string2Obj(str,List.class,TypeBean.class);
                    //设置分类
                    setTab(list);
                    setLoaddingView(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailed(String msg) {

            }
        });
    }

    private void setTab(List<TypeBean> list) {
        String[] tabTitle = new String[list.size()];
        int position = 0;
        String tabName = "";
        if (!StringUtils.isEmpty(getIntent().getStringExtra("typeName"))){
            tabName =getIntent().getStringExtra("typeName");
        }

        for (int i = 0; i < list.size(); i++) {
            HxFragment fragment = new HxFragment();
            fragment.setTypeId(list.get(i).getId());//设置第几页，以及每页的id
            mFragments.add(fragment);
            tabTitle[i] = list.get(i).getName();

            if (tabName.equals(list.get(i).getName())){
                position = i;
            }
        }
        mAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), mFragments, tabTitle);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//超过长度可滑动
        //设置当前显示哪个标签页
        mViewPager.setCurrentItem(position);

    }
}
