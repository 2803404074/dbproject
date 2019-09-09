package com.dabangvr.lbroadcast.activity;

import android.content.Context;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.DyUrl;
import okhttp3.Call;

public class ZbSearchActivity extends BaseActivity implements View.OnClickListener {

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
        host.add("叶子zzy");
        host.add("小甜心");
        host.add("yoke哈哈");
        host.add("大冬瓜");
        host.add("鹌鹑哥");
        host.add("张馨予");

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

        findViewById(R.id.search_share).setVisibility(View.INVISIBLE);

        findViewById(R.id.start_search).setOnClickListener(this);
        findViewById(R.id.search_share).setOnClickListener(this);

        recyclerView = findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new BaseLoadMoreHeaderAdapter<Goods>(this,recyclerView,list,R.layout.zb_gw_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, Goods o) {

            }
        };
        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

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
                            .hideSoftInputFromWindow(ZbSearchActivity.this
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
                        .hideSoftInputFromWindow(ZbSearchActivity.this
                                        .getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                getHttp(editText.getText().toString());
                break;
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
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }
}
