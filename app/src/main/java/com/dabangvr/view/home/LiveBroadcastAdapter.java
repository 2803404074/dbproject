package com.dabangvr.view.home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dabangvr.R;

import java.util.ArrayList;
import java.util.List;

import bean.ZBMain;

public class LiveBroadcastAdapter extends RecyclerView.Adapter {

    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private LayoutInflater inflater;
    private List<ZBMain> topData = new ArrayList<>();


    public LiveBroadcastAdapter(Context context){
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setData(List<ZBMain> topData){
        this.topData = topData;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_livebroadcast_layout,viewGroup,false);
        LiveHodler liveHodler = new LiveHodler(view,onItemClickListener);
        return liveHodler;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (position==0){

            return;
        }
    }

    @Override
    public int getItemCount() {
        return 12;
    }


    public class LiveHodler extends RecyclerView.ViewHolder implements View.OnClickListener{
        private OnItemClickListener mOnItemClickListener;
        private ImageView liveImg;
        private TextView tvLive;
        private TextView tvLiveUserName;

        public LiveHodler(@NonNull View itemView,OnItemClickListener onItemClickListener) {
            super(itemView);
            this.mOnItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
            liveImg = (ImageView) itemView.findViewById(R.id.v_user_hean);
            tvLive = (TextView)itemView.findViewById(R.id.v_guanzhu);
            tvLiveUserName = itemView.findViewById(R.id.zb_id);
        }

        @Override
        public void onClick(View v) {
            if(mOnItemClickListener != null){
                mOnItemClickListener.onItemClick(v,getPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }


}
