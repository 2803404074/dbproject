package com.dabangvr.view.home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dabangvr.R;
import com.dabangvr.model.ActivityBean;
import com.dabangvr.model.Goods;
import com.dabangvr.model.GoodsLabelBean;
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.GlideRoundedCornersTransform;
import com.dabangvr.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class HGoodsLabelAdapter extends RecyclerView.Adapter {

    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private LayoutInflater inflater;
    private List<GoodsLabelBean> goodsLabelBeanList = new ArrayList<>();

    public HGoodsLabelAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<GoodsLabelBean> mList) {
        this.goodsLabelBeanList = mList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.onItemClickListener = mOnItemClickListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_homegs_label_layout, viewGroup, false);
        GoodsHolder liveHodler = new GoodsHolder(view, onItemClickListener);
        return liveHodler;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        GoodsLabelBean goodsLabelBean = goodsLabelBeanList.get(position);
        GoodsHolder goodsHolder = (GoodsHolder) viewHolder;
//        Glide.with(mContext).load(goods.getListUrl()).into(goodsHolder.gsImg);

//        RequestOptions myOptions = new RequestOptions().optionalTransform
//                (new GlideRoundedCornersTransform(DensityUtil.dip2px(mContext,15f)
//                        , GlideRoundedCornersTransform.CornerType.TOP));
//        Glide.with(mContext).load(goods.getListUrl()).apply(myOptions).into(goodsHolder.gsImg);

        goodsHolder.tv_name.setText(goodsLabelBean.getName());
        goodsHolder.tv_name.setBackgroundResource(goodsLabelBean.getColour());
    }

    @Override
    public int getItemCount() {
        return goodsLabelBeanList.size();
    }


    public class GoodsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OnItemClickListener onItemClickListener;
        private TextView tv_name;


        public GoodsHolder(@NonNull View itemView, OnItemClickListener mOnItemClicklistenner) {
            super(itemView);
            this.onItemClickListener = mOnItemClicklistenner;
            itemView.setOnClickListener(this);
            tv_name = itemView.findViewById(R.id.tv_name);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }


}
