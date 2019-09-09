package com.dabangvr.my.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.model.FollowMo;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;

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
import config.GiftUrl;
import okhttp3.Call;

public class MyFanseActivity extends BaseActivity implements View.OnClickListener {

    private BaseLoadMoreHeaderAdapter adapter;
    private RecyclerView recyclerView;
    private List<FollowMo> mData = new ArrayList<>();

    private LinearLayout llNoLive;
    private TextView tvMess;
    private TextView tvFanseNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_my_fanse;
    }

    @Override
    protected void initView() {
        int type = getIntent().getIntExtra("type",0);//1是关注跳转，-1是粉丝跳转
        TextView title = findViewById(R.id.tv_type);
        TextView followType = findViewById(R.id.tv_follow_type);
        if (type == 1){
            title.setText("我的关注");
            followType.setText("关注量");
        }else {
            title.setText("我的粉丝");
            followType.setText("粉丝量");
        }
        llNoLive = findViewById(R.id.ll_no_live);
        tvMess = findViewById(R.id.tv_message);
        tvFanseNum = findViewById(R.id.tv_fanse_num);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.tv_go_iden).setOnClickListener(this);
        recyclerView = findViewById(R.id.recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseLoadMoreHeaderAdapter<FollowMo>(this,recyclerView,mData,R.layout.follow_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, FollowMo o) {
                SimpleDraweeView draweeView = holder.getView(R.id.drawee_img);
                draweeView.setImageURI(o.getHeadUrl());
                holder.setText(R.id.tv_name,o.getNickName());
            }
        };

        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (StringUtils.isEmpty(mData.get(position).getAnchorId())){
                    ToastUtil.showShort(MyFanseActivity.this,"该粉丝不是主播，无法查看动态信息");
                    return;
                }
                Intent intent = new Intent(MyFanseActivity.this,AuthMessActivity.class);
                intent.putExtra("authId",mData.get(position).getAnchorId());
                intent.putExtra("userId",mData.get(position).getId());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME,getSPKEY(this,"token"));
        map.put("type","fans");//anchor查询我关注的用户
        map.put("page","1");
        map.put("limit","20");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(GiftUrl.myFans, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result))return;

                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("errno") == 1){
                        //提示用户去申请主播
                        llNoLive.setVisibility(View.VISIBLE);
                        tvMess.setText(object.optString("errmsg"));
                    }
                    if (object.optInt("errno") == 0){
                        String str = object.optString("data");
                        mData = JsonUtil.string2Obj(str,List.class,FollowMo.class);
                        if (null != mData &&mData.size()>0){
                            adapter.updateData(mData);
                            tvFanseNum.setText(String.valueOf(mData.size()));
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
            case R.id.tv_go_iden:
                Intent intent = new Intent(MyFanseActivity.this, StartOpenZhuBoActivity.class);
                startActivity(intent);
                break;
            case R.id.back:
                finish();
                break;
                default:break;
        }
    }


}
