package com.dabangvr.lbroadcast.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.lbroadcast.fragment.page.FragmentGw;
import com.dabangvr.video.fragment.VideoFragment;

/**
 *  <android.support.design.widget.TabLayout
 *                 android:id="@+id/tabs"
 *                 android:layout_width="match_parent"
 *                 android:layout_height="48dp"
 *                 android:layout_gravity="bottom"
 *                 android:background="#ffffff"
 *                 app:layout_behavior="@string/appbar_scrolling_view_behavior"
 *                 app:tabIndicatorColor="#18dadc"
 *                 app:tabIndicatorFullWidth="false"
 *                 app:tabSelectedTextColor="#18dadc"
 *                 app:tabTextAppearance="@style/TabLayoutTextStyle" />
 */
public class FragmentZhibo extends Fragment {

    private FragmentManager fragmentManager;
    private FragmentGw fg_00;
    private VideoFragment fg_01;

    private TextView tvLive;
    private TextView tvShort;
    public static FragmentZhibo newInstance(int index) {
        FragmentZhibo zhibo = new FragmentZhibo();
        Bundle args = new Bundle();
        args.putInt("index", index);
        zhibo.setArguments(args);
        return zhibo;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_zhibo,null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        fragmentManager = getFragmentManager();
        changeFragment(0);
        tvLive = view.findViewById(R.id.tv_live);
        tvShort = view.findViewById(R.id.tv_short);
        tvLive.setTextColor(getResources().getColor(R.color.zhiBoT));


        tvLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(0);
                tvLive.setTextColor(getResources().getColor(R.color.zhiBoT));
                tvShort.setTextColor(getResources().getColor(R.color.white));
            }
        });

        tvShort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(1);
                tvShort.setTextColor(getResources().getColor(R.color.zhiBoT));
                tvLive.setTextColor(getResources().getColor(R.color.white));
            }
        });
    }

    public void changeFragment(int index) {

        // 调用FragmentTransaction中的方法来处理这个transaction
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        hideFragments(beginTransaction);

        // 根据不同的标签页执行不同的操作
        switch (index) {
            case 0:
                if (fg_00 == null) {
                    fg_00 = FragmentGw.newInstance();
                    beginTransaction.add(R.id.live_content, fg_00);
                } else {
                    beginTransaction.show(fg_00);
                }
                break;

            case 1:
                if (fg_01 == null) {
                    fg_01 = VideoFragment.newInstance();
                    beginTransaction.add(R.id.live_content, fg_01);
                } else {
                    beginTransaction.show(fg_01);
                }
                break;
            default:
                break;
        }

        //需要提交事务
        beginTransaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (fg_00 != null) {
            transaction.hide(fg_00);
        }
        if (fg_01 != null) {
            transaction.hide(fg_01);
        }
    }
}
