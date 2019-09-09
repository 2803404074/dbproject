package com.dabangvr.dep.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.onLoadMoreListener;
import com.dabangvr.dep.activity.DepMessActivity;
import com.dabangvr.dep.activity.DepSearchActivity;
import com.dabangvr.model.DepDynamic;
import com.dabangvr.model.DepTypeMo;
import com.dabangvr.util.BannerStart;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.ToastUtil;
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
import Utils.PdUtil;
import config.DyUrl;
import okhttp3.Call;


public class DepAdapter extends RecyclerView.Adapter<BaseRecyclerHolder> implements View.OnClickListener {

    private Context context;
    private SPUtils spUtils;
    private int COMMENT_FIRST = 0;
    private int COMMENT_SECOND = 1;

    private String lon = "";
    private String lat = "";
    private int dis = 3;//半径，分页传半径的一倍

    private String sidx = "";//店铺排序规则,不传默认按综合销量，juli:按距离，commnet：按好评


    private List<DepTypeMo> typeList = new ArrayList<>();
    private BaseLoadMoreHeaderAdapter adapterx;

    //商家列表
    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;
    private List<DepDynamic> list = new ArrayList<>();


    private PdUtil pdUtil;
    public DepAdapter(Context context,PdUtil pdUtil, String lon, String lat) {
        this.context = context;
        this.pdUtil = pdUtil;
        this.lon = lon;
        this.lat = lat;
        spUtils = new SPUtils(context, "db_user");
    }

    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == COMMENT_FIRST) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.dep_top, viewGroup, false);
            return BaseRecyclerHolder.getRecyclerHolder(context, view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.dep_bottom, viewGroup, false);
            return BaseRecyclerHolder.getRecyclerHolder(context, view);
        }
    }

    public void notifyData(String lat,String lon){
        getDepType();
        this.lat = lat;
        this.lat = lon;
        initData(0,lon,lat);
    }


    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final BaseRecyclerHolder holder, int position) {
        switch (position) {
            case 0:
                //顶部轮播
                Banner banner = holder.getView(R.id.top_banner);
                BannerStart.starBanner(context,banner,"5");

                getDepType();

                //店铺分类
                RecyclerView recyc = holder.getView(R.id.dep_type_recy);
                LinearLayoutManager managerDepType = new LinearLayoutManager(context);
                managerDepType.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyc.setLayoutManager(managerDepType);

                adapterx = new BaseLoadMoreHeaderAdapter<DepTypeMo>(context, recyc, typeList, R.layout.dep_type_item) {
                    @Override
                    public void convert(Context mContext, BaseRecyclerHolder holder, DepTypeMo o) {
                        holder.setImageByUrl(R.id.iv_img, o.getCategoryImg());
                        holder.setText(R.id.tv_name, o.getName());
                    }
                };
                recyc.setAdapter(adapterx);

                adapterx.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(context,DepSearchActivity.class);
                        intent.putExtra("lon",lon);
                        intent.putExtra("lat",lat);
                        intent.putExtra("foodType",typeList.get(position).getId());
                        context.startActivity(intent);
                    }
                });
                //限时抢购
                holder.getView(R.id.m_ms).setOnClickListener(null);

                break;
            case 1:
                //综合销量
                holder.getView(R.id.ll_sal).setOnClickListener(this);

                //距离最近
                holder.getView(R.id.ll_position).setOnClickListener(this);

                //好评
                holder.getView(R.id.ll_good).setOnClickListener(this);

                //商家列表
                recyclerView = holder.getView(R.id.recy);
                LinearLayoutManager manager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(manager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(false);


                adapter = new BaseLoadMoreHeaderAdapter<DepDynamic>(context, recyclerView, list, R.layout.dep_mess_item) {
                    @Override
                    public void convert(Context mContext, BaseRecyclerHolder holder, DepDynamic o) {
                        holder.setImageByUrl(R.id.logo, o.getLogo());
                        holder.setText(R.id.name, o.getName());
                        holder.setText(R.id.sales_num,"销量:"+o.getDeptSalesVolume());
                        holder.setText(R.id.dynamic,o.getActivityName());
                        holder.setText(R.id.reach_time,o.getReachTime()+"分内送达");
                        holder.setText(R.id.juli,"距您"+o.getJuli()+"米");
                        holder.setText(R.id.distance,"配送距离:"+o.getDistance());
                    }
                };

                adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(context, DepMessActivity.class);
                        intent.putExtra("depId",list.get(position).getDeptId());
                        context.startActivity(intent);
                    }
                });

                recyclerView.setAdapter(adapter);

                //加载更多
                recyclerView.addOnScrollListener(new onLoadMoreListener() {
                    @Override
                    protected void onLoading(int countItem, int lastItem) {
                        dis += 3;
                        initData(1,lon,lat);
                    }
                });


                initData(0,lon,lat);
                break;
            default:
                break;
        }
    }

    /**
     * 排序规则
     * @param sidx  juli:按距离，commnet：按好评
     */
    public void setSidx(String sidx) {
        this.sidx = sidx;
        initData(0,lon,lat);
    }

    /**
     * 综合排序
     */
    public void setSale() {
        sidx="";
        initData(0,lon,lat);
    }


    /**
     * 根据position的不同选择不同的布局类型
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) return COMMENT_FIRST;
        if (position == 1) return COMMENT_SECOND;
        return position;
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return 2;
    }

    /**
     * 加载数据
     *
     * @param index 0更新，1加载更多
     */
    public void initData(final int index,String lon,String lat) {
        Map<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token", ""));
        map.put("longitude", lon);//经度
        map.put("latitude", lat);//纬度
        map.put("dis", String.valueOf(dis));//半径
//        map.put("foodType", foodType);//商家类型，默认推荐商家
        map.put("sidx", sidx);//排序规则，不传默认按综合销量，juli:按距离，commnet：按好评
        map.put("limit", "10");
        map.put("page", "1");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getNearbyDeptList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (errno == 0) {
                        if (object.optInt("code")==500){
                            pdUtil.desLoding();
                            ToastUtil.showShort(context,"数据更新中，请重新进入页面");
                            return ;
                        }
                        JSONObject dataObj = object.optJSONObject("data");
                        String str = dataObj.optString("deptList");
                        Log.e("商家列表",str);
                        list = JsonUtil.string2Obj(str, List.class, DepDynamic.class);
                        if (null != list) {
                            if (index == 0) {
                                adapter.updateData(list);
                            }
                            if (index == 1) {
                                adapter.addAll(list);
                            }
                        }
                    }
                    pdUtil.desLoding();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }

    private void getDepType() {
        Map<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token", ""));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getFoodType, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (0 == errno) {
                        if (500 == object.optInt("code")) {
                            return ;
                        }
                        String str = object.optString("data");
                        Log.e("服务器返回:",str);
                       /* typeList = JsonUtil.string2Obj(str, List.class, DepTypeMo.class);
                        adapterx.updateData(typeList);*/
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_sal:
                pdUtil.showLoding("加载中...");
                setSale();
                break;
            case R.id.ll_position:
                pdUtil.showLoding("加载中...");
                setSidx("juli");
                break;
            case R.id.ll_good:
                pdUtil.showLoding("加载中...");
                setSidx("commnet");
                break;
        }
    }
}