package com.dabangvr.home.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.onLoadMoreListener;
import com.dabangvr.model.GoodsCategoryVo;
import com.dabangvr.model.GoodsVo;
import com.dabangvr.util.BannerStart;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.StatusBarUtil;
import com.youth.banner.Banner;

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

public class NewReleaseActivity extends BaseActivity implements View.OnClickListener, android.widget.PopupMenu.OnMenuItemClickListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private int page = 1;
    private int goodsPage = 1;

    private List<GoodsCategoryVo> list = new ArrayList<>();
    private BaseLoadMoreHeaderAdapter adapter;//竖列表适配

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_new_release;
    }

    @Override
    protected void initView() {
        //返回
        findViewById(R.id.comment_back).setOnClickListener(this);

        //列表
        recyclerView = findViewById(R.id.ms_recycler_view);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutmanager);

        //刷新控件
        refreshLayout = findViewById(R.id.swiperefreshlayout);

        //右上角菜单
        ImageView mess = findViewById(R.id.top_right_mess);
        mess.setOnClickListener(this);


        //设置下拉时圆圈的颜色（可以尤多种颜色拼成）
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light);
        //设置下拉时圆圈的背景颜色（这里设置成白色）
        refreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//刷新
                page = 1;
                goodsPage = 1;
                getDate(0, 0, "", null);
            }
        });

        //加载更多
        recyclerView.addOnScrollListener(new onLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                page += 1;
                getDate(0, 0, "", null);
            }
        });

        getDate(0, 0, "", null);
        setAdapter();
    }

    @Override
    protected void initData() {

    }

    private void setAdapter() {
        //适配
        View header = LayoutInflater.from(NewReleaseActivity.this).inflate(R.layout.banner_demo, null);
        Banner banner = header.findViewById(R.id.ms_banner);
        BannerStart.starBanner(NewReleaseActivity.this,banner,"7");
        View footer = LayoutInflater.from(NewReleaseActivity.this).inflate(R.layout.load_animation_layout, null);
        adapter = new BaseLoadMoreHeaderAdapter<GoodsCategoryVo>(NewReleaseActivity.this, recyclerView, list, R.layout.release_list) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, final GoodsCategoryVo bean) {

                //分类数据
                holder.setText(R.id.new_item_shop_name, bean.getName());
                holder.setImageByUrl(R.id.new_item_shop_img, bean.getCategoryImg());

                //商品列表
                final RecyclerView sunRecyclerView = holder.getView(R.id.new_item_recy);
                SwipeRefreshLayout refreshLayout = holder.getView(R.id.swiperefreshlayout);
                LinearLayoutManager layoutmanager = new LinearLayoutManager(NewReleaseActivity.this);
                layoutmanager.setOrientation(LinearLayoutManager.HORIZONTAL);
                sunRecyclerView.setLayoutManager(layoutmanager);

                final BaseLoadMoreHeaderAdapter sunAdapter = new BaseLoadMoreHeaderAdapter<GoodsVo>
                        (NewReleaseActivity.this, recyclerView, bean.getGoodsVoList(), R.layout.new_release_item) {
                    @Override
                    public void convert(Context mContext, BaseRecyclerHolder holder, final GoodsVo goodsVo) {
                        holder.setImageByUrl(R.id.new_item_img, goodsVo.getListUrl());
                        holder.setText(R.id.new_item_msg, goodsVo.getName());
                        holder.setText(R.id.new_item_salse, goodsVo.getSellingPrice());
                        holder.getView(R.id.new_item_go).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(NewReleaseActivity.this, HxxqLastActivity.class);
                                intent.putExtra("id", goodsVo.getId());
                                intent.putExtra("type", 0);
                                startActivity(intent);
                            }
                        });
                    }
                };


                refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                        android.R.color.holo_red_light,
                        android.R.color.holo_orange_light);
                //设置下拉时圆圈的背景颜色（这里设置成白色）
                refreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);

                //加载更多
                sunRecyclerView.addOnScrollListener(new onLoadMoreListener() {
                    @Override
                    protected void onLoading(int countItem, int lastItem) {
                        getDate(1, 1, bean.getId(), sunAdapter);
                    }
                });

                sunRecyclerView.setAdapter(sunAdapter);
            }
        };
        adapter.addHeadView(header);
        adapter.addFooterView(footer);
        recyclerView.setAdapter(adapter);
    }


    /**
     * 统一获取数据
     *
     * @param type        请求类型：0表示请求获取产品列表，1表示请求获取商品列表
     * @param getDataType 获取数据的类型，0表示刷新和初始化，1表示加载更多
     * @param categoryId  商品类型id 当 type 传1的时候需要传该参数,否则传""
     * @param adapterTag  设置的adapter
     */
    private void getDate(final int type, final int getDataType, String categoryId, final BaseLoadMoreHeaderAdapter adapterTag) {

        Map<String, String> map = new HashMap<>();

        if (type == 0) {
            //产品分类传这两个参数
            map.put("page", String.valueOf(page));
            map.put("limit", "5");
        } else {
            //商品分页传这四个参数
            map.put("goodsPage", String.valueOf(goodsPage));
            map.put("goodsLimit", "5");
            map.put("type", "1");
            map.put("categoryId", categoryId);
        }

        //获取网络数据后，给全局list赋值，然后设置adapter。
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.NEWS, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")) {
                        JSONObject data = object.optJSONObject("data");
                        if (type == 0) {
                            String str = data.optString("GoodsCategoryList");
                            list = JsonUtil.string2Obj(str, List.class, GoodsCategoryVo.class);
                            //添加数据
                            if (getDataType == 0) {
                                adapter.addAll(list);
                            } else {//重新设置数据
                                adapter.updateData(list);
                            }
                        } else {
                            String str = data.optString("goodsVolist");
                            List<GoodsVo> lists = JsonUtil.string2Obj(str, List.class, GoodsVo.class);
                            adapterTag.updateData(lists);
                        }

                        if (refreshLayout.isRefreshing()) {
                            refreshLayout.setRefreshing(false);
                        }
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                call.cancel();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_right_mess: {
                showPopupMenu(v);//弹出右上角二级菜单
                break;
            }
            case R.id.comment_back: {
                finish();
                break;
            }
        }
    }

    private void showPopupMenu(View v) {
        //创建弹出式菜单对象（最低版本11）
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, v);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.xp_top_right_menu, popup.getMenu());
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener(NewReleaseActivity.this);
        //显示(这一行代码不要忘记了)
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                break;
            case R.id.action_home:
                break;
            case R.id.action_share:
                break;
            case R.id.action_response:
                break;
        }
        return false;
    }
}
