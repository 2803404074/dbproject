package com.dabangvr.my.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.common.activity.CartActivity;
import com.dabangvr.common.fragment.TestFragment;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.RippleView;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.my.activity.LoginActivity;
import com.dabangvr.my.activity.MyOrtherActivity;
import com.dabangvr.my.activity.UserMessActivity;
import com.dabangvr.my.fragment.FragmentMyMy;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoadingDialog;
import com.dabangvr.util.LoginTipsDialog;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import bean.UserMess;
import config.DyUrl;
import okhttp3.Call;

public class MyAdapter extends RecyclerView.Adapter<BaseRecyclerHolder>{
    private Context context;
    private FragmentManager fragmentManager;
    private int COMMENT_FIRST = 0;
    private int COMMENT_SECOND = 1;
    private LoadingDialog loadingDialog;
    private SPUtils spUtils;
    private String token;

    private SimpleDraweeView head;
    private TextView nickName;
    private SwipeRefreshLayout refreshLayout;

    public MyAdapter(Context context,FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        loadingDialog = new LoadingDialog(context);
        spUtils = new SPUtils(context,"db_user");
        token = (String) spUtils.getkey("token","");
    }

    public MyAdapter(Context context,FragmentManager fragmentManager,SwipeRefreshLayout refreshLayout) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.refreshLayout = refreshLayout;
        loadingDialog = new LoadingDialog(context);
        spUtils = new SPUtils(context,"db_user");
        token = (String) spUtils.getkey("token","");
    }
    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == COMMENT_FIRST) {
            View view = View.inflate(viewGroup.getContext(), R.layout.my_fg_top, null);
            return  BaseRecyclerHolder.getRecyclerHolder(context,view);
        } else if (viewType == COMMENT_SECOND) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.my_fg_bottom, viewGroup, false);
            return BaseRecyclerHolder.getRecyclerHolder(context,view);
        }
        return null;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(BaseRecyclerHolder viewHolder, int position) {
        loadingDialog.show();
        if(position == 0){
            //头像
            head = viewHolder.getView(R.id.drawee_img);
            nickName = viewHolder.getView(R.id.tv_name);

            if(!StringUtils.isEmpty(token)){
                getHttp(token);
            }else {
                show(0,"您还未登录哦，赶紧成为海鲜会员吧！");
                if(null != refreshLayout){
                    if(refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                    }
                }
            }
            //设置
            ImageView mySet = viewHolder.getView(R.id.my_set);

            //任务中心
            RippleView rwzx  = viewHolder.getView(R.id.my_rwzx);
            //积分商城
            RippleView jfsc  = viewHolder.getView(R.id.my_jfsc);
            //我的订单
            RippleView wddd = viewHolder.getView(R.id.my_wddd);
            //购物车
            RippleView ggc = viewHolder.getView(R.id.my_ggc);

            //点击事件
            mySet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String token = LoginTipsDialog.getToken(context);
                    if(!StringUtils.isEmpty(token)){
                        Intent intent = new Intent(context,UserMessActivity.class);
                        intent.putExtra("token",token);
                        context.startActivity(intent);
                    }
                }
            });
            head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String token = LoginTipsDialog.getToken(context);
                    if(StringUtils.isEmpty(token)){
                        Intent intent = new Intent(context,LoginActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
            wddd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String token = (String) spUtils.getkey("token","");
                    if (StringUtils.isEmpty(token)) {
                        ToastUtil.showShort(context, "请登陆");
                        return;
                    } else {
                        Intent intent = new Intent(context, MyOrtherActivity.class);
                        context.startActivity(intent);
                    }
                }
            });

            ggc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StringUtils.isEmpty(token)) {
                        ToastUtil.showShort(context, "请登陆");
                        return;
                    } else {
                        Intent intent = new Intent(context, CartActivity.class);
                        intent.putExtra("token", token);
                        context.startActivity(intent);
                    }
                }
            });

            jfsc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show(1,"该功能正在更新，敬请期待");
                }
            });

            rwzx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show(1,"该功能正在更新，敬请期待");
                }
            });


        }else if (position == 1){
            TabLayout tabLayout = viewHolder.getView(R.id.m_tablayout);
            ViewPager viewPager = viewHolder.getView(R.id.m_viewpager);
            List<Fragment> mFragments = new ArrayList<>();
            mFragments.add(FragmentMyMy.newInstance(1));
//            mFragments.add(ZhiBoPage.newInstance(1));
//            mFragments.add(FragmentDynamic.newInstance());
//            mFragments.add(ZhiBoPage.newInstance(1));
            mFragments.add(new TestFragment());
            mFragments.add(new TestFragment());
            mFragments.add(new TestFragment());
            final String[] tabTitles = new String[]{"我的", "喜欢", "动态", "作品"};

            SimpleFragmentPagerAdapter adapte = new SimpleFragmentPagerAdapter(fragmentManager,mFragments,tabTitles);
            viewPager.setAdapter(adapte);
            //TabLaout和ViewPager进行关联
            tabLayout.setupWithViewPager(viewPager);
        }
        loadingDialog.dismiss();
    }

    /**
     * 根据position的不同选择不同的布局类型
     */
    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0: {
                return COMMENT_FIRST;
            }
            case 1:
                return COMMENT_SECOND;
        }
        return position;
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return 2;
    }

    public void getHttp(String token) {
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, token);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.USER_INFO, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if(errno == 401){
                        show(0,object.optString("errmsg"));
                        LoginTipsDialog.removeToken(context);
                        return ;
                    }
                    if (errno == 0){
                        if (object.optInt("code") == 500){
                            ToastUtil.showShort(context,"服务数据更新中...");
                            if(null != refreshLayout){
                                if(refreshLayout.isRefreshing()){
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                            return ;
                        }
                        String data = object.optString("data");
                        if(StringUtils.isEmpty(data)){
                            return ;
                        }
                        UserMess userMess = JsonUtil.string2Obj(data, UserMess.class);
                        //填充数据
                        Uri uri = Uri.parse(userMess.getHeadUrl());
                        head.setImageURI(uri);
                        nickName.setText(userMess.getNickName());

                        if(null != refreshLayout){
                            if(refreshLayout.isRefreshing()){
                                refreshLayout.setRefreshing(false);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                Toast.makeText(context, "登陆失败", Toast.LENGTH_LONG).show();
                loadingDialog.dismiss();
            }
        });
    }


    public void setData(UserMess userMess){
        if(null == userMess){
            Uri uri = Uri.parse("");
            head.setImageURI(uri);
            nickName.setText("");
            show(1, "请重新登陆");
            return;
        }
        Uri uri = Uri.parse(userMess.getHeadUrl());
        head.setImageURI(uri);
        nickName.setText(userMess.getNickName());
    }


    private void show(final int index, String mess) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("海风暴")
                .setMessage(mess)
                .setIcon(R.mipmap.application)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(index == 0){
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);

                        }
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {//添加普通按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create();
        alertDialog.show();
    }
}
