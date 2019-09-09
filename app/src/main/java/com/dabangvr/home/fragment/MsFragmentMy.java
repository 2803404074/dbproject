package com.dabangvr.home.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.onLoadMoreListener;
import com.dabangvr.model.Goods;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.ToastUtil;

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


/**
 * 正在秒杀fragment
 */

public class MsFragmentMy extends Fragment implements View.OnClickListener {

    private Context context;
    private int page;
    //private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private List<Goods> list = new ArrayList<>();
    private BaseLoadMoreHeaderAdapter adapter;
    private SPUtils spUtils;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("tag", "onCreateView()方法执行");
        context = MsFragmentMy.this.getContext();
        spUtils = new SPUtils(context,"db_user");
        View view = inflater.inflate(R.layout.recy_demo, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        page = 1;
        //列表
        recyclerView = view.findViewById(R.id.ms_recycler_view);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(context);
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutmanager);

        adapter = new BaseLoadMoreHeaderAdapter<Goods>(context, recyclerView, list, R.layout.ms_now_list) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, final Goods goods) {
                TextView textView = holder.getView(R.id.ms_li_go);
                textView.setVisibility(View.GONE);
                holder.setText(R.id.ms_li_title, goods.getName());
                holder.setImageByUrl(R.id.ms_li_img, goods.getListUrl());
                holder.setText(R.id.ms_li_market, goods.getMarketPrice());
                holder.setText(R.id.ms_li_price, goods.getSellingPrice());
            }
        };
        //adapter.addHeadView(head);
        recyclerView.setAdapter(adapter);

//        //刷新控件
//        refreshLayout = view.findViewById(R.id.swiperefreshlayout);
//
//        //设置下拉时圆圈的颜色（可以尤多种颜色拼成）
//        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
//                android.R.color.holo_red_light,
//                android.R.color.holo_orange_light);
//        //设置下拉时圆圈的背景颜色（这里设置成白色）
//        refreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
//
//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {//刷新
//                page = 1;
//            }
//        });

//        //加载更多
        recyclerView.addOnScrollListener(new onLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                page += 1;

            }
        });
        setDate(0);

    }

    /**
     * map 参数：
     * buyType购买方式：1普通购买，2购物车，3拼团购买，4新品首发，5全球购，6秒杀（不传为查询全部）
     * orderState订单状态:
     */
    private void setDate(final int isFlush) {
        Map<String, String> map = new HashMap<>();
        String token = (String) spUtils.getkey("token","");
        map.put(DyUrl.TOKEN_NAME,token);
        map.put("buyType", "6");
        map.put("orderState","201");
        map.put("page", String.valueOf(page));
        map.put("limit", "10");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getOrderList, map,
                new GsonObjectCallback<String>(DyUrl.BASE) {
                    @Override
                    public void onUi(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            int errno = object.optInt("errno");
                            if (errno == 0) {
                                if(500 == object.optInt("code")){
                                    return ;
                                }
                                JSONObject dataObj = object.optJSONObject("data");
                                String str = dataObj.optString("goodsList");
                                list = JsonUtil.string2Obj(str, List.class, Goods.class);
                                if(null != list && list.size()>0){
                                    if (isFlush == 0) {//刷新
                                        adapter.updateData(list);
                                    } else {
                                        adapter.addAll(list);
                                    }
                                }
//                                if (refreshLayout.isRefreshing()) {
//                                    refreshLayout.setRefreshing(false);
//                                }
                            }else {
                                ToastUtil.showShort(context,object.optString("errmsg"));
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
        switch (v.getId()){

            default:break;
        }
    }
}
