package com.dabangvr.view.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;


import com.dabangvr.R;
import com.dabangvr.base.BaseFragment;
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils2;
import com.dabangvr.util.ToastUtil;
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
import bean.UserMess;
import butterknife.BindView;
import config.DyUrl;
import config.GiftUrl;
import okhttp3.Call;


public class HomeZBragment extends BaseFragment {

    @BindView(R.id.recycler_goods_id)
    RecyclerView recyclerViewGoods;
    private int intPage = 1;


    private HomeHotAdapter hotAdapter;
    private List<PlayMode> mData = new ArrayList<>();
    private int type = 0;
    private UserMess userMess;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceState = getArguments();
        if (savedInstanceState != null) {
            type = savedInstanceState.getInt("type");
        }
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_homehot_layout;
    }

    @Override
    public void initView() {
        initRecycler();

    }

    @Override
    public void initData() {


        setDate(type, intPage);
    }

    /**
     * 初始化商品列表
     */
    private void initRecycler() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewGoods.setLayoutManager(gridLayoutManager);
        recyclerViewGoods.addItemDecoration(new GridDividerItemDecoration(DensityUtil.dip2px(getContext(), 7), ContextCompat.getColor(getContext(), R.color.color_00d8d6d6)));
        hotAdapter = new HomeHotAdapter(getContext());
        recyclerViewGoods.setAdapter(hotAdapter);

        hotAdapter.setOnItemClickListener(new HomeHotAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // TODO: 2019/8/18 点击item
                ToastUtil.showShort(getActivity(), "点击" + position).show();
                Log.d("luhuas", "点击：" + position);
            }
        });
        //商品列表滑到底部后加载更多数据
        recyclerViewGoods.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    // TODO: 2019/8/18  加载更多
                    intPage++;
                    setDate(type, intPage);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    /**
     * 获取网络数据
     * <p>
     * 第一个item   my_orther_item_dep
     * 第二个item   my_orther_item_goods
     */
    private void setDate(int typex, int page) {
        final Map<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(page));
        map.put("limit", "10");

        String url = "";
        if (typex == 0) {
            String str = (String) SPUtils2.instance(getContext()).getkey("userMo", "");
            if (TextUtils.isEmpty(str)) {
                return;
            } else {
                userMess = JsonUtil.string2Obj(str, UserMess.class);

                map.put(DyUrl.TOKEN_NAME, userMess.getToken());
            }
            url = GiftUrl.getLiveShortVideoList;
        } else {
            url = GiftUrl.indexHot;
        }
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(url, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) return;
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")) {
                        if (500 == object.optInt("code")) {
                            return;
                        }
                        String data = object.optString("data");
                        List<PlayMode> playModes = JsonUtil.string2Obj(data, List.class, PlayMode.class);
                        if (playModes != null && playModes.size() > 0) {
                            mData.addAll(playModes);
                            hotAdapter.setData(mData);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

                ToastUtil.showShort(getActivity(), "网络连接超时");

            }
        });
    }

}
