package com.example.gangedrecyclerview;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.potholibrary.R;

import java.util.List;


public class ClassifyDetailAdapter extends RvAdapter<RightBean> {

    public ClassifyDetailAdapter(Context context, List<RightBean> list, RvListener listener) {
        super(context, list, listener);
    }


    @Override
    protected int getLayoutId(int viewType) {
        return viewType == 0 ? R.layout.item_title : R.layout.item_classify_detail;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).isTitle() ? 0 : 1;
    }

    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new ClassifyHolder(view, viewType, listener);
    }

    public class ClassifyHolder extends RvHolder<RightBean> {
        TextView tvCity;
        ImageView avatar;
        TextView tvTitle;


        TextView tvPrice;
        TextView tvSalseNum;
        TextView tv_original_price;

        public ClassifyHolder(View itemView, int type, RvListener listener) {
            super(itemView, type, listener);
            switch (type) {
                case 0:
                    tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
                    break;
                case 1:
                    tvCity = (TextView) itemView.findViewById(R.id.tvCity);
                    avatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
                    tvPrice = itemView.findViewById(R.id.tv_price);
                    tvSalseNum = itemView.findViewById(R.id.tv_salse);
                    tv_original_price = itemView.findViewById(R.id.tv_original_price);
                    break;
            }

        }

        @Override
        public void bindHolder(RightBean sortBean, int position) {
            int itemViewType = ClassifyDetailAdapter.this.getItemViewType(position);
            switch (itemViewType) {
                case 0:
                    tvTitle.setText(sortBean.getName());
                    break;
                case 1:
                    tvCity.setText(sortBean.getName());//商品名称
                    String aa = sortBean.getListUrl();
                    Glide.with(mContext).load(aa).into(avatar);//图片

                    tvPrice.setText(sortBean.getSellingPrice());
                    tvSalseNum.setText("月销" + sortBean.getSalesVolume());
                    tv_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    String marketPrice = sortBean.getMarketPrice();
                    if (TextUtils.isEmpty(marketPrice) || TextUtils.equals("null", marketPrice)) {
                        marketPrice = "0";
                    }
                    tv_original_price.setText("￥" + marketPrice);

                    break;
            }

        }
    }
}
