package com.dabangvr.common.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.ToastUtil;
import com.rey.material.app.BottomSheetDialog;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.DyUrl;
import okhttp3.Call;

/**
 * 地址操作
 */
public class SetAdreessActivity extends AppCompatActivity implements View.OnClickListener {

    private String province;
    private String city;
    private String district;
    private TextView setAdress;//省市区
    private EditText set_name;//收货人
    private EditText set_phone;//手机号码
    private EditText set_detailed;//详细信息
    private Switch switch1;//设为默认地址
    private SPUtils spUtils;
    private List<ItemBean> list;
    private MyAdapterx adapterx;
    private TextView pcd;//显示省市县名称控件
    private String pcdName;//省市县字符串
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_adreess);
        initView();
    }

    private void initView() {
        spUtils = new SPUtils(this, "db_user");

        String id = getIntent().getStringExtra("id");

        findViewById(R.id.set_ssq).setOnClickListener(this);//选择省市区的控件按钮
        findViewById(R.id.set_add_save).setOnClickListener(this);//保存按钮
        findViewById(R.id.set_backe).setOnClickListener(this);//左上角返回
        setAdress = findViewById(R.id.set_address);//省市区
        set_name = findViewById(R.id.set_name);
        set_phone = findViewById(R.id.set_phone);
        set_detailed = findViewById(R.id.set_detailed);
        switch1 = findViewById(R.id.switch1);


        if (!StringUtils.isEmpty(id)){//更新
            setAdress.setText(getIntent().getStringExtra("provinceOne")+"-"+getIntent().getStringExtra("cityOne")+"-"+getIntent().getStringExtra("areaOne"));//省市县
            set_name.setText(getIntent().getStringExtra("consigneeName"));//收货人
            set_phone.setText(getIntent().getStringExtra("consigneePhone"));//电话
            set_detailed.setText(getIntent().getStringExtra("address"));//详细信息

            province = getIntent().getStringExtra("province");
            city = getIntent().getStringExtra("city");
            district = getIntent().getStringExtra("area");

            if (getIntent().getIntExtra("isDefault",0) == 1){
                switch1.setChecked(true);
            }else {
                switch1.setChecked(false);
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_ssq: {
                buttomDialog();
                break;
            }
            case R.id.set_add_save: {
                if (!StringUtils.isEmpty(getIntent().getStringExtra("id"))){
                    //修改地址请求
                    setHttp(DyUrl.addressUpdate,getIntent().getStringExtra("id"));
                }else {
                    //添加地址请求
                    setHttp(DyUrl.AddressAdd,"");
                }
                break;
            }
            case R.id.set_backe: {
                setResult(100);
                finish();
                break;
            }
        }
    }

    /**
     *
     * @param url
     * @param id  存在该id的就是修改否则是添加
     */
    private void setHttp(String url,String id) {
        String name = set_name.getText().toString();
        String phone = set_phone.getText().toString();
        String detailed = set_detailed.getText().toString();
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(phone) || StringUtils.isEmpty(detailed)) {
            ToastUtil.showShort(this, "请输入完整的收货信息");
            return;
        }
        if (phone.length() < 11) {
            ToastUtil.showShort(this, "您的手机号输入有误，请重新输入");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token", ""));
        map.put("consigneeName", set_name.getText().toString());//收货人
        map.put("consigneePhone", set_phone.getText().toString());//手机号
        map.put("receivingCountry", "1");//收货国家,中国 1
        map.put("province", province);//省
        map.put("city", city);//市
        map.put("area", district);//县/区
        map.put("address", set_detailed.getText().toString());//详细
        map.put("zipCode", "");//邮编

        if (!StringUtils.isEmpty(id)){
            map.put("id",id);
        }
        if (switch1.isChecked()) {
            map.put("isDefault", "1");//默认
        } else {
            map.put("isDefault", "0");//默认
        }

        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(url, map, new GsonObjectCallback<String>(DyUrl.BASE) {

            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (errno == 0) {
                        if(500 == object.optInt("code")){
                            return ;
                        }
                        ToastUtil.showShort(SetAdreessActivity.this,"添加成功");
                        setResult(100);
                        finish();
                    }else {
                        ToastUtil.showShort(SetAdreessActivity.this,"添加失败");
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

    private void buttomDialog() {
        //初始化底部弹窗
        final BottomSheetDialog bottomInterPasswordDialog = new BottomSheetDialog(this);

        //初始化 - 底部弹出框布局
        View view = LayoutInflater.from(this).inflate(R.layout.address_dialog, null);
        pcd = view.findViewById(R.id.address_show);
        final ListView listView = view.findViewById(R.id.address_list);
        getAddress(listView, "1", "0");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //ToastUtil.showShort(SetAdreessActivity.this, "点击了name = " + list.get(position).getName() + ",id=" + list.get(position).getId());

                if(type==0){
                    getAddress(listView, list.get(position).getId(), "1");
                    pcdName = list.get(position).getName();

                    province = list.get(position).getId();

                    pcd.setText(pcdName);
                    type+=1;
                }else if(type==1){
                    getAddress(listView, list.get(position).getId(), "2");
                    pcdName +="-" + list.get(position).getName();

                    city = list.get(position).getId();

                    pcd.setText(pcdName);
                    type +=1;
                }else {
                    pcdName +="-" + list.get(position).getName();

                    district = list.get(position).getId();

                    pcd.setText(pcdName);
                    type=0;

                    //设置控件
                    setAdress.setText(pcdName);
                    bottomInterPasswordDialog.dismiss();
                }
            }
        });

        bottomInterPasswordDialog
                .contentView(view)/*加载视图*/
                /*.heightParam(height/2),显示的高度*/
                /*动画设置*/
                .inDuration(500)
                .outDuration(500)
                .inInterpolator(new BounceInterpolator())
                .outInterpolator(new AnticipateInterpolator())
                .cancelable(true)
                .show();
        if (!bottomInterPasswordDialog.isShowing()) {
            list.clear();
            adapterx = null;
        }
    }

    private void getAddress(final ListView listView, String areaId, String type) {
        HashMap<String, String> map = new HashMap<>();
        map.put("areaId", areaId);
        map.put("type", type);
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.proviceList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int code = object.optInt("code");
                    if (code == 0) {
                        if (null == list) {
                            list = new ArrayList<>();
                        } else {
                            list.clear();
                        }
                        JSONArray array = object.optJSONArray("dataList");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object1 = (JSONObject) array.get(i);
                            String name = object1.optString("name");
                            String id = object1.optString("id");
                            ItemBean bean = new ItemBean();
                            bean.setId(id);
                            bean.setName(name);
                            list.add(bean);
                        }
                        adapterx = null;
                        adapterx = new MyAdapterx(list);
                        listView.setAdapter(adapterx);

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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_open_left, R.anim.activity_close);
    }

    private class MyAdapterx extends BaseAdapter {

        private List<ItemBean> nameList;

        public MyAdapterx(List<ItemBean> nameList) {
            this.nameList = nameList;
        }

        @Override
        public int getCount() {
            return nameList.size();
        }

        @Override
        public Object getItem(int position) {
            return nameList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder mViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(SetAdreessActivity.this).inflate(R.layout.address_province_item, parent, false);
                mViewHolder = new ViewHolder(convertView);
                convertView.setTag(mViewHolder);

            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            mViewHolder.mTvTitle.setText(nameList.get(position).getName());
            return convertView;
        }
    }

    static class ViewHolder {
        TextView mTvTitle;//收获人名称

        ViewHolder(View view) {
            mTvTitle = view.findViewById(R.id.address_item_tv);
        }
    }

    class ItemBean {
        private String id;
        private String name;

        public ItemBean() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(100);
        finish();
    }
}
