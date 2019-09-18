package com.dabangvr.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.dabangvr.R;
import com.dabangvr.util.LoaddingUtils;
import com.dabangvr.util.SPUtils2;

import butterknife.ButterKnife;

/**
 * Created by 黄仕豪 on 2019/7/03
 */

public abstract class BaseNewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private LoaddingUtils mLoaddingUtils;

    public void setLoaddingView(boolean isLoadding){
        if(mLoaddingUtils==null){
            mLoaddingUtils=new LoaddingUtils(this);
        }
        if(isLoadding){
            mLoaddingUtils.show();
        }else{
            mLoaddingUtils.dismiss();
        }
    }

    public abstract void initView();

    public abstract void initData();

    // 设置布局
    public abstract int setLayout();

    public String getSPKEY(Activity activity, String key){
        return (String) SPUtils2.instance(activity).getkey(key,"");
    }

    public void setSPKEY(Activity activity, String key, String values){
        SPUtils2.instance(activity).put(key,values);
    }

    public String getToken (Activity activity){
        return (String) SPUtils2.instance(activity).getkey("token","");
    }
    @Override
    protected void onDestroy() {
        setLoaddingView(false);
        super.onDestroy();
    }
    public Context getContext(){
        return this;
    }
}
