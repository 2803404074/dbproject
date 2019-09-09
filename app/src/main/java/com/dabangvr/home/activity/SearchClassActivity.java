package com.dabangvr.home.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.dabangvr.R;
import com.dabangvr.common.activity.CartActivity;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.home.fragment.SearchClassFragment;
import com.dabangvr.util.ToastUtil;

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

/**
 * 分类搜索页面
 */
public class SearchClassActivity extends AppCompatActivity implements View.OnClickListener, android.widget.PopupMenu.OnMenuItemClickListener {
    private TabLayout tabLayout;
    private ViewPager vp_pager;
    private List<Fragment> mFragments;
    private SimpleFragmentPagerAdapter adapter;

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_class);
        initView();
    }

    private void initView() {
        findViewById(R.id.start_search).setOnClickListener(this);
        findViewById(R.id.mess).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);

        editText = findViewById(R.id.search_edit);
        tabLayout = findViewById(R.id.tablayout);
        vp_pager = (ViewPager) findViewById(R.id.tab_viewpager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mFragments = new ArrayList<>();
        String[] listId = new String[3];
        String[] list = new String[3];
        list[0] = "全部";
        list[1] = "活动";
        list[2] = "拼团优惠";
        listId[0] = "拼团";
        listId[1] = "海鲜";
        listId[2] = "活动";
        for (int i = 0; i < list.length; i++) {
            SearchClassFragment fragment = new SearchClassFragment(0);
            fragment.setTabPos(i, listId[i]);//这里传 全部id、双11活动id、拼团优惠id
            mFragments.add(fragment);
        }

        adapter = new SimpleFragmentPagerAdapter(this.getSupportFragmentManager(), mFragments, list);
        vp_pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(vp_pager);
        //设置当前显示哪个标签页
        vp_pager.setCurrentItem(0);

        vp_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //滑动监听加载数据，一次只加载一个标签页
                ((SearchClassFragment) adapter.getItem(position)).sendMessage(0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mess: {
                //创建弹出式菜单对象（最低版本11）
                android.widget.PopupMenu popup = new android.widget.PopupMenu(SearchClassActivity.this, v);//第二个参数是绑定的那个view
                //获取菜单填充器
                MenuInflater inflater = popup.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.main_menu, popup.getMenu());
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(SearchClassActivity.this);
                //显示(这一行代码不要忘记了)
                popup.show();
                break;
            }
            case R.id.start_search: {
                setDate(0);
                break;
            }
            case R.id.back:{
                finish();
                break;
            }
        }
    }

    private void setDate(final int getDataType) {
        Map<String, String> map = new HashMap<>();
        //ToastUtil.showShort(context, "状态 : " + status);
        map.put("goodsName", editText.getText().toString());
        map.put("page", "1");
        map.put("limit", "10");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getSearchGoodsList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return ;
                }
                try {
                    JSONObject objectAll = new JSONObject(result);
                    int errno = objectAll.optInt("errno");
                    if (errno == 0) {
                        if(500 == objectAll.optInt("code")){
                            return ;
                        }
                        JSONObject dataObj = objectAll.optJSONObject("data");
                        String goods = dataObj.optString("goodsList");
                        //发送广播，更新adapter
                        Intent intent = new Intent("android.intent.action.SEARCH_CLASS");
                        intent.putExtra("goods",goods);
                        LocalBroadcastManager.getInstance(SearchClassActivity.this).sendBroadcast(intent);
                    } else {
                        ToastUtil.showShort(SearchClassActivity.this, objectAll.optString("errmsg"));
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_cart:{
                Intent intent = new Intent(SearchClassActivity.this, CartActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.action_mess:{
                break;
            }
        }
        return false;
    }
}
