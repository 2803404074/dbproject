package com.dabangvr.util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseRecyclerHolder;


/**
 * 弹窗，多类型
 */
public abstract class DialogUtilT{
    private Context context;
    private AlertDialog dialog;

    public DialogUtilT(Context context) {
        this.context = context;
    }

    /**
     * @param layout 该布局需要有一个iv_close id，用于通用关闭
     */
    public void show(int layout) {
        View view = LayoutInflater.from(context).inflate(layout, null, false);
        BaseRecyclerHolder holder = BaseRecyclerHolder.getRecyclerHolder(context, view);
        convert(holder);

        dialog = new AlertDialog.Builder(context, R.style.TransparentDialog).setView(view).create();

        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (ScreenUtils.getScreenWidth(context) / 4 * 3);
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setWindowAnimations(R.style.dialog_animation);
    }

    public void setDialogWidth(float w){
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (ScreenUtils.getScreenWidth(context) *w);
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setWindowAnimations(R.style.dialog_animation);
    }

    public void des(){
        if (dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }


    public abstract void convert(BaseRecyclerHolder holder);
}
