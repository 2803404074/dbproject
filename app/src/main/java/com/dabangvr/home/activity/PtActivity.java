package com.dabangvr.home.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.common.activity.CartActivity;
import com.dabangvr.home.fragment.PtHostFragment;
import com.dabangvr.home.fragment.PtMyFragment;
import com.dabangvr.home.fragment.PtNowFragment;
import com.dabangvr.model.TypeBean;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.StatusBarUtil;

import java.util.List;

public class PtActivity extends BaseNewActivity implements RadioGroup.OnCheckedChangeListener, PopupMenu.OnMenuItemClickListener {

    private RadioGroup radioGroup;
    private FragmentManager fragmentManager;
    private RadioButton radioButton_00, radioButton_01, radioButton_02;
    private PtNowFragment fragment01;
    private PtHostFragment fragment02;
    private PtMyFragment fragment03;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_pt;
    }

    @Override
    public void initView() {

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.mess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建弹出式菜单对象（最低版本11）
                PopupMenu popup = new PopupMenu(PtActivity.this, v);//第二个参数是绑定的那个view
                //获取菜单填充器
                MenuInflater inflater = popup.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.main_menu, popup.getMenu());
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(PtActivity.this);
                //显示(这一行代码不要忘记了)
                popup.show();
            }
        });

        fragmentManager = getSupportFragmentManager();
        radioGroup = findViewById(R.id.pt_radiogrop);
        radioGroup.setOnCheckedChangeListener(this);
        radioButton_00 = findViewById(R.id.pt_now);
        radioButton_01 = findViewById(R.id.pt_host);
        radioButton_02 = findViewById(R.id.pt_my);
        radioButton_00.setChecked(true);
        radioButton_00.setTextColor(this.getResources().getColor(R.color.colorDb));
        changeImageSize();

    }

    @Override
    public void initData() {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.pt_now: {
                changeFragment(0);
                radioButton_00.setTextColor(this.getResources().getColor(R.color.colorDb));
                radioButton_01.setTextColor(this.getResources().getColor(R.color.black));
                radioButton_02.setTextColor(this.getResources().getColor(R.color.black));
                break;
            }
            case R.id.pt_host: {
                changeFragment(1);
                radioButton_01.setTextColor(this.getResources().getColor(R.color.colorDb));
                radioButton_00.setTextColor(this.getResources().getColor(R.color.black));
                radioButton_02.setTextColor(this.getResources().getColor(R.color.black));
                break;
            }
            case R.id.pt_my: {
                changeFragment(3);
                radioButton_02.setTextColor(this.getResources().getColor(R.color.colorDb));
                radioButton_00.setTextColor(this.getResources().getColor(R.color.black));
                radioButton_01.setTextColor(this.getResources().getColor(R.color.black));
                break;
            }
        }
    }

    public void changeFragment(int index) {

        // 调用FragmentTransaction中的方法来处理这个transaction
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        hideFragments(beginTransaction);

        // 根据不同的标签页执行不同的操作
        switch (index) {
            case 0:
                if (fragment01 == null) {
                    List<TypeBean> list = JsonUtil.string2Obj(getIntent().getStringExtra("list"),List.class,TypeBean.class);
                    fragment01 = new PtNowFragment(list,getIntent().getIntExtra("position",0));
                    beginTransaction.add(R.id.pt_fragment, fragment01);
                } else {
                    beginTransaction.show(fragment01);
                }

                break;

            case 1:
                if (fragment02 == null) {
                    fragment02 = new PtHostFragment();
                    beginTransaction.add(R.id.pt_fragment, fragment02);
                } else {
                    beginTransaction.show(fragment02);
                }
                break;
            case 3:
                if (fragment03 == null) {
                    fragment03 = new PtMyFragment();
                    beginTransaction.add(R.id.pt_fragment, fragment03);
                } else {
                    beginTransaction.show(fragment03);
                }
                break;
            default:
                break;
        }

        //需要提交事务
        beginTransaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (fragment01 != null) {
            transaction.hide(fragment01);
        }
        if (fragment02 != null) {
            transaction.hide(fragment02);
        }
        if (fragment03 != null) {
            transaction.hide(fragment03);
        }
    }

    //定义底部标签图片大小
    private void changeImageSize() {
        Drawable drawableFirst = getResources().getDrawable(R.drawable.pt_assembling);
        drawableFirst.setBounds(0, 0, 69, 69);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        radioButton_00.setCompoundDrawables(null, drawableFirst, null, null);//只放上面

        Drawable drawableSearch = getResources().getDrawable(R.drawable.pt_host);
        drawableSearch.setBounds(0, 0, 69, 69);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        radioButton_01.setCompoundDrawables(null, drawableSearch, null, null);//只放上面

        Drawable drawableMe = getResources().getDrawable(R.drawable.pt_my_pt);
        drawableMe.setBounds(5,5,90,90);
        radioButton_02.setCompoundDrawables(null, drawableMe, null, null);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_cart:{
                Intent intent = new Intent(PtActivity.this, CartActivity.class);
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
