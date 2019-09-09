package com.dabangvr.lbroadcast.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.home.activity.SearchActivity;
import com.dabangvr.model.CheckMo;
import com.dabangvr.model.TypeBean;
import com.dabangvr.util.JsonUtil;
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
 * 直播选商品的分类页面
 */
public class HxActivityChoose extends BaseActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;
    private List<TypeBean> mData = new ArrayList<>();
    public static HxActivityChoose instant;
    private RadioGroup radioGroup;
    private List<TypeBean> list = new ArrayList<>();;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_hx_choose;
    }

    @Override
    protected void initData() {
        Map<String,String>map = new HashMap<>();
        map.put("parentId","1");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.CategoryList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (!StringUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (0 == object.optInt("errno")){
                            JSONObject data = object.optJSONObject("data");
                            String listStr = data.optString("goodsCategoryList");
                            list = JsonUtil.string2Obj(listStr,List.class,TypeBean.class);

                            for (int i=0;i<list.size();i++){
                                RadioButton tempButton = new RadioButton(HxActivityChoose.this);
                                Bitmap a=null;
                                tempButton.setBackgroundResource(R.drawable.hx_type_check);	// 设置RadioButton的背景图片
                                tempButton.setButtonDrawable(new BitmapDrawable(a));			// 设置按钮的样式
                                tempButton.setPadding(30, 40, 30, 40);           		// 设置文字距离按钮四周的距离
                                tempButton.setText(list.get(i).getName());
                                tempButton.setTextSize(15);
                                tempButton.setTextColor(getResources().getColorStateList(R.color.radio_text_color));
                                tempButton.setId(i);
                                if (i==0){
                                    tempButton.setChecked(true);
                                    setData(list.get(i).getId());
                                }
                                radioGroup.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            }


                            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    setData(list.get(checkedId).getId());
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });

        checkList = JsonUtil.string2Obj(getIntent().getStringExtra("data"),List.class,CheckMo.class);

        if (checkList != null){
            mess.setText("已选择("+checkList.size()+")个商品");
        }
    }

    private TextView mess;
    @Override
    protected void initView() {
        instant = this;
        //返回
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HxActivityChoose.this, PlayZhiBoActivity.class);
                intent.putExtra("data", JsonUtil.obj2String(checkList));
                setResult(77, intent);
                finish();
            }
        });
        //搜索
        findViewById(R.id.ll_search).setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { startActivity(new Intent(HxActivityChoose.this,SearchActivity.class)); }});

        //消息
        mess = findViewById(R.id.mess);
        mess.setOnClickListener(this);

        radioGroup = findViewById(R.id.hx_rg);

        recyclerView = findViewById(R.id.s_recy);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(manager);
        adapter = new BaseLoadMoreHeaderAdapter<TypeBean>(this, recyclerView, mData, R.layout.dep_type_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, TypeBean o) {
                holder.setText(R.id.tv_name,o.getName());
                holder.setImageByUrl(R.id.iv_img,o.getCategoryImg());
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(HxActivityChoose.this, HxActivityCheck.class);
                String listStr = JsonUtil.obj2String(mData);
                intent.putExtra("list",listStr);
                intent.putExtra("position",position);
                if (null!=checkList && checkList.size()>0){
                    intent.putExtra("data",JsonUtil.obj2String(checkList));
                }
                startActivityForResult(intent,88);
            }
        });
    }

    private void setData(String id) {
        Map<String,String>map = new HashMap<>();
        map.put("parentId",id);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.CategoryList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String s) {
                if (!StringUtils.isEmpty(s)){
                    try {
                        JSONObject object = new JSONObject(s);
                        if (object.optInt("errno") == 0){
                            JSONObject data = object.optJSONObject("data");
                            String str = data.optString("goodsCategoryList");
                            mData = JsonUtil.string2Obj(str,List.class,TypeBean.class);
                            adapter.updateData(mData);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 88){
            if(resultCode == 88){
                String str = data.getStringExtra("data");
                if (!StringUtils.isEmpty(str)){
                    checkList = JsonUtil.string2Obj(str,List.class,CheckMo.class);
                    mess.setText("已选择("+checkList.size()+")个商品");
                }

            }
        }
    }

    private List<CheckMo>checkList;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mess:
                if (checkList==null || checkList.size()==0) {
                    ToastUtil.showShort(this,"您未添加任何商品哦");
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Dialog_Alert);
                View view = View.inflate(getApplication(),R.layout.recy_demo_check,null);
                final TextView textView = view.findViewById(R.id.check_ok);

                if (null != checkList || checkList.size() > 0){
                    textView.setText("选好啦");
                }
                RecyclerView recyclerView = view.findViewById(R.id.ms_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                BaseLoadMoreHeaderAdapter adapter = new BaseLoadMoreHeaderAdapter<CheckMo>(
                        HxActivityChoose.this,recyclerView,checkList,R.layout.sc_recy_item) {
                    @Override
                    public void convert(Context mContext, BaseRecyclerHolder holder, CheckMo o) {
                        holder.setImageByUrl(R.id.sc_img,o.getListUrl());
                        holder.setText(R.id.sc_title,o.getName());
                        holder.setText(R.id.sc_price,o.getPrice());
                    }
                };
                recyclerView.setAdapter(adapter);

                builder.setView(view);
                final AlertDialog dialog = builder.create();
                view.findViewById(R.id.keep_check).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null == checkList || checkList.size() == 0){
                            ToastUtil.showShort(HxActivityChoose.this,"未添加直播商品哦");return;
                        }

                        Intent intent = new Intent(HxActivityChoose.this, PlayZhiBoActivity.class);
                        intent.putExtra("data", JsonUtil.obj2String(checkList));
                        setResult(77, intent);
                        finish();
                    }
                });
                dialog.show();
                break;
                default:break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HxActivityChoose.this, PlayZhiBoActivity.class);
        intent.putExtra("data", JsonUtil.obj2String(checkList));
        setResult(77, intent);
        finish();
    }
}
