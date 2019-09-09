package com.dabangvr.home.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dabangvr.R;

/**
 * 拼团-我的拼单
 */
public class PtMyFragment extends Fragment {
    private Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("tag", "onCreateView()方法执行");
        context = PtMyFragment.this.getContext();
        View view = inflater.inflate(R.layout.recy_demo_load, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

    }

}
