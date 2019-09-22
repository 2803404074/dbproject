package com.dabangvr.common.weight;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dabangvr.R;
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.GlideLoadUtils;
import com.dabangvr.util.GlideRoundedCornersTransform;

import org.apache.commons.lang.StringUtils;

/**
 * 万能RecyclerHolder
 */
public class BaseRecyclerHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> views;
    private Context context;

    private BaseRecyclerHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        //指定一个初始为8
        views = new SparseArray<>(8);
    }

    /**
     * 取得一个RecyclerHolder对象
     *
     * @param context  上下文
     * @param itemView 子项
     * @return 返回一个RecyclerHolder对象
     */
    public static BaseRecyclerHolder getRecyclerHolder(Context context, View itemView) {
        return new BaseRecyclerHolder(context, itemView);
    }

    public SparseArray<View> getViews() {
        return this.views;
    }

    /**
     * 通过view的id获取对应的控件，如果没有则加入views中
     *
     * @param viewId 控件的id
     * @return 返回一个控件
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置字符串
     */
    public BaseRecyclerHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        if (!StringUtils.isEmpty(text) && !text.equals("null")) {
            tv.setText(text);
        }
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageResource(int viewId, int drawableId) {
        ImageView iv = getView(viewId);
        iv.setImageResource(drawableId);
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView iv = getView(viewId);
        iv.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageByUrl(int viewId, String url) {
        // TODO: 2019/9/9  
        if (!StringUtils.isEmpty(url) && !url.endsWith("null")) {
            //GlideLoadUtils.getInstance().glideLoad(context,url,(ImageView) getView(viewId));
            Glide.with(context).load(url).into((ImageView) getView(viewId));
        }
        return this;
    }

    public BaseRecyclerHolder setImageByUrl(int viewId, String url,int resources) {
        // TODO: 2019/9/9
        if (!StringUtils.isEmpty(url) && !url.endsWith("null")) {

            RequestOptions options = new RequestOptions();
            options.placeholder(resources); //设置加载未完成时的占位图
            options.error(R.drawable.ic_image_loading);
            //GlideLoadUtils.getInstance().glideLoad(context,url,(ImageView) getView(viewId));
            Glide.with(context).load(url).apply(options).into((ImageView) getView(viewId));
        }
        return this;
    }

    public BaseRecyclerHolder setImageByUrl(int viewId, String url, GlideRoundedCornersTransform.CornerType cornerType, float dpValue) {
        // TODO: 2019/9/9
        if (!StringUtils.isEmpty(url) && !url.endsWith("null")) {
            GlideLoadUtils.getInstance().glideLoadRoundangle(context, url, (ImageView) getView(viewId), dpValue, cornerType);
        }
        return this;
    }
}
