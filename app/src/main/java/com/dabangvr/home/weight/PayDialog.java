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
    private int TAG;
    private String DIRECT_PAY_TAG = "";//直接支付
    private String AGAIN_PAY_TAG = "";//重新支付

    private BottomSheetDialog dialog;
    private String orderId;
    private String submitType;//提交类型 直接购买(buy),购物车购买(cart),团购购买(groupbuy)
    private String payType;//直接购买用orderSnTotal；重新付款用orderSn
    private int checkPayId;//支付类型，0微信、2支付宝

    private String liveMsg;//留言
    private String addressId;//地址id


    /**
     * 所有直接购买 所需要的构造方法
     * @param mContext
     * @param submitType 直接购买需要表示购买类型：0普通   1购物车   2团购
     * @param orderId
     * @param liveMsg
     * @param addressId
     */
    public PayDialog(Context mContext,int submitType,String orderId,String liveMsg, String addressId) {
        this.mContext = mContext;
        this.orderId = orderId;
        this.liveMsg = liveMsg;
        this.addressId = addressId;
        this.payType = DIRECT_PAY_TAG;//直接购买
        this.TAG = 100;//用于点击判断
        //购买类型
        if (submitType == 0){
            this.submitType = "buy";
        }
        if (submitType == 1){
            this.submitType = "cart";
        }
        if (submitType == 2){
            this.submitType = "groupbuy";
        }
        //ToastUtil.showShort(mContext,"直接支付");
    }

    /**
     *  重新支付的入口
     * @param mContext
     * @param orderId
     */
    public PayDialog(Context mContext,String orderId) {
        this.mContext = mContext;
        this.orderId = orderId;
        this.payType = AGAIN_PAY_TAG;//重新支付
        this.TAG = 200;//用于点击判断
        //ToastUtil.showShort(mContext,"重新支付");
    }

    public void showDialog(String price){
        dialog = new BottomSheetDialog(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.orther_dialog, null);

        //支付价钱
        TextView tvPrice = view.findViewById(R.id.dialog_price);
        tvPrice.setText("¥"+price);

        RadioGroup radioGroup = view.findViewById(R.id.orther_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int cId) {
                //获取下标
                checkPayId = group.indexOfChild(group.findViewById(cId));
                ToastUtil.showShort(mContext,String.valueOf(checkPayId));
            }
        });
        //立即支付，当前跳过支付，直接提交
        view.findViewById(R.id.zf_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPayId == 0){//微信支付
                    if (TAG == 100){
                        submitToOrder();
                    }
                    if (TAG == 200){
                        prepayOrderAgain();
                    }
                }
                if (checkPayId == 2){//支付宝支付
                    ToastUtil.showShort(mContext,"支付宝支付维护中，将于2019.10.11开放");
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
     * 直接支付
     */
    private void submitToOrder() {
        HashMap<String, Object> map = new HashMap<>();
        String token = (String) SPUtils2.instance(mContext).getkey("token","");
        map.put("submitType", submitType);//购买类型
        map.put("addressId", addressId);//收货地址id
        map.put("leaveMessage", liveMsg);//留言
        OkHttp3Utils.getInstance(DyUrl.BASE).doPostJson(DyUrl.submitOrder, map, token,new TObjectCallback<String>(DyUrl.BASE) {
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
        String token = (String) SPUtils2.instance(mContext).getkey("token","");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPostJson(DyUrl.prepayOrderAgain, map,token, new TObjectCallback<String>(DyUrl.BASE) {
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
                ToastUtil.showShort(mContext,msg);
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

    public void desDialogView(){
        if (dialog!=null){
            dialog.dismiss();
        }
    }
}
