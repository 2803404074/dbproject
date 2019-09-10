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
    private OnItemTableClick onItemTableClick;
    public interface OnItemTableClick{
        void viewName(String parentId,String name);
    }

    public void setOnItemTableClick(OnItemTableClick onItemTableClick){
        this.onItemTableClick = onItemTableClick;
    }

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
                final RightBean finalItem = item;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemTableClick.viewName(String.valueOf(finalItem.getParentId()),finalItem.getName());
                    }
                });
                break;
            case RightBean.TYPE_TITLE:
                helper.setText(R.id.tv_title, item.getName());
                break;
            default:
                break;
        }
    }
}
