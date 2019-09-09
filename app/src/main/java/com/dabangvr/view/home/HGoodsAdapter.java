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
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.GlideRoundedCornersTransform;
import com.dabangvr.util.TextUtil;
import com.shehuan.niv.NiceImageView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HGoodsAdapter extends RecyclerView.Adapter {

    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private LayoutInflater inflater;
    private List<Goods> goodsList = new ArrayList<>();

    public HGoodsAdapter(Context context){
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<Goods> mList){
        this.goodsList = mList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.onItemClickListener = mOnItemClickListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_homegs_layout,viewGroup,false);
        GoodsHolder liveHodler = new GoodsHolder(view,onItemClickListener);
        return liveHodler;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Goods goods = goodsList.get(position);
        GoodsHolder goodsHolder = (GoodsHolder) viewHolder;
//        Glide.with(mContext).load(goods.getListUrl()).into(goodsHolder.gsImg);

        RequestOptions myOptions = new RequestOptions().optionalTransform
                (new GlideRoundedCornersTransform(DensityUtil.dip2px(mContext,15f)
                        , GlideRoundedCornersTransform.CornerType.TOP));
        Glide.with(mContext).load(goods.getListUrl()).apply(myOptions).into(goodsHolder.gsImg);

        goodsHolder.tvTitle.setText(goods.getName());
        goodsHolder.tvPric.setText("￥" + TextUtil.isNull(goods.getSellingPrice()));
        goodsHolder.tvSalesVolume.setText("销量 ：" + TextUtil.isNull(goods.getSalesVolume()));
        List<ActivityBean> listActivity = goods.getActivitys();
        if(listActivity != null && listActivity.size() > 0){
        }
        goodsHolder.tvNameType.setText("海鲜专卖");
        if(listActivity != null && listActivity.size() > 0){
            goodsHolder.tvPreferential.setText(listActivity.get(0).getName());
        }

    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }


    public class GoodsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private OnItemClickListener onItemClickListener;
        private ImageView gsImg;
        private TextView  tvTitle;
        private TextView  tvNameType;
        private TextView  tvPreferential;
        private TextView  tvPric;
        private TextView  tvSalesVolume;

        public GoodsHolder(@NonNull View itemView,OnItemClickListener mOnItemClicklistenner) {
            super(itemView);
            this.onItemClickListener = mOnItemClicklistenner;
            itemView.setOnClickListener(this);
            gsImg =  itemView.findViewById(R.id.nice_iv0);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title_id);
            tvNameType = (TextView) itemView.findViewById(R.id.tv_nameType_id);
            tvPreferential = (TextView) itemView.findViewById(R.id.tv_preferential_id);
            tvPric = (TextView) itemView.findViewById(R.id.tv_pric_id);
            tvSalesVolume = (TextView) itemView.findViewById(R.id.tv_sales_volume_id);
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListener != null){
                onItemClickListener.onItemClick(v,getPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }


}
