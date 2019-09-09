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

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dabangvr.R;
import com.dabangvr.dep.activity.DepActivity;
import com.dabangvr.home.activity.two.RightBean;
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

import com.dabangvr.common.activity.BaseActivity;

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


public class MsActivity extends BaseActivity {

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
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        initView();
    }

    @Override
    public int setLayout() {
        return R.layout.ms_layout;
    }

    public void initView() {

        initRecylerViewChannel();
        initBanner();
        initTablayout();
    }

    @Override
    public void initData() {
        getMenu();
    }


    private void initTablayout() {

        String[] titles = new String[]{"点菜", "评价", "商家"};
        tabLayout.removeAllTabs();
        int leng = titles.length;
        for (int i = 0; i < leng; i++) {
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.getTabAt(i).setText(titles[i]);
        }

        int[] types = {0, 1, 2};
        mFragments = new ArrayList<>(leng);
        for (int j = 0; j < leng; j++) {
            HomeGoodsFragment homeGoodsFragment = new HomeGoodsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", types[j]);
            homeGoodsFragment.setArguments(bundle);
            mFragments.add(homeGoodsFragment);
        }

        mAdapter = new TabLayoutFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        viewPager.setAdapter(mAdapter);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }


    /**
     * 初始化渠道列表
     */
    private void initRecylerViewChannel() {
//        String str = MainActivity.getSPKEY(HomeFragment.this.getActivity(), "menuData");
//        if (!StringUtils.isEmpty(str)) {
//            menuData = JsonUtil.string2Obj(str, List.class, MenuMo.class);
////            view.findViewById(R.id.menu_ll).setVisibility(View.VISIBLE);
//        } else {
//        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5);
        rvChannel.setNestedScrollingEnabled(false);
        rvChannel.setLayoutManager(gridLayoutManager);
        channelAdapter = new ChannelAdapter(this);
        rvChannel.setAdapter(channelAdapter);
        channelAdapter.setData(menuData);
        channelAdapter.setOnItemClickListener(new ChannelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MenuMo menuMo = menuData.get(position);
                Class T = null;
                String jumpUrl = menuMo.getJumpUrl();
                switch (jumpUrl) {
                    case "dsp":
                        LoginTipsDialog.ortehrTips(MsActivity.this, "短视频功能维护中，敬请期待");
                        return;
                    case "hx":
                        // TODO: 2019/8/3 海鲜分类
                        T = HxActivity.class;
                        break;
                    case "pt":
                        T = PtActivityType.class;
                        break;
                    case "xsms":
                        T = XsMsActivity.class;
                        break;
                    case "zb":
                        if (callBack == null) {
                            callBack = new ChangeRadioButtonCallBack() {
                                @Override
                                public void change(int index) {
                                    callBack.change(1);
                                }
                            };
                        }
                        return;//跳转直播模块
                    case "xpsf":
                        T = NewReleaseActivity.class;
                        break;
                    case "md":
                        T = DepActivity.class;
                        break;
                    case "rz":
                        T = StartOpenShopActivity.class;
                        break;
                    case "ms":
                        T = MsActivity.class;
                        break;
                    default:
                        T = HxActivityType.class;
                        break;
                }

                if (null != T) {
                    Intent intent = new Intent(MsActivity.this, T);
                    startActivity(intent);
                }
            }
        });
    }


    /**
     * 初始化轮播图
     */

    private void initBanner() {
        BannerStart.starBanner(this, homeBanner, "1");
    }


    /**
     * 获取渠道列表
     */
    private void getMenu() {
        Map<String, String> map = new HashMap<>();
        map.put("mallSpeciesId", "1");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getChannelMenuList, map,
                new GsonObjectCallback<String>(DyUrl.BASE) {
                    //主线程处理
                    @Override
                    public void onUi(String msg) {
                        try {
                            JSONObject object = new JSONObject(msg);
                            int code = object.optInt("errno");
                            if (code == 0) {//成功
                                JSONObject data = object.optJSONObject("data");
                                String str = data.optString("channelMenuList");
                                menuData = JsonUtil.string2Obj(str, List.class, MenuMo.class);
                                channelAdapter.setData(menuData);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //请求失败
                    @Override
                    public void onFailed(Call call, IOException e) {
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                    }
                });
    }

    @OnClick({R.id.back,R.id.search_edit})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.search_edit:
                // TODO: 2019/8/3 美食搜索
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (homeBanner != null) {
            homeBanner.stopAutoPlay();
        }
    }



}
