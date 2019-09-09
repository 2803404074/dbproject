package com.dabangvr.home.fragment;

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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.home.activity.HxxqLastActivity;
import com.dabangvr.model.Goods;
import com.dabangvr.util.BannerStart;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.ToastUtil;
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
import config.DyUrl;
import okhttp3.Call;

@SuppressLint("ValidFragment")
public class SearchClassFragment extends Fragment {

    private Context context;
    private RecyclerView recyclerView;



    private LocalBroadcastManager broadcastManager;
    private IntentFilter intentFilter;
    private BroadcastReceiver mReceiver;

    private boolean IS_LOADED = false;
    private static int mSerial = 0;
    private boolean isFirst = true;
    private int mTabPos = 0;//第几个商品类型
    private String status = "";//三个页面的搜索的关键字
    private String searchSdatus;//如果是搜索的话使用这个关键字查询
    private int page = 1;

    private BaseLoadMoreHeaderAdapter adapter;
    private List<Goods>mData = new ArrayList<>();


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (!IS_LOADED) {
                IS_LOADED = true;
                int type = msg.what;
                setDate(type);
            }
            return;
        }
    };

    public SearchClassFragment(int serial) {
        mSerial = serial;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = SearchClassFragment.this.getContext();
        View view = inflater.inflate(R.layout.recy_demo, container, false);
        recyclerView = view.findViewById(R.id.ms_recycler_view);

        final GridLayoutManager manager = new GridLayoutManager(context, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? manager.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(manager);
        //设置页和当前页一致时加载，防止预加载
        if (isFirst && mTabPos == mSerial) {
            isFirst = false;
            sendMessage(0);
        }

        setAdapter();
        return view;
    }

    private void setAdapter() {
        View header = LayoutInflater.from(context).inflate(R.layout.banner_demo, null);
        Banner banner = header.findViewById(R.id.ms_banner);
        BannerStart.starBanner(context,banner,"6");
        adapter = new BaseLoadMoreHeaderAdapter<Goods>(context, recyclerView, mData, R.layout.new_release_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, final Goods goods) {
                holder.setImageByUrl(R.id.new_item_img, goods.getListUrl());
                holder.setText(R.id.new_item_msg, goods.getName());
                holder.setText(R.id.new_item_salse, goods.getSellingPrice());
                holder.getView(R.id.new_item_go).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, HxxqLastActivity.class);
                        intent.putExtra("id", goods.getId());
                        intent.putExtra("type", 0);
                        startActivity(intent);
                    }
                });
            }
        };
        adapter.addHeadView(header);
        recyclerView.setAdapter(adapter);
    }

    public void sendMessage(int getDataType) {
        Message message = handler.obtainMessage();
        message.obj = getDataType;
        message.sendToTarget();
    }

    public void setTabPos(int mTabPos, String status) {
        this.mTabPos = mTabPos;
        this.status = status;
    }
    /**
     * 获取网络数据
     * @param getDataType 初始化和刷新传0，加载更多传1
     */
    private void setDate(final int getDataType) {
        Map<String, String> map = new HashMap<>();
        //ToastUtil.showShort(context, "状态 : " + status);
        if(!StringUtils.isEmpty(searchSdatus)){
            map.put("goodsName", searchSdatus);
        }else {
            map.put("goodsName", status);
        }
        map.put("page", String.valueOf(page));
        map.put("limit", "10");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getSearchGoodsList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return ;
                }
                try {
                    JSONObject objectAll = new JSONObject(result);
                    int errno = objectAll.optInt("errno");
                    if(errno == 0){
                        if(500 == objectAll.optInt("code")){
                            return ;
                        }
                        JSONObject dataObj = objectAll.optJSONObject("data");
                        String goods = dataObj.optString("goodsList");
                        mData = JsonUtil.string2Obj(goods, List.class, Goods.class);
                        if(mData != null && mData.size()>0){
                            //添加数据
                            if (getDataType == 0) {
                                adapter.addAll(mData);
                            } else {//重新设置数据
                                adapter.updateData(mData);
                            }
                        }
                    }else {
                        ToastUtil.showShort(context,objectAll.optString("errmsg"));
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    //处理通知或状态
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SEARCH_CLASS");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String goods = intent.getStringExtra("goods");
                mData = JsonUtil.string2Obj(goods, List.class, Goods.class);
                adapter.addAll(mData);
            }
        };
        broadcastManager.registerReceiver(mReceiver, intentFilter);
        return super.onCreateAnimation(transit, enter, nextAnim);
    }
}
