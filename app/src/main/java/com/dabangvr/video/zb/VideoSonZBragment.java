package com.dabangvr.video.zb;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.dabangvr.R;
import com.dabangvr.base.BaseFragment;
import com.dabangvr.main.WellcomActivity;
import com.dabangvr.model.Goods;
import com.dabangvr.model.ZhiboMo;
import com.dabangvr.util.BannerStart;
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils2;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.video.adapter.BGANormalRefreshViewHolder;
import com.dabangvr.video.adapter.ItemOnClickListener;
import com.dabangvr.video.adapter.ThreadUtil;
import com.dabangvr.video.fragment.model.PlayMode;
import com.dabangvr.view.home.SpacesItemDecoration;
import com.dabangvr.widget.GridDividerItemDecoration;
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
import butterknife.BindView;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import config.DyUrl;
import okhttp3.Call;


public class VideoSonZBragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    @BindView(R.id.recycler_goods_id)
    RecyclerView recyclerViewGoods;
    @BindView(R.id.refreshLayout)
    BGARefreshLayout mRefreshLayout;
    private int intPage = 1;

    private VideoHeaderAdapter videoHeaderAdapter;
    private List<ZhiboMo> mData = new ArrayList<>();

    private String type;
    private Banner homeBanner;
    private int mNewPageNumber = 0;
    private int mMorePageNumber = 1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceState = getArguments();
        if (savedInstanceState != null) {
            type = savedInstanceState.getString("type");
        }
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_vieo_son_layout;
    }

    @Override
    public void initView() {
        BGANormalRefreshViewHolder moocStyleRefreshViewHolder = new BGANormalRefreshViewHolder(getActivity(), true);
        moocStyleRefreshViewHolder.setRefreshLayout(mRefreshLayout);
        mRefreshLayout.setRefreshViewHolder(moocStyleRefreshViewHolder);
        initRecycler();

    }

    @Override
    public void initData() {
        mRefreshLayout.setDelegate(this);
        setDate(intPage, type, true);
        Log.d("luhuas", "initData: " + type);
        initBanner();

    }

    /**
     * 初始化轮播图
     */

    private void initBanner() {
        BannerStart.starBanner(getContext(), homeBanner, "1");
    }

    /**
     * 初始化主播列表
     */
    private void initRecycler() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewGoods.setLayoutManager(gridLayoutManager);
        videoHeaderAdapter = new VideoHeaderAdapter(mData, getContext());
        View inflate = View.inflate(getContext(), R.layout.item_good_header_layout, null);
        homeBanner = inflate.findViewById(R.id.home_banner);
        videoHeaderAdapter.addHeaderView(inflate);
        recyclerViewGoods.addItemDecoration(new GridDividerItemDecoration(DensityUtil.dip2px(getContext(), 7), ContextCompat.getColor(getContext(), R.color.color_00d8d6d6)));
        recyclerViewGoods.setAdapter(videoHeaderAdapter);
        videoHeaderAdapter.setItemOnClickListener(new ItemOnClickListener() {
            @Override
            public void onClickListener(int position) {
                Log.d("luhuas", "onClickListener点击主播: " + position);
            }
        });
    }


    /**
     * 获取网络数据
     * <p>
     * 第一个item   my_orther_item_dep
     * 第二个item   my_orther_item_goods
     */
    private void setDate(int page, String type, final boolean isRefresh) {

        Map<String, String> map = new HashMap<>();
        map.put("anchorCategory","0");
        map.put("page", String.valueOf(page));
        map.put("limit", "10");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.LiveList, map,
                new GsonObjectCallback<String>(DyUrl.BASE) {
                    //主线程处理
                    @Override
                    public void onUi(String msg) {
                        try {
                            JSONObject object = new JSONObject(msg);
                            int code = object.optInt("errno");
                            if (code == 0) {//成功
                                String data = object.optString("data");
                                mData = JsonUtil.string2Obj(data, List.class, ZhiboMo.class);

                                if (mData != null && mData.size() > 0) {
                                    if (isRefresh) {
                                        videoHeaderAdapter.addNewData(mData);
                                    } else {
                                        videoHeaderAdapter.addMoreData(mData);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //请求失败
                    @Override
                    public void onFailed(Call call, IOException e) {

                        ToastUtil.showShort(getActivity(), "网络连接超时");

                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);

                        ToastUtil.showShort(getActivity(), "网络连接超时");

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


    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

        setDate(1, "", true);

        ThreadUtil.runInUIThread(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.endRefreshing();
                recyclerViewGoods.smoothScrollToPosition(0);
            }
        }, 500);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        mMorePageNumber++;
        setDate(mMorePageNumber, "", false);
        ThreadUtil.runInUIThread(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.endLoadingMore();

            }
        }, 500);


        return true;
    }
}
