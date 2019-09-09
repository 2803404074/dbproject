package com.dabangvr.common.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.dabangvr.R;
import com.dabangvr.common.adapter.DiscussAdapter;
import com.dabangvr.util.StatusBarUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *  评论页面Activity
 *  2019/6/25
 */

public class DiscussActivity extends BaseActivity{

    @BindView(R.id.car_top_id)
    LinearLayout topLinearLayout;
    @BindView(R.id.recy_discuss_id)
    RecyclerView recyclerView;


    private String goodsId;
    private String orderId;

    private DiscussAdapter discussAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);


    }

    @Override
    public int setLayout() {
        return R.layout.activity_discuss_layout;
    }



    @Override
    protected void initView() {
        goodsId = getIntent().getStringExtra("goodsId");
        orderId = getIntent().getStringExtra("orderId");

        initRecyclerView();

    }

    @Override
    protected void initData() {

    }


    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DiscussActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        discussAdapter = new DiscussAdapter(DiscussActivity.this);
        recyclerView.setAdapter(discussAdapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(DiscussActivity.this, LinearLayoutManager.VERTICAL));

    }




    @OnClick({R.id.iv_back_id})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.iv_back_id:
                DiscussActivity.this.finish();
                break;
            default:
                break;
        }
    }




}
