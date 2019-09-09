package com.dabangvr.home.fragment;

import android.view.View;

import com.dabangvr.R;
import com.dabangvr.base.BaseFragmentFromType;

public class HxFragment extends BaseFragmentFromType {

    @Override
    protected int initLayout() {
        return R.layout.hx_fragment;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void setDate(boolean isLoad) {
        //根据分类id，查询分类商品
        getCtype();
    }
}
