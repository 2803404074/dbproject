package com.dabangvr.home.activity;

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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.common.activity.AddressActivity;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.MyTextChangedListener;
import com.dabangvr.model.order.DepGoods;
import com.dabangvr.model.order.OrderMo;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.StatusBarUtil;
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
import config.DyUrl;
import okhttp3.Call;

public class OrderActivity extends BaseActivity implements View.OnClickListener{
    private int dropType;//跳转状态 1普通,  2拼团,  3秒杀,   4重新支付 ,5购物车

    private TextView ad_name;//收货人
    private TextView ad_address;//详细地址
    private TextView ad_phone;//联系人
    private TextView or_money_count;//总计
    private RecyclerView recyclerView;//商品列表
    public Map<String, String> contents = new HashMap<>();//留言

    private RadioButton rb;//选择支付类型的RadioButton


    public static OrderActivity instants;

    private String payOrderSnType;//支付类型
    private String orderSn;

    protected String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        instants = this;
    }

    @Override
    public int setLayout() {
        return R.layout.activity_order;
    }


    public static boolean isLbroad = false;//是否是直播跳转的页面，是的话监听用户下单，下单后通知主播和其它人下单信息
    public static String tag;//直播间身份

    /**
     * 控件初始化
     */
    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(OrderActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                //没有权限则申请权限
                ActivityCompat.requestPermissions(OrderActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }

        isLbroad = getIntent().getBooleanExtra("lbroadcast", false);
        tag = getIntent().getStringExtra("tag");

        ad_name = findViewById(R.id.orther_address_name);//收货人
        ad_phone = findViewById(R.id.orther_address_phone);//收货人手机号
        ad_address = findViewById(R.id.orther_address);//收货地址
        recyclerView = findViewById(R.id.ms_recycler_view);//商品列表
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(manager);

        or_money_count = findViewById(R.id.or_money_count);//合计
        tvJf = findViewById(R.id.tv_jf);//提示折扣了多少钱
        tvMyJf = findViewById(R.id.tv_my_jf);//我的积分

        findViewById(R.id.orther_ok).setOnClickListener(this);//提交订单

        Intent intent = getIntent();
        dropType = intent.getIntExtra("dropType", 100);

        if (dropType != 4) {
            //设置地址点击监听事件
            findViewById(R.id.orther_set_address).setOnClickListener(this);
        }

        findViewById(R.id.comment_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Switch sw = findViewById(R.id.sw_jf);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//使用

                    //抵扣后的订单总价为空
                    if (StringUtils.isEmpty(orderMo.getDeductPrice())){
                        ToastUtil.showShort(OrderActivity.this,"抱歉，该订单不支持积分抵扣");
                        sw.setChecked(false);
                    }else {
                        integralTag = "1";
                        or_money_count.setText(orderMo.getDeductOrderPrice());
                        tvJf.setVisibility(View.VISIBLE);
                        tvJf.setText("已使用积分抵扣:"+orderMo.getDeductPrice()+"元");
                    }

                } else {//不使用
                    integralTag = "-1";
                    or_money_count.setText(orderMo.getOrderTotalPrice());
                    tvJf.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    private OrderMo orderMo;
    private TextView tvJf;//显示已经折扣了多少钱
    private TextView tvMyJf;//我的积分

    @Override
    protected void initData() {
        HashMap<String, String> map = new HashMap<>();
        Intent intent = getIntent();
        payOrderSnType = intent.getStringExtra("payOrderSnType");
        String token = getSPKEY(this, "token");
        orderId = intent.getStringExtra("orderId");
        if (!StringUtils.isEmpty(orderId)) {
            map.put("orderId", orderId);
            setSPKEY(this, "orderId", orderId);
        }
        map.put(DyUrl.TOKEN_NAME, token);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getConfirmGoods, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 500) {
                        ToastUtil.showShort(OrderActivity.this, object.optString("msg"));
                        return;
                    }
                    int errno = object.optInt("errno");
                    if (errno == 0) {
                        if (500 == object.optInt("code")) {
                            ToastUtil.showShort(OrderActivity.this, "网络突然不行啦~~~");
                            return;
                        }
                        String dataStr = object.optString("data");
                        orderMo = JsonUtil.string2Obj(dataStr, OrderMo.class);
                        setDate();
                    }else {
                        ToastUtil.showShort(OrderActivity.this,object.optString("errmsg"));
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                    ToastUtil.showShort(OrderActivity.this, "收货地址是空的...");
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                ToastUtil.showShort(OrderActivity.this, "获取失败");
            }
        });

    }


    /**
     * 填充数据
     * 1）：商品列表
     * 2）：收货地址信息
     * 3）：合计
     *
     * @param
     */

    private void setDate() {
        //收货地址
        ad_name.setText(TextUtil.isNull2Url(orderMo.getReceivingAddress().getConsigneeName()));
        ad_phone.setText(TextUtil.isNull2Url(orderMo.getReceivingAddress().getConsigneePhone()));
        ad_address.setText(TextUtil.isNull2Url(orderMo.getReceivingAddress().getAddress()));

        //我的积分
        tvMyJf.setText(TextUtil.isNull(getSPKEY(this,"integral")));

        //订单总价
        or_money_count.setText(orderMo.getOrderTotalPrice());//总计(商品总价+物流总价)

        BaseLoadMoreHeaderAdapter adapter = new BaseLoadMoreHeaderAdapter<DepGoods>(this,recyclerView,orderMo.getDeptGoodsList(),R.layout.order_dep_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, DepGoods o) {
                //商家名称
                holder.setText(R.id.orther_shop_name, o.getDeptName());
                //商家logo
                holder.setImageByUrl(R.id.orther_shop_img, o.getDeptLogo());
                //本店总消费
                holder.setText(R.id.dep_count, "共:" + o.getDeptTotalPrice() + "元");

                //快递
                holder.setText(R.id.express, o.getDeptLogisticsTotalPrice() + "元");

                //店铺的商品信息
                RecyclerView recy = holder.getView(R.id.ms_recycler_view);
                recy.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
                BaseLoadMoreHeaderAdapter adapter1 = new BaseLoadMoreHeaderAdapter<DepGoods.GoodsList>(OrderActivity.this,recy,o.getGoodsList(),R.layout.order_goods_item) {
                    @Override
                    public void convert(Context mContext, BaseRecyclerHolder holder, DepGoods.GoodsList o) {
                        //商品信息
                        holder.setText(R.id.orther_info, o.getGoodsName());

                        //商品图片
                        holder.setImageByUrl(R.id.orther_img, StringUtils.isEmpty(o.getListUrl())? o.getGoodsListUrl():o.getListUrl());

                        //统一价钱
                        holder.setText(R.id.orther_salse, o.getRetailPrice() + "元");

                        //规格名称
                        holder.setText(R.id.or_item_gg, TextUtil.isNull2Url(o.getProductName()));

                        //商品数量
                        holder.setText(R.id.or_lv_item_num, "x"+o.getNumber());
                    }
                };
                recy.setAdapter(adapter1);
                //留言
                EditText editText = holder.getView(R.id.orther_msg);
                editText.addTextChangedListener(new MyTextChangedListener(holder, o.getDeptId(), contents));
            }
        };
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.orther_ok: {
                showDialog();
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
        //立即支付，当前跳过支付，直接提交
        view.findViewById(R.id.zf_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dropType == 4) {//重新支付
                    prepayOrderAgain();
                    bottomInterPasswordDialog.dismiss();
                } else if (TextUtil.isNullFor(orderMo.getReceivingAddress().getId())) {//首次支付，需要地址id
                    ToastUtil.showShort(OrderActivity.this, "请添加收货地址");
                } else {
                    comitOrther();
                    bottomInterPasswordDialog.dismiss();
                }
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
     * 重新支付
     */
    private void prepayOrderAgain() {
        HashMap<String, String> map = new HashMap<>();
        String token = getSPKEY(this, "token");
        Intent intent = getIntent();
        String orderId = intent.getStringExtra("orderId");
        map.put(DyUrl.TOKEN_NAME, token);
        map.put("orderId", orderId);
        map.put("payOrderSnType", payOrderSnType);// 直接购买用orderSnTotal；重新付款用orderSn
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

    private String integralTag = "-1";//是否使用积分，1使用，-1不使用

    /**
     * 第一步
     * 提交订单方法
     */
    private void comitOrther() {
        HashMap<String, String> map = new HashMap<>();
        String token = getSPKEY(this, "token");
        map.put(DyUrl.TOKEN_NAME, token);
        //直接购买(buy),购物车购买(cart),团购购买(groupbuy)
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        map.put("submitType", type);
        map.put("addressId", String.valueOf(orderMo.getReceivingAddress().getId()));//收货地址id
        if (contents != null) {
            map.put("leaveMessage", contents.toString());//留言
        }
        if (type.equals("live")) {
            String anchorId = intent.getStringExtra("anchorId");
            map.put("anchorId", anchorId);
        }
        map.put("integralTag", integralTag);//1使用，-1不使用
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.submitOrder, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (errno == 0) {//得到微信支付需要的东西
                        if (500 == object.optInt("code")) {
                            return;
                        }
                        getWXMESS(object.optString("orderSn"));//再次请求后端获取微信支付需要的参数值
                    } else if (errno == 1) {
                        ToastUtil.showShort(OrderActivity.this, object.optString("errmsg"));
                    } else {
                        ToastUtil.showShort(OrderActivity.this, object.optString("errmsg"));
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
     * 第二步，获取微信支付需要的参数值
     *
     * @param orderSn 订单号
     *                支付类型
     *                * orderSnTotal和orderSn只能二选一
     *                * 1：立即购买=》直接支付     使用orderSnTotal
     *                * 2：立即购买=》取消=》重新付款     使用orderSnTotal
     *                * 3：立即购买=》取消=》查看订单=》订单详情=》去付款    使用orderSn
     *                * 4：购物车=》去付款    使用orderSnTotal
     *                * 5：购物车=》去付款=》取消=》重新付款    使用orderSnTotal
     *                * 6：购物车=》去付款=》取消=》查看订单=》订单详情=》去付款    使用orderSn
     */
    private void getWXMESS(String orderSn) {
        this.orderSn = orderSn;
        HashMap<String, String> map = new HashMap<>();

        String token = getSPKEY(this, "token");
        map.put(DyUrl.TOKEN_NAME, token);
        map.put("orderSn", orderSn);
        map.put("payOrderSnType", payOrderSnType);//orderSnTotal
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.prepayOrder, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                //第三步，唤起微信支付
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (errno == 0) {
                        if (500 == object.optInt("code")) {
                            return;
                        }
                        JSONObject dataObj = object.optJSONObject("data");
                        setSPKEY(OrderActivity.this, "orderId", object.optString("orderId"));
                        toWXPay(dataObj);
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
        setSPKEY(this,"payType","goods");//支付什么东西，用于微信支付成功失败回调的处理
        WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
        builder.setAppId(object.optString("appid"))
                .setPartnerId(object.optString("partnerid"))
                .setPrepayId(object.optString("prepayid"))
                .setPackageValue(object.optString("package"))
                .setNonceStr(object.optString("noncestr"))
                .setTimeStamp(object.optString("timestamp"))
                .setSign(object.optString("sign"))
                .build().toWXPayNotSign(OrderActivity.this);
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
        if (requestCode == 100) {
            if (resultCode == 99) {
                ad_name.setText(data.getStringExtra("name"));
                ad_phone.setText(data.getStringExtra("phone"));
                ad_address.setText(data.getStringExtra("address"));
                String addressId = data.getStringExtra("id");
                orderMo.getReceivingAddress().setId(addressId);
            }
        }
    }
}
