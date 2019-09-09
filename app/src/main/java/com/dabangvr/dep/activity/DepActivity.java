package com.dabangvr.dep.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.activity.PositionActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.PositionUtils;
import com.dabangvr.model.DepDynamic;
import com.dabangvr.model.DepTypeMo;
import com.dabangvr.util.BannerStart;
import com.dabangvr.util.GlideLoadUtils;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
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

public class DepActivity extends BaseActivity implements AMapLocationListener, View.OnClickListener{

    private NestedScrollView scrollView;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private String[] strMsg;
    private String lat = "0";//纬度
    private String lot = "0";//经度
    private int page;

    private TextView tvCity;
    private PdUtil pdUtil;
    private String sidx = "";//店铺排序规则,不传默认按综合销量，juli:按距离，commnet：按好评

    //商家列表
    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;
    private List<DepDynamic> list = new ArrayList<>();

    //店铺类型列表
    private RecyclerView recyclerViewType;
    private BaseLoadMoreHeaderAdapter adapterType;
    private List<DepTypeMo> typeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    private boolean per() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            };
            if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(permissions, 0);
                return false;
            }
        }
        return true;
    }

    @Override
    public int setLayout() {
        return R.layout.activity_dep;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != aMapLocation) {
            Message msg = mHandler.obtainMessage();
            msg.obj = aMapLocation;
            msg.what = PositionUtils.MSG_LOCATION_FINISH;
            mHandler.sendMessage(msg);
        }
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //定位完成
                case PositionUtils.MSG_LOCATION_FINISH:
                    String result = "";
                    try {
                        AMapLocation loc = (AMapLocation) msg.obj;
                        result = PositionUtils.getLocationStr(loc, 1);
                        strMsg = result.split(",");

                        if (StringUtils.isEmpty(loc.getAoiName())) {
                            tvCity.setText(loc.getStreet() + loc.getStreetNum());
                        } else {
                            tvCity.setText(loc.getAoiName());
                        }

                        loc.getDistrict();
                        loc.getStreet();
                        lot = strMsg[1];
                        lat = strMsg[2];
                        startLoadDepList(0);
                    } catch (Exception e) {
                        Toast.makeText(DepActivity.this, "定位失败", Toast.LENGTH_LONG).show();
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /**
     * 获取商家列表
     *
     * @param index 0刷新,1加载更多
     */
    private void startLoadDepList(final int index) {
        Map<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this, "token"));
        map.put("longitude", lot);//经度
        map.put("latitude", lat);//纬度
