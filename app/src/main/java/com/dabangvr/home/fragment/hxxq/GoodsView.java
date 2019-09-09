package com.dabangvr.home.fragment.hxxq;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.dabangvr.R;

public class GoodsView extends LinearLayout {

    public GoodsView(Context context) {
        this(context, null);
    }

    public GoodsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoodsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.hxxq_view_goods, this, true);
    }
}
