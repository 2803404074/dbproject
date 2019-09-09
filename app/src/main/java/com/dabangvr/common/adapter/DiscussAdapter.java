package com.dabangvr.common.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dabangvr.R;

public class DiscussAdapter extends RecyclerView.Adapter {

    private Context mContext;



    public DiscussAdapter(Context context){
        this.mContext = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discuss_layout, parent, false);
        DiscussViewHodler discussViewHodler = new DiscussViewHodler(view);
        return discussViewHodler;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class DiscussViewHodler extends RecyclerView.ViewHolder{


        public DiscussViewHodler(View itemView) {
            super(itemView);
        }
    }

}