//        map.put("foodType", foodType);//商家类型，默认推荐商家
        map.put("sidx", sidx);//排序规则，不传默认按综合销量，juli:按距离，commnet：按好评
        map.put("limit", "10");
        if (index > 0) {
            page += 1;
        } else {
            page = 1;
        }
        map.put("page", String.valueOf(page));

        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getNearbyDeptList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (errno == 0) {
                        if (object.optInt("code") == 500) {
                            pdUtil.desLoding();
                            ToastUtil.showShort(DepActivity.this, "数据更新中，请重新进入页面");
                            return;
                        }
                        JSONObject dataObj = object.optJSONObject("data");
                        String str = dataObj.optString("deptList");
                        Log.e("商家列表", str);
                        list = JsonUtil.string2Obj(str, List.class, DepDynamic.class);
                        if (null != list) {
                            if (index == 0) {
                                adapter.updateData(list);
                            }
                            if (index == 1) {
                                if (null == list || list.size() == 0){
                                    if (page>1)page--;
                                }
                                adapter.addAll(list);
                            }
                        }
                    }
                    if (null != pdUtil){
                        if (pdUtil.isShow()){
                            pdUtil.desLoding();
                        }
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
    protected void initView() {
        if (per()) {
            initLocation();
        }

        ImageView imageView = findViewById(R.id.top_img);
        GlideLoadUtils.getInstance().glideLoad(this, DyUrl.MD_TOP_IMG, imageView);

        pdUtil = new PdUtil(this);

        scrollView = findViewById(R.id.scrollView);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                View view = v.getChildAt(0);
                if (v.getHeight() + v.getScrollY() == view.getHeight()) {
                    _calCount++;
                    if (_calCount == 1) {
                        startLoadDepList(1);
                    }
                } else {
                    _calCount = 0;
                }
            }
        });

        //搜索商家
        findViewById(R.id.start_search).setOnClickListener(this);

        //当前定位
        findViewById(R.id.top_position).setOnClickListener(this);

        //城市名称
        tvCity = findViewById(R.id.show_city);

        //店铺列表
        recyclerView = findViewById(R.id.recy);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(manager);

        adapter = new BaseLoadMoreHeaderAdapter<DepDynamic>(this, recyclerView, list, R.layout.dep_mess_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, DepDynamic o) {
                int ii = list.size();
                holder.setImageByUrl(R.id.logo, o.getLogo());
                holder.setText(R.id.name, o.getName());
                if (null == o.getDeptSalesVolume() || o.getDeptSalesVolume().equals("null")) {
                    holder.setText(R.id.sales_num, "销量:0");
                } else {
                    holder.setText(R.id.sales_num, "销量:" + o.getDeptSalesVolume());
                }
                holder.setText(R.id.dynamic, o.getActivityName());
                holder.setText(R.id.reach_time, o.getReachTime() + "分内送达");
                holder.setText(R.id.juli, "距您" + o.getJuli() + "米");
                holder.setText(R.id.distance, "配送距离:" + o.getDistance());
            }
        };

        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                list = adapter.getData();
                Intent intent = new Intent(DepActivity.this, DepMessActivity.class);
                intent.putExtra("depId", list.get(position).getDeptId());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);


        //店铺类型
        recyclerViewType = findViewById(R.id.type_recy);
        GridLayoutManager managerDepType = new GridLayoutManager(this, 5);
        recyclerViewType.setLayoutManager(managerDepType);
        recyclerViewType.setNestedScrollingEnabled(false);

        adapterType = new BaseLoadMoreHeaderAdapter<DepTypeMo>(this, recyclerViewType, typeList, R.layout.dep_type_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, DepTypeMo o) {
                SimpleDraweeView head = holder.getView(R.id.iv_img);
                head.setImageURI(o.getCategoryImg());
                holder.setText(R.id.tv_name, o.getName());
            }
        };
        recyclerViewType.setAdapter(adapterType);

        adapterType.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(DepActivity.this, DepSearchActivity.class);
                intent.putExtra("lon", lot);
                intent.putExtra("lat", lat);
                intent.putExtra("foodType", String.valueOf(typeList.get(position).getId()));
                startActivity(intent);
            }
        });

        //中间的轮播
        Banner banner = findViewById(R.id.banner);
        BannerStart.starBanner(this, banner, "7");

        findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        pdUtil.showLoding("正在获取您的位置信息");

        //获取店铺类型
        Map<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (getSPKEY(this, "token")));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getFoodType, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (0 == errno) {
                        if (500 == object.optInt("code")) {
                            return;
                        }
                        String str = object.optString("data");
                        Log.e("服务器返回:", str);
                        typeList = JsonUtil.string2Obj(str, List.class, DepTypeMo.class);
                        adapterType.updateData(typeList);
                    }
                } catch (
                        JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });

    }


    /**
     * 确定权限后，开始请求定位
     */
    private void initLocation() {
        try {
            locationClient = new AMapLocationClient(this);
            locationOption = new AMapLocationClientOption();
            // 设置定位模式为低功耗模式
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
            // 设置定位监听
            locationClient.setLocationListener(this);
            locationOption.setOnceLocation(true);//设置为单次定位
            locationClient.setLocationOption(locationOption);// 设置定位参数
            // 启动定位
            locationClient.startLocation();
            mHandler.sendEmptyMessage(PositionUtils.MSG_LOCATION_START);
        } catch (Exception e) {
            Toast.makeText(DepActivity.this, "定位失败", Toast.LENGTH_LONG).show();
            pdUtil.desLoding();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.top_position:
                Intent intent = new Intent(this, PositionActivity.class);
                startActivityForResult(intent, 99);
                break;
            case R.id.start_search:
                Intent intent1 = new Intent(this, DepSearchActivity.class);
                intent1.putExtra("lon", lot);
                intent1.putExtra("lat", lat);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            initLocation();
        }
        if (resultCode == 100) {
            if (requestCode == 99) {
                String city = data.getStringExtra("city");
                lot = data.getStringExtra("lot");
                lat = data.getStringExtra("lat");
                if (!StringUtils.isEmpty(city)) {
                    if (city.length() > 7) {
                        String a = city.substring(0, 7);
                        a += "...";
                        tvCity.setText(a);
                    } else {
                        tvCity.setText(city);
                    }
                }
                startLoadDepList(0);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        pdUtil = null;
        OkHttp3Utils.desInstance();
    }

    private int _calCount;

}
