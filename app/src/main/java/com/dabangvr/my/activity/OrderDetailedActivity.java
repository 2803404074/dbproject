package com.dabangvr.my.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.activity.DiscussActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.home.activity.HxxqLastActivity;
import com.dabangvr.model.OrderGoodsList;
import com.dabangvr.util.DateUtil;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoadingDialog;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.util.WXPayUtils;
import com.rey.material.app.BottomSheetDialog;

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

public class OrderDetailedActivity extends BaseActivity implements View.OnClickListener {
    private LoadingDialog loadingDialog;
    private int orderState;//订单状态
    private int payState;//支付状态
    private int logisticsState;//配送状态
    private String orderId;

    private RecyclerView recyclerView;
    private List<OrderGoodsList> goodsList = new ArrayList<>();
    private BaseLoadMoreHeaderAdapter adapter;

    private TextView tvAddress;//地址
    private TextView tvPsType;//配送类型
    private TextView tvOrderSn;//订单编号
    private TextView tvOrderData;//订单日期


    private TextView tvPayment;//去付款
    private TextView tvCancel;//取消订单


    private TextView tvOrderStatus;//订单状态标题
    private TextView tvOrderTips;//未支付的订单，将显示关闭信息
    private ImageView ivStatus;//状态图标


    private TextView tvOgprice;//订单商品总价
    private TextView tvOtprice;//订单物流总价
    private TextView tvOallprice;//订单总价

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_order_detailed;
    }

    @Override
    protected void initView() {

        loadingDialog = new LoadingDialog(this);

        findViewById(R.id.backe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvAddress = findViewById(R.id.od_address);//收货地址
        tvPsType = findViewById(R.id.od_ps_type);//配送方式
        tvOrderSn = findViewById(R.id.od_order_number);//订单编号
        tvOrderData = findViewById(R.id.od_order_date);//订单创建时间

        tvPayment = findViewById(R.id.tv_payment);//付款，确认收货  按钮
        tvCancel = findViewById(R.id.tv_cancel);//取消订单，退款,评价   按钮
        tvCancel.setOnClickListener(this);
        tvPayment.setOnClickListener(this);

        tvOrderStatus = findViewById(R.id.tv_orderStatus);//订单状态标题
        tvOrderTips = findViewById(R.id.tv_orderTips);//未支付的订单，将显示关闭信息
        ivStatus = findViewById(R.id.iv_status);


        tvOgprice = findViewById(R.id.tv_og_price);//订单商品总价
        tvOtprice = findViewById(R.id.tv_ot_price);//订单物流总价
        tvOallprice = findViewById(R.id.tv_oall_price);//订单总价

        recyclerView = findViewById(R.id.od_recy);//订单关联的商品
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new BaseLoadMoreHeaderAdapter<OrderGoodsList>(this, recyclerView, goodsList, R.layout.od_content_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, OrderGoodsList o) {
                holder.setImageByUrl(R.id.odc_img, o.getChartUrl());
                holder.setText(R.id.odc_price, o.getRetailPrice());
                holder.setText(R.id.odc_num, "x"+o.getGoodsNumber());
                holder.setText(R.id.odc_title, o.getGoodsName());
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(OrderDetailedActivity.this, HxxqLastActivity.class);
                intent.putExtra("goodsId",goodsList.get(position).getGoodsId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {
        Map<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this, "token"));
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        map.put("orderId", orderId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getOrderDetails, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    if (StringUtils.isEmpty(result)) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(result);
                    int errno = jsonObject.optInt("errno");
                    if (errno == 0) {
                        if (jsonObject.optInt("code") == 500) {
                            ToastUtil.showShort(OrderDetailedActivity.this, "服务数据更新中...");
                            return;
                        }

                        JSONObject dataObje = jsonObject.optJSONObject("data");
                        JSONObject orderObj = dataObje.optJSONObject("orderDetails");

                        //订单状态
                        orderState = orderObj.optInt("orderState");
                        //支付状态
                        payState = orderObj.optInt("payState");
                        //配送状态
                        logisticsState = orderObj.optInt("logisticsState");

                        //订单编号
                        String orderSn = orderObj.optString("orderSn");
                        //订单时间
                        String orderTime = orderObj.optString("orderTime");

                        String province = orderObj.optString("province");//省
                        String city = orderObj.optString("city");//市
                        String area = orderObj.optString("area");//县
                        String address = orderObj.optString("address");//地址
                        String name = orderObj.optString("consigneeName");//收货人
                        String phone = orderObj.optString("consigneePhone");//收货人电话
                        String orderAddress = province + "-" + city + "-" + area + "-" + address + "\n" + name + "-" + phone;


                        String totalPrice = orderObj.optString("totalPrice"); //订单商品总价
                        String logisticsTotalPrice = orderObj.optString("logisticsTotalPrice");//物流费
                        String orderTotalPrice = orderObj.optString("orderTotalPrice"); //订单总价


                        //订单的基本数据
                        setOrderDataMess(orderSn, orderTime, orderAddress,totalPrice,logisticsTotalPrice,orderTotalPrice);

                        //订单的商品数据
                        String orderDetails = orderObj.optString("orderGoodslist");//商品信息列表
                        goodsList = JsonUtil.string2Obj(orderDetails, List.class, OrderGoodsList.class);
                        adapter.updateData(goodsList);
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
     * @param orderSn   订单编号
     * @param orderTime 订单创建时间
     * @param address   收货地址
     */
    private void setOrderDataMess(String orderSn, String orderTime, String address,
                                  String totalPrice,String logisticsTotalPrice,String orderTotalPrice) {
        tvAddress.setText(address);
        tvPsType.setText("快递");
        tvOrderSn.setText(orderSn);
        tvOrderData.setText(DateUtil.stampToDate(orderTime));

        tvOgprice.setText(totalPrice);
        tvOtprice.setText(logisticsTotalPrice);
        tvOallprice.setText(orderTotalPrice);

        /**
         * 订单状态：（0待付款101订单已取消102订单已删除201订单已付款300订单已发货301用户确认收货400申请退款401退款中402完成500已评论）
         *
         * 支付状态（0未付款;1付款中;2已付款;3退款中4退款成功5拒绝退款）
         *
         * 物流状态（0未发货、1已发货、2已收货、4已完成）
         */
        switch (orderState) {
            //0去付款，
            case 0://待付款
                ivStatus.setImageDrawable(getResources().getDrawable(R.mipmap.pay_dfk));
                tvOrderTips.setVisibility(View.VISIBLE);
                tvOrderStatus.setText("等待买家付款");
                tvPayment.setText("去付款");
                tvCancel.setText("取消订单");
                break;
            //待发货
            case 201:
                ivStatus.setImageDrawable(getResources().getDrawable(R.mipmap.pay_dfh));
                tvOrderStatus.setText("等待卖家发货");
                tvCancel.setText("退款");
                tvPayment.setVisibility(View.GONE);
                break;
            //待收货
            case 300:
                ivStatus.setImageDrawable(getResources().getDrawable(R.mipmap.pay_dsh));
                tvOrderStatus.setText("等待买家确认收货");
                tvCancel.setText("退款");
                tvPayment.setText("确认收货");
                break;
            //待评价
            case 301:
                ivStatus.setImageDrawable(getResources().getDrawable(R.mipmap.pay_success));
                tvOrderStatus.setText("等待买家评论");
                tvCancel.setText("评价");
                tvPayment.setVisibility(View.GONE);
                break;
            //订单已完成
            case 402:
                ivStatus.setImageDrawable(getResources().getDrawable(R.mipmap.pay_success));
                tvOrderStatus.setText("交易完成");
                tvCancel.setVisibility(View.GONE);
                tvPayment.setVisibility(View.GONE);
            //订单已取消
            case 101:
                ivStatus.setImageDrawable(getResources().getDrawable(R.mipmap.pay_cancel));
                tvOrderStatus.setText("订单已取消");
                tvPayment.setVisibility(View.GONE);
                tvCancel.setVisibility(View.GONE);
                break;
            default:
                ivStatus.setImageDrawable(getResources().getDrawable(R.mipmap.pay_success));
                tvOrderStatus.setText("--");
                tvPayment.setVisibility(View.GONE);
                tvCancel.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_payment:
                switch (orderState) {
                    case 0://付款
                        loadingDialog.show();
                        payment();
                        break;
                    case 300://确认收货
                        loadingDialog.show();
                        ConfirmationOfReceipt();
                        break;
                    default:
                        break;
                }
                break;

            //取消订单
            case R.id.tv_cancel:
                switch (orderState){
                    case 0://待付款
                        orderCancelAndRefundView();
                        break;
                    //待发货
                    case 201:
                        orderCancelAndRefundView();
                        break;
                    //待收货
                    case 300:
                        orderCancelAndRefundView();
                        break;
                    //待评价
                    case 301:
//                        Intent intent = new Intent(OrderDetailedActivity.this,CommentActivity.class);
                        Intent intent = new Intent(OrderDetailedActivity.this, DiscussActivity.class);
                        intent.putExtra("orderId",orderId);
                        startActivity(intent);
                        break;
                }
                break;
            default:
                break;
        }
    }


    /**
     * 取消订单和退款弹出的视图
     */
    private void orderCancelAndRefundView(){
        final BottomSheetDialog bottomInterPasswordDialog = new BottomSheetDialog(OrderDetailedActivity.this);
        //初始化 - 底部弹出框布局
        View view = LayoutInflater.from(OrderDetailedActivity.this).inflate(R.layout.order_cancel_refund, null);
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomInterPasswordDialog.dismiss();
            }
        });

        view.findViewById(R.id.tv_quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                canCelOrder();
            }
        });
        bottomInterPasswordDialog
                .contentView(view)/*加载视图*/
                /*.heightParam(height/1)*//*显示的高度*/
                /*动画设置*/
                .inDuration(200)
                .outDuration(200)
                .cancelable(true)
                .show();
    }

    /**
     * 取消订单
     */
    private void canCelOrder(){
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this,"token"));
        map.put("orderId",orderId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.refundRequest, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result))return;
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")){
                        if (500 == object.optInt("code"))return;
                        ToastUtil.showShort(OrderDetailedActivity.this,"取消成功");
                        loadingDialog.dismiss();
                        finish();
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
     * 确认收货
     */
    private void ConfirmationOfReceipt() {
        //301
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this,"token"));
        map.put("orderId",orderId);
        map.put("orderState","301");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.updateOrderState, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result))return;
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")){
                        if (500 == object.optInt("code"))return;
                        ToastUtil.showShort(OrderDetailedActivity.this,"确认收货成功，积分+2");
                        loadingDialog.dismiss();
                        finish();
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
     * 付款
     */
    private void payment() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this,"token"));
        map.put("orderId", orderId);
        map.put("payOrderSnType", "orderSn");// 直接购买用orderSnTotal；重新付款用orderSn
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.prepayOrderAgain, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (errno == 0) {
                        if (500 == object.optInt("code")) {
                            return;
                        }
                        toWXPay(object.optJSONObject("data"));
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
     * 吊起微信支付
     * @param object
     */
    private void toWXPay(JSONObject object) {
        setSPKEY(this,"payType","goods");
        WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
        builder.setAppId(object.optString("appid"))
                .setPartnerId(object.optString("partnerid"))
                .setPrepayId(object.optString("prepayid"))
                .setPackageValue(object.optString("package"))
                .setNonceStr(object.optString("noncestr"))
                .setTimeStamp(object.optString("timestamp"))
                .setSign(object.optString("sign"))
                .build().toWXPayNotSign(this);
        ToastUtil.showShort(this, "正在打开微信...");
        loadingDialog.dismiss();
    }



}
