package com.dabangvr.common.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.model.CommentBean;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.DyUrl;
import config.JsonUtil;
import okhttp3.Call;

public class CommentListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;
    private List<CommentBean> mData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);
        initView();
        setCommentData();
        getDataFromHttp(true);
    }


    private void initView() {
        recyclerView = findViewById(R.id.recy);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void setCommentData() {
        adapter = new BaseLoadMoreHeaderAdapter<CommentBean>(this,recyclerView,mData,R.layout.hxxq_comment_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, CommentBean bean) {
                holder.setImageByUrl(R.id.hx_lv_icon,bean.getHeadUrl());
                holder.setText(R.id.tv_name,bean.getNickName());
                holder.setText(R.id.hx_lv_content,bean.getCommentContent());
            }

        };
        recyclerView.setAdapter(adapter);
    }



    private void getDataFromHttp(final boolean isFlush) {
        HashMap<String, String> map = new HashMap<>();
        Intent intent = getIntent();
        String goodsId = intent.getStringExtra("goodsId");
        map.put("goodsId", goodsId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getCommentListTwo, map, new GsonObjectCallback<String>(DyUrl.BASE) {

            @Override
            public void onUi(String result) {
                if(StringUtils.isEmpty(result)){
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
                        String commentVoList = dataObj.optString("commentVoList");
                        mData = JsonUtil.string2Obj(commentVoList,List.class,CommentBean.class);
                        if(isFlush){
                            adapter.updateData(mData);
                        }else {
                            adapter.addAll(mData);
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
