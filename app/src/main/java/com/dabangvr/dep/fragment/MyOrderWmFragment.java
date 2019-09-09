package com.dabangvr.dep.fragment;

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
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.dep.activity.DepMessActivity;
import com.dabangvr.dep.activity.WmDetialsActivity;
import com.dabangvr.dep.model.OrderBean;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoadingDialog;
import com.dabangvr.util.LoginTipsDialog;
import com.dabangvr.util.ToastUtil;

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
public class MyOrderWmFragment extends Fragment {
    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;
    private List<OrderBean> orderList = new ArrayList<>();

    private Context context;
    private boolean IS_LOADED = false;
    private static int mSerial = 0;
    private int mTabPos = 0;//第几个商品类型
    private String orderStatus;
    private boolean isFirst = true;
    private int page = 1;
    private LoadingDialog loadingDialog;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (!IS_LOADED) {
                IS_LOADED = true;
                //这里执行加载数据的操作
                int type = msg.what;
                setDate(type);
            }
            return;
        }
    };
    private String token;

    public MyOrderWmFragment(int serial) {
        mSerial = serial;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = MyOrderWmFragment.this.getContext();
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
        LinearLayoutManager layoutmanager = new LinearLayoutManager(context);
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutmanager);

        token = LoginTipsDialog.getToken(context);

        adapter = new BaseLoadMoreHeaderAdapter<OrderBean>(context, recyclerView, orderList, R.layout.wm_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, final OrderBean o) {
                holder.setImageByUrl(R.id.iv_dep_logo,o.getLogo());
                holder.setText(R.id.tv_det_name,o.getDeptName());
                holder.setText(R.id.goods_name,o.getGoodsName()+" 等"+o.getGoodsNumber()+"件商品");
                holder.setText(R.id.price,"￥"+o.getTotalPrice());
                holder.getView(R.id.tv_dep).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DepMessActivity.class);
                        intent.putExtra("depId",o.getDeptId());
                        startActivity(intent);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, WmDetialsActivity.class);
                intent.putExtra("orderId",orderList.get(position).getId());
                startActivityForResult(intent,0);
            }
        });
    }

    /**
     * 获取网络数据
     * <p>
     * 第一个item   my_orther_item_dep
     * 第二个item   my_orther_item_goods
     */
    private void setDate(final int getDataType) {
        loadingDialog.show();
        Map<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, token);
        map.put("page", String.valueOf(page));
        map.put("orderState", orderStatus);
        map.put("limit", "10");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getDeliveryOrderList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return ;
                }
                try {
                    JSONObject objectAll = new JSONObject(result);
                    int errno = objectAll.optInt("errno");
                    if (errno == 0) {
                        int code = objectAll.optInt("code");
                        if(500 == code){
                            ToastUtil.showShort(context,objectAll.optString("msg"));
                            loadingDialog.dismiss();
                            return ;
                        }
                        JSONObject objectData = objectAll.optJSONObject("data");
                        String orderString = objectData.optString("orderVoList");
                        orderList = JsonUtil.string2Obj(orderString, List.class, OrderBean.class);
                        //添加数据
                        if (getDataType == 0) {
                            adapter.updateData(orderList);
                        } else {//重新设置数据
                            adapter.addAll(orderList);
                        }
                    } else {
                        ToastUtil.showShort(context, objectAll.optString("errmsg"));
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

    public void sendMessage(int type) {
        Message message = handler.obtainMessage();
        message.what = type;
        message.sendToTarget();
    }

    public void setTabPos(int mTabPos, String status) {
        this.mTabPos = mTabPos;
        this.orderStatus = status;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setDate(0);
    }
}
