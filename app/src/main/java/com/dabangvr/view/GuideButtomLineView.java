package com.dabangvr.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dabangvr.R;

public class GuideButtomLineView extends LinearLayout {
    private Context mContext;
    private LinearLayout mLinearLayoutRoot;

    public GuideButtomLineView(Context context) {
        this(context, null);
    }

    public GuideButtomLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context contextArp) {
        mContext = contextArp;
        View view = View.inflate(mContext, R.layout.guide_buttom_view, this);
        mLinearLayoutRoot =view.findViewById(R.id.ll_root);
    }


    public void setSelectIndex(int index){
        int childCount = mLinearLayoutRoot.getChildCount();
        for(int i=0;i<childCount;i++){
           ImageView imageView= (ImageView) mLinearLayoutRoot.getChildAt(i);
           imageView.setSelected(index==i);
        }
    }
}
