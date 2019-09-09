package com.dabangvr.my.fragment;

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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.dep.activity.DepMessActivity;
import com.dabangvr.model.OrderGoodsList;
import com.dabangvr.model.OrderListMo;
import com.dabangvr.my.activity.OrderDetailedActivity;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoadingDialog;
import com.dabangvr.util.SPUtils;
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

@SuppressLint("ValidFragment")
public class MyOrtherPageFragment extends Fragment {
    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;
    private List<OrderListMo> orderList = new ArrayList<>();

    private Context context;
    private boolean IS_LOADED = false;
    private static int mSerial = 0;
    private int mTabPos = 0;//第几个商品类型
    private String orderStatus;
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
    private SPUtils spUtils;

    public MyOrtherPageFragment(int serial) {
        mSerial = serial;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = MyOrtherPageFragment.this.getContext();
        spUtils = new SPUtils(context,"db_user");
        View view = inflater.inflate(R.layout.recy_demo_margin_hori, container, false);

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
        adapter = new BaseLoadMoreHeaderAdapter<OrderListMo>(context, recyclerView, orderList, R.layout.my_orther_item_dep) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, final OrderListMo orderListMo) {

                //店铺跳转
                holder.getView(R.id.ll_dep).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DepMessActivity.class);
                        intent.putExtra("depId",orderListMo.getDeptId());
                        startActivity(intent);
                    }
                });

                //店铺logo
                holder.setImageByUrl(R.id.img_business, orderListMo.getDeptLogo());

                //店铺名称
                holder.setText(R.id.my_or_dep_name, orderListMo.getDeptName());

                //商品数量
                holder.setText(R.id.tv_number,String.valueOf(orderListMo.getOrderGoodslist().size()));

                //订单总价
                holder.setText(R.id.tv_all_price,orderListMo.getOrderTotalPrice());

                //订单关联的商品
                RecyclerView recy = holder.getView(R.id.pt_recycler2);
                LinearLayoutManager manager = new LinearLayoutManager(context);
                recy.setLayoutManager(manager);
                final BaseLoadMoreHeaderAdapter adapterSun = new BaseLoadMoreHeaderAdapter<OrderGoodsList>
                        (context, recy, orderListMo.getOrderGoodslist(), R.layout.my_orther_item_goods) {
                    @Override
                    public void convert(Context mContext, BaseRecyclerHolder holder, final OrderGoodsList goods) {
                        holder.setText(R.id.title, goods.getGoodsName());
                        holder.setText(R.id.guige, goods.getGoodsSpecNames());
                        holder.setText(R.id.number, "x"+goods.getGoodsNumber());
                        holder.setText(R.id.price, goods.getRetailPrice());
                        holder.setImageByUrl(R.id.img,goods.getChartUrl());

                        //点击信息跳转订单详情页
                        holder.getView(R.id.order_goods).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, OrderDetailedActivity.class);
                                intent.putExtra("orderId",goods.getOrderId());
                                startActivityForResult(intent,100);
                            }
                        });

                        if (orderListMo.getOrderState() == 301){
                            TextView tvComment = holder.getView(R.id.tv_comment);
                            tvComment.setVisibility(View.VISIBLE);
                            tvComment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //评论activity
                                }
                            });
                        }
                    }
                };
                recy.setAdapter(adapterSun);

                TextView tvCancel = holder.getView(R.id.tv_cancel);
                TextView tvPayment = holder.getView(R.id.tv_payment);
                //根据订单状态，显示按钮链接以及订单提示信息
                switch (orderListMo.getOrderState()){
                    //待付款
                    case 0:
                        holder.setText(R.id.tv_orderStatus, "等待买家付款");
                        tvCancel.setText("取消订单");
                        tvPayment.setText("付款");
                        break;
                    //待发货
                    case 201:
                        holder.setText(R.id.tv_orderStatus, "等待卖家发货");
                        tvCancel.setText("退款");
                        tvPayment.setVisibility(View.GONE);
                        break;

                    //待收货
                    case 300:
                        holder.setText(R.id.tv_orderStatus, "等待买家确认收货");
                        tvCancel.setText("退款");
                        tvPayment.setText("确认收货");
                        break;

                    //待评价
                    case 301:
                        holder.setText(R.id.tv_orderStatus, "交易完成");
                        tvCancel.setVisibility(View.GONE);
                        tvPayment.setVisibility(View.GONE);
                        break;
                    //订单取消
                    case 101:
                        holder.setText(R.id.tv_orderStatus, "订单已取消");
                        tvCancel.setVisibility(View.GONE);
                        tvPayment.setVisibility(View.GONE);
                        break;
                    //订单已完成
                    case 402:
                        holder.setText(R.id.tv_orderStatus, "已完成");
                        tvCancel.setVisibility(View.GONE);
                        tvPayment.setVisibility(View.GONE);
                        break;
                    //订单已完成
                    case 401:
                        holder.setText(R.id.tv_orderStatus, "退款中");
                        tvCancel.setVisibility(View.GONE);
                        tvPayment.setVisibility(View.GONE);
                        break;
                        default:
                            holder.setText(R.id.tv_orderStatus, "---");
                            tvCancel.setVisibility(View.GONE);
                            tvPayment.setVisibility(View.GONE);
                            break;
                }

                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (orderListMo.getOrderState()){
                            //取消订单
                            case 0:
                                orderCancelAndRefundView(orderListMo.getId());
                                break;
                            //退款
                            case 201:
                                orderCancelAndRefundView(orderListMo.getId());
                                break;

                            //退款
                            case 300:
                                orderCancelAndRefundView(orderListMo.getId());
                                break;
                                default:break;
                        }
                    }
                });

                tvPayment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (orderListMo.getOrderState()){
                            //付款
                            case 0:
                                paymentDialog(orderListMo.getId(),orderListMo.getOrderTotalPrice());
                                break;

                            //确认收货
                            case 300:
                                loadingDialog.show();
                                ConfirmationOfReceipt(orderListMo.getId());
                                break;
                        }
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

    /**
     * 确认收货
     * @param orderId
     */
    private void ConfirmationOfReceipt(String orderId) {
        //301
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
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
                        loadingDialog.dismiss();
                        ToastUtil.showShort(context,"确认收货成功，积分+2");
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
     * @param orderId
     */
    private void payment(String orderId) {
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
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
        spUtils.put("payType","goods");
        WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
        builder.setAppId(object.optString("appid"))
                .setPartnerId(object.optString("partnerid"))
                .setPrepayId(object.optString("prepayid"))
                .setPackageValue(object.optString("package"))
                .setNonceStr(object.optString("noncestr"))
                .setTimeStamp(object.optString("timestamp"))
                .setSign(object.optString("sign"))
                .build().toWXPayNotSign(context);
        ToastUtil.showShort(context, "正在打开微信...");
        loadingDialog.dismiss();
    }

    /**
     * 支付类型弹窗
     * @param orderId 订单id
     * @param price 订单总价
     */
    private void paymentDialog(final String orderId, String price) {
        //初始化底部弹窗
        final BottomSheetDialog bottomInterPasswordDialog = new BottomSheetDialog(context);
        //初始化 - 底部弹出框布局
        View view = LayoutInflater.from(context).inflate(R.layout.orther_dialog, null);
        TextView tvPrice = view.findViewById(R.id.dialog_price);
        tvPrice.setText(price);
        RadioGroup radioGroup = view.findViewById(R.id.orther_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //rb = group.findViewById(checkedId);
                //ToastUtil.showShort(OrderActivity.this,"选中的是id = "+checkedId+","+rb.getText().toString());
            }
        });
        view.findViewById(R.id.zf_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                payment(orderId);
            }
        });

        bottomInterPasswordDialog
                .contentView(view)/*加载视图*/
                /*.heightParam(height/1)*//*显示的高度*/
                /*动画设置*/
                .inDuration(200)
                .outDuration(200)
                /* .inInterpolator(new BounceInterpolator())
                 .outInterpolator(new AnticipateInterpolator())*/
                .cancelable(true)
                .show();
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
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
        map.put("orderState", orderStatus);
        map.put("page", String.valueOf(page));
        map.put("limit", "10");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getOrderList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
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
                        String orderString = objectData.optString("orderList");
                        orderList = JsonUtil.string2Obj(orderString, List.class, OrderListMo.class);
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

    /**
     * 取消订单
     */
    private void canCelOrder(String orderId){
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
        map.put("orderId",orderId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.refundRequest, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result))return;
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")){
                        if (500 == object.optInt("code"))return;
                        ToastUtil.showShort(context,"取消成功");
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
     * 取消订单和退款弹出的视图
     */
    private void orderCancelAndRefundView(final String orderId){
        final BottomSheetDialog bottomInterPasswordDialog = new BottomSheetDialog(context);
        //初始化 - 底部弹出框布局
        View view = LayoutInflater.from(context).inflate(R.layout.order_cancel_refund, null);
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomInterPasswordDialog.dismiss();
            }
        });

        view.findViewById(R.id.tv_quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canCelOrder(orderId);
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

}
