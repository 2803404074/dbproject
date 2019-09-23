package com.dabangvr.view.home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.GlideRoundedCornersTransform;
import com.dabangvr.util.TextUtil;
import com.dabangvr.video.fragment.model.PlayMode;

import java.util.ArrayList;
import java.util.List;

public class HomeHotAdapter extends RecyclerView.Adapter {

    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private LayoutInflater inflater;
    private List<PlayMode> playModes = new ArrayList<>();

    public HomeHotAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<PlayMode> mList) {
        this.playModes = mList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.onItemClickListener = mOnItemClickListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_homehot_layout, viewGroup, false);
        GoodsHolder liveHodler = new GoodsHolder(view, onItemClickListener);
        return liveHodler;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        PlayMode playMode = playModes.get(position);
        GoodsHolder goodsHolder = (GoodsHolder) viewHolder;

        if (!TextUtils.isEmpty(playMode.getLivestate()) && playMode.getLivestate() == "1") {
            goodsHolder.image_livestate.setVisibility(View.VISIBLE);
        } else {
            goodsHolder.image_livestate.setVisibility(View.GONE);
        }
        RequestOptions myOptions = new RequestOptions().optionalTransform
                (new GlideRoundedCornersTransform(DensityUtil.dip2px(mContext, 15f)
                        , GlideRoundedCornersTransform.CornerType.ALL));
        Glide.with(mContext).load(playMode.getCoverPath()).apply(myOptions).into(goodsHolder.image_coverPath);

        RequestOptions myOptionss = new RequestOptions().optionalTransform
                (new GlideRoundedCornersTransform(DensityUtil.dip2px(mContext, 12f)
                        , GlideRoundedCornersTransform.CornerType.ALL));
        Glide.with(mContext).load(playMode.getHeadUrl()).apply(myOptionss).into(goodsHolder.image_headUrl);

        goodsHolder.dsp_name.setText(playMode.getNickName());
        goodsHolder.dsp_likeCounts.setText(playMode.getPraseCount());


    }

    @Override
    public int getItemCount() {
        return playModes.size();
    }


    public class GoodsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OnItemClickListener onItemClickListener;
        private ImageView image_coverPath;
        private ImageView image_headUrl;
        private ImageView image_livestate;
        private TextView dsp_name;
        private TextView dsp_likeCounts;


        public GoodsHolder(@NonNull View itemView, OnItemClickListener mOnItemClicklistenner) {
            super(itemView);
            this.onItemClickListener = mOnItemClicklistenner;
            itemView.setOnClickListener(this);
            dsp_name = itemView.findViewById(R.id.dsp_name);
            dsp_likeCounts = itemView.findViewById(R.id.dsp_likeCounts);
            image_coverPath = itemView.findViewById(R.id.image_coverPath);
            image_headUrl = itemView.findViewById(R.id.image_headUrl);
            image_livestate = itemView.findViewById(R.id.image_livestate);
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
