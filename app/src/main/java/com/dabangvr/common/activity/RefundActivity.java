package com.dabangvr.common.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.model.OrderGoodsList;
import com.dabangvr.util.JsonUtil;
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
import Utils.PdUtil;
import config.DyUrl;
import okhttp3.Call;

/**
 * 退款
 */
public class RefundActivity extends BaseActivity implements View.OnClickListener {

    public static RefundActivity instants;
    private PdUtil pdUtil;
    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;
    private List<OrderGoodsList> mData = new ArrayList<>();

    private String orderId;//订单id
    private String orderSn;//订单编号
    private String orderTotalPrice;//订单总价钱
    private int orderState;//订单状态

    private TextView tvOrderSn;
    private TextView tvPrice;
    private TextView tvCarMess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int setLayout() {
        return R.layout.activity_refund;
    }

    protected void initView() {
        instants = this;
        pdUtil = new PdUtil(this);

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.click).setOnClickListener(this);

        tvOrderSn = findViewById(R.id.ordersn);//订单编号
        tvPrice = findViewById(R.id.order_price);//退款金额
        tvCarMess = findViewById(R.id.wuliu_mess);//物流信息

        recyclerView = findViewById(R.id.recy);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        adapter = new BaseLoadMoreHeaderAdapter<OrderGoodsList>(this,recyclerView,mData,R.layout.od_content_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, OrderGoodsList o) {
                holder.setImageByUrl(R.id.odc_img, o.getChartUrl());
                holder.setText(R.id.odc_price, "￥"+o.getRetailPrice());
                holder.setText(R.id.odc_num, o.getGoodsNumber());
                holder.setText(R.id.odc_title,o.getGoodsName());
            }
        };
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void initData() {
        orderId = getIntent().getStringExtra("orderId");
        Map<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this,"token"));
        map.put("orderId", orderId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getOrderDetails, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    if (StringUtils.isEmpty(result)) {
                        return ;
                    }
                    JSONObject jsonObject = new JSONObject(result);
                    int errno = jsonObject.optInt("errno");
                    if (errno == 0) {
                        if (jsonObject.optInt("code") == 500){
                            ToastUtil.showShort(RefundActivity.this,"服务数据更新中...");
                            return ;
                        }
                        JSONObject dataObje = jsonObject.optJSONObject("data");
                        JSONObject orderObj = dataObje.optJSONObject("orderDetails");
                        orderState = orderObj.optInt("orderState");//订单状态
                        orderSn = orderObj.optString("orderSn");//订单编号
                        orderTotalPrice = orderObj.optString("orderTotalPrice");//订单总价钱

                        setText();
                        String orderDetails = orderObj.optString("orderGoodslist");//商品信息列表
                        mData = JsonUtil.string2Obj(orderDetails, List.class, OrderGoodsList.class);
                        adapter.updateData(mData);
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

    private void setText() {

        //订单编号
        tvOrderSn.setText(orderSn);

        //订单总价钱（退款）
        tvPrice.setText(orderTotalPrice);

        //订单状态
        switch (orderState) {
            case 0:
                tvCarMess.setText( "待付款");
                break;
            case 101:
                tvCarMess.setText( "订单已取消");
                break;
            case 102:
                tvCarMess.setText( "订单已删除");
                break;
            case 201:
                tvCarMess.setText( "订单已付款");
                break;
            case 300:
                tvCarMess.setText( "订单已发货");
                break;
            case 301:
                tvCarMess.setText( "用户确认收货");
                break;
            case 400:
                tvCarMess.setText( "已申请退款");
                break;
            case 401:
                tvCarMess.setText( "退款中");
                break;
            case 402:
                tvCarMess.setText( "完成");
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:finish();break;
            case R.id.click:Refund();break;
            default:break;
        }
    }

    private void Refund() {
        pdUtil.showLoding("退款中");
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this,"token"));
        map.put("orderId",orderId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.refundRequest, map, new GsonObjectCallback<String>(DyUrl.BASE){

            @Override
            public void onUi(String result) {
                if(StringUtils.isEmpty(result)){
                    ToastUtil.showShort(RefundActivity.this,"退款失败");
                    pdUtil.desLoding();
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    if(1 == object.optInt("errno")){
                        pdUtil.desLoding();
                        ortehrTips("退款成功,请注意查收支付源账单回馈单");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return ;
            }
            @Override
            public void onFailed(Call call, IOException e) {
            }
        });
    }


    //其他提示
    private void ortehrTips( String mess){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.application)
                .setMessage(mess)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
