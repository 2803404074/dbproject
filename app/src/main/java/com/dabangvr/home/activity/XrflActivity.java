package com.dabangvr.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.dep.activity.DepActivity;
import com.dabangvr.home.interf.ChangeRadioButtonCallBack;
import com.dabangvr.model.MenuMo;
import com.dabangvr.model.TypeBean;
import com.dabangvr.my.activity.StartOpenShopActivity;
import com.dabangvr.util.BannerStart;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoginTipsDialog;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.view.home.ChannelAdapter;
import com.dabangvr.view.home.HomeGoodsFragment;
import com.dabangvr.view.home.LiveBroadcastAdapter;
import com.dabangvr.view.home.TabLayoutFragmentPagerAdapter;
import com.dabangvr.view.home.TypeRVAdapter;
import com.youth.banner.Banner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import bean.ZBMain;
import butterknife.BindView;
import butterknife.OnClick;
import config.DyUrl;
import okhttp3.Call;


/**
 * MsActivity 美食页面
 * 2019、8、3
 */


public class XrflActivity extends BaseNewActivity {

    @BindView(R.id.viewpager_id)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.recy_content)
    RecyclerView rvChannel;
    @BindView(R.id.recy_type)
    RecyclerView recyclerType;
    @BindView(R.id.home_banner)
    Banner homeBanner;

    @BindView(R.id.bl_id)
    AppBarLayout appBarLayout;


    private LiveBroadcastAdapter liveBroadcastAdapter;
    private TabLayoutFragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;
    private ChannelAdapter channelAdapter;

    private List<MenuMo> menuData = new ArrayList<>();  //渠道
    private ChangeRadioButtonCallBack callBack;
    private List<ZBMain> topData = new ArrayList<>();
    private TypeRVAdapter typeRVAdapter;
    private List<TypeBean> typeData = new ArrayList<>(); //分类


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
    }

    @Override
    public void initView() {

    }

    @Override
    public int setLayout() {
        return R.layout.xrfl_layout;
    }


    @Override
    public void initData() {
    }


    @OnClick({})
    public void onClick(View view) {
        switch (view.getId()){

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
