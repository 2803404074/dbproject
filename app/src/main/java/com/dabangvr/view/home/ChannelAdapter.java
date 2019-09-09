package com.dabangvr.view.home;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dabangvr.R;
import com.dabangvr.model.MenuMo;

import java.util.ArrayList;
import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter {

    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private LayoutInflater inflater;
    private List<MenuMo> mMenuData = new ArrayList<>();


    public ChannelAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<MenuMo> menuData) {
        this.mMenuData = menuData;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.onItemClickListener = mOnItemClickListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_home_channel_layout, viewGroup, false);
        ChannelHodler liveHodler = new ChannelHodler(view, onItemClickListener);
        return liveHodler;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        MenuMo menuMo = mMenuData.get(position);
        ChannelHodler channelHodler = null;
        channelHodler = (ChannelHodler) viewHolder;
        channelHodler.tvName.setText(menuMo.getTitle());
        String iconUrl = menuMo.getIconUrl();
        String substring = iconUrl.substring(iconUrl.length() - 4, iconUrl.length());
        if (iconUrl != null) {
            if (TextUtils.equals(substring, ".gif")) {
//                Glide.with(mContext).load(menuMo.getIconUrl()).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(channelHodler.chgImg);
            } else {
            }
                Glide.with(mContext).load(menuMo.getIconUrl()).into(channelHodler.chgImg);
        }


//        Glide.with(mContext).load(menuMo.getIconUrl()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new GlideDrawableImageViewTarget(imageView, 1));

//        Glide.with(mContext).load(menuMo.getIconUrl()).diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .listener(new RequestListener<Integer, GlideDrawable>() {
//
//                    @Override
//                    public boolean onException(Exception arg0, Integer arg1,
//                                               Target<GlideDrawable> arg2, boolean arg3) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource,
//                                                   Integer model, Target<GlideDrawable> target,
//                                                   boolean isFromMemoryCache, boolean isFirstResource) {
//                        // 计算动画时长
//                        GifDrawable drawable = (GifDrawable) resource;
//                        GifDecoder decoder = drawable.getDecoder();
//                        int duration=1;
//                        for (int i = 0; i < drawable.getFrameCount(); i++) {
//                            duration += decoder.getDelay(i);
//                        }
//                        //发送延时消息，通知动画结束
////                        handler.sendEmptyMessageDelayed(MESSAGE_SUCCESS,
////                                duration);
//                        return false;
//                    }
//                }).into(new GlideDrawableImageViewTarget(channelHodler.chgImg, 5));

    }

    @Override
    public int getItemCount() {
        return mMenuData.size();
    }


    public class ChannelHodler extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OnItemClickListener monItemClickListener;
        private ImageView chgImg;
        private TextView tvName;

        public ChannelHodler(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.monItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
            chgImg = (ImageView) itemView.findViewById(R.id.iv_img);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
        }

        @Override
        public void onClick(View v) {
            if (monItemClickListener != null) {
                monItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }


}
