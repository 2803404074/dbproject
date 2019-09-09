package com.dabangvr.home.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.dabangvr.R;
import com.dabangvr.common.activity.CartActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.onLoadMoreListener;
import com.dabangvr.model.Goods;
import com.dabangvr.model.QCountry;
import com.dabangvr.util.BannerStart;
import com.dabangvr.util.JsonUtil;
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
import Utils.PdUtil;
import config.DyUrl;
import okhttp3.Call;

public class QQGActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private int page = 1;
    private boolean isFlush = true;
    private boolean justLoadCountryGoods = false;
    private BaseLoadMoreHeaderAdapter adapter;//全球购所有商品适配
    private BaseLoadMoreHeaderAdapter adapterCountry;//国家新品推荐商品适配
    private BaseLoadMoreHeaderAdapter Countyadapter;//国家列表适配器

    private List<QCountry.MyContry> staticList;//所有国家列表
    private List<Goods> listCountry = new ArrayList<>();//国家新品推荐列表
    private List<Goods> mData = new ArrayList<>();//所有商品列表

    private GridLayoutManager manager;
    private Banner banner;
    private int height;//轮播图的高度
    private boolean show = true;

    private PdUtil pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qqg);
        pd = new PdUtil(this);
        initView();
        setAdapter();
    }

    private void initView() {

        pd.showLoding("努力加载中。。。");
        //返回
        findViewById(R.id.back).setOnClickListener(this);

        //右上角菜单
        findViewById(R.id.mess).setOnClickListener(this);

        //轮播图
        banner = findViewById(R.id.ms_banner);
        BannerStart.starBanner(QQGActivity.this,banner,"5");

        //国家列表-静态
        RecyclerView countyRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        layoutmanager.setOrientation(LinearLayoutManager.HORIZONTAL);
        countyRecyclerView.setLayoutManager(layoutmanager);
        QCountry qCountry = new QCountry();
        staticList = qCountry.getContryData();
        Countyadapter = new BaseLoadMoreHeaderAdapter<QCountry.MyContry>(QQGActivity.this, countyRecyclerView, staticList, R.layout.country_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, QCountry.MyContry contry) {
                holder.setText(R.id.name, contry.getName());
                holder.setImageResource(R.id.img, contry.getCategoryImg());
            }
        };
        countyRecyclerView.setAdapter(Countyadapter);
        Countyadapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //国家类型点击
                isFlush = true;
                pd.showLoding("努力加载中");
                justLoadCountryGoods  = true;
                getData(staticList.get(position).getId());
            }
        });


        //所有商品列表
        recyclerView = findViewById(R.id.qqg_recy);
        manager = new GridLayoutManager(this, 2);//参数：列数，方向
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? manager.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(manager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {//向上滑动
                    if (show) {
                        performAnim2(height);
                        show = false;
                    }
                }

                if (recyclerView.canScrollVertically(-1)) {

                } else {
                    if (!show) {
                        performAnim2(height);
                        show = true;
                    }
                }
            }
        });

        //刷新控件
        refreshLayout = findViewById(R.id.swiperefreshlayout);

        //设置下拉时圆圈的颜色（可以尤多种颜色拼成）
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light);
        //设置下拉时圆圈的背景颜色（这里设置成白色）
        refreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//刷新
                page = 1;
                isFlush = true;
                justLoadCountryGoods = false;
                pd.showLoding("努力加载中");
                getData("-1");
            }
        });

