package com.dabangvr.my.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.home.activity.HxxqLastActivity;
import com.dabangvr.model.ShouCangMo;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.StatusBarUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.DyUrl;
import okhttp3.Call;

/**
 * 个人中心-收藏
 */
public class MyScActivity extends BaseNewActivity {

    @BindView(R.id.sc_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.rl_no_contant)
    RelativeLayout layout;

    @BindView(R.id.back)
    ImageView back;

    private List<ShouCangMo>lists;
    private BaseLoadMoreHeaderAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_my_sc;
    }


    @Override
    public void initView() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.sc_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BaseLoadMoreHeaderAdapter<ShouCangMo>(this,recyclerView,lists,R.layout.sc_recy_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, ShouCangMo o) {
                holder.setImageByUrl(R.id.sc_img,o.getListUrl());
                holder.setText(R.id.sc_title,o.getGoodsName());
                holder.setText(R.id.sc_markp,o.getMarketPrice());
                holder.setText(R.id.sc_price,o.getSellingPrice());
            }
        };
        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ShouCangMo shouCangMo = (ShouCangMo) adapter.getData().get(position);
                Intent intent = new Intent(MyScActivity.this, HxxqLastActivity.class);
                intent.putExtra("id", shouCangMo.getGoodsId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {
        Map<String,String>map = new HashMap<>();
        String token = getSPKEY(this,"token");
        map.put(DyUrl.TOKEN_NAME,token);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoodsCollectList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {

                if (StringUtils.isEmpty(result)){
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if(errno == 0){
                        if(500 == object.optInt("code")){
                            return ;
                        }
                        JSONObject dataObj = object.optJSONObject("data");
                        String str = dataObj.optString("goodsCollectVoList");
                        lists = JsonUtil.string2Obj(str,List.class, ShouCangMo.class);
                        if(lists == null || lists.size()<1){
                            layout.setVisibility(View.VISIBLE);
                        }else {
                            adapter.updateDataa(lists);
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
}
