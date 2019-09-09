package com.example.hxxq;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {

    private int scroll_y;
    private boolean isLoad = true;


    public Callbacks mCallbacks;

    public CallbacksBottom mCallbacksBottom;

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCallbacks(Callbacks callbacks) {
        this.mCallbacks = callbacks;
    }

    public void setBottomCallbacks(CallbacksBottom callbacks) {
        this.mCallbacksBottom = callbacks;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mCallbacks != null) {
            mCallbacks.onScrollChanged(l, t, oldl, oldt);
        }


        scroll_y = t;  //监听赋值，监听scrollView的滑动状态，当滑动到顶部的时候才可以下拉刷新
        if(t == 0){
        }else if(t+this.getMeasuredHeight() == this.getChildAt(0).getMeasuredHeight()){  //滑动距离+scrollView的高度如果等于scrollView的内部子view的高度则证明滑动到了底部，则自动加载更多数据
            if (isLoad){
                mCallbacksBottom.loadMore();  //加载更多
                isLoad = false;
            }
        }else {
            isLoad = true;
        }

    }
    //定义接口用于回调
    public interface Callbacks {
        void onScrollChanged(int x, int y, int oldx, int oldy);
    }

    public interface CallbacksBottom {
        void loadMore();
    }
}
