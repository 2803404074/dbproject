package com.dabangvr.home.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.common.activity.AddressActivity;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.MyTextChangedListener;
import com.dabangvr.home.weight.PayDialog;
import com.dabangvr.model.CouponMo;
import com.dabangvr.model.goods.ParameterMo;
import com.dabangvr.model.order.DepGoods;
import com.dabangvr.model.order.OrderMo;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.ScreenUtils;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.TextUtil;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.util.WXPayUtils;
import com.dabangvr.wxapi.AppManager;
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

public class OrderActivity extends BaseNewActivity implements View.OnClickListener{
    private int dropType;//跳转状态 直接购买0、购物车购买1、团购购买2(未设置),重新购买3（未设置）

    @BindView(R.id.orther_address_name)
    TextView ad_name;//收货人

    @BindView(R.id.orther_address)
    TextView ad_address;//详细地址

    @BindView(R.id.orther_address_phone)
    TextView ad_phone;//联系人

    @BindView(R.id.ll_Coupon)
    LinearLayout llCoupon;//优惠券

    @BindView(R.id.or_money_count)
    TextView or_money_count;//总计

    @BindView(R.id.ms_recycler_view)
    RecyclerView recyclerView;//商品列表

    public Map<String, String> contents = new HashMap<>();//留言
    protected String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        AppManager.getAppManager().addActivity(this);
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
    public void initView() {
        dropType = getIntent().getIntExtra("dropType", 0);
        isLbroad = getIntent().getBooleanExtra("lbroadcast", false);
        tag = getIntent().getStringExtra("tag");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.orther_ok).setOnClickListener(this);//提交订单

        //如果不是重新支付则设置地址点击监听事件
        if (dropType != 3) {
            findViewById(R.id.orther_set_address).setOnClickListener(this);
        }
        findViewById(R.id.comment_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        llCoupon.setOnClickListener(this);
    }

    private OrderMo orderMo;
    @Override
    public void initData() {
        orderId = getIntent().getStringExtra("orderId");
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this, "token"));
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
                    ToastUtil.showShort(OrderActivity.this, "收货地址是空的...");
                }
            }
            @Override
            public void onFailed(Call call, IOException e) {
                ToastUtil.showShort(OrderActivity.this, "获取失败");
            }
        });

    }

    private void setDate() {
        //收货地址
        ad_name.setText(TextUtil.isNull2Url(orderMo.getReceivingAddress().getConsigneeName()));
        ad_phone.setText(TextUtil.isNull2Url(orderMo.getReceivingAddress().getConsigneePhone()));
        ad_address.setText(TextUtil.isNull2Url(orderMo.getReceivingAddress().getAddress()));

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
            //优惠券
            case R.id.ll_Coupon:
                showCoupon();
                break;
            case R.id.orther_ok: {
                if (orderMo.getReceivingAddress() != null && !TextUtil.isNullFor(orderMo.getReceivingAddress().getId())){
                    setLoaddingView(true);
                    showDialog();
                }else {
                    ToastUtil.showShort(getContext(),"完善收货地址~~有助于快速到货哦");
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

    private PayDialog payDialog;


    /**
     * 提交订单-成功就弹出支付窗口
     */
    private void showDialog() {
        HashMap<String, Object> map = new HashMap<>();
        //购买类型
        if (dropType == 0){//普通购买
            map.put("submitType", "buy");
        }
        if (dropType == 1){//购物车购买
            map.put("submitType", "cart");
        }
        if (dropType == 2){//团购购买
            map.put("submitType", "groupbuy");
        }
        //收货地址id
        map.put("addressId", String.valueOf(orderMo.getReceivingAddress().getId()));

        //各店铺的留言
        if (contents != null) {
            map.put("leaveMessage", contents.toString());//留言
        }
        OkHttp3Utils.getInstance(DyUrl.BASE).doPostJson(DyUrl.submitOrder, map, getToken(this), new TObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) throws JSONException {
                setLoaddingView(false);
                JSONObject object = new JSONObject(result);
                String orderSn = object.optString("orderSn");
                if (payDialog == null)payDialog = new PayDialog(getContext(),orderSn,"orderSnTotal","");
                payDialog.showDialog(or_money_count.getText().toString());
            }

            @Override
            public void onFailed(String msg) {
                setLoaddingView(false);
                ToastUtil.showShort(getContext(),msg);
                if (null != payDialog){
                    payDialog.desDialogView();
                }
            }
        });
    }

    /**
     * 优惠券相关
     */
    private BottomSheetDialog dialog;
    private List<CouponMo> couponMoList = new ArrayList<>();
    private void showCoupon(){
        couponMoList.add(new CouponMo(true));
        couponMoList.add(new CouponMo(false));
        couponMoList.add(new CouponMo(false));
        couponMoList.add(new CouponMo(false));
        couponMoList.add(new CouponMo(false));

        dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_coupon, null);
        TextView tvSize = view.findViewById(R.id.tv_couponSize);
        tvSize.setText("可用优惠券("+couponMoList.size()+")");
        RecyclerView recyc =  view.findViewById(R.id.recy_coupon);
        recyc.setLayoutManager(new LinearLayoutManager(this));
        final BaseLoadMoreHeaderAdapter couAdapter = new BaseLoadMoreHeaderAdapter<CouponMo>
                (this,recyc,couponMoList,R.layout.coupon_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, CouponMo o) {
                CheckBox checkBox = holder.getView(R.id.cb_check);
                checkBox.setChecked(o.isCheck());
            }
        };
        recyc.setAdapter(couAdapter);

        couAdapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                for (int i = 0; i < couponMoList.size(); i++) {
                    couponMoList.get(position).setCheck(i==position?true:false);
                    if (position == i){
                        couponMoList.get(position).setCheck(true);
                        continue;
                    }
                    couponMoList.get(i).setCheck(false);
                }
                couAdapter.updateDataa(couponMoList);
            }
        });

        int hight = (int) (Double.valueOf(ScreenUtils.getScreenHeight(this)) / 1.3);
        dialog.contentView(view)
                .heightParam(hight)
                .inDuration(200)
                .outDuration(200)
                .cancelable(true)
                .show();
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
