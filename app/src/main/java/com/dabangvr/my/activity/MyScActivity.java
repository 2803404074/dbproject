package com.dabangvr.my.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
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
import config.DyUrl;
import okhttp3.Call;

/**
 * 个人中心-收藏
 */
public class MyScActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private List<ShouCangMo>lists;
    private RelativeLayout layout;
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
    protected void initView() {
        layout = findViewById(R.id.rl_no_contant);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.sc_recycler);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutmanager);
        getDataFromHttp();
    }

    @Override
    protected void initData() {

    }

    private void getDataFromHttp() {
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
                        }
                        //看具体数据

                        BaseRecyclerAdapter adapter = new BaseRecyclerAdapter<ShouCangMo>(MyScActivity.this,lists,R.layout.sc_recy_item) {
                            @Override
                            public void convert(BaseRecyclerHolder holder, ShouCangMo item, int position, boolean isScrolling) {
                                holder.setImageByUrl(R.id.sc_img,item.getListUrl());
                                holder.setText(R.id.sc_title,item.getGoodsName());
                                holder.setText(R.id.sc_markp,item.getMarketPrice());
                                holder.setText(R.id.sc_price,item.getSellingPrice());
                            }
                        };

                        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(RecyclerView parent, View view, int position) {
                                Intent intent = new Intent(MyScActivity.this, HxxqLastActivity.class);
                                intent.putExtra("id", lists.get(position).getGoodsId());
                                intent.putExtra("type", 0);
                                startActivity(intent);
                            }
                        });
                        recyclerView.setAdapter(adapter);
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
