package com.dabangvr.base.im;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import com.dabangvr.util.DensityUtil;

public class MyAnimatorUtil {
    private Context mContext;
    private View view;//需要动画的view
    private ValueAnimator valueAnimator;

    public MyAnimatorUtil(Context mContext, View view) {
        this.mContext = mContext;
        this.view = view;
    }

    public void startAnimator(){
        if (view.getVisibility() == View.VISIBLE)return;
        view.measure(0, 0);
        valueAnimator = ValueAnimator.ofInt(0, DensityUtil.dip2px(mContext,50)).setDuration(300);

        setTing(View.VISIBLE);
    }

    public void stopAnimator(){
        if (view.getVisibility() == View.INVISIBLE)return;
        view.measure(0, 0);
        valueAnimator = ValueAnimator.ofInt(DensityUtil.dip2px(mContext,50),0).setDuration(300);
        setTing(View.INVISIBLE);
    }

    /**
     * 控件显示，从0高度到指定高度
     */
    public void startHeight(View view){
        boolean show;
        view.measure(0, 0);
        if (view.getVisibility() == View.VISIBLE){
            valueAnimator = ValueAnimator.ofInt(DensityUtil.dip2px(mContext,100),0).setDuration(300);
            show = false;
        }else {
            valueAnimator = ValueAnimator.ofInt(0,DensityUtil.dip2px(mContext,100)).setDuration(300);
            show = true;
        }
        setHeightTing(view,show);
    }

    private void setHeightTing(View view,boolean isShow){
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.getLayoutParams().height = (int) animation.getAnimatedValue();
                view.requestLayout();
            }
        });
        valueAnimator.start();
        if (isShow){
            view.setVisibility(View.VISIBLE);
        }else {
            view.setVisibility(View.INVISIBLE);
        }
    }


    private void setTing(int visi){
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.getLayoutParams().width = (int) animation.getAnimatedValue();
                view.requestLayout();
            }
        });
        valueAnimator.start();
        view.setVisibility(visi);
    }


}
