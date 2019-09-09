package com.dabangvr.common.weight;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

public class MyTextView extends TextView {
    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!StringUtils.isEmpty((String) text)){
            super.setText(text, type);
        }
        super.setText("", type);
    }
}
