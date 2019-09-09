package com.dabangvr.video.zb;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dabangvr.R;
import com.dabangvr.model.Goods;
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.GlideRoundedCornersTransform;
import com.dabangvr.video.adapter.ItemOnClickListener;

import java.util.ArrayList;
import java.util.List;

import bean.ZBMain;

public class VideoHeaderAdapter extends RecyclerView.Adapter<VideoHeaderAdapter.MyHolder> {
    private RecyclerView mRecyclerView;

    private List<Goods> data = new ArrayList<>();
    private Context mContext;

    private View VIEW_FOOTER;
    private View VIEW_HEADER;

    //Type
    private int TYPE_NORMAL = 1000;
    private int TYPE_HEADER = 1001;
    private int TYPE_FOOTER = 1002;

    public VideoHeaderAdapter(List<Goods> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return new MyHolder(VIEW_FOOTER);
        } else if (viewType == TYPE_HEADER) {
            return new MyHolder(VIEW_HEADER);
        } else {
            return new MyHolder(getLayout(R.layout.item_homehot_layout,parent));
        }
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        if (!isHeaderView(position) && !isFooterView(position)) {
            if (haveHeaderView()) position--;
            Goods goods = data.get(position);
            RequestOptions myOptions = new RequestOptions().optionalTransform
                    (new GlideRoundedCornersTransform(DensityUtil.dip2px(mContext, 15f)
                            , GlideRoundedCornersTransform.CornerType.ALL));
            Glide.with(mContext).load(goods.getListUrl()).apply(myOptions).into(holder.gsImg);
            RequestOptions myOptionss = new RequestOptions().optionalTransform
                    (new GlideRoundedCornersTransform(DensityUtil.dip2px(mContext, 12f)
                            , GlideRoundedCornersTransform.CornerType.ALL));
            Glide.with(mContext).load(goods.getListUrl()).apply(myOptionss).into(holder.gsImgs);

            final int finalPosition = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemOnClickListener!=null){
                        itemOnClickListener.onClickListener(finalPosition);
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        int count = (data == null ? 0 : data.size());
        if (VIEW_FOOTER != null) {
            count++;
        }

        if (VIEW_HEADER != null) {
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderView(position)) {
            return TYPE_HEADER;
        } else if (isFooterView(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        try {
            if (mRecyclerView == null && mRecyclerView != recyclerView) {
                mRecyclerView = recyclerView;
            }
            ifGridLayoutManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View getLayout(int layoutId,ViewGroup parent) {

        return LayoutInflater.from(parent.getContext()).inflate(layoutId,parent, false);
    }

    public void addHeaderView(View headerView) {
        if (haveHeaderView()) {
            throw new IllegalStateException("hearview has already exists!");
        } else {
            //避免出现宽度自适应
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            headerView.setLayoutParams(params);
            VIEW_HEADER = headerView;
            ifGridLayoutManager();
            notifyItemInserted(0);
        }

    }

    public void addFooterView(View footerView) {
        if (haveFooterView()) {
            throw new IllegalStateException("footerView has already exists!");
        } else {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            footerView.setLayoutParams(params);
            VIEW_FOOTER = footerView;
            ifGridLayoutManager();
            notifyItemInserted(getItemCount() - 1);
        }
    }

    private void ifGridLayoutManager() {
        if (mRecyclerView == null) return;
        final RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isHeaderView(position) || isFooterView(position)) ?
                            ((GridLayoutManager) layoutManager).getSpanCount() :
                            1;
                }
            });
        }
    }

    private boolean haveHeaderView() {
        return VIEW_HEADER != null;
    }

    public boolean haveFooterView() {
        return VIEW_FOOTER != null;
    }

    private boolean isHeaderView(int position) {
        return haveHeaderView() && position == 0;
    }

    private boolean isFooterView(int position) {
        return haveFooterView() && position == getItemCount() - 1;
    }


    public static class MyHolder extends RecyclerView.ViewHolder {
        ImageView gsImg;
        ImageView gsImgs;
        public MyHolder(View itemView) {
            super(itemView);
           gsImg = itemView.findViewById(R.id.zb_iv);
           gsImgs = itemView.findViewById(R.id.i_zb);
        }
    }

    public void addNewData(List<Goods> list) {
        if (list != null && !list.isEmpty()) {
            data.addAll(0, list);
            notifyDataSetChanged();
        }
    }

    public final void notifyItemRangeInsertedWrapper(int positionStart, int itemCount) {
        if (!isHeaderView(positionStart)) {
            notifyItemRangeInserted(positionStart, itemCount);
        } else {
            notifyDataSetChanged();
        }
    }
    public void addMoreData(List<Goods> list) {
        if (list != null && !list.isEmpty()) {
            int positionStart = data.size();
            data.addAll(data.size(), list);
           notifyDataSetChanged();
        }
    }
    private ItemOnClickListener itemOnClickListener;
    public void setItemOnClickListener(ItemOnClickListener onClickListener){
        itemOnClickListener=onClickListener;
    }

}

