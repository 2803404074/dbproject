package com.dabangvr.my.fragment;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.common.activity.CartActivity;
import com.dabangvr.common.activity.MyFragment;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.dep.activity.MyWmActivity;
import com.dabangvr.main.MainActivity;
import com.dabangvr.my.activity.LoginActivity;
import com.dabangvr.my.activity.MyFanseActivity;
import com.dabangvr.my.activity.MyOrtherActivity;
import com.dabangvr.my.activity.UserMessActivity;
import com.dabangvr.util.JsonUtil;
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

/**
 *
 * 个人中心
 */
@SuppressLint("ValidFragment")
public class FragmentMy extends MyFragment implements View.OnClickListener {
    private Context context;
    private LinearLayout llMyPage;
    private UserMess userMess;
    private SimpleDraweeView head;
    private TextView nickName;
    private TextView title;
    private TextView uFans;
    private TextView uGz;
    private TextView uHz;

    private List<Fragment> mFragments;

    private ImageView linImg;
    private TextView linTv;
    private TextView linTips;

    private RelativeLayout myMain;
    private int height;

    private TextView tvLineTop;

    public static FragmentMy newInstance(int index) {
        FragmentMy my = new FragmentMy();
        Bundle args = new Bundle();
        args.putInt("index", index);
        my.setArguments(args);
        return my;
    }
    @Override
    protected int setContentView() {
        context = FragmentMy.this.getContext();
        return R.layout.fg_my;
    }

