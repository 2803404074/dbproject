package com.dabangvr.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class BottomImgSize<T> {
    private Context context;

    public BottomImgSize(Context context) {
        this.context = context;
    }

    /**
     * 设置图片大小
     * @param length 长度
     * @param with 宽度
     * @param index 0，1，2，3     左上右下
     */
    public void setImgSize(int length, int with, int index, T view, int resources){
        Drawable drawableFirst = context.getResources().getDrawable(resources);
        drawableFirst.setBounds(0, 0, length, with);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        if (view instanceof RadioButton){
            RadioButton radioButton = (RadioButton) view;
            switch (index){
                case 0:radioButton.setCompoundDrawables(drawableFirst, null, null, null);break;
                case 1:radioButton.setCompoundDrawables(null, drawableFirst, null, null);break;
                case 2:radioButton.setCompoundDrawables(null, null, drawableFirst, null);break;
                case 3:radioButton.setCompoundDrawables(null, null, null, drawableFirst);break;
            }

        }
        if (view instanceof CheckBox){
            CheckBox radioButton = (CheckBox) view;
            switch (index){
                case 0:radioButton.setCompoundDrawables(drawableFirst, null, null, null);break;
                case 1:radioButton.setCompoundDrawables(null, drawableFirst, null, null);break;
                case 2:radioButton.setCompoundDrawables(null, null, drawableFirst, null);break;
                case 3:radioButton.setCompoundDrawables(null, null, null, drawableFirst);break;
            }

        }

        if (view instanceof TextView){
            TextView textView = (TextView) view;
            switch (index){
                case 0:textView.setCompoundDrawables(drawableFirst, null, null, null);break;
                case 1:textView.setCompoundDrawables(null, drawableFirst, null, null);break;
                case 2:textView.setCompoundDrawables(null, null, drawableFirst, null);break;
                case 3:textView.setCompoundDrawables(null, null, null, drawableFirst);break;
            }
        }

        if (view instanceof EditText){
            EditText textView = (EditText) view;
            switch (index){
                case 0:textView.setCompoundDrawables(drawableFirst, null, null, null);break;
                case 1:textView.setCompoundDrawables(null, drawableFirst, null, null);break;
                case 2:textView.setCompoundDrawables(null, null, drawableFirst, null);break;
                case 3:textView.setCompoundDrawables(null, null, null, drawableFirst);break;
            }
        }
    }
}
