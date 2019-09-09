package com.dbvr.imglibrary.ui.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dbvr.imglibrary.R;
import com.dbvr.imglibrary.model.Image;
import com.dbvr.imglibrary.widget.recyclerview.CommonRecycleAdapter;
import com.dbvr.imglibrary.widget.recyclerview.CommonViewHolder;


import java.util.ArrayList;

/**
 * Description:
 * Data：9/4/2018-3:14 PM
 *
 * @author yanzhiwen
 */
public class SelectedImageAdapter extends CommonRecycleAdapter<Image> {
    private Context mContext;

    public SelectedImageAdapter(Context context, ArrayList<Image> data, int layoutId) {
        super(context, data, layoutId);
        this.mContext = context;
    }

    @Override
    protected void convert(CommonViewHolder holder, Image image, int position) {
        ImageView iv = holder.getView(R.id.iv_selected_image);
        Glide.with(mContext)
                .load(image.getPath())
                .into(iv);
    }
}