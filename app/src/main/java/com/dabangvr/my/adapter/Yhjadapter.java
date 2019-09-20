package com.dabangvr.my.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.model.CouponBean;

import java.util.List;

import top.androidman.SuperButton;

public class Yhjadapter {
    private Context mContenxt;

    private BaseLoadMoreHeaderAdapter<CouponBean> adapter;

    public Yhjadapter(Context context) {
        this.mContenxt = context;
    }

    public BaseLoadMoreHeaderAdapter setAdaper(RecyclerView recyclerView, List<CouponBean> DiscontList, final int type) {
        adapter = new BaseLoadMoreHeaderAdapter<CouponBean>(mContenxt, recyclerView, DiscontList, R.layout.my_yhj_recy_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, CouponBean couponBean) {
                holder.setText(R.id.name, couponBean.getName());
                holder.setText(R.id.limit_two, couponBean.getLimit_two());
                holder.setText(R.id.favorablePrice, couponBean.getFavorablePrice());
                TextView title = holder.getView(R.id.title);
                LinearLayout llTitle = holder.getView(R.id.llTitle);
                holder.setText(R.id.title, couponBean.getTitle());
                holder.setText(R.id.date, couponBean.getStartDate() + "-" + couponBean.getEndDate());
                SuperButton linear_buy_state = holder.getView(R.id.linear_buy_state);
                ImageView image_state = holder.getView(R.id.image_state);
                String state = couponBean.getState();
                if (TextUtils.equals(state, "0")) {
                    linear_buy_state.setVisibility(View.VISIBLE);
                    image_state.setVisibility(View.GONE);
                    llTitle.setBackgroundResource(R.mipmap.my_yhj_item_blue);
                    title.setTextColor(mContext.getResources().getColor(R.color.colorDb3));
                } else {
                    linear_buy_state.setVisibility(View.GONE);
                    image_state.setVisibility(View.VISIBLE);
                    title.setTextColor(mContext.getResources().getColor(R.color.colorTextG1));
                    if (type == 0) {
                        image_state.setImageResource(R.mipmap.iocn_already_used);
                    } else {
                        image_state.setImageResource(R.mipmap.iocn_expired);
                    }
                    llTitle.setBackgroundResource(R.mipmap.my_yhj_item_gray);
                }
                linear_buy_state.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("luhuas", "onClick:点击buttom ");
                    }
                });
            }
        };
        return adapter;
    }

}
