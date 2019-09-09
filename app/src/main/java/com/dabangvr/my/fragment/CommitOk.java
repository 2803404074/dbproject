package com.dabangvr.my.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dabangvr.R;
import com.dabangvr.util.SPUtils;
import com.example.mylibrary.ViewPagerSlide;

/**
 * 申请主播第一个页面
 */

@SuppressLint("ValidFragment")
public class CommitOk extends Fragment{
    private Context context;
    private SPUtils spUtils;
    private int type;

    private OnOkClike onOkClike;

    public interface OnOkClike{
        void onOkclike(boolean isOk);
    }
    public CommitOk(int type,OnOkClike onOkClike) {
        this.type = type;
        this.onOkClike = onOkClike;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        context = CommitOk.this.getContext();
        View view = inflater.inflate(R.layout.send_ok, null);
        spUtils = new SPUtils(context, "db_user");
        initView(view);
        return view;
    }

    //初始化控件
    private void initView(View view) {

        if(type == 1){
            view.findViewById(R.id.tv_02).setVisibility(View.VISIBLE);
        }
        if(type == 0){
            view.findViewById(R.id.tv_01).setVisibility(View.VISIBLE);
        }

        view.findViewById(R.id.bt_pri).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spUtils.remove("zbrz");
                onOkClike.onOkclike(true);
            }
        });

    }
}
