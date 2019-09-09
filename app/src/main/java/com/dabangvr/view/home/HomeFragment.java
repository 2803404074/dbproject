package com.dabangvr.view.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.base.BaseFragment;
import com.dabangvr.contens.ParameterContens;
import com.dabangvr.dep.activity.DepActivity;
import com.dabangvr.dynamic.page.DynamicPage;
import com.dabangvr.home.activity.HxClassActivity;
import com.dabangvr.home.activity.HxClassToActivity;
import com.dabangvr.home.activity.MsActivity;
import com.dabangvr.home.activity.NineMsActivity;
import com.dabangvr.home.activity.XsMsActivity;
import com.dabangvr.home.activity.NewReleaseActivity;
import com.dabangvr.home.activity.PtActivityType;
import com.dabangvr.home.interf.ChangeRadioButtonCallBack;
import com.dabangvr.model.MenuMo;
import com.dabangvr.model.TypeBean;
import com.dabangvr.my.activity.StartOpenShopActivity;
import com.dabangvr.util.BannerStart;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoginTipsDialog;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.video.adapter.ItemOnClickListener;
import com.dabangvr.video.utils.ToastUtils;
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
import config.DyUrl;
import okhttp3.Call;

/**
 * homeFragment 首页新版重构
 * 2019、6、24
 */


public class HomeFragment extends BaseFragment {

