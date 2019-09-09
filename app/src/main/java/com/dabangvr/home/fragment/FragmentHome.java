package com.dabangvr.home.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.CartActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.dep.activity.DepActivity;
import com.dabangvr.home.activity.HxClassActivity;
import com.dabangvr.home.activity.HxClassToActivity;
import com.dabangvr.home.activity.XsMsActivity;
import com.dabangvr.home.activity.NewReleaseActivity;
import com.dabangvr.home.activity.PtActivityType;
import com.dabangvr.home.activity.SearchActivity;
import com.dabangvr.home.interf.ChangeRadioButtonCallBack;
import com.dabangvr.main.MainActivity;
import com.dabangvr.model.MenuMo;
import com.dabangvr.model.TypeBean;
import com.dabangvr.my.activity.StartOpenShopActivity;
import com.dabangvr.util.BannerStart;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoginTipsDialog;
import com.example.mylibrary.ViewPagerForScrollView;
import com.youth.banner.Banner;

import org.apache.commons.lang.StringUtils;
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
import config.DyUrl;
import okhttp3.Call;

@SuppressLint("ValidFragment")
public class FragmentHome extends Fragment implements View.OnClickListener {

    private int tabPosition;
    private SwipeRefreshLayout refreshLayout;

    private RecyclerView topRecy;
    private BaseLoadMoreHeaderAdapter topAdapter;
    private List<ZBMain> topData = new ArrayList<>();

    //渠道列表
    private RecyclerView menuRecy;
    private BaseLoadMoreHeaderAdapter menuAdapter;
    private List<MenuMo> menuData = new ArrayList<>();

    //分类列表
    private RecyclerView typeRecy;
    private BaseLoadMoreHeaderAdapter typeAdapter;
    private List<TypeBean> typeData = new ArrayList<>();
    private TextView manyType;

    //动态
    private TabLayout tabLayout;
    private ViewPagerForScrollView vp_pager;
    private List<Fragment> mFragments;
    private SimpleFragmentPagerAdapter adapter;


    private Context context;

    private ChangeRadioButtonCallBack callBack;


    public static FragmentHome newInstance() {
        FragmentHome home = new FragmentHome();
//        Bundle args = new Bundle();
//        args.putInt("index", index);
//        home.setArguments(args);
        return home;
    }

    public FragmentHome() {
    }

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = FragmentHome.this.getContext();

        view = LayoutInflater.from(container.getContext()).
                inflate(R.layout.fg_home, container, false);
        initView();

