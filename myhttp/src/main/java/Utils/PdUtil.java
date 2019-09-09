package Utils;

import android.app.ProgressDialog;
import android.content.Context;

public class PdUtil {
    private ProgressDialog pd;
    private Context context;

    public PdUtil(Context context) {
        this.context = context;
        this.pd = new ProgressDialog(context);
    }

    /**
     * 显示
     * @param s
     */
    public void showLoding(String s) {
        pd.setMessage(s);    //要显示的文字
        pd.setCanceledOnTouchOutside(false);    //不能通过触屏屏幕空白处取消进度条
        pd.show();        //显示进度条
    }

    /**
     * 销毁
     */
    public void desLoding(){
        pd.dismiss();
        if(pd!=null){
            pd.cancel();
        }
    }

    /**
     * 判断
     */
    public boolean isShow(){
        return pd.isShowing();
    }
}
