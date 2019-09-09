package com.dabangvr.common.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments = null;
    private Context context;

    public PagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        //this.context = context;
        this.fragments = fragments;
        // notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fragments.size();//;NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        //return ArrayListFragment.newInstance(position);
        return fragments.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return PagerAdapter.POSITION_NONE;
    }
}