    @Override
    protected void lazyLoad() {
        llMyPage = findViewById(R.id.ll_my_page);
        myMain = findViewById(R.id.my_main);
        myMain.post(new Runnable() {
            @Override
            public void run() {
                height = myMain.getHeight();
            }
        });

        tvLineTop = findViewById(R.id.tv_line_top);

        linImg = findViewById(R.id.iv_no_line);
        linTv = findViewById(R.id.tv_no_line);
        linTips = findViewById(R.id.tv_tips);

        //头像
        head = findViewById(R.id.drawee_img);

        //昵称
        nickName = findViewById(R.id.tv_name);

        //签名
        title = findViewById(R.id.tv_u_title);

        //粉丝数量
        uFans = findViewById(R.id.u_fanse);

        //关注数量
        uGz = findViewById(R.id.u_guanzhu);

        //获赞数量
        uHz = findViewById(R.id.u_zan);

        //粉丝页面
        findViewById(R.id.ll_fanse).setOnClickListener(this);

        //关注页面
        findViewById(R.id.ll_follow).setOnClickListener(this);

        //设置
//        findViewById(R.id.my_set).setOnClickListener(this);

        //任务中心
        findViewById(R.id.my_rwzx).setOnClickListener(this);

        //我的外卖
        findViewById(R.id.my_wm).setOnClickListener(this);

        //我的订单
        findViewById(R.id.my_wddd).setOnClickListener(this);

        //购物车
        findViewById(R.id.my_ggc).setOnClickListener(this);

        setUserMess();
    }
    private void performAnim2(boolean show,int height) {
        //View是否显示的标志
        //属性动画对象
        ValueAnimator va;
        if (show) {
            //隐藏view，高度从height变为0
            va = ValueAnimator.ofInt(height, 0);
        } else {
            //显示view，高度从0变到height值
            va = ValueAnimator.ofInt(0, height);
        }
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int h = (Integer) valueAnimator.getAnimatedValue();
                //动态更新view的高度
                myMain.getLayoutParams().height = h;
                myMain.requestLayout();
            }
        });
        va.setDuration(500);
        //开始动画
        va.start();
    }

    //填充数据
    private void setUserMess() {
        if (isNoLin()){
            llMyPage.setVisibility(View.GONE);
            linImg.setVisibility(View.VISIBLE);
            linTv.setVisibility(View.VISIBLE);
            linTips.setVisibility(View.VISIBLE);
            linTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getUser();
                }
            });
            return;
        }

        String str = getSPKEY(context,"user");
        if (!StringUtils.isEmpty(str)){
            userMess = JsonUtil.string2Obj(str,UserMess.class);
        }
        if (userMess != null) {

            llMyPage.setVisibility(View.VISIBLE);
            linImg.setVisibility(View.GONE);
            linTv.setVisibility(View.GONE);
            linTips.setVisibility(View.GONE);

            TabLayout tabLayout = findViewById(R.id.m_tablayout);
            ViewPager viewPager = findViewById(R.id.m_viewpager);
            mFragments = new ArrayList<>();
            int auId = userMess.getIsAnchor();//1是主播
            mFragments.add(FragmentMyMy.newInstance(auId));
            mFragments.add(FragmentMyZb.newInstance(userMess.getAnchorId()));//看直播历史，传主播id
            mFragments.add(FragmentMyDynamic.newInstance(String.valueOf(userMess.getId())));//看说说历史，传用户id
            mFragments.add(FragmentMyShort.newInstance(String.valueOf(userMess.getId())));//看短视频历史，传用户id
            final String[] tabTitles = new String[]{"我的", "直播", "动态", "作品"};
            SimpleFragmentPagerAdapter adapte = new SimpleFragmentPagerAdapter(getFragmentManager(), mFragments, tabTitles);
            viewPager.setAdapter(adapte);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //滑动监听加载数据，一次只加载一个标签页
                    if (position == 1){
                        if (tvLineTop.getVisibility() == View.VISIBLE){
                            performAnim2(true,height);
                            tvLineTop.setVisibility(View.GONE);
                        }
                    }else if (position == 0){
                        performAnim2(false,height);
                        tvLineTop.setVisibility(View.VISIBLE);
                    }else {

                    }
                }
                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            tabLayout.setupWithViewPager(viewPager);

            if (!StringUtils.isEmpty(userMess.getHeadUrl())) {
                head.setImageURI(userMess.getHeadUrl());
            }

            if (!StringUtils.isEmpty(userMess.getNickName())) {
                nickName.setText(userMess.getNickName());
            }

            if (!StringUtils.isEmpty(userMess.getAutograph())) {
                title.setText(userMess.getAutograph());
            }

            if (!StringUtils.isEmpty(userMess.getFansNumber())) {
                uFans.setText(userMess.getFansNumber());
            }

            if (!StringUtils.isEmpty(userMess.getFollowNumber())) {
                uGz.setText(userMess.getFollowNumber());
            }

            if (!StringUtils.isEmpty(userMess.getPraisedNumber())) {
                uHz.setText(userMess.getPraisedNumber());
            }
        }else {
            llMyPage.setVisibility(View.GONE);
            linImg.setVisibility(View.VISIBLE);
            linTv.setVisibility(View.VISIBLE);
            linTips.setVisibility(View.VISIBLE);
            linTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getUser();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //设置
//            case R.id.my_set: {
//                if (isNoLin()){
//                    ToastUtil.showShort(context,"网络不可用");
//                    return;
//                }
//                Intent intent = new Intent(context, UserMessActivity.class);
//                intent.putExtra("user", JsonUtil.obj2String(userMess));
//                startActivity(intent);
//                break;
//            }
            //任务中心
            case R.id.my_rwzx: {
                ToastUtil.showShort(context, "功能维护中，敬请期待");
                break;
            }

            //我的外卖
            case R.id.my_wm:
                if (isNoLin()){
                    ToastUtil.showShort(context,"网络不可用");
                    return;
                }
                Intent intentx = new Intent(context, MyWmActivity.class);
                startActivity(intentx);
                break;

            //我的订单
            case R.id.my_wddd:
                if (isNoLin()){
                    ToastUtil.showShort(context,"网络不可用");
                    return;
                }
                Intent intent = new Intent(context, MyOrtherActivity.class);
                startActivity(intent);
                break;

            //购物车
            case R.id.my_ggc:
                if (isNoLin()){
                    ToastUtil.showShort(context,"网络不可用");
                    return;
                }
                Intent intent2 = new Intent(context, CartActivity.class);
                intent2.putExtra("token", getSPKEY(context, "token"));
                startActivity(intent2);
                break;

            case R.id.ll_fanse:
                Intent intent1 = new Intent(context, MyFanseActivity.class);
                intent1.putExtra("type",-1);
                startActivity(intent1);
                break;
            case R.id.ll_follow:
                Intent intent3 = new Intent(context, MyFanseActivity.class);
                intent3.putExtra("type",1);
                startActivity(intent3);
                break;
                default:break;
        }
    }


    private void getUser(){
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME,getSPKEY(context,"token"));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.USER_INFO, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (errno == 400){
                        removeSPKEY(context,"token");
                        removeSPKEY(context,"user");
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        ToastUtil.showShort(context,"登录已失效，请重新登录");
                        return ;
                    }
                    if (errno == 0) {
                        if (500 == object.optInt("code")) {
                            removeSPKEY(context,"token");
                            removeSPKEY(context,"user");
                            Intent intent = new Intent(context, LoginActivity.class);
                            startActivity(intent);
                            ToastUtil.showShort(context,"登录已失效，请重新登录");
                            return ;
                        }
                        String data = object.optString("data");
                        if (StringUtils.isEmpty(data)) {
                            removeSPKEY(context,"token");
                            removeSPKEY(context,"user");
                            Intent intent = new Intent(context, LoginActivity.class);
                            startActivity(intent);
                            ToastUtil.showShort(context,"登录已失效，请重新登录");
                            return ;
                        }
                        userMess = JsonUtil.string2Obj(data, UserMess.class);
                        if (null != userMess){
                            setSPKEY(context,"user",data);
                            if (null !=userMess.getToken()){
                                setSPKEY(context,"token",userMess.getToken());
                            }
                            if (1 == userMess.getIsAnchor()){
                                MainActivity.isAnchor = true;
                            }

                            setUserMess();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailed(Call call, IOException e) {
                Toast.makeText(context, "网络无法达到哦", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                //Toast.makeText(context, "网络无法达到哦", Toast.LENGTH_LONG).show();
            }
        });
    }

}
