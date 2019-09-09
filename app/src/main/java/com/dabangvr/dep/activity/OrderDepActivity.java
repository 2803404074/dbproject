package com.dabangvr.dep.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.common.activity.AddressActivity;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.dep.model.DepOrderGoods;
import com.dabangvr.util.GlideLoadUtils;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.TextUtil;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.util.WXPayUtils;
import com.rey.material.app.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import Utils.PdUtil;
import config.DyUrl;
import okhttp3.Call;

public class OrderDepActivity extends BaseActivity implements View.OnClickListener {
    private boolean isDynamic = false;
    private PdUtil pdUtil;
    private TextView ad_name;//收货人
    private TextView ad_address;//详细地址
    private TextView ad_phone;//联系人
    private TextView or_money_count;//底部总计

    private ImageView ivDep;//店铺图标
    private TextView tvDepName;//店铺名称
    private TextView tvDepPrice;//店铺物流总价
    private TextView tvPagPrice;//打包费
    private TextView tvDynamicName;//活动名称
    private TextView tvDynamicNameOrder;//活动名称
    private TextView tvDynamicPrice;//活动后的订单价

    private LinearLayout llDynamic;//活动控件

    private RadioButton rb;//选择支付类型的RadioButton

    private BaseLoadMoreHeaderAdapter adapter;
    private RecyclerView recyclerView;//商品列表
    private List<DepOrderGoods>mData = new ArrayList<>();

    private EditText contents;//留言
    private String addressId;

