package com.dabangvr.view.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.base.BaseFragment;
import com.dabangvr.home.activity.HxxqLastActivity;
import com.dabangvr.model.Goods;
import com.dabangvr.model.GoodsLabelBean;
import com.dabangvr.util.BannerStart;
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.widget.GridDividerItemDecoration;
import com.youth.banner.Banner;

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
import config.DyUrl;
import okhttp3.Call;

public class HomeGoodsFragment extends BaseFragment {

    @BindView(R.id.recycler_goods_id)
    RecyclerView recyclerViewGoods;

    @BindView(R.id.recycler_goods_label)
    RecyclerView recycler_goods_label;
    @BindView(R.id.goods_banner)
    Banner goods_banner;
    private int intPage = 1;

    private List<Goods> mData = new ArrayList<>();
    private HGoodsAdapter goodsAdapter;
    private HGoodsLabelAdapter goodsLabelAdapter;
    private List<GoodsLabelBean> mDataLabel = new ArrayList<>();
    private int type = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceState = getArguments();
        if (savedInstanceState != null) {
            type = savedInstanceState.getInt("type");
        }
    }
    @Override
    public int layoutId() {
        return R.layout.fragment_homegoods_layout;
    }
    @Override
    public void initView() {
        initRecycler();
        initBanner();
        initRecylabel();
    }
    @Override
    public void initData() {
        setDate(type, intPage);
        setLabelDate();
    }


    private void initRecylabel() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        recycler_goods_label.setLayoutManager(gridLayoutManager);
        recycler_goods_label.addItemDecoration(new GridDividerItemDecoration(DensityUtil.dip2px(getContext(), 10), ContextCompat.getColor(getContext(), R.color.color_00d8d6d6)));
        goodsLabelAdapter = new HGoodsLabelAdapter(getContext());
        recycler_goods_label.setAdapter(goodsLabelAdapter);
        goodsLabelAdapter.setOnItemClickListener(new HGoodsLabelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
    }

    private void initBanner() {
        BannerStart.starBanner(getContext(), goods_banner, "1");
    }

    private void setLabelDate() {
        String[] name={"鱼类","蟹类","贝类","果类","肉类","茶类","干货","更多"};
        int[]  colour={R.drawable.roun_label_bg_a,R.drawable.roun_label_bg_b,R.drawable.roun_label_bg_c,R.drawable.roun_label_bg_d,R.drawable.roun_label_bg_e,R.drawable.roun_label_bg_f,R.drawable.roun_label_bg_g,R.drawable.roun_label_bg_h};
        for (int i = 0; i < name.length; i++) {
            GoodsLabelBean goodsLabelBean = new GoodsLabelBean();
            goodsLabelBean.setName(name[i]);
            goodsLabelBean.setColour(colour[i]);
            mDataLabel.add(goodsLabelBean);
        }
        goodsLabelAdapter.setData(mDataLabel);
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
                Goods goods =  mData.get(position);
                Intent intent = new Intent(HomeGoodsFragment.this.getContext(), HxxqLastActivity.class);
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
                    Toast.makeText(getActivity(), "获取数据失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject object = new JSONObject(newsBean);
                    int err = object.optInt("errno");
                    if (err == 0) {
                        if (object.optInt("code") == 500) {
                            //ToastUtil.showShort(context, "服务数据更新中...");
                            return;
                        }
                        JSONObject data = object.optJSONObject("data");
                        String str = data.optString("goodsList");
//                        mData=JsonUtil.string2Obj(str, List.class, Goods.class);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        super.onDestroy();
        if (goods_banner != null) {
            goods_banner.stopAutoPlay();
        }
    }
}
