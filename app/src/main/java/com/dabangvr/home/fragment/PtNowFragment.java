package com.dabangvr.home.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dabangvr.R;
import com.dabangvr.common.weight.ShowButtonLayout;
import com.dabangvr.common.weight.ShowButtonLayoutData;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.model.CategoryMo;
import com.dabangvr.model.TypeBean;
import com.dabangvr.util.JsonUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.DyUrl;
import okhttp3.Call;

@SuppressLint("ValidFragment")
public class PtNowFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private SimpleFragmentPagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager vp_pager;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private int position=0;

    private List<TypeBean>list;
    private List<String>mTab;
    private ShowButtonLayout hotLayout;

    public PtNowFragment(List<TypeBean> list,int position) {
        this.list = list;
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("tag", "onCreateView()方法执行");
        context = PtNowFragment.this.getContext();
        View view = inflater.inflate(R.layout.fragment_pt_now, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.many_type).setOnClickListener(this);
        hotLayout = view.findViewById(R.id.hot_layout);
        tabLayout = view.findViewById(R.id.tablayout);
        vp_pager = (ViewPager) view.findViewById(R.id.tab_viewpager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        setData();


    }

    /**
     */
    private void setData(){
        mTab = new ArrayList<>();
        String tabTitles[] = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            PtPageFragment fragment = new PtPageFragment(position);
            fragment.setTabPos(i,list.get(i).getId());//设置第几页，以及每页的id
            mFragments.add(fragment);
            tabTitles[i] = list.get(i).getName();

            //设置展示更多分类是显示的名字
            mTab.add(list.get(i).getName());
        }
        adapter = new SimpleFragmentPagerAdapter(getActivity().getSupportFragmentManager(), mFragments, tabTitles);
        vp_pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(vp_pager);
        //设置当前显示哪个标签页
        vp_pager.setCurrentItem(position);

        vp_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //滑动监听加载数据，一次只加载一个标签页
                ((PtPageFragment) adapter.getItem(position)).sendMessage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ShowButtonLayoutData data1 = new ShowButtonLayoutData<String>(context, hotLayout, mTab, new ShowButtonLayoutData.MyClickListener() {
            @Override
            public void clickListener(View v, double arg1,double arg2 ,boolean isCheck) {
                int tag = (int) v.getTag();
                hotLayout.setVisibility(View.GONE);
                vp_pager.setCurrentItem(tag);
            }
        });
        data1.setData2();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.many_type:
                if (hotLayout.getVisibility() == View.VISIBLE){
                    hotLayout.setVisibility(View.GONE);
                }else {
                    hotLayout.setVisibility(View.VISIBLE);
                }
                break;
                default:break;
        }
    }
}
