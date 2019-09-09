package com.dabangvr.home.activity.two;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dabangvr.R;

import java.util.List;

/**
 * @author parting_soul
 * @date 2019/1/13
 */
public class RightListAdapter extends BaseMultiItemQuickAdapter<RightBean, BaseViewHolder> {
    private Context context;

    public RightListAdapter(List<RightBean> data, Context context) {
        super(data);
        this.context = context;
        addItemType(RightBean.TYPE_ITEM, R.layout.adapter_right_sort);
        addItemType(RightBean.TYPE_TITLE, R.layout.adapter_item_title);
    }

    @Override
    protected void convert(BaseViewHolder helper, RightBean item) {
        switch (helper.getItemViewType()) {
            case RightBean.TYPE_ITEM:
                helper.setText(R.id.tv_name, item.getName());
                ImageView imageView = helper.getView(R.id.iv_img);
                Glide.with(context).load(item.getImgRes()).into(imageView);
                break;
            case RightBean.TYPE_TITLE:
                helper.setText(R.id.tv_title, item.getName());
                break;
            default:
                break;
        }
    }
}
