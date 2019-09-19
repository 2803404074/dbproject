package com.dabangvr.base.im;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.hyphenate.chat.EMMessage;

import java.util.List;

public abstract class ChatAdapter extends RecyclerView.Adapter<BaseRecyclerHolder>{
    private Context mContext;
    private List<EMMessage> msgs;

    public ChatAdapter(Context mContext, List<EMMessage> msgs) {
        this.mContext = mContext;
        this.msgs = msgs;
    }
    public void upData(List<EMMessage> msgs){
        msgs.clear();
        this.msgs = msgs;
        notifyDataSetChanged();
    }

    public void addData(List<EMMessage> msgs){
        msgs.addAll(msgs);
        notifyDataSetChanged();
    }

    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i==0){
            View layout = LayoutInflater.from(mContext).
                    inflate(R.layout.item_message_received, viewGroup, false);
            return BaseRecyclerHolder.getRecyclerHolder(mContext, layout);
        }else {
            View layout = LayoutInflater.from(mContext).
                    inflate(R.layout.item_message_sent, viewGroup, false);
            return BaseRecyclerHolder.getRecyclerHolder(mContext,layout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerHolder holder, int i) {
        convert(mContext, holder, msgs.get(i),i);
    }
    @Override
    public int getItemCount() {
        return msgs.size();
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = msgs.get(position);
        return message.direct() == EMMessage.Direct.RECEIVE ? 0 : 1;
    }
    public abstract void convert(Context mContext, BaseRecyclerHolder holder, EMMessage t,int position);
}
