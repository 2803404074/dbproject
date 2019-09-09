package com.dabangvr.my.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.StatusBarUtil;
import com.example.model.mess.MessMsg;
import com.example.mylibrary.MessDetailsActivity;

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
import config.DyUrl;
import okhttp3.Call;

/**
 * 个人中心-消息
 */
public class MyMessageActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private List<MessMsg>list = new ArrayList<>();
    private BaseLoadMoreHeaderAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_my_message;
    }

    @Override
    protected void initView() {
        findViewById(R.id.my_fanse).setOnClickListener(this);
        findViewById(R.id.iv_follow).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.my_mess_recy);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutmanager);
        adapter = new BaseLoadMoreHeaderAdapter<MessMsg>(this,recyclerView,list,R.layout.my_mess_recyitem) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, MessMsg o) {
                holder.setText(R.id.name,o.getName());
                holder.setImageByUrl(R.id.img_mess,o.getLogo());
                holder.setText(R.id.mes,o.getMsg());
                holder.setText(R.id.datas,o.getDate());
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MyMessageActivity.this,MessDetailsActivity.class);
                intent.putExtra("logo",list.get(position).getLogo());
                intent.putExtra("mes",list.get(position).getMsg());
                intent.putExtra("name",list.get(position).getName());
                intent.putExtra("data",list.get(position).getDate());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this,"token"));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.deptOrAnchor, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if(StringUtils.isEmpty(result)){
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    if(0 == object.optInt("errno")){
                        JSONObject dataObj = object.optJSONObject("data");
                        String str = dataObj.optString("messageList");
                        if(!StringUtils.isEmpty(str)){
                            list = JsonUtil.string2Obj(str,List.class,MessMsg.class);
                            if(null != list){
                                adapter.updateData(list);
                            }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.my_fanse:
                Intent intent = new Intent(MyMessageActivity.this,MyFanseActivity.class);
                intent.putExtra("type",-1);
                startActivity(intent);
                break;
            case R.id.iv_follow:
                Intent intent2 = new Intent(MyMessageActivity.this,MyFanseActivity.class);
                intent2.putExtra("type",-1);
                startActivity(intent2);
                break;
        }
    }
}
