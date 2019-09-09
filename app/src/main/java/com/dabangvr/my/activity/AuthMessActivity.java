package com.dabangvr.my.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.my.fragment.FragmentMyDynamic;
import com.dabangvr.my.fragment.FragmentMyShort;
import com.dabangvr.my.fragment.FragmentMyZb;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.TextUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.commons.lang.StringUtils;
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

public class AuthMessActivity extends BaseActivity {

    private TextView fanse;
    private TextView follow;
    private TextView title;
    private SimpleDraweeView head;
    private TextView nickName;
    private String aId;//主播id
    private TextView tvTitle;//xxx的主页
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_auth_mess;
    }

    @Override
    protected void initView() {
        aId = getIntent().getStringExtra("authId");
        String uId = getIntent().getStringExtra("userId");
        tvTitle = findViewById(R.id.tv_title);
        head = findViewById(R.id.drawee_img);
        nickName = findViewById(R.id.nick_name);
        fanse = findViewById(R.id.fans);
        follow = findViewById(R.id.follow);
        title = findViewById(R.id.title);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
        TabLayout tabLayout = findViewById(R.id.m_tablayout);
        ViewPager viewPager = findViewById(R.id.m_viewpager);
        List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(FragmentMyZb.newInstance(aId));
        mFragments.add(FragmentMyDynamic.newInstance(uId));
        mFragments.add(FragmentMyShort.newInstance(uId));
        final String[] tabTitles = new String[]{"直播历史", "动态", "短视频"};
        SimpleFragmentPagerAdapter adapte = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), mFragments, tabTitles);
        viewPager.setAdapter(adapte);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void initData() {
        if (StringUtils.isEmpty(aId))return;
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME,getSPKEY(this,"token"));
        map.put("anchorId",aId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getAnchor, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if(StringUtils.isEmpty(result)){
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if(errno == 0){
                        if(500 == object.optInt("code")){
                            return ;
                        }
                        JSONObject dataObj = object.optJSONObject("data");
                        nickName.setText(TextUtil.isNull2Url(dataObj.optString("nickName")));
                        title.setText(TextUtil.isNull2Url(dataObj.optString("autograph")));
                        head.setImageURI(TextUtil.isNull2Url(dataObj.optString("headUrl")));
                        follow.setText(TextUtil.isNull2Url(dataObj.optString("follow")));
                        fanse.setText(TextUtil.isNull2Url(dataObj.optString("myFans")));

                        tvTitle.setText(TextUtil.isNull2Url(dataObj.optString("nickName"))+"的主页");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }
}
