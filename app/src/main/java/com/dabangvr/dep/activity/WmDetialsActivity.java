package com.dabangvr.dep.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.activity.CommentActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.dep.model.WmDetailsMo;
import com.dabangvr.util.DateUtil;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoginTipsDialog;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.TextUtil;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.util.WXPayUtils;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.DyUrl;
import okhttp3.Call;

/**
 * 订单详情
 */
public class WmDetialsActivity extends BaseActivity implements View.OnClickListener {

    private WmDetailsMo mData;
    private TextView tvStatus;//订单状态

    private TextView tvUserName;//收货人姓名
    private TextView tvUserPhone;//收货人电话
    private TextView tvUserAddress;//收货人地址

    private TextView tvDepName;//商家名称

    //订单的商品列表
    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;

    private TextView tvAllPrice;//订单总价
    private TextView tvKdPrice;//快递费
    private TextView tvRrprice;//付款金额
    private TextView tvZfType;//支付类型

    private TextView tvOrderNumber;//订单编号
    private TextView tvOrderDate;//下单时间
    private TextView tvFhDate;//发货时间

    private TextView tvTips;//根据状态提示跳转

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_wm_detials;
    }

    @Override
    protected void initView() {
        tvStatus = findViewById(R.id.tv_wm_status);

        tvUserName = findViewById(R.id.tv_wm_name);
        tvUserPhone = findViewById(R.id.tv_wm_phone);
        tvUserAddress = findViewById(R.id.tv_wm_address);

        tvDepName = findViewById(R.id.tv_wm_depname);


        recyclerView = findViewById(R.id.ms_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvAllPrice = findViewById(R.id.tv_wm_allprice);
        tvKdPrice = findViewById(R.id.tv_wm_kdprice);
        tvRrprice = findViewById(R.id.tv_wm_rrprice);

        tvZfType = findViewById(R.id.tv_wm_zftype);

        tvOrderNumber = findViewById(R.id.tv_wm_ordernumber);
        tvOrderDate = findViewById(R.id.tv_wm_orderdate);
        tvFhDate = findViewById(R.id.tv_wm_fhdate);

        tvTips = findViewById(R.id.tv_wm_qrsh);
        tvTips.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    private void setMess() {
        /*订单状态：-1拒单，0待付款，201已付款，300待收货，301待评价，400退款（不传为查询全部）*/
        switch (mData.getOrderState()){
            case -1:
                tvStatus.setText("拒单");
                tvTips.setVisibility(View.GONE);
                break;
            case 0:
                tvStatus.setText("待付款");
                tvTips.setText("去付款");
                break;
            case 201:
                tvStatus.setText("已付款");
                tvTips.setVisibility(View.GONE);
                break;
            case 300:
                tvStatus.setText("待收货");
                tvTips.setText("去评价");
                break;
            case 301:
                tvStatus.setText("待评价");
                tvTips.setText("去评价");
                break;
            case 400:
                tvStatus.setText("退款");
                tvTips.setVisibility(View.GONE);
                break;
                default:
                    tvStatus.setText("订单生效中");
                    tvTips.setVisibility(View.GONE);
                    break;
        }

        tvUserName.setText(TextUtil.isNull2Url(mData.getConsigneeName()));
        tvUserPhone.setText(TextUtil.isNull2Url(mData.getConsigneePhone()));
        tvUserAddress.setText(TextUtil.isNull2Url(mData.getAddress()));
        tvDepName.setText(TextUtil.isNull2Url(mData.getDeptName()));

        tvAllPrice.setText(TextUtil.isNull2Url(mData.getTotalPrice()));

        tvAllPrice.setText("￥"+TextUtil.isNull2Url(mData.getDeptName()));
        tvKdPrice.setText("￥"+TextUtil.isNull2Url(mData.getDeliverPrice()));
        tvRrprice.setText("￥"+TextUtil.isNull2Url(mData.getActualPayPrice()));

        if (TextUtil.isNull2Url(mData.getPayType()).equals("1")){
            tvZfType.setText("微信");//支付类型
        }
        if (TextUtil.isNull2Url(mData.getPayType()).equals("2")){
            tvZfType.setText("支付宝");//支付类型
        }
        tvOrderNumber.setText(TextUtil.isNull2Url(mData.getOrderSn()));
        tvOrderDate.setText(DateUtil.stampToDate(TextUtil.isNull2Url(mData.getOrderTime())));

        if (!TextUtil.isNullFor(mData.getDeliveryTime())){
            tvFhDate.setText(DateUtil.stampToDate(TextUtil.isNull2Url(mData.getDeliveryTime())));
        }

        adapter = new BaseLoadMoreHeaderAdapter<WmDetailsMo.WmGoods>(this,
                recyclerView,mData.getOrderDetailsVoList(),R.layout.order_goods_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, WmDetailsMo.WmGoods o) {
                holder.setImageByUrl(R.id.orther_img,o.getChartUrl());
                holder.setText(R.id.orther_info,o.getGoodsName());
                holder.setText(R.id.orther_salse,"￥"+o.getSellingPrice());
                holder.setText(R.id.or_lv_item_num,"x"+o.getGoodsNumber());
            }
        };
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void initData() {
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME,getSPKEY(this,"token"));
        map.put("orderId",getIntent().getStringExtra("orderId"));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getDeliveryOrderDetails, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)){
                    return ;
                }

                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")){
                        if (500 == object.optInt("code")){
                            return ;
                        }
                        JSONObject data = object.optJSONObject("data");
                        String str = data.optString("deliveryOrderDetails");
                        mData = JsonUtil.string2Obj(str, WmDetailsMo.class);
                        setMess();
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_wm_qrsh:
                switch (mData.getOrderState()){
                    case 0:
                        //吊起支付（重新支付）
                        pay();
                        break;
                    case 301:
                        Intent intent5 = new Intent(this, CommentActivity.class);
                        intent5.putExtra("goodsId", mData.getOrderDetailsVoList().get(0).getGoodsId());
                        intent5.putExtra("url", mData.getOrderDetailsVoList().get(0).getChartUrl());
                        intent5.putExtra("name", mData.getOrderDetailsVoList().get(0).getGoodsName());
                        intent5.putExtra("price", mData.getOrderDetailsVoList().get(0).getSellingPrice());
                        intent5.putExtra("orderId",mData.getId());
                        startActivity(intent5);
                        break;
                    case 400:
                        LoginTipsDialog.ortehrTips(this,"可在支付源查看退款进度，如微信等");
                        break;
                }
                break;
            case R.id.back:
                finish();
                break;
                default:break;
        }
    }


    /**
     * 重新支付
     */
    private void pay(){
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this, "token"));
        map.put("orderId", mData.getId());
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.prepayDeliveryOrderAgain, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return ;
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
     * 微信支付
     *
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
                .build().toWXPayNotSign(WmDetialsActivity.this);
        ToastUtil.showShort(this, "正在打开微信...");
    }
}
