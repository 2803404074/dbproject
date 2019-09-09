package com.dabangvr.common.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.dabangvr.R;
import com.dabangvr.common.weight.AMapUtil;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.PositionUtils;
import com.dabangvr.model.LocationAddressInfo;
import com.dabangvr.my.fragment.FragmentBusinessIden;
import com.dabangvr.util.ToastUtil;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class PositionActivityTen extends AppCompatActivity implements AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener, PoiSearch.OnPoiSearchListener {

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private TextView textView;
    private String[] strMsg;
    private com.amap.api.maps.AMap aMap;
    private MapView mapView;
    private GeocodeSearch geocoderSearch;
    private Marker geoMarker;
    private static LatLonPoint latLonPoint;

    //搜索
    private EditText etSearch;
    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;
    private List<LocationAddressInfo> mData = new ArrayList<>();

    private static final int REQUEST_PERMISSION_LOCATION = 0;
    private String keyWord = "";// 要输入的poi搜索关键字
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索


    //
    private String province;
    private String city;
    private String county;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_ten);
        etSearch = findViewById(R.id.position_search);
        textView = (TextView) findViewById(R.id.text_map);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        Location();


        recyclerView = findViewById(R.id.recy);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        adapter = new BaseLoadMoreHeaderAdapter<LocationAddressInfo>(PositionActivityTen.this,
                recyclerView, mData, R.layout.location_text) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, LocationAddressInfo o) {
                holder.setText(R.id.lo_title, o.getTitle());
                holder.setText(R.id.lo_mess, o.getText());
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                strMsg[1] = mData.get(position).getLon();
                strMsg[2] = mData.get(position).getLat();
                strMsg[0] = mData.get(position).getText();

                province = mData.get(position).getProvince();
                city = mData.get(position).getCity();
                county = mData.get(position).getCounty();

                etSearch.setText(mData.get(position).getText());

                latLonPoint = new LatLonPoint(Double.valueOf(mData.get(position).getLat()), Double.valueOf(mData.get(position).getLon()));
                getAddress(latLonPoint);
                mData.clear();
                adapter.updateData(mData);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(latLonPoint), 15));
                geoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
            }
        });

        //搜索监听
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                keyWord = String.valueOf(charSequence);
                if ("".equals(keyWord)) {
                    ToastUtil.showShort(PositionActivityTen.this, "请输入搜索关键字");
                } else {
                    doSearchQuery(keyWord);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 搜索
     *
     * @param key
     */
    protected void doSearchQuery(String key) {
        currentPage = 0;
        //不输入城市名称有些地方搜索不到
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query(key, "", "");
        // 设置每页最多返回多少条poiitem
        query.setPageSize(10);
        // 设置查询页码
        query.setPageNum(currentPage);

        //构造 PoiSearch 对象，并设置监听
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        //调用 PoiSearch 的 searchPOIAsyn() 方法发送请求。
        poiSearch.searchPOIAsyn();
    }


    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();

            //用高德默认图标
            geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
//            //自定义图标
//            geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
//                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.h_position))));
        }
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        getAddress(latLonPoint);
    }


    /**
     * 确定返回
     * 名字起错了
     * @param view
     */
    public void startSearch(View view) {
        if (null == strMsg || strMsg.length<3 || StringUtils.isEmpty(province) || StringUtils.isEmpty(city)||StringUtils.isEmpty(county)){
            ToastUtil.showShort(this,"请选择地址");
        }else {
            FragmentBusinessIden.lat = strMsg[2];//纬度
            FragmentBusinessIden.lon = strMsg[1];//经度
            FragmentBusinessIden.setAddress(strMsg[0]);

            FragmentBusinessIden.province = province;
            FragmentBusinessIden.city = city;
            FragmentBusinessIden.county = county;

            finish();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation loc) {
        if (null != loc) {
            Message msg = mHandler.obtainMessage();
            msg.obj = loc;
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
//latitude=22.808254#longitude=108.365177#province=广西壮族自治区#coordType=GCJ02#city=南宁市
// #district=青秀区#cityCode=0771#adCode=450103#address=广西壮族自治区南宁市青秀区金湖路西一巷33号靠近中国建设银行(琅西支行)
// #country=中国#road=金湖路西一巷#poiName=中国建设银行(琅西支行)#street=金湖路西一巷#streetNum=33号#aoiName=广西壮族自治区人才大厦
// #poiid=#floor=#errorCode=0#errorInfo=success#locationDetail=#csid:52f1716ad54b4bd1bdbc654f16bca2bd
// #description=在中国建设银行(琅西支行)附近#locationType=5

                        AMapLocation loc = (AMapLocation) msg.obj;
                        province = loc.getProvince();
                        city = loc.getCity();
                        county = loc.getDistrict();
                        result = PositionUtils.getLocationStr(loc, 1);
                        strMsg = result.split(",");
                        Toast.makeText(PositionActivityTen.this, "定位成功", Toast.LENGTH_LONG).show();
                        textView.setText(strMsg[0]);
                        // + "\n" + "经    度：" + strMsg[1] + "\n" + "纬    度：" + strMsg[2]

                        latLonPoint = new LatLonPoint(Double.valueOf(strMsg[2]), Double.valueOf(strMsg[1]));
                        initMap();
                    } catch (Exception e) {
                        Toast.makeText(PositionActivityTen.this, "定位失败", Toast.LENGTH_LONG).show();
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

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
            Toast.makeText(PositionActivityTen.this, "定位失败", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {

    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {

//                Toast.makeText(PositionActivityTen.this, result.getRegeocodeAddress().getFormatAddress()
//                        + "附近", Toast.LENGTH_LONG).show();
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(latLonPoint), 15));
                geoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
            } else {

            }
        } else {
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
//rCode 为1000 时成功,其他为失败
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            // 解析result   获取搜索poi的结果
            if (result != null && result.getQuery() != null) {
                if (result.getQuery().equals(query)) {  // 是否是同一条
                    poiResult = result;
                    ArrayList<LocationAddressInfo> data = new ArrayList<LocationAddressInfo>();//自己创建的数据集合
                    // 取得第一页的poiitem数据，页数从数字0开始
                    //poiResult.getPois()可以获取到PoiItem列表
                    List<PoiItem> poiItems = poiResult.getPois();

                    //若当前城市查询不到所需POI信息，可以通过result.getSearchSuggestionCitys()获取当前Poi搜索的建议城市
                    List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();
                    //如果搜索关键字明显为误输入，则可通过result.getSearchSuggestionKeywords()方法得到搜索关键词建议。
                    List<String> suggestionKeywords = poiResult.getSearchSuggestionKeywords();

                    //解析获取到的PoiItem列表
                    for (PoiItem item : poiItems) {
                        //获取经纬度对象
                        LatLonPoint llp = item.getLatLonPoint();
                        double lon = llp.getLongitude();
                        double lat = llp.getLatitude();
                        //返回POI的名称
                        String title = item.getTitle();
                        //返回POI的地址
                        String text = item.getSnippet();

                        String province = item.getProvinceName();
                        String city = item.getCityName();
                        String county = item.getAdName();
                        data.add(new LocationAddressInfo(String.valueOf(lon), String.valueOf(lat), title, text, province, city, county));
                    }
                    mData = data;
                    adapter.updateData(data);
                }
            } else {
                ToastUtil.showShort(PositionActivityTen.this, "无搜索结果");
            }
        } else {
            ToastUtil.showShort(PositionActivityTen.this, "错误码" + rCode);

        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}