//        //加载更多
        recyclerView.addOnScrollListener(new onLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                page += 1;
                isFlush = false;
                justLoadCountryGoods = false;
                getData("-1");
            }
        });
        getData("-1");
    }

    private void setAdapter() {

        //国家新品推荐商品适配
        View header = LayoutInflater.from(QQGActivity.this).inflate(R.layout.recy_demo, null);
        RecyclerView headRecy = header.findViewById(R.id.ms_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        headRecy.setLayoutManager(manager);
        adapterCountry = new BaseLoadMoreHeaderAdapter<Goods>(QQGActivity.this, headRecy, listCountry, R.layout.country_product_item) {

            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, Goods goods) {
                holder.setImageByUrl(R.id.img, goods.getListUrl());
                holder.setText(R.id.title, goods.getName());
                holder.setText(R.id.price, goods.getSellingPrice());
                holder.setText(R.id.address, "联系客服");
            }
        };
        headRecy.setAdapter(adapterCountry);
        adapterCountry.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(QQGActivity.this, HxxqLastActivity.class);
                intent.putExtra("id", listCountry.get(position).getId());
                intent.putExtra("type", 0);
                startActivity(intent);
            }
        });

        //所有商品适配
        adapter = new BaseLoadMoreHeaderAdapter<Goods>(QQGActivity.this, recyclerView, mData, R.layout.qqg_product_list) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, Goods goods) {
                holder.setImageByUrl(R.id.img, goods.getListUrl());
                holder.setText(R.id.msg, goods.getName());
            }
        };
        adapter.addHeadView(header);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(QQGActivity.this, HxxqLastActivity.class);
                intent.putExtra("id", mData.get(position).getId());
                intent.putExtra("type", 0);
                startActivity(intent);

            }
        });
    }

    /**
     * 统一获取数据
     *
     * @param
     * @param categoryId 国家id，点击国家的时候传
     */
    private void getData(String categoryId) {
        Map<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(page));
        map.put("limit", "10");
        map.put("categoryId", categoryId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGlobalList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")) {
                        JSONObject data = object.optJSONObject("data");
                        //轮播图数组
                        String array = data.optString("bannerList");

                        //国家列表,转做静态
//                        String cStr = data.optString("goodsCategoryList");
//                        List<QCountry> cList = JsonUtil.string2Obj(cStr, List.class, QCountry.class);

                        //国家商品列表
                        if (isFlush) {//刷新才需要加载推荐商品
                            String goodsCountry = data.optString("goodsList");
                            listCountry = JsonUtil.string2Obj(goodsCountry, List.class, Goods.class);
                            adapterCountry.updateData(listCountry);
                        }
                        if(justLoadCountryGoods){
                            pd.desLoding();
                            return ;
                        }


                        //全球购商品列表
                        String allGoods = data.optString("goodsLists");
                        List<Goods> list = JsonUtil.string2Obj(allGoods, List.class, Goods.class);

                        if (isFlush) {//刷新
                            mData = list;
                            adapter.updateData(mData);
                        } else {
                            if (list.size() > 0) {
                                adapter.addAll(list);
                            }
                        }
                        if (refreshLayout.isRefreshing()) {
                            refreshLayout.setRefreshing(false);
                        }
                        pd.desLoding();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back: {
                finish();
                break;
            }
            case R.id.mess: {
                //创建弹出式菜单对象（最低版本11）
                PopupMenu popup = new PopupMenu(QQGActivity.this, v);//第二个参数是绑定的那个view
                //获取菜单填充器
                MenuInflater inflater = popup.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.main_menu, popup.getMenu());
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(QQGActivity.this);
                //显示(这一行代码不要忘记了)
                popup.show();
                break;
            }
            default:
                break;
        }
    }

    private void performAnim2(int height) {
        //View是否显示的标志
        //属性动画对象
        ValueAnimator va;
        if (show) {
            //隐藏view，高度从height变为0
            va = ValueAnimator.ofInt(height, 0);
        } else {
            //显示view，高度从0变到height值
            va = ValueAnimator.ofInt(0, height);
        }
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int h = (Integer) valueAnimator.getAnimatedValue();
                //动态更新view的高度
                banner.getLayoutParams().height = h;
                banner.requestLayout();
            }
        });
        va.setDuration(500);
        //开始动画
        va.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Banner banner = findViewById(R.id.ms_banner);
        banner.post(new Runnable() {
            @Override
            public void run() {
                height = banner.getHeight();
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart: {
                Intent intent = new Intent(QQGActivity.this, CartActivity.class);
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