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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.home.activity.HxxqLastActivity;
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
public class HomeTabGoodsFragment extends Fragment {
    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;
    //private List<DynamicMo> mData = new ArrayList<>();
    private List<Goods> mData = new ArrayList<>();
    private Context context;
    private boolean IS_LOADED = false;
    private static int mSerial = 0;
    private int mTabPos = 0;//第几个商品类型
    private String type;
    private boolean isFirst = true;
    private int page = 1;
    private LoadingDialog loadingDialog;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (!IS_LOADED) {
                IS_LOADED = true;
                //这里执行加载数据的操作
                int type = msg.what;
                setDate(type);
            }
            return false;
        }
    });
    private String token;

    public HomeTabGoodsFragment(int serial) {
        mSerial = serial;
    }

    public HomeTabGoodsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = HomeTabGoodsFragment.this.getContext();
        View view = inflater.inflate(R.layout.recy_demo, container, false);
        initView(view);

        //设置页和当前页一致时加载，防止预加载
        if (isFirst && mTabPos == mSerial) {
            isFirst = false;
            sendMessage(0);
        }
        return view;
    }

    private void initView(View view) {
        loadingDialog = new LoadingDialog(context);
        recyclerView = view.findViewById(R.id.ms_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new BaseLoadMoreHeaderAdapter<Goods>(context, recyclerView, mData, R.layout.pt_goods_item) {//
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, Goods o) {
                holder.setImageByUrl(R.id.new_item_img, o.getListUrl());
                holder.setText(R.id.new_item_msg, o.getName());
                holder.setText(R.id.sales_num, "销量: "+ TextUtil.isNull(o.getSalesVolume()));
                holder.setText(R.id.new_item_salse,TextUtil.isNull(o.getSellingPrice()));
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Goods o = (Goods) adapter.getData().get(position);
                Intent intent = new Intent(HomeTabGoodsFragment.this.getContext(), HxxqLastActivity.class);
                intent.putExtra("id", o.getId());
                intent.putExtra("type", 0);
                startActivity(intent);
            }
        });

    }

    /**
     * 获取网络数据
     * <p>
     * 第一个item   my_orther_item_dep
     * 第二个item   my_orther_item_goods
     */
    private void setDate(int typex) {
        HashMap<String, String> map = new HashMap<>();
        map.put("page", "1");
        map.put("limit", "10");
        /*map.put("type",String.valueOf(typex));*/
        //ToastUtil.showShort(context,"加载的类型是:"+typex+","+type);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoodsLists, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            //主线程处理
            @Override
            public void onUi(String newsBean) {
                if (StringUtils.isEmpty(newsBean)) {
                    Toast.makeText(context, "获取数据失败", Toast.LENGTH_SHORT).show();
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(newsBean);
                    int err = object.optInt("errno");
                    if (err == 0) {
                        if (object.optInt("code") == 500) {
                            //ToastUtil.showShort(context, "服务数据更新中...");
                            return ;
                        }
                        JSONObject data = object.optJSONObject("data");
                        String str = data.optString("goodsList");
                        mData = JsonUtil.string2Obj(str, List.class, Goods.class);
                        adapter.updateData(mData);
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

    public void sendMessage(int type) {
        if (type == 1) {
            return;
        }
        Message message = handler.obtainMessage();
        message.what = type;
        message.sendToTarget();
    }

    public void setTabPos(int mTabPos, String type) {
        this.mTabPos = mTabPos;
        this.type = type;
    }
}