        return view;
    }

    private void initView() {
        //下拉刷新
        refreshLayout = view.findViewById(R.id.swipe);
        refreshLayout.setColorSchemeResources(android.R.color.background_dark, android.R.color.background_light, android.R.color.black, android.R.color.darker_gray);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMenu();
                getType();
                refreshLayout.setRefreshing(false);
            }
        });

        //定位
        view.findViewById(R.id.home_type).setOnClickListener(this);

        //搜索
        view.findViewById(R.id.home_search).setOnClickListener(this);

        //购物车
        view.findViewById(R.id.h_cart).setOnClickListener(this);

        //顶部直播列表
        for (int i = 0; i < 10; i++) {
            topData.add(new ZBMain());
        }
        topRecy = view.findViewById(R.id.recy_top);
        LinearLayoutManager topManager = new LinearLayoutManager(context);
        topManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        topRecy.setLayoutManager(topManager);
        topRecy.setNestedScrollingEnabled(false);
        topAdapter = new BaseLoadMoreHeaderAdapter<ZBMain>(context, topRecy, topData, R.layout.home_top_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, ZBMain o) {

            }
        };
        topRecy.setAdapter(topAdapter);


        //渠道列表
        menuRecy = view.findViewById(R.id.recy_content);
        menuRecy.setNestedScrollingEnabled(false);
        GridLayoutManager menuManager = new GridLayoutManager(context, 5);
        menuRecy.setLayoutManager(menuManager);

        String str = MainActivity.getSPKEY(FragmentHome.this.getActivity(), "menuData");
        if (!StringUtils.isEmpty(str)) {
            menuData = JsonUtil.string2Obj(str, List.class, MenuMo.class);
            view.findViewById(R.id.menu_ll).setVisibility(View.VISIBLE);
        } else {
            getMenu();
        }
        menuAdapter = new BaseLoadMoreHeaderAdapter<MenuMo>(context, menuRecy, menuData, R.layout.item_channel_layout) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, MenuMo o) {
                holder.setImageByUrl(R.id.iv_img, o.getIconUrl());
                holder.setText(R.id.tv_name, o.getTitle());
            }
        };
        menuRecy.setAdapter(menuAdapter);
        setMenuOnclik();


        manyType = view.findViewById(R.id.many_type);
        manyType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manyType.setVisibility(View.GONE);
            }
        });

        //分类列表
        typeRecy = view.findViewById(R.id.recy_type);
        GridLayoutManager typeManager = new GridLayoutManager(context, 5);
        typeRecy.setLayoutManager(typeManager);
        typeRecy.setNestedScrollingEnabled(false);
        typeAdapter = new BaseLoadMoreHeaderAdapter<TypeBean>(context, typeRecy, typeData, R.layout.dep_type_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, TypeBean o) {
                holder.setImageByUrl(R.id.iv_img, o.getCategoryImg());
                holder.setText(R.id.tv_name, o.getName());
            }
        };
        typeRecy.setAdapter(typeAdapter);
        getType();
        setTypeOnclik();


        //轮播图
        Banner banner = view.findViewById(R.id.home_banner);
        BannerStart.starBanner(context, banner, "1");

        //商品
        tabLayout = view.findViewById(R.id.tablayout);
        vp_pager = (ViewPagerForScrollView) view.findViewById(R.id.tab_viewpager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mFragments = new ArrayList<>();

        setTabViewPager();

    }


    /**
     * 设置底部动态列表
     */
    private void setTabViewPager() {
        String[] ids = {"0", "1", "2"};
        String[] title = {"精选", "关注", "热门"};
        for (int i = 0; i < ids.length; i++) {
            HomeTabGoodsFragment fragment = new HomeTabGoodsFragment(0);
            fragment.setTabPos(i, ids[i]);//设置第几页，以及每页的id
            mFragments.add(fragment);
        }
        adapter = new SimpleFragmentPagerAdapter(getFragmentManager(), mFragments, title);
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
                tabPosition = position;
                ((HomeTabGoodsFragment) adapter.getItem(position)).sendMessage(0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 渠道点击事件
     */
    private void setMenuOnclik() {
        menuAdapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Class T = null;
                switch (menuData.get(position).getJumpUrl()) {
                    case "dsp":
                        LoginTipsDialog.ortehrTips(FragmentHome.this.getActivity(), "短视频功能维护中，敬请期待");
                        return;
                    case "hx":
                        T = HxClassActivity.class;
                        break;
                    case "pt":
                        T = PtActivityType.class;
                        break;
                    case "xsms":
                        T = XsMsActivity.class;
                        break;
                    case "zb":
                        if (callBack == null){
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
                    default:
                        T = HxClassToActivity.class;
                        break;
                }
                if (null != T) {
                    Intent intent = new Intent(context, T);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 分类点击事件
     */
    private void setTypeOnclik() {
        typeAdapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(FragmentHome.this.getContext(), HxClassToActivity.class);
                intent.putExtra("id", typeData.get(position).getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_type: {
                Intent intent = new Intent(FragmentHome.this.getContext(), HxClassActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.h_cart: {
                Intent intent = new Intent(context, CartActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.home_search: {
                Intent intent = new Intent(FragmentHome.this.getContext(), SearchActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.vedio_layout:
                callBack.change(1);
                break;
            default:
                break;
        }
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
                                MainActivity.setSPKEY(FragmentHome.this.getActivity(), "menuData", str);
                                menuData = JsonUtil.string2Obj(str, List.class, MenuMo.class);
                                menuAdapter.updateData(menuData);
                                view.findViewById(R.id.menu_ll).setVisibility(View.VISIBLE);
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
                                typeData = JsonUtil.string2Obj(str, List.class, TypeBean.class);
                                typeAdapter.updateData(typeData);
                                view.findViewById(R.id.menu_ll).setVisibility(View.VISIBLE);
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

}
