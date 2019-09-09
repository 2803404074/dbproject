package com.dabangvr.common.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.dabangvr.R;
import com.dabangvr.common.adapter.ContactAdapter;
import com.dabangvr.common.weight.PositionUtils;
import com.dabangvr.common.weight.ShowButtonLayout;
import com.dabangvr.common.weight.ShowButtonLayoutData;
import com.dabangvr.model.ChildrenCity;
import com.dabangvr.model.CityBean;
import com.dabangvr.util.FileUtil;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoadingDialog;
import com.dabangvr.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import Utils.PdUtil;
import me.yokeyword.indexablerv.IndexableAdapter;
import me.yokeyword.indexablerv.IndexableHeaderAdapter;
import me.yokeyword.indexablerv.IndexableLayout;


/**
 * 定位
 */

public class
PositionActivity extends BaseActivity implements AMapLocationListener {
    private PdUtil pdUtil;
    private String[] strMsg;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private LoadingDialog loadingDialog;
    private ContactAdapter mAdapter;
    private BannerHeaderAdapter mBannerHeaderAdapter;
    private List<ChildrenCity> city;
    private IndexableLayout indexableLayout;
    private ImageView pic_contact_back;
    private Intent intent;
    private EditText editText;
    private TextView nowCity;//当前显示的城市
    private ImageView searchImg;//搜索按钮

    private LinearLayout searchContent;//搜索结果控件
    private TextView tvSearchResult;//搜索结果

    private double lat;//纬度
    private double lot;//经度


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        Location();
    }
    @Override
    public int setLayout() {
        return R.layout.activity_pick_contact;
    }

    public void Location() {
        // TODO Auto-generated method stub
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
            Toast.makeText(PositionActivity.this, "定位失败", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void initData() {
        city = new ArrayList<>();
        city.add(new ChildrenCity("北京", 116.46, 39.92));
        city.add(new ChildrenCity("上海", 121.48, 31.22));
        city.add(new ChildrenCity("广州", 113.23, 23.16));
        city.add(new ChildrenCity("深圳", 114.07, 22.62));
        city.add(new ChildrenCity("杭州", 120.19, 30.26));
        city.add(new ChildrenCity("重庆", 106.54, 29.59));
        city.add(new ChildrenCity("海南", 110.35, 20.02));
        city.add(new ChildrenCity("厦门", 118.1, 24.46));
        city.add(new ChildrenCity("南宁", 108.33, 22.84));
    }


    public void initAdapter() {
        mAdapter = new ContactAdapter(this);
        indexableLayout.setAdapter(mAdapter);
        indexableLayout.setOverlayStyle_Center();
        mAdapter.setDatas(initDatas());
        indexableLayout.setCompareMode(IndexableLayout.MODE_FAST);

        List<String> bannerList = new ArrayList<>();
        bannerList.add("");
        mBannerHeaderAdapter = new BannerHeaderAdapter("↑", null, bannerList);
        indexableLayout.addHeaderAdapter(mBannerHeaderAdapter);
    }

    @Override
    protected void initView() {
        pdUtil = new PdUtil(this);
        intent = getIntent();
        loadingDialog = new LoadingDialog(this);
        pic_contact_back = findViewById(R.id.pic_contact_back);
        indexableLayout = findViewById(R.id.indexableLayout);
        indexableLayout.setLayoutManager(new LinearLayoutManager(this));
        editText = findViewById(R.id.po_search_edit);//搜索控件
        nowCity = findViewById(R.id.item_header_city_dw);//当前显示的城市
        searchImg = findViewById(R.id.po_start_search);//开始搜索按钮

        searchContent = findViewById(R.id.search_result);//显示/隐藏的搜索结果区域
        tvSearchResult = findViewById(R.id.search_result_city);//搜索结果

        //点击搜索监听
        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();

            }
        });

        //点击右上角当前城市名称监听
        nowCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishMather(nowCity.getText().toString(), lot, lat);
            }
        });

        //左上角返回按钮监听
        pic_contact_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishMather(nowCity.getText().toString(), lot, lat);
            }
        });

        initAdapter();

        //城市列表点击回调
        mAdapter.setOnItemContentClickListener(new IndexableAdapter.OnItemContentClickListener<ChildrenCity>() {
            @Override
            public void onItemClick(View v, int originalPosition, int currentPosition, ChildrenCity entity) {
                if (originalPosition >= 0) {
                    finishMather(entity.getName(), entity.getLog(), entity.getLat());
                } else {
                    // ToastUtil.showShort(PositionActivity.this, "选中Header/Footer:" + entity.getNick() + "  当前位置:" + currentPosition);
                }
            }
        });
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
                        nowCity.setText(strMsg[0]);
                        //nowCity.setText(strMsg[1]);
                        lot = Double.parseDouble(strMsg[1]);
                        lat = Double.parseDouble(strMsg[2]);
                    } catch (Exception e) {
                        Toast.makeText(PositionActivity.this, "定位失败", Toast.LENGTH_LONG).show();
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /**
     * 自定义的Banner Header
     */
    class BannerHeaderAdapter extends IndexableHeaderAdapter {
        private static final int TYPE = 1;
        private int TAG = 0;

        public BannerHeaderAdapter(String index, String indexTitle, List datas) {
            super(index, indexTitle, datas);
        }

        @Override
        public int getItemViewType() {
            return TYPE;
        }

        @Override
        public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(PositionActivity.this).inflate(R.layout.item_city_header, parent, false);
            VH holder = new VH(view);
            return holder;
        }

        @Override
        public void onBindContentViewHolder(RecyclerView.ViewHolder holder, Object entity) {
            // 数据源为null时, 该方法不用实现
            if (TAG == 0) {
                final VH vh = (VH) holder;
                //热门城市点击监听
                ShowButtonLayoutData data = new ShowButtonLayoutData<ChildrenCity>(PositionActivity.this, vh.hotLayout, city,
                        new ShowButtonLayoutData.MyClickListener() {
                            @Override
                            public void clickListener(View v, double lot, double lat, boolean isCheck) {
                                String tag = (String) v.getTag();
                                finishMather(tag, lot, lat);
                            }
                        });
                data.setData();
                TAG++;
            }
        }

        //显示热门城市控件 ShowButtonLayout 初始化
        private class VH extends RecyclerView.ViewHolder {
            ShowButtonLayout hotLayout;

            public VH(View itemView) {
                super(itemView);
                hotLayout = itemView.findViewById(R.id.po_hot_layout);
            }
        }
    }

    /**
     * 引用静态城市数据
     *
     * @return
     */
    private List<ChildrenCity> initDatas() {
        String assetsData = FileUtil.getAssetsData(this, "city.json");
        List<ChildrenCity> hList = new ArrayList<>();
        List<CityBean> list = JsonUtil.string2Obj(assetsData, List.class, CityBean.class);
        for (int i = 0; i < list.size(); i++) { // TODO: 2019/8/3 list 为空
            List<ChildrenCity> mList = list.get(i).getChildren();
            for (int j = 0; j < mList.size(); j++) {
                hList.add(mList.get(j));
            }
        }
        return hList;
    }


    /**
     * 选择后结束当前页面，利用广播通知回调的城市名称
     *
     * @param city
     */
    private void finishMather(String city, double lot, double lat) {
        Intent intent = new Intent();
        intent.putExtra("city", city);
        intent.putExtra("lot", String.valueOf(lot));
        intent.putExtra("lat", String.valueOf(lat));
        setResult(100,intent);
        finish();
    }

    /**
     * 显示搜索结果区域和结果
     *
     * @param msg
     * @param
     */
    private void showHiden(final String msg) {
        searchContent.setVisibility(View.VISIBLE);
        tvSearchResult.setText(msg);
        tvSearchResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishMather(msg, lot, lat);
            }
        });
    }

}
