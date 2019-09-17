package com.dabangvr.common.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.model.AddressBean;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.StringUtils;
import com.dabangvr.util.ToastUtil;

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

/**
 * 个人中心-设置-我的收货地址
 */
public class AddressActivity extends AppCompatActivity implements View.OnClickListener {

    private SPUtils spUtils;
    private List<AddressBean> datas = new ArrayList();
    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;


    //若是订单页面跳转到本页面的话，以下基本信息是返回的地址资料
    private String id;
    private String name;
    private String address;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        initView();
        initData();
    }

    private void initData() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token", ""));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.addressList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (errno == 0) {
                        if (500 == object.optInt("code")) {
                            ToastUtil.showShort(AddressActivity.this, "服务数据更新中");
                            return;
                        }
                        JSONObject object1 = object.optJSONObject("data");
                        String str = object1.optString("receivingAddressVoList");
                        datas = JsonUtil.string2Obj(str, List.class, AddressBean.class);
                        adapter.updateData(datas);
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

    //修改，删除
    private void updateAddress(String requestUrl, Map<String, String> map) {
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token", ""));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(requestUrl, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (errno == 0) {
                        if (500 == object.optInt("code")) {
                            ToastUtil.showShort(AddressActivity.this, "服务器更新中");
                        }
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

    private void initView() {
        spUtils = new SPUtils(this, "db_user");
        findViewById(R.id.linear_address).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.text_addads).setOnClickListener(this);
        recyclerView = findViewById(R.id.recy);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new BaseLoadMoreHeaderAdapter<AddressBean>(this, recyclerView, datas, R.layout.address_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, final AddressBean o) {
                holder.setText(R.id.tv_title, o.getConsigneeName());//收货人
                if (!TextUtils.isEmpty(o.getConsigneePhone())) {
                    holder.setText(R.id.ad_phone, StringUtils.hidePhoneNum(o.getConsigneePhone()));//电话
                }

                holder.setText(R.id.ad_address, o.getProvinceOne() + " " + o.getCityOne() + " " + o.getAreaOne() + " " + o.getAddress());//详细地址
                CheckBox checkBox = holder.getView(R.id.cb_checkbox);

                holder.getView(R.id.add_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, String> map = new HashMap<>();
                        map.put("id", o.getId());
                        updateAddress(DyUrl.addressDelete, map);
                        datas.remove(o);
                        adapter.updateData(datas);
                    }
                });

                holder.getView(R.id.add_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AddressActivity.this, SetAdreessActivity.class);
                        intent.putExtra("id", o.getId());
                        intent.putExtra("address", o.getAddress());
                        intent.putExtra("zipCode", o.getZipCode());
                        intent.putExtra("consigneeName", o.getConsigneeName());
                        intent.putExtra("consigneePhone", o.getConsigneePhone());
                        intent.putExtra("isDefault", o.getIsDefault());
                        intent.putExtra("province", o.getProvince());
                        intent.putExtra("city", o.getCity());
                        intent.putExtra("area", o.getArea());
                        intent.putExtra("provinceOne", o.getProvinceOne());
                        intent.putExtra("cityOne", o.getCityOne());
                        intent.putExtra("areaOne", o.getAreaOne());
                        startActivityForResult(intent, 100);
                    }
                });

                //1默认
                if (1 == o.getIsDefault()) {
                    checkBox.setChecked(true);
                    id = o.getId();
                    name = o.getConsigneeName();
                    phone = o.getConsigneePhone();
                    address = o.getAddress();
                } else {
                    checkBox.setChecked(false);
                }

                //设置默认
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (o.isCheck()) {//判断当前是否选中
                            //如果已经是选中的话则不做任何操作
                        } else {
                            //未选中
                            for (AddressBean addressBean : datas) {//循环地址，获取选中状态

                                //如果选的是当前的按钮，将他的状态该成选中
                                if (addressBean.getId().equals(o.getId())) {
                                    addressBean.setCheck(true);
                                    addressBean.setIsDefault(1);
                                } else {
                                    //其他的都设置为不选中
                                    addressBean.setCheck(false);
                                    addressBean.setIsDefault(0);
                                }
                            }
                            adapter.updateData(datas);
                            for (AddressBean addressBean : datas) {
                                if (addressBean.getIsDefault() == 1) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("id", addressBean.getId());
                                    map.put("isDefault", "1");
                                    updateAddress(DyUrl.addressUpdate, map);
                                    id = addressBean.getId();
                                    name = addressBean.getConsigneeName();
                                    phone = addressBean.getConsigneePhone();
                                    address = addressBean.getAddress();
                                    break;
                                }
                            }

                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_address: {
                Intent intent = new Intent(this, SetAdreessActivity.class);
                startActivityForResult(intent, 100);
                overridePendingTransition(R.anim.activity_open_right, R.anim.activity_open_left);
                break;
            }
            case R.id.text_addads: {
                Intent intent = new Intent(this, SetAdreessActivity.class);
                startActivityForResult(intent, 100);
                overridePendingTransition(R.anim.activity_open_right, R.anim.activity_open_left);
                break;
            }
            case R.id.back:
                if (null != datas && datas.size() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    intent.putExtra("address", address);
                    intent.putExtra("phone", phone);
                    setResult(99, intent);
                }
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_open_left, R.anim.activity_close);
        if (null != datas && datas.size() > 0) {
            Intent intent = new Intent();
            intent.putExtra("id", id);
            intent.putExtra("name", name);
            intent.putExtra("address", address);
            intent.putExtra("phone", phone);
            setResult(99, intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            initData();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (null != datas && datas.size() > 0) {
            Intent intent = new Intent();
            intent.putExtra("id", id);
            intent.putExtra("name", name);
            intent.putExtra("address", address);
            intent.putExtra("phone", phone);
            setResult(100, intent);
        }
        finish();
    }
}