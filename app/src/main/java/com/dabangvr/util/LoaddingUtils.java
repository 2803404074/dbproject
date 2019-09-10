package com.dabangvr.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import com.dabangvr.R;


public class LoaddingUtils extends Dialog {

    public LoaddingUtils(Context context) {
        super(context);
        Window window = getWindow();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loadding_dialog);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.height= WindowManager.LayoutParams.MATCH_PARENT;
        attributes.width= WindowManager.LayoutParams.MATCH_PARENT;
        attributes.dimAmount=0;
        attributes.gravity= Gravity.CENTER;
        window.setAttributes(attributes);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context,android.R.color.transparent)));
    }

}
