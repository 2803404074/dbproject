package com.dabangvr.video.dsp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.dabangvr.R;
import com.dabangvr.base.BaseFragment;
import com.dabangvr.model.Goods;
import com.dabangvr.util.BannerStart;
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils2;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.video.adapter.BGANormalRefreshViewHolder;
import com.dabangvr.video.adapter.ItemOnClickListener;
import com.dabangvr.video.adapter.ThreadUtil;
import com.dabangvr.video.fragment.model.PlayMode;
import com.dabangvr.video.zb.VideoHeaderAdapter;
import com.dabangvr.widget.GridDividerItemDecoration;
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
import bean.UserMess;
import butterknife.BindView;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import config.DyUrl;
import config.GiftUrl;
import okhttp3.Call;


public class VideoSonDspFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    @BindView(R.id.recycler_goods_id)
    RecyclerView recyclerViewGoods;
    @BindView(R.id.refreshLayout)
    BGARefreshLayout mRefreshLayout;
    private int intPage = 1;

    private VideoDspAdapter videoDspAdapter;
    private List<PlayMode> mData = new ArrayList<>();
    private String type;

    private int mNewPageNumber = 1;
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
        return R.layout.fragment_vieo_dsp_layout;
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
        Log.d("luhuas", "initData: " + type);
        setDate(1, 1, true);
    }


    /**
     * 初始化商品列表
     */
    private void initRecycler() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewGoods.setLayoutManager(gridLayoutManager);
        videoDspAdapter = new VideoDspAdapter(mData, getContext());
        recyclerViewGoods.addItemDecoration(new GridDividerItemDecoration(DensityUtil.dip2px(getContext(), 7), ContextCompat.getColor(getContext(), R.color.color_00d8d6d6)));
        recyclerViewGoods.setAdapter(videoDspAdapter);
        videoDspAdapter.setItemOnClickListener(new ItemOnClickListener() {
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


    private void setDate(int typex, int page, final boolean isRefresh) {
        final Map<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(page));
        map.put("limit", "10");

        String url = GiftUrl.indexHot;
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(url, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) return;
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")) {
                        if (500 == object.optInt("code")) {
                            return;
                        }
                        String data = object.optString("data");
                        List<PlayMode> playModes = JsonUtil.string2Obj(data, List.class, PlayMode.class);
                        if (playModes != null && playModes.size() > 0) {
                            if (isRefresh) {
                                videoDspAdapter.addNewData(playModes);
                            } else {
                                videoDspAdapter.addMoreData(playModes);
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

                ToastUtil.showShort(getActivity(), "网络连接超时");

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
//        mNewPageNumber++;

        setDate(1, 1, true);

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
        setDate(1, mMorePageNumber, false);
        ThreadUtil.runInUIThread(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.endLoadingMore();

            }
        }, 500);


        return true;
    }
}
