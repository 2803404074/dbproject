package com.dabangvr.home.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseRecyclerAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.home.activity.HxxqLastActivity;
import com.dabangvr.model.Goods;
import com.dabangvr.util.LoadingDialog;
import com.dabangvr.util.ToastUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
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
public class PtPageFragment extends Fragment {
    private RecyclerView recyclerView;
    private BaseRecyclerAdapter adapter;
    private Context context;
    private boolean IS_LOADED = false;
    private static int mSerial = 0;
    private int mTabPos = 0;//第几个商品类型
    private String cId = "";//类型id
    private boolean isFirst = true;
    private int page = 1;
    private LoadingDialog loadingDialog;
    private List<Goods> goodsList;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (!IS_LOADED) {
                IS_LOADED = true;
                //这里执行加载数据的操作
                setDate();
            }
            return;
        }
    };

    public PtPageFragment(int serial) {
        mSerial = serial;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = PtPageFragment.this.getContext();
        View view = inflater.inflate(R.layout.pt_fragment, container, false);
        recyclerView = view.findViewById(R.id.pt_recycler);

        LinearLayoutManager layoutmanager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutmanager);

        //设置页和当前页一致时加载，防止预加载
        if (isFirst && mTabPos == mSerial) {
            isFirst = false;
            sendMessage();
        }
        return view;
    }

    /**
     * 获取网络数据
     */
    private void setDate() {
        if (null == loadingDialog){
            loadingDialog = new LoadingDialog(context);
        }
        loadingDialog.show();
        Map<String, String> map = new HashMap<>();
        map.put("categoryId", cId);
        map.put("page", String.valueOf(page));
        map.put("limit", "10");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGroupGoodsList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                //ToastUtil.showShort(context,"加载了..."+cId);
                if (StringUtils.isEmpty(result)) {
                    return ;
                }
                try {
                    JSONObject objectAll = new JSONObject(result);
                    int errno = objectAll.optInt("errno");
                    if (errno == 0) {
                        if(500 == objectAll.optInt("code")){
                            return ;
                        }
                        JSONObject objectData = objectAll.optJSONObject("data");
                        JSONArray array = objectData.optJSONArray("goodsList");
                        goodsList = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = (JSONObject) array.get(i);
                            Goods goods = new Goods();
                            goods.setId(object.optString("id"));
                            goods.setCategoryId(object.optString("categoryId"));
                            goods.setName(object.optString("name"));
                            goods.setTitle(object.optString("title"));
                            goods.setDescribe(object.optString("describe"));
                            goods.setListUrl(object.optString("listUrl"));
                            goods.setMarketPrice(object.optString("marketPrice"));
                            goods.setSellingPrice(object.optString("sellingPrice"));
                            goods.setAssembleId(object.optString("assembleId"));
                            goods.setAssembleNumber(object.optString("assembleNumber"));
                            goods.setStartTime(object.optString("startTime"));
                            if (StringUtils.isEmpty(object.optString("endTime"))) {
                                goods.setEndTime("");
                            } else {
                                goods.setEndTime(object.optString("endTime"));
                            }
                            goodsList.add(goods);
                        }

                        adapter = new BaseRecyclerAdapter<Goods>(context, goodsList, R.layout.pt_item) {
                            @Override
                            public void convert(BaseRecyclerHolder holder, Goods item, int position, boolean isScrolling) {
                                //holder.setText(R.id.item_text,item.getText());
                                holder.setImageByUrl(R.id.pt_item_img, item.getListUrl());
                                holder.setText(R.id.pt_item_msg, item.getName());
                                holder.setText(R.id.pt_item_yj, item.getMarketPrice());
                                holder.setText(R.id.pt_item_xj, item.getSellingPrice());
                            }
                        };
                        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(RecyclerView parent, View view, int position) {
                                String id = goodsList.get(position).getId();
                                Intent intent = new Intent(context, HxxqLastActivity.class);
                                intent.putExtra("id", id);
                                intent.putExtra("type", 1);//商品详细页面，1代表拼团类型
                                intent.putExtra("assembleId", goodsList.get(position).getAssembleId());
                                startActivity(intent);
                            }
                        });
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    } else {
                        ToastUtil.showShort(context, "加载了..." + objectAll.optInt("errmsg"));
                    }
                    loadingDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                loadingDialog.dismiss();
            }
        });

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
