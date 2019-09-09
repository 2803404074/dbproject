package com.dabangvr.home.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.home.activity.HxxqLastActivity;
import com.dabangvr.home.activity.PtActivity;
import com.dabangvr.model.PtGoodsItem;
import com.dabangvr.model.TypeBean;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.TextUtil;
import com.example.hxxq.CustomScrollView;

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

@SuppressLint("ValidFragment")
public class PtFragment extends Fragment implements CustomScrollView.CallbacksBottom {

    private LinearLayout tvLine;
    private String id;
    private CustomScrollView customScrollView;

    private RecyclerView recyType;
    private BaseLoadMoreHeaderAdapter adapterType;
    private List<TypeBean> mDataType = new ArrayList<>();

    private RecyclerView recyGoods;
    private BaseLoadMoreHeaderAdapter adapterGoods;
    private List<PtGoodsItem> mDataGoods = new ArrayList<>();

    private Context context;
    private boolean IS_LOADED = false;
    private static int mSerial = 0;
    private int mTabPos = 0;//第几个商品类型
    private boolean isFirst = true;
    private int page = 1;


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (!IS_LOADED) {
                IS_LOADED = true;
                //这里执行加载数据的操作
                boolean isLoad = (boolean) msg.obj;
                setDate(isLoad);
            }
            return false;
        }
    });

    public PtFragment(int serial) {
        mSerial = serial;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = PtFragment.this.getContext();
        View view = inflater.inflate(R.layout.pt_page, container, false);
        initView(view);

        //设置页和当前页一致时加载，防止预加载
        if (isFirst && mTabPos == mSerial) {
            isFirst = false;
            sendMessage(false);
        }
        return view;
    }

    private void initView(View view) {
        tvLine = view.findViewById(R.id.tv_line);
        customScrollView = view.findViewById(R.id.custom_view);
        customScrollView.setBottomCallbacks(this);

        //类型列表(第一个页面将此view隐藏)
        recyType = view.findViewById(R.id.ms_recycler_view);
        recyType.setNestedScrollingEnabled(false);
        recyType.setLayoutManager(new GridLayoutManager(context,5));
        adapterType = new BaseLoadMoreHeaderAdapter<TypeBean>(context,recyType,mDataType,R.layout.dep_type_item) {
                @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, TypeBean o) {
                holder.setImageByUrl(R.id.iv_img, o.getCategoryImg());
                holder.setText(R.id.tv_name, o.getName());
            }
        };
        recyType.setAdapter(adapterType);

        adapterType.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, PtActivity.class);
                intent.putExtra("list",JsonUtil.obj2String(mDataType));
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });

        //推荐团购商品列表
        recyGoods = view.findViewById(R.id.recycler_view);
        recyGoods.setNestedScrollingEnabled(false);
        recyGoods.setLayoutManager(new GridLayoutManager(context,2));
        adapterGoods = new BaseLoadMoreHeaderAdapter<PtGoodsItem>(context,recyGoods,mDataGoods,R.layout.pt_goods_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, PtGoodsItem o) {
                holder.setImageByUrl(R.id.new_item_img, o.getListUrl());
                holder.setText(R.id.new_item_msg, o.getName());
                holder.setText(R.id.sales_num, "销量: "+TextUtil.isNull(o.getSalesVolume()));
                holder.setText(R.id.new_item_salse,TextUtil.isNull(o.getGroupPrice()));
            }
        };
        recyGoods.setAdapter(adapterGoods);

        adapterGoods.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PtGoodsItem ptGoodsItem = (PtGoodsItem) adapterGoods.getData().get(position);
                Intent intent = new Intent(context, HxxqLastActivity.class);
                intent.putExtra("id", ptGoodsItem.getId());
                intent.putExtra("type", 1);//商品详细页面，1代表拼团类型
                startActivity(intent);
            }
        });
    }

    /**
     * 获取网络数据
     */
    private void setDate(final boolean isLoad) {

        Map<String,String>map = new HashMap<>();
        if (StringUtils.isEmpty(id)){
            map.put("parentId","1");
        }else {
            map.put("parentId",id);
        }
        map.put("type","1");//代表拼团商品
        map.put("page",String.valueOf(page));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.CategoryList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")){
                        if (500 == object.optInt("code"))return;
                        JSONObject data = object.optJSONObject("data");
                        String str = data.optString("groupGoodsList");
                        mDataGoods = JsonUtil.string2Obj(str,List.class,PtGoodsItem.class);
                        if (null != mDataGoods){
                            if (isLoad){
                                adapterGoods.addAll(mDataGoods);
                            }else {
                                adapterGoods.updateData(mDataGoods);
                            }
                        }

                        if (!isLoad){
                            //如果id不为空，则获取二级分类
                            if (!StringUtils.isEmpty(id)){
                                String strList = data.optString("goodsCategoryList");
                                mDataType = JsonUtil.string2Obj(strList,List.class,TypeBean.class);
                                if (null != mDataType && mDataType.size()>0){
                                    tvLine.setVisibility(View.VISIBLE);
                                    adapterType.updateData(mDataType);
                                }
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

        //如果id等于空，则加载的是推荐拼团商品
        if (StringUtils.isEmpty(id)){

        }
    }

    public void sendMessage(boolean isLoad) {
        Message message = handler.obtainMessage();
        message.obj = isLoad;
        message.sendToTarget();
    }

    public void setTabPos(int mTabPos, String id) {
        this.mTabPos = mTabPos;
        this.id = id;
    }

    @Override
    public void loadMore() {

    }
}
