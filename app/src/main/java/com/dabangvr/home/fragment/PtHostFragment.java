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

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.model.GoodsVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 拼团热卖排行
 */
public class PtHostFragment extends Fragment {

    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;
    private List<GoodsVo>goodsList = new ArrayList<>();
    private Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("tag", "onCreateView()方法执行");
        context = PtHostFragment.this.getContext();
        View view = inflater.inflate(R.layout.recy_demo, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.ms_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        adapter = new BaseLoadMoreHeaderAdapter<GoodsVo>(context,recyclerView,goodsList,R.layout.pt_success_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, GoodsVo goods) {
                //商品图片
                holder.setImageByUrl(R.id.pt_item_img,goods.getListUrl());
                //商品名称
                holder.setText(R.id.pt_item_msg,goods.getName());
                //市场价
                holder.setText(R.id.pt_item_yj,goods.getMarketPrice());
                //团购价
                holder.setText(R.id.pt_item_xj,goods.getGroupPrice());
                //团购人数
                //团购人的头像

            }
        };
        recyclerView.setAdapter(adapter);
    }
}
