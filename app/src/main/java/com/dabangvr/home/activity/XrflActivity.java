package com.dabangvr.home.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.model.CouponBean;
import com.dabangvr.model.Goods;
import com.dabangvr.my.StatusBarUtil;
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.view.home.HGoodsAdapter;
import com.dabangvr.widget.GridDividerItemDecoration;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import butterknife.OnClick;
import config.DyUrl;
import okhttp3.Call;
import top.androidman.SuperButton;


/**
 * MsActivity 美食页面
 * 2019、8、3
 */


public class XrflActivity extends BaseNewActivity {


    @BindView(R.id.xrfl_recy_discount)
    RecyclerView xrfl_recy_discount;

    @BindView(R.id.xrfl_recy_good)
    RecyclerView recyclerViewGoods;
    private BaseLoadMoreHeaderAdapter<CouponBean> adapter;
    private List<CouponBean> DiscontList = new ArrayList<>();
    private HGoodsAdapter goodsAdapter;
    private int intPage = 1;

    private List<Goods> mData = new ArrayList<>();
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            //解决Android5.0以上，状态栏设置颜色后变灰的问题
            StatusBarUtil.setTransparentForWindow(this);
            StatusBarUtil.setDarkMode(this);


        }
    }

    @Override
    public int setLayout() {
        return R.layout.xrfl_layout;
    }

    @Override
    public void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        xrfl_recy_discount.setLayoutManager(manager);
        adapter = new BaseLoadMoreHeaderAdapter<CouponBean>(this, xrfl_recy_discount, DiscontList, R.layout.xrfl_discount_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, CouponBean couponBean) {
                holder.setText(R.id.name, couponBean.getName());
                holder.setText(R.id.details, couponBean.getDetails());
                holder.setText(R.id.limit, couponBean.getLimit());
                holder.setText(R.id.limit_two, couponBean.getLimit_two());
                SuperButton linear_buy_state = holder.getView(R.id.linear_buy_state);
                String state = couponBean.getState();
                if (TextUtils.equals(state, "0")) {
                    linear_buy_state.setText("立即领取");
                } else {
                    linear_buy_state.setText("已领取");
                }
                linear_buy_state.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToastUtil.showShort(XrflActivity.this, "点击button").show();
                    }
                });
            }
        };
        xrfl_recy_discount.setAdapter(adapter);
        initRecycler();
    }


    @Override
    public void initData() {
        for (int i = 0; i < 4; i++) {
            CouponBean couponBean = new CouponBean();
            couponBean.setName("1000元优惠卷");
            couponBean.setDetails("全场满200元可立即使用");
            couponBean.setLimit("限时优惠");
            couponBean.setLimit_two("全场通用");
            if (i == 0) {

                couponBean.setState("1");
            } else {

                couponBean.setState("0");
            }
            DiscontList.add(couponBean);
        }
        setDate(0, intPage);

        rcyScroll();
    }

    private void rcyScroll() {

    }


    @OnClick({R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    /**
     * 初始化商品列表
     */
    private void initRecycler() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        recyclerViewGoods.setLayoutManager(gridLayoutManager);
        recyclerViewGoods.addItemDecoration(new GridDividerItemDecoration(DensityUtil.dip2px(getContext(), 7), ContextCompat.getColor(getContext(), R.color.color_00d8d6d6)));
        goodsAdapter = new HGoodsAdapter(getContext());
        recyclerViewGoods.setAdapter(goodsAdapter);
        goodsAdapter.setOnItemClickListener(new HGoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Goods goods = (Goods) mData.get(position);
                Intent intent = new Intent(XrflActivity.this, HxxqLastActivity.class);
                intent.putExtra("id", goods.getId());
                intent.putExtra("type", 0);
                startActivity(intent);
            }
        });
        //商品列表滑到底部后加载更多数据
        recyclerViewGoods.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    intPage++;
                    setDate(type, intPage);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    /**
     * 获取网络数据
     * <p>
     * 第一个item   my_orther_item_dep
     * 第二个item   my_orther_item_goods
     */
    private void setDate(int typex, int page) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(page));
        map.put("limit", "10");
        map.put("type", String.valueOf(typex));
        //ToastUtil.showShort(context,"加载的类型是:"+typex+","+type);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoodsLists, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            //主线程处理
            @Override
            public void onUi(String newsBean) {
                if (StringUtils.isEmpty(newsBean)) {
                    Toast.makeText(XrflActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject object = new JSONObject(newsBean);
                    int err = object.optInt("errno");
                    if (err == 0) {
                        if (object.optInt("code") == 500) {
                            return;
                        }
                        JSONObject data = object.optJSONObject("data");
                        String str = data.optString("goodsList");
                        Collection<? extends Goods> goods = (Collection<? extends Goods>) JsonUtil.string2Obj(str, List.class, Goods.class);
                        if (goods != null && goods.size() > 0) {
                            mData.addAll(goods);
                            goodsAdapter.setData(mData);
                        }

//                        adapter.updateData(mData);
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

}
