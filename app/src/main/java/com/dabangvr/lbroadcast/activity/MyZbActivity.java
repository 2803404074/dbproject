package com.dabangvr.lbroadcast.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.activity.SeeVideoActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.model.GwBean;
import com.dabangvr.my.activity.JfActivity;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.TextUtil;
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

public class MyZbActivity extends BaseActivity implements View.OnClickListener {

    private BaseLoadMoreHeaderAdapter adapter;
    private RecyclerView recyclerView;
    private List<GwBean> mData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_my_zb;
    }

    @Override
    protected void initView() {
        TextView tvJf = findViewById(R.id.tv_jf);
        TextView tvName = findViewById(R.id.tv_name);
        SimpleDraweeView ivHead = findViewById(R.id.iv_head);

        tvName.setText(getSPKEY(this,"name"));
        ivHead.setImageURI(getSPKEY(this,"head"));
        tvJf.setText(TextUtil.isNull(getSPKEY(this,"integral")));


        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.iv_my_jf).setOnClickListener(this);
        recyclerView = findViewById(R.id.recy);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new BaseLoadMoreHeaderAdapter<GwBean>(this,recyclerView,mData,R.layout.zb_gw_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, GwBean o) {
                //封面图
                holder.setImageByUrl(R.id.iv_live_bg,o.getCoverUrl());

                //直播状态
                holder.setText(R.id.tv_status,"历史记录");

                //观看看数量（屏蔽）
                holder.getView(R.id.tv_see_count).setVisibility(View.GONE);


                //观看数量
                holder.setText(R.id.tv_see_count,o.getLookCount());

                if (null != o.getLiveGoodsList() && o.getLiveGoodsList().size()>0){
                    //商品图片
                    holder.setImageByUrl(R.id.iv_goods_img,o.getLiveGoodsList().get(0).getListUrl());
                    //商品标题
                    holder.setText(R.id.tv_goods_name,o.getLiveGoodsList().get(0).getName());
                    //商品价钱
                    holder.setText(R.id.tv_goods_price,"￥"+o.getLiveGoodsList().get(0).getSellingPrice());
                }else {
                    holder.getView(R.id.ll_live_goods).setVisibility(View.GONE);
                }

                //直播标题
                holder.setText(R.id.title,o.getLiveTitle());
                //主播名称
                holder.setText(R.id.tv_auth,o.getAnchorName());
                //主播头像

                SimpleDraweeView draweeView = holder.getView(R.id.drawee_img);
                draweeView.setImageURI(o.getHeadUrl());
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MyZbActivity.this, SeeVideoActivity.class);
                intent.putExtra("path",mData.get(position).getHlsPlayURL());
                intent.putExtra("title",mData.get(position).getLiveTitle());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {
        Map<String,String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (getSPKEY(this,"token")));
        map.put("anchorId",getSPKEY(this,"anchorId"));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(GiftUrl.getActivityRecords, map,
                new GsonObjectCallback<String>(DyUrl.BASE) {
                    @Override
                    public void onUi(String result) {
                        if (StringUtils.isEmpty(result)) return;
                        try {
                            JSONObject object = new JSONObject(result);
                            if (0==object.optInt("errno")){
                                if (500 == object.optInt("code"))return;
                                String str =  object.optString("data");
                                mData = JsonUtil.string2Obj(str, List.class, GwBean.class);
                                if (null != mData && mData.size()>0){
                                    adapter.updateData(mData);
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
            case R.id.back:
                finish();
                break;
            case R.id.iv_my_jf:
                Intent intent = new Intent(MyZbActivity.this, JfActivity.class);
                startActivity(intent);
                break;
                default:break;
        }
    }
}