    @BindView(R.id.viewpager_id)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.recycler_view_id)
    RecyclerView recyclerView;
    @BindView(R.id.recy_content)
    RecyclerView rvChannel;
    @BindView(R.id.recy_type)
    RecyclerView recyclerType;
    @BindView(R.id.home_banner)
    Banner homeBanner;
    //@BindView(R.id.rl_more_id
    // RelativeLayout moreRelative;
    @BindView(R.id.bl_id)
    AppBarLayout appBarLayout;
    @BindView(R.id.iv_suspension)
    ImageView iv_suspension;
    @BindView(R.id.linear_layout)
    LinearLayout linear_layout;

    //    @BindView(R.id.content)
    //    WithBottomContentView  contentView;
    private TabLayoutFragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;
    private ChannelAdapter channelAdapter;
    private List<MenuMo> menuData = new ArrayList<>();  //渠道
    private ChangeRadioButtonCallBack callBack;
    private List<ZBMain> topData = new ArrayList<>();
    private TypeRVAdapter typeRVAdapter;
    private List<TypeBean> typeData = new ArrayList<>(); //分类
    private List<TypeBean> typeData1 = new ArrayList<>(); //分类
    private AddHeaderAdapter addHeaderAdapter;
    private int[] bgs = {R.mipmap.follow_noml, R.mipmap.shop_noml, R.mipmap.fire_noml, R.mipmap.find_noml};
    private int[] bgs1 = {R.mipmap.follow_selected, R.mipmap.shop_selected, R.mipmap.fire_selected, R.mipmap.find_selected};
    private ImageView iv;
    private int height;


    @Override
    public int layoutId() {
        return R.layout.fragment_home_layout;
    }

    @Override
    public void initView() {

        initAnchorRecyclerView();
        initRecylerViewChannel();
        initBanner();
        initTypeRecyclerView();
        initTablayout();
        ViewTreeObserver viewTreeObserver = linear_layout.getViewTreeObserver();
        viewTreeObserver.addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                height = linear_layout.getMeasuredHeight();
            }
        });

        try {
            Glide.with(this).load("http://image.vrzbgw.com/upload/20190808/03385165642ccc.png").into(iv_suspension);
        } catch (Exception e) {
            iv_suspension.setVisibility(View.GONE);
            e.printStackTrace();
        }
        iv_suspension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showShort(getContext(), "该功能正在努力升级中，敬请期待!");
            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (i < -height + 1) {
                    tabLayout.setBackgroundResource(R.drawable.roun_button_tabbg);
                } else {
                    tabLayout.setBackgroundResource(R.drawable.roun_tabbg);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void initData() {
        getMenu();
        getType();

    }


    private void initTablayout() {

        tabLayout.removeAllTabs();
        int leng = 4;
        for (int i = 0; i < 4; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.main_top_layout, null);
            iv = (ImageView) view.findViewById(R.id.main_tv);
            iv.setImageResource(bgs[i]);
//            view.setPadding(20,0,20,0);
            tab.setCustomView(view);
            if (i == 0) {
                iv.setImageResource(bgs1[0]);
            }
            tabLayout.addTab(tab);
        }

        int[] types = {0, 1, 2, 3};
        mFragments = new ArrayList<>(leng);
        for (int j = 0; j < leng; j++) {
            if (j == types[0]) {
                HomeZBragment homeZBragment = new HomeZBragment();
                Bundle bundle = new Bundle();
                bundle.putInt("type", types[j]);
                homeZBragment.setArguments(bundle);
                mFragments.add(homeZBragment);
            } else if (j == types[2]) {
                HomeZBragment homeZBragment = new HomeZBragment();
                Bundle bundle = new Bundle();
                bundle.putInt("type", types[j]);
                homeZBragment.setArguments(bundle);
                mFragments.add(homeZBragment);
            } else if (j == types[3]) {
                DynamicPage fragment = new DynamicPage(0);
                fragment.setTabPos(2, "关注");//设置第几页，以及每页的id
                mFragments.add(fragment);
            } else {
                HomeGoodsFragment homeGoodsFragment = new HomeGoodsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("type", types[j]);
                homeGoodsFragment.setArguments(bundle);
                mFragments.add(homeGoodsFragment);
            }


        }

        mAdapter = new TabLayoutFragmentPagerAdapter(getChildFragmentManager(), mFragments);
        viewPager.setAdapter(mAdapter);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ImageView viewById = tab.getCustomView().findViewById(R.id.main_tv);
                viewById.setImageResource(bgs1[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.main_tv).setFocusable(false);
                ImageView viewById = tab.getCustomView().findViewById(R.id.main_tv);
                viewById.setImageResource(bgs[tab.getPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    /**
     * 初始化主播列表头像列表
     */
    private void initAnchorRecyclerView() {
        String arrurl[] = {"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565298698598&di=54fd66fb65702697d4cdf82ae529f81f&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201612%2F12%2F20161212061637_WUAdF.jpeg"
                , "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565893460&di=5fd735d05598db423a79f6c0529575f4&imgtype=jpg&er=1&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fitem%2F201411%2F04%2F20141104214413_ivUjv.jpeg"
                , "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3951107419,1294462328&fm=26&gp=0.jpg"
                , "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=20577780,3476556477&fm=26&gp=0.jpg"
                , "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565298790094&di=0766b04dfa2b3adb3d04c5168336583f&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201611%2F27%2F20161127223627_UhKxQ.jpeg"
                , "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565298698598&di=54fd66fb65702697d4cdf82ae529f81f&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201612%2F12%2F20161212061637_WUAdF.jpeg"
                , "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565893460&di=5fd735d05598db423a79f6c0529575f4&imgtype=jpg&er=1&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fitem%2F201411%2F04%2F20141104214413_ivUjv.jpeg"
                , "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3951107419,1294462328&fm=26&gp=0.jpg"
                , "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=20577780,3476556477&fm=26&gp=0.jpg"
                , "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565298790094&di=0766b04dfa2b3adb3d04c5168336583f&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201611%2F27%2F20161127223627_UhKxQ.jpeg"};
        for (int i = 0; i < 10; i++) {
            ZBMain zbMain = new ZBMain();
            zbMain.setHeadUrl(arrurl[i]);
            zbMain.setAnchorName("飘飘" + 1);
            if (i == 0) {
                zbMain.setRoundurl(R.mipmap.rounda);
            } else if (i == 1) {
                zbMain.setRoundurl(R.mipmap.roundb);
            } else {
                zbMain.setRoundurl(R.mipmap.roundc);
            }

            topData.add(zbMain);
        }


        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        addHeaderAdapter = new AddHeaderAdapter(topData, getContext());
        View inflate = View.inflate(getActivity(), R.layout.item_header_layout, null);
        ImageView imageView = inflate.findViewById(R.id.v_user_hean);
        Glide.with(this).load(R.mipmap.gif_zb).into(imageView);
        addHeaderAdapter.addHeaderView(inflate);
        recyclerView.addItemDecoration(new SpacesItemDecoration(0));
        recyclerView.setAdapter(addHeaderAdapter);

        addHeaderAdapter.setItemOnClickListener(new ItemOnClickListener() {
            @Override
            public void onClickListener(int position) {
                Log.d("luhuas", "主页主播头像点击onClickListener: " + position);
            }
        });
        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("luhuas", "主页主播头像点击onClickListener:进入直播 ");
            }
        });
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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        rvChannel.setNestedScrollingEnabled(false);
        rvChannel.setLayoutManager(gridLayoutManager);
        channelAdapter = new ChannelAdapter(getContext());
        rvChannel.setAdapter(channelAdapter);
        channelAdapter.setData(menuData);
        channelAdapter.setOnItemClickListener(new ChannelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MenuMo menuMo = menuData.get(position);
                Class T = null;
                String jumpUrl = menuMo.getJumpUrl();
                Log.d("luhuas", "onItemClick: " + jumpUrl);
                switch (jumpUrl) {
                    case ParameterContens.DSP:
                        LoginTipsDialog.ortehrTips(HomeFragment.this.getActivity(), "短视频功能维护中，敬请期待");
                        return;
                    case ParameterContens.HXFL:
                        // TODO: 2019/8/3 海鲜分类
                        T = HxClassActivity.class;
                        break;
                    case ParameterContens.PT:  //拼团
                        T = PtActivityType.class;
                        break;
                    case ParameterContens.XSMS: //限时秒杀
                        T = XsMsActivity.class;
                        break;
                    case ParameterContens.NINE_NINE: //9.9抢购
                        T = NineMsActivity.class;
                        break;

                    case ParameterContens.QDHB: //签到红包
                        T = XsMsActivity.class;
                        break;
                    case ParameterContens.ZB:  //直播
                        if (callBack == null) {
                            callBack = new ChangeRadioButtonCallBack() {
                                @Override
                                public void change(int index) {
                                    callBack.change(1);
                                }
                            };
                        }
                        return;//跳转直播模块
                    case ParameterContens.xpsf:  //新品首发
                        T = NewReleaseActivity.class;
                        break;
                    case ParameterContens.WM: //外卖
                        T = DepActivity.class;
                        break;
                    case ParameterContens.SJRZ: // 商家入驻
                        T = StartOpenShopActivity.class;
                        break;
                    case ParameterContens.MS:  //美食
                        T = MsActivity.class;
                        break;
                    default:
                        T = HxClassToActivity.class;
                        break;
                }

                if (null != T) {
                    Intent intent = new Intent(getContext(), T);
                    startActivity(intent);
                }
            }
        });
    }


    /**
     * 初始化分类
     */
    private void initTypeRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        recyclerType.setNestedScrollingEnabled(false);
        recyclerType.setLayoutManager(gridLayoutManager);
        typeRVAdapter = new TypeRVAdapter(getContext());
        recyclerType.setAdapter(typeRVAdapter);
        typeRVAdapter.setOnItemClickListener(new TypeRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (TextUtils.equals(typeData.get(position).getJumpUrl(), ParameterContens.HXFL)) {
                    Intent intent = new Intent(getContext(), HxClassActivity.class);
                    startActivity(intent);
                } else if (TextUtils.equals(typeData.get(position).getJumpUrl(), ParameterContens.TTTEJ)) {
                    ToastUtils.s(getContext(), "功能正在完善中");
                } else if (TextUtils.equals(typeData.get(position).getJumpUrl(), ParameterContens.QBFL)) {
                    Intent intent = new Intent(getContext(), HxClassActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomeFragment.this.getContext(), HxClassToActivity.class);
                    intent.putExtra("id", typeData.get(position).getId());
                    intent.putExtra("title", typeData.get(position).getName());
                    startActivity(intent);
                }

            }
        });
    }


    /**
     * 初始化轮播图
     */

    private void initBanner() {
        BannerStart.starBanner(getContext(), homeBanner, "1");
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
//                                MainActivity.setSPKEY(HomeFragment.this.getActivity(), "menuData", str);
                                List<MenuMo> list = JsonUtil.string2Obj(str, List.class, MenuMo.class);
                                for (int i = 0; i < list.size(); i++) {
                                    if (TextUtils.equals(list.get(i).getJumpUrl(), ParameterContens.HXFL) || (TextUtils.equals(list.get(i).getJumpUrl(), ParameterContens.TTTEJ)) || (TextUtils.equals(list.get(i).getJumpUrl(), ParameterContens.QBFL))) {
                                        TypeBean typeBean = new TypeBean();
                                        typeBean.setJumpUrl(list.get(i).getJumpUrl());
                                        typeBean.setName(list.get(i).getTitle());
                                        typeBean.setCategoryImg(list.get(i).getIconUrl());
                                        typeData1.add(typeBean);
                                    } else {
                                        menuData.add(list.get(i));
                                    }
                                }

                                channelAdapter.setData(menuData);
//                                menuAdapter.updateData(menuData);
//                                view.findViewById(R.id.menu_ll).setVisibility(View.VISIBLE);
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

    /**
     * 获取分类列表
     */
    private void getType() {
        Map<String, String> map = new HashMap<>();
        map.put("parentId", "1");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoodsCategoryList, map,
                new GsonObjectCallback<String>(DyUrl.BASE) {
                    //主线程处理
                    @Override
                    public void onUi(String msg) {
                        try {
                            JSONObject object = new JSONObject(msg);
                            int code = object.optInt("errno");
                            if (code == 0) {//成功
                                JSONObject data = object.optJSONObject("data");
                                String str = data.optString("goodsCategoryList");
                                List<TypeBean> list1 = JsonUtil.string2Obj(str, List.class, TypeBean.class);
                                List<TypeBean> list = new ArrayList<>();
                                for (int i = 0; i < 8; i++) {
                                    list.add(list1.get(i));
                                }
                                for (int j = 0; j < list.size(); j++) {

                                    typeData.add(list.get(j));
                                }
                                if (typeData1 != null && typeData1.size() > 0) {
                                    for (int i = 0; i < typeData1.size(); i++) {
                                        if (TextUtils.equals(typeData1.get(i).getJumpUrl(), ParameterContens.TTTEJ)) {
                                            typeData.add(typeData1.get(i));
                                        }
                                    }
                                }
                                if (typeData1 != null && typeData1.size() > 0) {
                                    for (int i = 0; i < typeData1.size(); i++) {
                                        if (TextUtils.equals(typeData1.get(i).getJumpUrl(), ParameterContens.QBFL)) {
                                            typeData.add(typeData1.get(i));
                                        }
                                    }
                                }

                                typeRVAdapter.setData(typeData);
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (homeBanner != null) {
            homeBanner.stopAutoPlay();
        }
    }
}
