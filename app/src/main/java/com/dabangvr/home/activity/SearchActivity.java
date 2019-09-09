package com.dabangvr.home.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.ShowButtonLayout;
import com.dabangvr.common.weight.ShowButtonLayoutData;
import com.dabangvr.model.Goods;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.ToastUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import cn.sharesdk.onekeyshare.OnekeyShare;
import config.DyUrl;
import okhttp3.Call;

public class SearchActivity extends BaseActivity implements View.OnClickListener {

    private ShowButtonLayout hotLayout;
    private ShowButtonLayout historyLayout;
    private LinearLayout search_title;

    private EditText editText;
    private List<String> listHist;//历史
    private List<String> host;//热门

    private TextView doNothing;//什么也没有找到

    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;
    private List<Goods> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected void initData() {
        //热门搜索
        host = new ArrayList<>();
        host.add("海鲜出笼");
        host.add("大闸蟹");
        host.add("龙虾");
        host.add("干货");
        host.add("鲜汤");
        host.add("海鲜粉");

        //历史数据
        String history = getSPKEY(this,"search_history");
        listHist = JsonUtil.string2Obj(history,List.class,String.class);
        if(null == listHist){
            listHist = new ArrayList<>();
        }

        //热门搜索
        ShowButtonLayoutData data1 = new ShowButtonLayoutData<String>(this, hotLayout, host, new ShowButtonLayoutData.MyClickListener() {
            @Override
            public void clickListener(View v, double arg1,double arg2 ,boolean isCheck) {
                String tag = (String) v.getTag();
                getHttp(tag);
            }
        });

        //历史搜索
        ShowButtonLayoutData data2 = new ShowButtonLayoutData<String>(this, historyLayout, listHist, new ShowButtonLayoutData.MyClickListener() {
            @Override
            public void clickListener(View v,  double arg1,double arg2 ,boolean isCheck) {
                String tag = (String) v.getTag();
                getHttp(tag);
            }
        });
        data1.setData();
        data2.setData();
    }

    @Override
    protected void initView() {
        doNothing = findViewById(R.id.do_nothing);
        hotLayout = findViewById(R.id.hot_layout);
        historyLayout = findViewById(R.id.history_layout);
        search_title = findViewById(R.id.search_title);//需要隐藏的板块
        editText = findViewById(R.id.search_edit);

        findViewById(R.id.start_search).setOnClickListener(this);
        findViewById(R.id.search_share).setOnClickListener(this);

        recyclerView = findViewById(R.id.search_recycler_view);
        GridLayoutManager layoutmanager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutmanager);

        adapter = new BaseLoadMoreHeaderAdapter<Goods>(this,recyclerView,list,R.layout.search_rv_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, Goods o) {
                holder.setImageByUrl(R.id.sc_img_01,o.getListUrl());
                holder.setText(R.id.sc_tv_info01,o.getDescribe());
                holder.setText(R.id.sc_tv_money01,o.getSellingPrice());
            }
        };
        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(SearchActivity.this, HxxqLastActivity.class);
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("type", 0);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setFocusableInTouchMode(true);
                editText.setFocusable(true);
            }
        });


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) editText.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SearchActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    // 搜索，进行自己要的操作...
                    getHttp(editText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_search: {
                editText.setFocusableInTouchMode(false);
                editText.clearFocus();
                ((InputMethodManager) editText.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(SearchActivity.this
                                        .getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                getHttp(editText.getText().toString());
                break;
            }
            case R.id.search_share: {
                share();
            }
        }
    }


    private void getHttp(final String key) {
        HashMap<String, String> map = new HashMap<>();
        map.put("goodsName", key);
        map.put("limit", "10");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getSearchGoodsList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)){
                    doNothing.setVisibility(View.VISIBLE);
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if(errno == 0){
                        if(500 == object.optInt("code")){
                            return ;
                        }
                        JSONObject dataObj = object.optJSONObject("data");
                        String goods = dataObj.optString("goodsList");
                        List<Goods> goodsList = JsonUtil.string2Obj(goods, List.class, Goods.class);
                        if(goodsList.size()<1 || null == goodsList){
                            doNothing.setVisibility(View.VISIBLE);
                            hotLayout.setVisibility(View.GONE);
                            historyLayout.setVisibility(View.GONE);
                        }else {
                            search_title.setVisibility(View.GONE);

                            adapter.updateData(goodsList);
                        }
                        //保存搜索过的数据
                        listHist.add(key);
                        String str = JsonUtil.obj2String(listHist);
                        setSPKEY(SearchActivity.this,"search_history",str);
                    }else {
                        ToastUtil.showShort(SearchActivity.this,object.optString("errmsg"));
                        doNothing.setVisibility(View.VISIBLE);
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

    private void share() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle("新鲜海鲜出炉");
        // titleUrl QQ和QQ空间跳转链接
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("最牛逼的海风暴");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("http://activity_camera.fuxingsc.com/upload/20190222/16181347727f2b.jpg");//确保SDcard下面存在此张图片
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl("http://www.baidu.com");
        // comment是我对这条分享的评论，仅在人人网使用
//        oks.setComment("我是测试评论文本");
        // 启动分享GUI
        oks.show(this);
    }
}
