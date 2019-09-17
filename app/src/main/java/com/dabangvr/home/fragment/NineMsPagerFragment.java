package com.dabangvr.home.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.home.activity.HxxqLastActivity;
import com.dabangvr.model.ActivityBean;
import com.dabangvr.model.Goods;
import com.dabangvr.model.GoodsVo;
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoaddingUtils;
import com.dabangvr.util.LoadingDialog;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.video.adapter.BGANormalRefreshViewHolder;
import com.dabangvr.video.adapter.ThreadUtil;
import com.facebook.common.logging.LoggingDelegate;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import config.DyUrl;
import okhttp3.Call;

@SuppressLint("ValidFragment")
public class NineMsPagerFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    private Context context;
    private String hoursTime;
    private BaseLoadMoreHeaderAdapter adapter;
    private RecyclerView recyclerView;
    private List<Goods> list = new ArrayList<>();

    private LocalBroadcastManager broadcastManager;
    private IntentFilter intentFilter;
    private BroadcastReceiver mReceiver;

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


    public NineMsPagerFragment(int serial) {
        mSerial = serial;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = NineMsPagerFragment.this.getContext();
        View view = inflater.inflate(R.layout.recy_demo_load, container, false);

        initView(view);
        //设置页和当前页一致时加载，防止预加载
        if (isFirst && mTabPos == mSerial) {
            sendMessage(0, "1");
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
        adapter = new BaseLoadMoreHeaderAdapter<Goods>(context, recyclerView, list, R.layout.ms_nine_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, Goods goods) {

                //商品图片
                holder.setImageByUrl(R.id.ms_li_img, goods.getListUrl());

                //商品标题
                holder.setText(R.id.ms_li_title, goods.getName());

                //商品市场价，设置中划线
                holder.setText(R.id.ms_li_market, "已销售：" + goods.getSalesVolume() + "件");

                //秒杀价
                holder.setText(R.id.ms_li_price, goods.getSellingPrice());
                TextView tv_discount_one = holder.getView(R.id.tv_discount_one);
                TextView tv_discount_two = holder.getView(R.id.tv_discount_two);
                List<ActivityBean> activitys = goods.getActivitys();
                if (activitys != null && activitys.size() > 0) {
                    if (activitys.size() >= 2) {
                        tv_discount_one.setText(activitys.get(0).getName());
                        tv_discount_two.setText(activitys.get(1).getName());
                    } else {
                        tv_discount_one.setText(activitys.get(0).getName());
                    }
                }
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // TODO: 2019/9e
                Intent intent = new Intent(context, HxxqLastActivity.class);
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("type", 0);
                context.startActivity(intent);
            }
        });
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

    /**
     * 每一页都设置了 时间id
     *
     * @param mTabPos
     * @param hoursTime
     */
    public void setTabPos(int mTabPos, String hoursTime) {
        this.mTabPos = mTabPos;
        this.hoursTime = hoursTime;
        Log.d("luhuas", "setTabPos: " + hoursTime);
    }

    /**
     * @param rankingType：排名类型：1默认时间排序，2销量排行
     * @param isFlush                        是否刷新
     */
    private void getDataFromHttp(final int isFlush, String rankingType, long page) {
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

    /**
     * 获取网络数据
     * <p>
     * 第一个item   my_orther_item_dep
     * 第二个item   my_orther_item_goods
     */
    //处理通知或状态
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MS_HOST");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getDataFromHttp(0, "2", 1);
            }
        };
        broadcastManager.registerReceiver(mReceiver, intentFilter);
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    //下拉刷新，上拉加载更多
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mNewPageNumber++;
        ThreadUtil.runInUIThread(new Runnable() {
            @Override
            public void run() {
                getDataFromHttp(0, "1", mNewPageNumber);
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
                getDataFromHttp(0, "1", mMorePageNumber);
                mRefreshLayout.endLoadingMore();
            }
        }, 500);
        return true;
    }
}
