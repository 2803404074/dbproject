package com.dabangvr.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.CartActivity;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.home.fragment.HxFragment;
import com.dabangvr.model.TypeBean;
import com.dabangvr.util.JsonUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.DyUrl;
import okhttp3.Call;

/**
 * 海鲜跳转的页面
 */
public class HxActivityType extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private SimpleFragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private int position = 0;

    private int curTab = 0;
    private TextView hxTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_hx_type);
        initView();
    }

    private void initView() {
        position = getIntent().getIntExtra("position", 0);
        String title = getIntent().getStringExtra("title");
        mTabLayout = findViewById(R.id.m_tablayout);
        mViewPager = findViewById(R.id.m_viewpager);
        hxTitle = findViewById(R.id.hx_title);
        hxTitle.setText(title);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.mess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建弹出式菜单对象（最低版本11）
                PopupMenu popup = new PopupMenu(HxActivityType.this, v);//第二个参数是绑定的那个view
                //获取菜单填充器
                MenuInflater inflater = popup.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.main_menu, popup.getMenu());
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(HxActivityType.this);
                //显示(这一行代码不要忘记了)
                popup.show();

            }
        });
        getList();
    }

    private void getList() {
        String str = getIntent().getStringExtra("list");
        List<TypeBean> list = JsonUtil.string2Obj(str, List.class, TypeBean.class);
        try {
            if (null == list || list.size() == 0) {
                String id = getIntent().getStringExtra("id");
                Map<String, String> map = new HashMap<>();
                map.put("parentId", id);
                OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.CategoryList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
                    @Override
                    public void onUi(String s) {
                        if (!StringUtils.isEmpty(s)) {
                            try {
                                JSONObject object = new JSONObject(s);
                                if (object.optInt("errno") == 0) {
                                    JSONObject data = object.optJSONObject("data");
                                    String str = data.optString("goodsCategoryList");
                                    List<TypeBean> list = JsonUtil.string2Obj(str, List.class, TypeBean.class);
                                    setTab(list);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailed(Call call, IOException e) {

                    }
                });
            } else {
                setTab(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTab(List<TypeBean> list) {
        String[] tabTitle = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            HxFragment fragment = new HxFragment(position);
            fragment.setTabPos(i, list.get(i).getId());//设置第几页，以及每页的id
            mFragments.add(fragment);
            tabTitle[i] = list.get(i).getName();
        }
        mAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), mFragments, tabTitle);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
//       mTabLayout.setSmoothScrollingEnabled(true);
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
                ((HxFragment) mAdapter.getItem(position)).sendMessage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart: {
                Intent intent = new Intent(HxActivityType.this, CartActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.action_mess: {
                break;
            }
        }
        return false;
    }
}
