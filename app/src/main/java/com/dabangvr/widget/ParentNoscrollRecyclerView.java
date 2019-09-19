package com.dabangvr.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ParentNoscrollRecyclerView extends RecyclerView {
    public ParentNoscrollRecyclerView(@NonNull Context context) {
        super(context);
    }

    public ParentNoscrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ParentNoscrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (callBack!=null){
            callBack.moveEvent();
        }
        return super.onInterceptTouchEvent(event);

    }

    public void setCallBack(CallBack mcall) {
        this.callBack = mcall;
    }

    private CallBack callBack;

    public interface CallBack {
        void moveEvent();
    }
}
