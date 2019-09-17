package com.dabangvr.my.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.base.BaseFragment;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.dep.activity.DepMessActivity;
import com.dabangvr.home.weight.PayDialog;
import com.dabangvr.model.OrderGoodsList;
import com.dabangvr.model.OrderListMo;
import com.dabangvr.my.activity.OrderDetailedActivity;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoadingDialog;
import com.dabangvr.util.ToastUtil;
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
import Utils.TObjectCallback;
import butterknife.BindView;
import config.DyUrl;
import okhttp3.Call;

public class MyOrtherPageFragment extends BaseFragment {
    @BindView(R.id.ms_recycler_view)
    RecyclerView recyclerView;
    private String orderStatus;
    private BaseLoadMoreHeaderAdapter adapter;
    private List<OrderListMo> orderList = new ArrayList<>();

    private int page = 1;
    private LoadingCallBack callBack;

    //activity响应的loading
    public interface LoadingCallBack{
        void show();
        void hide();
    }

    public void setCallBack(LoadingCallBack callBack) {
        this.callBack = callBack;
    }

    //订单状态表示
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    @Override
    public int layoutId() {
        return R.layout.recy_demo_margin_hori;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callBack.show();
    }

    @Override
    public void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseLoadMoreHeaderAdapter<OrderListMo>(getContext(), recyclerView, orderList, R.layout.my_orther_item_dep) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, final OrderListMo orderListMo) {

                //店铺跳转
                holder.getView(R.id.ll_dep).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), DepMessActivity.class);
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
                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                recy.setLayoutManager(manager);
                final BaseLoadMoreHeaderAdapter adapterSun = new BaseLoadMoreHeaderAdapter<OrderGoodsList>
                        (getContext(), recy, orderListMo.getOrderGoodslist(), R.layout.my_orther_item_goods) {
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
                                Intent intent = new Intent(getContext(), OrderDetailedActivity.class);
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
                                callBack.show();
                                ConfirmationOfReceipt(orderListMo.getId());
                                break;
                        }
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);

    }

    @Override
    public void initData() {
        Map<String, Object> map = new HashMap<>();
        map.put("orderState", orderStatus);
        map.put("page", String.valueOf(page));
        map.put("limit", "10");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPostJson(DyUrl.getOrderList, map,getToken(), new TObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) throws JSONException {
                JSONObject objectData = new JSONObject(result);
                String orderString = objectData.optString("orderList");
                orderList = JsonUtil.string2Obj(orderString, List.class, OrderListMo.class);
                //添加数据
                if (page == 1) {
                    adapter.updateData(orderList);
                } else {//重新设置数据
                    adapter.addAll(orderList);
                }
                callBack.hide();
            }
            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(),msg);
                callBack.hide();
            }
        });
    }

    /**
     * 确认收货
     * @param orderId
     */
    private void ConfirmationOfReceipt(String orderId) {
        Map<String,Object>map = new HashMap<>();
        map.put("orderId",orderId);
        map.put("orderState","301");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPostJson(DyUrl.updateOrderState, map,getToken(), new TObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                ToastUtil.showShort(getContext(),"确认收货成功");
                callBack.hide();
            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(),"确认收货失败："+msg);
                callBack.hide();
            }
        });

    }

    /**
     * 支付弹窗
     */
    private void paymentDialog(final String orderId, final String price) {
        PayDialog payDialog = new PayDialog(getContext(),"","orderSn",orderId);
        payDialog.showDialog(price);
        payDialog.setRequestPay(new PayDialog.RequestPay() {
            @Override
            public void show() {
                callBack.show();
            }
            @Override
            public void hied() {
                callBack.hide();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initData();
    }

    /**
     * 取消订单
     */
    private void canCelOrder(String orderId){
        Map<String,Object>map = new HashMap<>();
        map.put("orderId",orderId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPostJson(DyUrl.refundRequest, map,getToken(), new TObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result){
                ToastUtil.showShort(getContext(),"取消成功");
                callBack.hide();
                if (orderCancelDialog != null){
                    orderCancelDialog.dismiss();
                }

            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(),msg);
                callBack.hide();
                if (orderCancelDialog != null){
                    orderCancelDialog.dismiss();
                }
            }
        });
    }

    private BottomSheetDialog orderCancelDialog;
    /**
     * 取消订单和退款弹出的视图
     */
    private void orderCancelAndRefundView(final String orderId){
        if (orderCancelDialog == null)orderCancelDialog = new BottomSheetDialog(getContext());
        //初始化 - 底部弹出框布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.order_cancel_refund, null);
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderCancelDialog.dismiss();
            }
        });

        view.findViewById(R.id.tv_quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.show();
                canCelOrder(orderId);
            }
        });
        orderCancelDialog
                .contentView(view)/*加载视图*/
                .inDuration(200)
                .outDuration(200)
                .cancelable(true)
                .show();
    }

}
