package com.dabangvr.my.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.home.activity.HxxqLastActivity;
import com.dabangvr.model.ActivityBean;
import com.dabangvr.model.CouponBean;
import com.dabangvr.model.Goods;
import com.dabangvr.my.adapter.Yhjadapter;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoadingDialog;
import com.dabangvr.video.adapter.BGANormalRefreshViewHolder;
import com.dabangvr.video.adapter.ThreadUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import config.DyUrl;
import okhttp3.Call;

@SuppressLint("ValidFragment")
public class MyYhjRceordPagerFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    private Context context;
    private String hoursTime;
    private BaseLoadMoreHeaderAdapter adapter;
    private RecyclerView recyclerView;
    private List<Goods> list = new ArrayList<>();

    private LocalBroadcastManager broadcastManager;
    private IntentFilter intentFilter;
    private BroadcastReceiver mReceiver;

    private List<CouponBean> DiscontList = new ArrayList<>();
    private boolean IS_LOADED = false;
    private static int mSerial = 0;
    private int mTabPos = 0;//第几个商品类型
    private boolean isFirst = true;
    private int page = 1;
    private BGARefreshLayout mRefreshLayout;
    private long mMorePageNumber = 1;
    private long mNewPageNumber = 1;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (!IS_LOADED) {
                IS_LOADED = true;
                switch (msg.what) {
                    case 100: {
                        int type = msg.getData().getInt("type");
                        String rankingType = msg.getData().getString("rankingType");
                        getDataFromHttp(type, rankingType, 1);
                    }
                }
            }
            return;
        }
    };
    private LoadingDialog loadingDialog;


    public MyYhjRceordPagerFragment(int serial) {
        mSerial = serial;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = MyYhjRceordPagerFragment.this.getContext();
        View view = inflater.inflate(R.layout.recy_demo_load, container, false);

        initView(view);
        //设置页和当前页一致时加载，防止预加载
        if (isFirst && mTabPos == mSerial) {
//            sendMessage(0, "1");
            isFirst = false;
        }
        getDataFromHttp(0, "1", 1);
        return view;
    }

    private void initView(View view) {
        loadingDialog = new LoadingDialog(getContext());
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        BGANormalRefreshViewHolder moocStyleRefreshViewHolder = new BGANormalRefreshViewHolder(getActivity(), true);
        moocStyleRefreshViewHolder.setRefreshLayout(mRefreshLayout);
        mRefreshLayout.setRefreshViewHolder(moocStyleRefreshViewHolder);
        mRefreshLayout.setDelegate(this);

        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        //View header = LayoutInflater.from(context).inflate(R.layout.ms_tips, null);
        Yhjadapter yhjadapter = new Yhjadapter(getContext());
        //添加adapter
        adapter = yhjadapter.setAdaper(recyclerView, DiscontList,mTabPos);
        recyclerView.setAdapter(adapter);


        for (int i = 0; i < 4; i++) {
            CouponBean couponBean = new CouponBean();
            couponBean.setName("满减卷");
            couponBean.setDetails("全场满200元可立即使用");
            couponBean.setLimit_two("使用规则:该优惠卷仅限于贝壳类产品使用");
            couponBean.setTitle("仅限新用户使用");
            couponBean.setStartDate("2019.9.20");
            couponBean.setEndDate("2019.10.20");
            couponBean.setFavorablePrice("25");
            couponBean.setStartDate("1");
            DiscontList.add(couponBean);
        }
//        adapter.addAll(DiscontList);

    }


    /**
     * 每一页都设置了 时间id
     *
     * @param mTabPos
     * @param hoursTime
     */
    public void setTabPos(int mTabPos, String hoursTime) {
        this.mTabPos = mTabPos;
        this.hoursTime = hoursTime;

    }

    /**
     * @param rankingType：排名类型：1默认时间排序，2销量排行
     * @param isFlush                        是否刷新
     */
    private void getDataFromHttp(final int isFlush, String rankingType, long page) {
        if (true) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("priceRange", hoursTime);
        map.put("rankingType", rankingType);
        map.put("page", String.valueOf(page));
        map.put("limit", "10");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoodsLists, map,
                new GsonObjectCallback<String>(DyUrl.BASE) {
                    @Override
                    public void onUi(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            int errno = object.optInt("errno");
                            if (errno == 0) {
                                if (500 == object.optInt("code")) {
                                    return;
                                }
                                JSONObject dataObj = object.optJSONObject("data");
                                String str = dataObj.optString("goodsList");
                                list = JsonUtil.string2Obj(str, List.class, Goods.class);
                                if (isFlush == 0) {//刷新
                                    adapter.updateData(list);
                                } else {
                                    adapter.addAll(list);
                                }
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


    //下拉刷新，上拉加载更多
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mNewPageNumber++;
        ThreadUtil.runInUIThread(new Runnable() {
            @Override
            public void run() {
//                getDataFromHttp(0, "1", mNewPageNumber);
                mRefreshLayout.endRefreshing();

            }
        }, 500);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        mMorePageNumber++;

        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        ThreadUtil.runInUIThread(new Runnable() {
            @Override
            public void run() {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
//                getDataFromHttp(0, "1", mMorePageNumber);
                mRefreshLayout.endLoadingMore();
            }
        }, 500);
        return true;
    }

    /**
     * @param type        刷新标志
     * @param rankingType 排序 1时间，2销量
     */
    public void sendMessage(int type, String rankingType) {
        Message message = handler.obtainMessage();
        message.what = 100;
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);  //往Bundle中存放数据
        bundle.putString("rankingType", rankingType);
        message.setData(bundle);//mes利用Bundle传递数据
        handler.sendMessage(message);//用activity中的handler发送消息
    }

}
