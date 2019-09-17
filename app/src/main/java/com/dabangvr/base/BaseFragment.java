package com.dabangvr.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dabangvr.util.LoaddingUtils;
import com.dabangvr.util.SPUtils2;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *      BaseFragment 新加
 *      2019、6、24
 */

public abstract class BaseFragment extends Fragment {

    private View rootView;
    protected Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(layoutId(), container, false);
        }else {
            //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        unbinder = ButterKnife.bind(this, rootView);
        return  rootView;
    }

    private LoaddingUtils mLoaddingUtils;

    public void setLoaddingView(boolean isLoadding){
        if(mLoaddingUtils==null){
            mLoaddingUtils=new LoaddingUtils(BaseFragment.this.getContext());
        }
        if(isLoadding){
            mLoaddingUtils.show();
        }else{
            mLoaddingUtils.dismiss();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public abstract int layoutId();

    public abstract void initView();

    public abstract void  initData();

    public String getToken(){
        return (String) SPUtils2.instance(BaseFragment.this.getContext()).getkey("token","");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