    public static OrderDepActivity instants;
    private boolean hasAddress = false;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        instants = this;
    }

    @Override
    public int setLayout() {
        return R.layout.activity_order_dep;
    }

    /**
     * 控件初始化
     */
    @Override
    protected void initView() {
        pdUtil = new PdUtil(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(OrderDepActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                //没有权限则申请权限
                ActivityCompat.requestPermissions(OrderDepActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
        ad_name = findViewById(R.id.orther_address_name);//收货人
        ad_phone = findViewById(R.id.orther_address_phone);//收货人手机号
        ad_address = findViewById(R.id.orther_address);//收货地址

        recyclerView = findViewById(R.id.ms_recycler_view);//商品列表
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(manager);
        or_money_count = findViewById(R.id.or_money_count);//合计
        findViewById(R.id.orther_ok).setOnClickListener(this);//提交订单
        findViewById(R.id.orther_set_address).setOnClickListener(this);//设置地址
        findViewById(R.id.comment_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivDep = findViewById(R.id.orther_shop_img);
        tvDepName = findViewById(R.id.orther_shop_name);
        tvDepPrice = findViewById(R.id.dep_count);
        tvPagPrice = findViewById(R.id.dep_pakagprice);
        tvDynamicName = findViewById(R.id.tv_dynamic_name);
        tvDynamicNameOrder = findViewById(R.id.tv_dynamic);
        tvDynamicPrice = findViewById(R.id.tv_dynamic_price);

        llDynamic = findViewById(R.id.ll_dynamic);

        contents = findViewById(R.id.tv_ly);

        contents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contents.requestFocus();
            }
        });

        adapter = new BaseLoadMoreHeaderAdapter<DepOrderGoods>(this,recyclerView,mData,R.layout.order_goods_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, DepOrderGoods bean) {
                //商品信息
                holder.setText(R.id.orther_info, bean.getName());

                //商品图片
                holder.setImageByUrl(R.id.orther_img, bean.getListUrl());

                //统一价钱
                holder.setText(R.id.orther_salse, bean.getSellingPrice() + "元");

                //规格
                holder.setText(R.id.or_item_gg, bean.getProductValue());

                //商品数量
                holder.setText(R.id.or_lv_item_num, bean.getNumber());
            }
        };

        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void initData() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this, "token"));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getConfirmGoods2Delivery, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 500) {
                        ToastUtil.showShort(OrderDepActivity.this, object.optString("msg"));
                    }
                    int errno = object.optInt("errno");
                    if (errno == 0) {
                        JSONObject dataObj = object.optJSONObject("data");
                        if (500 == dataObj.optInt("code")) {
                            ToastUtil.showShort(OrderDepActivity.this, dataObj.optString("msg"));
                            return ;
                        }

                        //获取地址
                        JSONObject addressObj = dataObj.optJSONObject("receivingAddress");

                        //获取门店信息
                        JSONObject deptObj = dataObj.optJSONObject("deptVo");
                        GlideLoadUtils.getInstance().glideLoad(OrderDepActivity.this,deptObj.optString("logo"),ivDep);
                        tvDepName.setText(TextUtil.isNull2Url(dataObj.optString("name")));

                        //获取订单总价，总物流价,打包费
                        tvDepPrice.setText("配送费:"+TextUtil.isNull(dataObj.optString("distributionPrice")));
                        or_money_count.setText(TextUtil.isNull2Url(dataObj.optString("totalPrice")));
                        tvPagPrice.setText("打包费"+TextUtil.isNull(dataObj.optString("packingPrice")));
                        tvDynamicName.setText(TextUtil.isNull2Url(dataObj.optString("dynamicName")));

                        //如果没有活动，隐藏活动控件
                        if (TextUtil.isNullFor(dataObj.optString("dynamicName"))){
                            llDynamic.setVisibility(View.GONE);
                        }else {
                            //显示活动
                            tvDynamicNameOrder.setText(TextUtil.isNull2Url(dataObj.optString("dynamicName")));

                            //显示活动后的价钱
                            tvDynamicPrice.setText(TextUtil.isNull2Url(dataObj.optString("totalPrice")));
                            isDynamic = true;

                            //将订单原价设置为横线样式
                            //or_money_count.setTextAppearance();
                        }

                        //获取商品列表
                        String str = dataObj.optString("goodsVoList");
                        List<DepOrderGoods> list = JsonUtil.string2Obj(str, List.class, DepOrderGoods.class);
                        adapter.updateData(list);

                        setAddress(addressObj);
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                    ToastUtil.showShort(OrderDepActivity.this, "收货地址是空的...");
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                ToastUtil.showShort(OrderDepActivity.this, "获取失败");
            }
        });

    }


    /**
     * @param object 地址object
     * @param
     */
    private void setAddress(JSONObject object) {
        if (TextUtil.isNullFor(object.optString("id"))){
            hasAddress = false;
            ad_name.setText("---");
            ad_phone.setText("---");
            ad_address.setText("---");
        }else {
            ad_name.setText(object.optString("consigneeName"));
            ad_phone.setText(object.optString("consigneePhone"));
            ad_address.setText(object.optString("address"));
            addressId = object.optString("id");
            hasAddress = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.orther_ok: {
                if (hasAddress){
                    showDialog();
                }else {
                    ToastUtil.showShort(this,"请先设置地址");
                }
                break;
            }
            case R.id.orther_set_address: {
                Intent intent = new Intent(this, AddressActivity.class);
                startActivityForResult(intent, 100);
                break;
            }
        }
    }

    /**
     * 支付弹窗
     */
    private void showDialog() {
        //初始化底部弹窗
        final BottomSheetDialog bottomInterPasswordDialog = new BottomSheetDialog(this);

        //初始化 - 底部弹出框布局
        View view = LayoutInflater.from(this).inflate(R.layout.orther_dialog, null);
        TextView tvPrice = view.findViewById(R.id.dialog_price);
        tvPrice.setText(or_money_count.getText().toString());
        RadioGroup radioGroup = view.findViewById(R.id.orther_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rb = group.findViewById(checkedId);
                //ToastUtil.showShort(OrderActivity.this,"选中的是id = "+checkedId+","+rb.getText().toString());
            }
        });
        //提交订单
        view.findViewById(R.id.orther_radio_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (rb.getId()) {
                    case 1: {//微信支付
                        ToastUtil.showShort(OrderDepActivity.this, "正在跳转微信支付页面...");
                        break;
                    }
                    case 2: {//支付宝支付
                        ToastUtil.showShort(OrderDepActivity.this, "支付宝支付控件更新中");
                        break;
                    }
                }
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

        //立即支付，当前跳过支付，直接提交
        view.findViewById(R.id.zf_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comitOrther();
            }
        });
    }

    /**
     * 第一步
     * 提交订单方法
     */
    private void comitOrther() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this, "token"));
        map.put("payType", "1");//1微信，2支付宝
        map.put("addressId", String.valueOf(addressId));//收货地址id
        if (contents != null) {
            map.put("leaveMessage", contents.getText().toString());//留言
        }
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.submitGoods2Delivery, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (errno == 0) {//得到微信支付需要的东西
                        if (500 == object.optInt("code")) {
                            return ;
                        }
                        JSONObject dataObj = object.optJSONObject("data");
                        //将订单本地存储，用于重新付款
                        setSPKEY(OrderDepActivity.this,"orderId",object.optString("orderId"));

                        //吊起微信
                        toWXPay(dataObj);
                    } else if (errno == 1) {
                        ToastUtil.showShort(OrderDepActivity.this, object.optString("errmsg"));
                    } else {
                        ToastUtil.showShort(OrderDepActivity.this, object.optString("errmsg"));
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
                .build().toWXPayNotSign(OrderDepActivity.this);
        ToastUtil.showShort(this, "正在打开微信...");
    }

    /**
     * 权限申请
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "申请失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){
            if (resultCode == 99){
                ad_name.setText(data.getStringExtra("name"));
                ad_phone.setText(data.getStringExtra("phone"));
                ad_address.setText(data.getStringExtra("address"));
                addressId = data.getStringExtra("id");
                hasAddress = true;
            }
        }
    }
}
