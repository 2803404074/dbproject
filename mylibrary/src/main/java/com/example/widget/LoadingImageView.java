package com.example.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.mylibrary.R;

public class LoadingImageView extends android.support.v7.widget.AppCompatImageView{

    private int mTop;

    public LoadingImageView(Context context) {
        super(context);
        init();
    }

    public LoadingImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ValueAnimator animator = ValueAnimator.ofInt(0,50,0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int dx = (int) animation.getAnimatedValue();
                setTop(mTop-dx);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {

            private int curImgIndex = 0;
            private int imgCount = 3;

            @Override
            public void onAnimationStart(Animator animation) {
                setImageResource(R.drawable.ic_fav);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                curImgIndex++;
                switch (curImgIndex%imgCount){
                    case 0:
                        setImageResource(R.drawable.ic_fav);
                        break;
                    case 1:
                        setImageResource(R.drawable.ic_fav);
                        break;
                    case 2:
                        setImageResource(R.drawable.ic_fav);
                        break;
                }
            }
        });
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(1000);
        animator.start();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mTop = top;
    }
}
