package com.dabangvr.dep.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.onLoadMoreListener;
import com.dabangvr.model.DepDynamic;
import com.dabangvr.util.BottomImgSize;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.TextUtil;
import com.dabangvr.util.ToastUtil;

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
import Utils.PdUtil;
import config.DyUrl;
import okhttp3.Call;

public class DepSearchActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;
    private int page = 1;

    private String foodType;
    private String lon;
    private String lat;

    private List<DepDynamic> list = new ArrayList<>();

    private SPUtils spUtils;
    private PdUtil pdUtil;

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_dep_search;
    }

    @Override
    protected void initView() {
        spUtils = new SPUtils(this, "db_user");
        pdUtil = new PdUtil(this);

        editText = findViewById(R.id.et_search);
        editText.setOnClickListener(this);

        BottomImgSize bis = new BottomImgSize<>(this);
        bis.setImgSize(49, 49, 0, editText, R.drawable.h_search);


        foodType = String.valueOf(getIntent().getStringExtra("foodType"));
        lon = getIntent().getStringExtra("lon");
        lat = getIntent().getStringExtra("lat");

        /**
         * 店铺分类搜索
         */
        if (!StringUtils.isEmpty(foodType)) {
            if (foodType.equals("null")) {
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();//获取焦点 光标出现
            } else {
                pdUtil.showLoding("加载中...");
                editText.clearFocus();
                initData(0);
            }
        } else {
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();//获取焦点 光标出现
        }

        findViewById(R.id.start_search).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        recyclerView = findViewById(R.id.recy);//商家列表
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);


        adapter = new BaseLoadMoreHeaderAdapter<DepDynamic>(this, recyclerView, list, R.layout.dep_mess_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, DepDynamic o) {
                holder.setImageByUrl(R.id.logo, o.getLogo());
                holder.setText(R.id.name, o.getName());
                holder.setText(R.id.sales_num, "销量:" + TextUtil.isNull(o.getDeptSalesVolume()));
                holder.setText(R.id.dynamic, o.getActivityName());
                holder.setText(R.id.reach_time, o.getReachTime() + "分内送达");
                holder.setText(R.id.juli, "距您" + o.getJuli() + "米");
                holder.setText(R.id.distance, "配送距离:" + o.getDistance());
            }
        };

        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(DepSearchActivity.this, DepMessActivity.class);
                intent.putExtra("depId", list.get(position).getDeptId());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        //加载更多
        recyclerView.addOnScrollListener(new onLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                page += 1;
                pdUtil.showLoding("加载中...");
                initData(1);
            }
        });

        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(DepSearchActivity.this, DepMessActivity.class);
                intent.putExtra("depId", list.get(position).getDeptId());
                startActivity(intent);
            }
        });


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    pdUtil.showLoding("加载中...");
                    initData(0);
                }
                return false;
            }
        });
    }

    @Override
    protected void initData() {

    }

    private void initData(final int index) {
        Map<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token", ""));
        map.put("longitude", lon);
        map.put("latitude", lat);

        if (!StringUtils.isEmpty(editText.getText().toString())) {
            map.put("name", editText.getText().toString());
            foodType = null;
        } else if (!StringUtils.isEmpty(foodType)) {
            map.put("foodType", foodType);
        }
        map.put("limit", "10");
        map.put("page", String.valueOf(page));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.searchDeptList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
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
                            pdUtil.desLoding();
                            ToastUtil.showShort(DepSearchActivity.this, object.optString("errmsg"));
                            return;
                        }
                        JSONObject object1 = object.optJSONObject("data");
                        String str = object1.optString("deptList");
                        list = JsonUtil.string2Obj(str, List.class, DepDynamic.class);
                        if (null != list) {
                            if (index == 0) {
                                adapter.updateData(list);
                            }
                            if (index == 1) {
                                adapter.addAll(list);
                            }
                        }
                    }
                    pdUtil.desLoding();
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
        switch (v.getId()) {
            case R.id.start_search:
                pdUtil.showLoding("加载中...");
                initData(0);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.et_search:
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();//获取焦点 光标出现
                break;
            default:
                break;

        }
    }


}
