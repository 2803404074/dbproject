package com.dabangvr.view.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


import com.dabangvr.R;
import com.dabangvr.base.BaseFragment;
import com.dabangvr.model.Goods;
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.widget.GridDividerItemDecoration;
import java.util.ArrayList;

import java.util.List;

import butterknife.BindView;


public class HomeZBragment extends BaseFragment {

    @BindView(R.id.recycler_goods_id)
    RecyclerView recyclerViewGoods;
    private int intPage = 1;

    private HomeHotAdapter hotAdapter;
    private List<Goods> mData = new ArrayList<>();
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
        return R.layout.fragment_homehot_layout;
    }

    @Override
    public void initView() {
        initRecycler();

    }

    @Override
    public void initData() {
        setDate(type, intPage);
    }

    /**
     * 初始化商品列表
     */
    private void initRecycler() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        recyclerViewGoods.setLayoutManager(gridLayoutManager);
        recyclerViewGoods.addItemDecoration(new GridDividerItemDecoration(DensityUtil.dip2px(getContext(), 7), ContextCompat.getColor(getContext(), R.color.color_00d8d6d6)));
        hotAdapter = new HomeHotAdapter(getContext());
        recyclerViewGoods.setAdapter(hotAdapter);

        hotAdapter.setOnItemClickListener(new HomeHotAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // TODO: 2019/8/18 点击item
                ToastUtil.showShort(getActivity(),"点击"+position).show();
                Log.d("luhuas","点击："+position);
            }
        });
        //商品列表滑到底部后加载更多数据
        recyclerViewGoods.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    // TODO: 2019/8/18  加载更多
//                    intPage++;
//                    setDate(type, intPage);
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
        for (int i = 0; i < 9; i++) {
            Goods goods = new Goods();
            goods.setId("222");
            goods.setListUrl("http://5b0988e595225.cdn.sohucs.com/images/20180805/0ca1f0922bb4482daed2e108353239af.jpeg");
            mData.add(goods);
        }
        hotAdapter.setData(mData);
//        final HashMap<String, String> map = new HashMap<>();
//        map.put("page", String.valueOf(page));
//        map.put("limit", "10");
//        map.put("type", String.valueOf(typex));
//        //ToastUtil.showShort(context,"加载的类型是:"+typex+","+type);
//        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoodsLists, map, new GsonObjectCallback<String>(DyUrl.BASE) {
//            //主线程处理
//            @Override
//            public void onUi(String newsBean) {
//                if (StringUtils.isEmpty(newsBean)) {
//                    Toast.makeText(getActivity(), "获取数据失败", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                try {
//                    JSONObject object = new JSONObject(newsBean);
//                    int err = object.optInt("errno");
//                    if (err == 0) {
//                        if (object.optInt("code") == 500) {
//                            //ToastUtil.showShort(context, "服务数据更新中...");
//                            return;
//                        }
//                        JSONObject data = object.optJSONObject("data");
//                        String str = data.optString("goodsList");
////                        mData=JsonUtil.string2Obj(str, List.class, Goods.class);
//                        Collection<? extends Goods> goods = (Collection<? extends Goods>) JsonUtil.string2Obj(str, List.class, Goods.class);
//                        if (goods != null && goods.size() > 0) {
//                            mData.addAll(goods);
//                            hotAdapter.setData(mData);
//                        }
//
////                        adapter.updateData(mData);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            //请求失败
//            @Override
//            public void onFailed(Call call, IOException e) {
//
//            }
//        });
    }


}
