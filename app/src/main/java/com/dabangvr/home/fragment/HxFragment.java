package com.dabangvr.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.base.BaseFragment;
import com.dabangvr.base.BaseFragmentFromType;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.home.activity.HxxqLastActivity;
import com.dabangvr.model.goods.BaseGoods;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.ToastUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.OkHttp3Utils;
import Utils.TObjectCallback;
import butterknife.BindView;
import config.DyUrl;

public class HxFragment extends BaseFragment {

    private String typeId;

    @BindView(R.id.iv_tips)
    ImageView ivTips;

    @BindView(R.id.hx_recyclerView)
    RecyclerView recyclerView;

    private BaseLoadMoreHeaderAdapter adapter;
    private List<BaseGoods>mData = new ArrayList<>();

    @Override
    public int layoutId() {
        return R.layout.hx_fragment;
    }

    public void setTypeId(String typeId){
        this.typeId = typeId;
    }
    @Override
    public void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseLoadMoreHeaderAdapter<BaseGoods>(getContext(),recyclerView,mData,R.layout.hx_fragment_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, BaseGoods o) {
                holder.setImageByUrl(R.id.hx_img,o.getListUrl());
                holder.setText(R.id.tv_name,o.getName());
                holder.setText(R.id.tv_title, StringUtils.isEmpty(o.getTitle())?"该列表的接口没有title字段":o.getTitle());

                TextView tvMarket = holder.getView(R.id.hx_market);
                tvMarket.setText(getString(R.string.app_money)+o.getMarketPrice());
                tvMarket.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                holder.setText(R.id.hx_money,o.getSellingPrice());
                holder.setText(R.id.sales_num,"已售"+o.getSalesVolume()+"件");
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BaseGoods goods = (BaseGoods) adapter.getData().get(position);
                Intent intent = new Intent(getContext(), HxxqLastActivity.class);
                intent.putExtra("id",goods.getGoodsId());
                //0普通，1拼团，2秒杀
                intent.putExtra("type",0);
                startActivity(intent);
            }
        });
    }

    private int page = 1;
    @Override
    public void initData() {
        ivTips.setVisibility(View.GONE);
        //根据分类id，查询分类商品
        Map<String,Object>map = new HashMap<>();
        map.put("categoryId",typeId);
        map.put("page",String.valueOf(page));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPostJson(DyUrl.getGoodsList, map, getToken(), new TObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    String str = object.optString("goodsList");
                    mData = JsonUtil.string2Obj(str, List.class,BaseGoods.class);
                    //分页
                    if (page > 1){
                        if (null == mData || mData.size()==0)return;
                        adapter.addAll(mData);

                        //刷新
                    }else {
                        if (null == mData || mData.size()==0){
                            ivTips.setVisibility(View.VISIBLE);
                            return;
                        }
                        adapter.updateDataa(mData);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String msg) {
                ivTips.setVisibility(View.VISIBLE);
                ToastUtil.showShort(getContext(),msg);
            }
        });
    }



}
