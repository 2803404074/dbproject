package com.dabangvr.video.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.video.zb.VideoZBFragment;

import java.util.ArrayList;
import java.util.List;

import bean.ZBMain;

public class AddtabAdapter extends RecyclerView.Adapter<AddtabAdapter.MyHolder> {
    private RecyclerView mRecyclerView;

    private List<VideoZBFragment.SataterBean> data = new ArrayList<>();
    private Context mContext;

    private View VIEW_FOOTER;
    private View VIEW_HEADER;

    //Type
    private int TYPE_NORMAL = 1000;
    private int TYPE_HEADER = 1001;
    private int TYPE_FOOTER = 1002;

    public AddtabAdapter(List<VideoZBFragment.SataterBean> data, Context mContext) {
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
            return new MyHolder(getLayout(R.layout.item_vido_tab_layout, parent));
        }
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        if (!isHeaderView(position) && !isFooterView(position)) {
            if (haveHeaderView()) position--;
            TextView content = holder.itemView.findViewById(R.id.tv_tab_title);
            VideoZBFragment.SataterBean sataterBean = data.get(position);
            if (sataterBean != null) {
                String state = sataterBean.getState();
                if (!TextUtils.isEmpty(state)) {
                    if (TextUtils.equals(state, "1")) {
                        content.setTextColor(Color.parseColor("#D81B60"));
                        content.setTextSize(17);
                    } else {
                        content.setTextColor(Color.parseColor("#000000"));
                        content.setTextSize(14);
                    }

                }
                content.setText(data.get(position).getTatile());
            }
            final int finalPosition = position;
            content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (textOnClickListener != null) {
                        textOnClickListener.onClickListener(finalPosition);
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

    private View getLayout(int layoutId, ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(layoutId, parent, false);
    }

    public void addHeaderView(View headerView) {
        if (haveHeaderView()) {
            throw new IllegalStateException("hearview has already exists!");
        } else {
            //避免出现宽度自适应
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
    public void setNewData(List<VideoZBFragment.SataterBean> newData){
        if (newData != null && !newData.isEmpty()) {
            data.clear();
            data.addAll(newData);
            notifyDataSetChanged();
        }
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public MyHolder(View itemView) {
            super(itemView);
        }
    }

    private TextOnClickListener textOnClickListener;

    public void setTextOnClickListener(TextOnClickListener textOnClickListener) {
        this.textOnClickListener = textOnClickListener;
    }

}

