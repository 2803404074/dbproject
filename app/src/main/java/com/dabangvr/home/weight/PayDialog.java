package com.dabangvr.home.weight;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.home.activity.OrderActivity;
import com.dabangvr.model.goods.GoodsDetails;
import com.dabangvr.util.SPUtils2;
import com.dabangvr.util.ScreenUtils;
import com.dabangvr.util.TextUtil;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.util.WXPayUtils;
import com.rey.material.app.BottomSheetDialog;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import Utils.TObjectCallback;
import config.DyUrl;
import okhttp3.Call;

public class PayDialog {
    private Context mContext;
    private BottomSheetDialog dialog;
    private String orderSn;
    private String payType;//直接购买用orderSnTotal；重新付款用orderSn
    private int checkPayId;//支付类型，0微信、2支付宝
    private String orderId;//重新支付需要

    public PayDialog(Context mContext, String orderSn, String payType, String orderId) {
        this.mContext = mContext;
        this.orderSn = orderSn;
        this.payType = payType;
        this.orderId = orderId;
    }

    public void showDialog(String price) {
        dialog = new BottomSheetDialog(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.orther_dialog, null);

        //支付价钱
        TextView tvPrice = view.findViewById(R.id.dialog_price);
        tvPrice.setText("¥" + price);

        RadioGroup radioGroup = view.findViewById(R.id.orther_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int cId) {
                //获取下标
                checkPayId = group.indexOfChild(group.findViewById(cId));
                //ToastUtil.showShort(mContext,String.valueOf(checkPayId));
            }
        });
        //立即支付，当前跳过支付，直接提交
        view.findViewById(R.id.zf_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPayId == 0) {//微信支付
                    getWXMESS();
                }
                if (checkPayId == 2) {//支付宝支付
                    ToastUtil.showShort(mContext, "支付宝支付维护中，将于2019.10.11开放");
                }
            }
        });
        dialog.contentView(view)
                .inDuration(200)
                .outDuration(200)
                .cancelable(true)
                .show();
    }

    /**
     * 获取微信支付需要的参数值
     *
     * @param
     *                * orderSnTotal和orderSn只能二选一
     *                * 1：立即购买=》直接支付     使用orderSnTotal
     *                * 2：立即购买=》取消=》重新付款     使用orderSnTotal
     *                * 3：立即购买=》取消=》查看订单=》订单详情=》去付款    使用orderSn
     *                * 4：购物车=》去付款    使用orderSnTotal
     *                * 5：购物车=》去付款=》取消=》重新付款    使用orderSnTotal
     *                * 6：购物车=》去付款=》取消=》查看订单=》订单详情=》去付款    使用orderSn
     */
    private void getWXMESS() {
        Map<String, Object> map = new HashMap<>();
        map.put("orderSn", orderSn);
        map.put("payOrderSnType", payType);//orderSnTotal
        OkHttp3Utils.getInstance(DyUrl.BASE).doPostJson(DyUrl.prepayOrder, map, (String) SPUtils2.instance(mContext).getkey("token", "")
                , new TObjectCallback<String>(DyUrl.BASE) {
                    @Override
                    public void onUi(String result) throws JSONException {
                        JSONObject object = new JSONObject(result);
                        toWXPay(object);
                    }
                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShort(mContext,msg);
                    }
                });
    }

    /**
     * 重新支付
     */
    private void prepayOrderAgain() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        map.put("payOrderSnType", payType);
        String token = (String) SPUtils2.instance(mContext).getkey("token", "");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPostJson(DyUrl.prepayOrderAgain, map, token, new TObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    toWXPay(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(mContext, msg);
            }
        });
    }

    private void toWXPay(JSONObject object) {
        WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
        builder.setAppId(object.optString("appid"))
                .setPartnerId(object.optString("partnerid"))
                .setPrepayId(object.optString("prepayid"))
                .setPackageValue(object.optString("package"))
                .setNonceStr(object.optString("noncestr"))
                .setTimeStamp(object.optString("timestamp"))
                .setSign(object.optString("sign"))
                .build().toWXPayNotSign(mContext);
        ToastUtil.showShort(mContext, "正在打开微信...");
    }

    public void desDialogView() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
