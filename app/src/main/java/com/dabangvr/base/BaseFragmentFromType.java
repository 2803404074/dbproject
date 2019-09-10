package com.dabangvr.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dabangvr.R;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.SPUtils2;

import Utils.OkHttp3Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 懒加载fragment基类
 * 针对 展示多类型商品的页面
 * 懒加载
 * auth 黄仕豪
 * data 2019/7/8
 */
public abstract class BaseFragmentFromType extends Fragment {



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    private Unbinder unbinder;
    //获取TAG的fragment名称
    protected final String TAG = this.getClass().getSimpleName();
    protected String STATE_SAVE_IS_HIDDEN ;
    public Context context;

    private boolean IS_LOADED = false;
    public static int mSerial = 0;
    private int mTabPos = 0;//第几个类型
    private String cType = "";//类型id
    private boolean isFirst = true;


    public String getCtype(){
        return this.cType;
    }
    public Context getContext(){
        return this.context;
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (!IS_LOADED) {
                IS_LOADED = true;
                //这里执行加载数据的操作
                setDate(false);
            }
            return false;
        }
    });

    public void showTips(View view){
        view.setVisibility(View.VISIBLE);
    }
    public void hideTips(View view){
        view.setVisibility(View.GONE);
    }



    @Override
        public void onAttach(Context ctx) {
        super.onAttach(ctx);
        context = ctx;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(initLayout(), container, false);
        unbinder = ButterKnife.bind(this, rootView);
        if (isFirst && mTabPos == mSerial) {
            isFirst = false;
            sendMessage();
        }
        initView();

        return rootView;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN,isHidden());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            OkHttp3Utils.desInstance();
        }
    }

    /**
     * 请求网络
     */
    public void sendMessage() {
        Message message = handler.obtainMessage();
        message.sendToTarget();
    }



    /**
     * 初始化布局
     *
     * @return 布局id
     */
    protected abstract int initLayout();

    /**
     * 初始化控件
     */
    protected abstract void initView();


    /**
     * 加载数据
     * @param isLoad
     */
    protected abstract void setDate(boolean isLoad);


    public void setTabPos(int mTabPos, String cType) {
        this.mTabPos = mTabPos;
        this.cType = cType;
    }

    /**
     * 保证同一按钮在1秒内只响应一次点击事件
     */
    public abstract class OnSingleClickListener implements View.OnClickListener {
        //两次点击按钮的最小间隔，目前为1000
        private static final int MIN_CLICK_DELAY_TIME = 1000;
        private long lastClickTime;

        public abstract void onSingleClick(View view);

        @Override
        public void onClick(View v) {
            long curClickTime = System.currentTimeMillis();
            if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
                lastClickTime = curClickTime;
                onSingleClick(v);
            }
        }
    }

    /**
     * 同一按钮在短时间内可重复响应点击事件
     */
    public abstract class OnMultiClickListener implements View.OnClickListener {
        public abstract void onMultiClick(View view);

        @Override
        public void onClick(View v) {
            onMultiClick(v);
        }
    }

    public String getToken(){
        return (String) SPUtils2.instance(context).getkey("token","");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onDetach();
    }
}