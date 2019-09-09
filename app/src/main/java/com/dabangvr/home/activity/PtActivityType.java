package com.dabangvr.home.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.home.fragment.HomeTabGoodsFragment;
import com.dabangvr.home.fragment.PtFragment;
import com.dabangvr.model.TypeBean;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.StatusBarUtil;
import com.example.mylibrary.ViewPagerForScrollView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.DyUrl;
import okhttp3.Call;

public class PtActivityType extends BaseActivity{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SimpleFragmentPagerAdapter adapter;
    private List<Fragment> mFragments;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_pt_type;
    }

    @Override
    protected void initView() {
       findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
           }
       });
       tabLayout = findViewById(R.id.tablayout);
       viewPager = findViewById(R.id.tab_viewpager);

    }

    @Override
    protected void initData() {
        Map<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME,getSPKEY(this,"token"));
        map.put("parentId", "1");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.CategoryList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            //主线程处理
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")){
                        if (500 == object.optInt("code"))return;

                        JSONObject data = object.optJSONObject("data");
                        String str = data.optString("goodsCategoryList");
                        List<TypeBean> mData = JsonUtil.string2Obj(str,List.class,TypeBean.class);
                        mData.add(0,new TypeBean("","推荐"));
                        createTab(mData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //请求失败
            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }

    private void createTab(List<TypeBean> mData) {
        if (null == mFragments) mFragments = new ArrayList<>();
        String[] title = new String[mData.size()];
        for (int i = 0; i < mData.size(); i++) {
            PtFragment fragment = new PtFragment(0);
            fragment.setTabPos(i, mData.get(i).getId());//设置第几页，以及每页的id
            title[i] = mData.get(i).getName();
            mFragments.add(fragment);
        }
        adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), mFragments, title);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //设置当前显示哪个标签页
        viewPager.setCurrentItem(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                ((PtFragment) adapter.getItem(position)).sendMessage(false);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
