package com.dabangvr.my.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.SeeVideoActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.model.GwBean;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils;
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

/**
 * 我的直播历史
 */
@SuppressLint("ValidFragment")
public class FragmentMyZb extends Fragment {
    private BaseLoadMoreHeaderAdapter adapter;
    private RecyclerView recyclerView;
    private List<GwBean> mData = new ArrayList<>();
    private int page = 1;
    private Context context;
    private String authId;//主播id
    private SPUtils spUtils;

    public static FragmentMyZb newInstance(String authId) {
        FragmentMyZb fragment = new FragmentMyZb();
        Bundle args = new Bundle();
        args.putString("authId", authId);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("tag", "onCreateView()方法执行");
        if (getArguments() != null) {
            authId = getArguments().getString("authId");
        }
        context = FragmentMyZb.this.getContext();
        spUtils = new SPUtils(context,"db_user");
        View view = inflater.inflate(R.layout.recy_demo, container, false);
        initView(view);
        initData();
        return view;
    }

    /**
     * 获取某直播历史
     */
    private void initData() {
        if (StringUtils.isEmpty(authId))return;
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
        map.put("anchorId",authId);
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
                        mData = JsonUtil.string2Obj(str,List.class,GwBean.class);
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

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.ms_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context,2));
        adapter = new BaseLoadMoreHeaderAdapter<GwBean>(context,recyclerView,mData,R.layout.zb_gw_item) {
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
                Intent intent = new Intent(context, SeeVideoActivity.class);
                intent.putExtra("path",mData.get(position).getHlsPlayURL());
                intent.putExtra("title",mData.get(position).getLiveTitle());
                startActivity(intent);
            }
        });
    }
}
