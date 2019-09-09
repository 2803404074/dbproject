package com.dabangvr.my.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dabangvr.R;
import com.dabangvr.my.activity.BusinessActivity;

public class IntroduceFragment extends Fragment {
    private int mTabPos = 0;//第几个商品类型

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mTabPos == 0){
            View view = inflater.inflate(R.layout.fg_introduce, container, false);
            initView(view);
            return view;
        }else if(mTabPos == 1){
            View view = inflater.inflate(R.layout.fg_process, container, false);
            initView(view);
            return view;
        }else if (mTabPos == 2){
            View view = inflater.inflate(R.layout.fg_requirement, container, false);
            initView(view);
            return view;
        }else {
            View view = inflater.inflate(R.layout.fg_postage, container, false);
            initView(view);
            return view;
        }
    }


    private void initView(View view){
        view.findViewById(R.id.start_rz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroduceFragment.this.getContext(), BusinessActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setTabPos(int mTabPos) {
        this.mTabPos = mTabPos;
    }
}
