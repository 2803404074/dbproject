package com.dabangvr.my.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.home.activity.HxxqLastActivity;
import com.dabangvr.home.activity.XrflActivity;
import com.dabangvr.model.CouponBean;
import com.dabangvr.model.ShouCangMo;
import com.dabangvr.my.adapter.Yhjadapter;
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.StatusBarUtil;
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
import butterknife.BindView;
import butterknife.OnClick;
import config.DyUrl;
import okhttp3.Call;
import top.androidman.SuperButton;

/**
 * 个人中心-我的优惠卷
 */
public class MyYhjActivity extends BaseNewActivity {

    @BindView(R.id.sc_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.rl_no_contant)
    RelativeLayout layout;


    private List<CouponBean> DiscontList = new ArrayList<>();
    private BaseLoadMoreHeaderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_my_yhj;
    }


    @Override
    public void initView() {
        recyclerView = findViewById(R.id.sc_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Yhjadapter yhjadapter = new Yhjadapter(this);
        //添加adapter
        adapter = yhjadapter.setAdaper(recyclerView, DiscontList,0);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {

        for (int i = 0; i < 4; i++) {
            CouponBean couponBean = new CouponBean();
            couponBean.setName("满减卷");
            couponBean.setDetails("全场满200元可立即使用");
            couponBean.setLimit_two("使用规则:该优惠卷仅限于贝壳类产品使用");
            couponBean.setTitle("仅限新用户使用");
            couponBean.setStartDate("2019.9.20");
            couponBean.setEndDate("2019.10.20");
            couponBean.setFavorablePrice("25");
            couponBean.setState("0");
            DiscontList.add(couponBean);
        }

//        Map<String,String>map = new HashMap<>();
//        String token = getSPKEY(this,"token");
//        map.put(DyUrl.TOKEN_NAME,token);
//        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoodsCollectList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
//            @Override
//            public void onUi(String result) {
//
//                if (StringUtils.isEmpty(result)){
//                    return ;
//                }
//                try {
//                    JSONObject object = new JSONObject(result);
//                    int errno = object.optInt("errno");
//                    if(errno == 0){
//                        if(500 == object.optInt("code")){
//                            return ;
//                        }
//                        JSONObject dataObj = object.optJSONObject("data");
//                        String str = dataObj.optString("goodsCollectVoList");
//                        lists = JsonUtil.string2Obj(str,List.class, ShouCangMo.class);
//                        if(lists == null || lists.size()<1){
//                            layout.setVisibility(View.VISIBLE);
//                        }else {
//                            adapter.updateDataa(lists);
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailed(Call call, IOException e) {
//
//            }
//        });


    }

    @OnClick({R.id.back, R.id.historical_record})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.historical_record:
                Intent intent = new Intent(this, MyYhjRecordActivity.class);
                startActivity(intent);
                break;
        }
    }
}
