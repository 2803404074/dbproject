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
import com.dabangvr.R;
import com.dabangvr.model.MenuMo;
import com.dabangvr.model.TypeBean;

import java.util.ArrayList;
import java.util.List;

public class TypeRVAdapter extends RecyclerView.Adapter {

    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private LayoutInflater inflater;
    private List<TypeBean> typeData = new ArrayList<>();


    public TypeRVAdapter(Context context){
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<TypeBean> typeBeans){
        this.typeData = typeBeans;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.onItemClickListener = mOnItemClickListener;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_home_type_layout,viewGroup,false);
        ChannelHodler liveHodler = new ChannelHodler(view,onItemClickListener);
        return liveHodler;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        TypeBean typeBean = typeData.get(position);
        ChannelHodler channelHodler = (ChannelHodler)viewHolder;
        channelHodler.tvName.setText(typeBean.getName());
        Glide.with(mContext).load(typeBean.getCategoryImg()).into(channelHodler.chgImg);

    }

    @Override
    public int getItemCount() {
        return typeData.size();
    }


    public class ChannelHodler extends RecyclerView.ViewHolder implements View.OnClickListener{
        private OnItemClickListener monItemClickListener;
        private ImageView chgImg;
        private TextView tvName;

        public ChannelHodler(@NonNull View itemView,OnItemClickListener onItemClickListener) {
            super(itemView);
            this.monItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
            chgImg = (ImageView) itemView.findViewById(R.id.iv_img);
            tvName = (TextView)itemView.findViewById(R.id.tv_name);
        }

        @Override
        public void onClick(View v) {
            if(monItemClickListener != null){
                monItemClickListener.onItemClick(v,getPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }


}
