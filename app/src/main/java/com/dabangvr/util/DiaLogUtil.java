package com.dabangvr.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.dabangvr.R;


public class DiaLogUtil implements View.OnClickListener {
    private View inflate;
    private Context context;
    private int layout;
    private DiaLogClickListener mListener;

    public DiaLogUtil(Context context, int layout, DiaLogClickListener listener) {
        this.context = context;
        this.layout = layout;
        this.mListener = listener;
    }

    public Dialog getDialog() {
        Dialog dialog = new Dialog(context);
        //填充对话框的布局
        inflate = LayoutInflater.from(context).inflate(layout, null);
        inflate.findViewById(R.id.choosePhoto).setOnClickListener(this);
        inflate.findViewById(R.id.takePhoto).setOnClickListener(this);
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;//设置Dialog距离底部的距离
//       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
        return dialog;
    }

    @Override
    public void onClick(View v) {
        mListener.click(v);
    }

    public interface DiaLogClickListener{
        public void click(View view);
    }
}
