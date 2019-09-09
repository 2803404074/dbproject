package com.dabangvr.my.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dabangvr.R;
import com.dabangvr.common.activity.SeeVideoActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.util.DateUtil;
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.GlideLoadUtils;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils;
import com.dabangvr.video.fragment.model.PlayMode;
import com.dabangvr.widget.GridDividerItemDecoration;

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
 * 我发布的短视频
 */
@SuppressLint("ValidFragment")
public class FragmentMyShort extends Fragment {
    private BaseLoadMoreHeaderAdapter adapter;
    private RecyclerView recyclerView;
    private List<PlayMode> mData = new ArrayList<>();
    private int page = 1;
    private Context context;
    private SPUtils spUtils;
    private String uId;
    private boolean isFirst = true;
    public static FragmentMyShort newInstance(String uId) {
        FragmentMyShort fragment = new FragmentMyShort();
        Bundle args = new Bundle();
        args.putString("uId", uId);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("tag", "onCreateView()方法执行");
        isFirst = false;
        if (getArguments() != null) {
            uId = getArguments().getString("uId");
        }
        context = FragmentMyShort.this.getContext();
        spUtils = new SPUtils(context,"db_user");
        View view = inflater.inflate(R.layout.recy_demo, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.ms_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context,3));
        recyclerView.addItemDecoration(new GridDividerItemDecoration(DensityUtil.dip2px(getContext(),7), ContextCompat.getColor(getContext(),R.color.white)));
        adapter = new BaseLoadMoreHeaderAdapter<PlayMode>(context,recyclerView,mData,R.layout.my_short_item) {
            @Override
            public void convert(Context mContext, final BaseRecyclerHolder holder, final PlayMode o) {
                holder.setImageByUrl(R.id.iv_head,o.getHeadUrl());
                holder.setText(R.id.tv_title,o.getTitle());
                holder.setText(R.id.tv_dz_num,"获赞:"+o.getPraseCount());
                holder.setText(R.id.tv_dz_id,"获赞：" + o.getPraseCount());
                holder.setText(R.id.tv_comment_num,"评论:"+o.getCommentCount());
                holder.setText(R.id.tv_data, DateUtil.stampToDate(o.getAddTime()));

                ImageView imageView = holder.getView(R.id.iv_url);
                GlideLoadUtils.getInstance().glideLoadVideo(context,o.getVideoUrl(),imageView,1000000);
            }
        };
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, SeeVideoActivity.class);
                intent.putExtra("path",mData.get(position).getVideoUrl());
                intent.putExtra("title",mData.get(position).getTitle());
                startActivity(intent);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            if (!isFirst){
                initData();
            }

        }
    }

    private void initData() {
        if (StringUtils.isEmpty(uId))return;
        final Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
        map.put("userId",uId);
        map.put("page","1");
        map.put("limit","20");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(GiftUrl.getLiveShortVideoList, map,
                new GsonObjectCallback<String>(DyUrl.BASE) {
                    @Override
                    public void onUi(String result) {
                        if (StringUtils.isEmpty(result))return;
                        try {
                            JSONObject object = new JSONObject(result);
                            if (0 == object.optInt("errno")){
                                if (500 == object.optInt("code"))return;

                                String str = object.optString("data");
                                mData = JsonUtil.string2Obj(str,List.class,PlayMode.class);
                                if (null!=mData && mData.size()>0){
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
}
