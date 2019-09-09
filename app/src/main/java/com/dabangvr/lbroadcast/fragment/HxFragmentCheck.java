package com.dabangvr.lbroadcast.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.onLoadMoreListener;
import com.dabangvr.model.CheckMo;
import com.dabangvr.model.Goods;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoadingDialog;
import com.dabangvr.util.TextUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.DyUrl;
import okhttp3.Call;

@SuppressLint("ValidFragment")
public class HxFragmentCheck extends Fragment {
    private int page = 1;
    private LoadingDialog loadingDialog;
    private boolean IS_LOADED = false;
    private static int mSerial = 0;
    private int mTabPos = 0;//第几个商品类型
    private String cId = "";//类型id
    private boolean isFirst = true;
    private RecyclerView recyclerView;
    private List<Goods> goodsList = new ArrayList<>();
    private Context context;
    private BaseLoadMoreHeaderAdapter adapter;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (!IS_LOADED) {
                IS_LOADED = true;
                //这里执行加载数据的操作
                page = 1;
                setDate(false);
            }
        }
    };

    private List<CheckMo> checkMoList;
    private OnSelect onSelect;
    public interface OnSelect{
        void select(String goodsId,String listUrl,String name,String price);
    }

    public void setOnSelect(OnSelect onSelect, List<CheckMo>checkMoList){
        this.onSelect = onSelect;
        this.checkMoList = checkMoList;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("tag", "onCreateView()方法执行");
        context = HxFragmentCheck.this.getContext();
        View view = inflater.inflate(R.layout.hx_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        loadingDialog = new LoadingDialog(context);
        recyclerView = view.findViewById(R.id.hx_recyclerView);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(context);
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutmanager);

        adapter = new BaseLoadMoreHeaderAdapter<Goods>(context, recyclerView, goodsList, R.layout.hx_fragment_item_check) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, final Goods o) {
                holder.setImageByUrl(R.id.hx_img, TextUtil.isNull2Url(o.getListUrl()));
                holder.setText(R.id.hx_title, TextUtil.isNull(o.getName()));

                //市场价
                TextView tvMarket = holder.getView(R.id.hx_market);
                tvMarket.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                tvMarket.setText(TextUtil.isNull(o.getMarketPrice()));

                //销量
                holder.setText(R.id.sales_num, TextUtil.isNull(o.getSalesVolume()));

                holder.setText(R.id.hx_money, TextUtil.isNull(o.getSellingPrice()));

                final TextView textView = holder.getView(R.id.tv_check);

                if (checkMoList !=null && checkMoList.size()>0){
                    for (int i=0;i<checkMoList.size();i++){
                        if (checkMoList.get(i).getGoodsId().equals(o.getId())){
                            textView.setBackgroundColor(getResources().getColor(R.color.background_gray1));
                            textView.setClickable(false);
                        }
                    }
                }


                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textView.setBackgroundColor(getResources().getColor(R.color.background_gray1));
                        onSelect.select(o.getId(),o.getListUrl(),o.getName(),o.getSellingPrice());
                        textView.setClickable(false);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);


        //设置页和当前页一致时加载，防止预加载
        if (isFirst && mTabPos == mSerial) {
            isFirst = false;
            sendMessage();
        }
        //下拉刷新
        final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipe);
        refreshLayout.setColorSchemeResources(android.R.color.background_dark, android.R.color.background_light, android.R.color.black, android.R.color.darker_gray);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (goodsList != null) {
                    goodsList.clear();
                }
                page = 1;
                setDate(false);
                refreshLayout.setRefreshing(false);
            }
        });

        //加载更多
        recyclerView.addOnScrollListener(new onLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                page += 1;
                setDate(true);
            }
        });


    }

    private void setDate(final boolean isLoad) {
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("categoryId", cId);
        map.put("page", String.valueOf(page));
        map.put("limit", "10");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoodsList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            //主线程处理
            @Override
            public void onUi(String newsBean) {
                if (StringUtils.isEmpty(newsBean)) {
                    if (loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    Toast.makeText(context, "获取数据失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject object = new JSONObject(newsBean);
                    int err = object.optInt("errno");
                    if (err != 0) {
                        return;
                    } else {
                        JSONObject object1 = object.optJSONObject("data");
                        String str = object1.optString("goodsList");
                        goodsList = JsonUtil.string2Obj(str, List.class, Goods.class);
                        if (isLoad){
                            adapter.addAll(goodsList);
                        }else {
                            adapter.updateData(goodsList);
                        }
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
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

    public HxFragmentCheck(int serial) {
        mSerial = serial;
    }

    public void sendMessage() {
        Message message = handler.obtainMessage();
        message.sendToTarget();
    }

    public void setTabPos(int mTabPos, String cId) {
        this.mTabPos = mTabPos;
        this.cId = cId;
    }
}
