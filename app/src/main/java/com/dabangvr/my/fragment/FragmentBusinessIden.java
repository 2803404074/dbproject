package com.dabangvr.my.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.common.activity.PositionActivityTen;
import com.dabangvr.model.DepTypeMo;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.ToastUtil;
import com.dbvr.imglibrary.model.Image;
import com.dbvr.imglibrary.ui.SelectImageActivity;
import com.dbvr.imglibrary.ui.adapter.SelectedImageAdapter;
import com.dbvr.imglibrary.utils.TDevice;
import com.dbvr.imglibrary.widget.recyclerview.SpaceGridItemDecoration;
import com.example.model.BusinessMo;
import com.example.mylibrary.ViewPagerSlide;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import Utils.PdUtil;
import config.DyUrl;
import okhttp3.Call;

import static android.app.Activity.RESULT_OK;
import static com.mob.tools.utils.ResHelper.getFileSize;

/**
 * 申请主播第一个页面
 */

@SuppressLint("ValidFragment")
public class FragmentBusinessIden extends Fragment implements View.OnClickListener {
    private Context context;
    private ViewPagerSlide viewPager;
    private SPUtils spUtils;
    private List<DepTypeMo> typeList;

    private RadioGroup radioGroup;
    private String depType;
    private EditText et_name;
    private EditText et_jj;


    private static TextView et_pc;

    public FragmentBusinessIden(ViewPagerSlide viewPager) {
        this.viewPager = viewPager;
    }

    public static String lon;
    public static String lat;

    public static String province;
    public static String city;
    public static String county;
    private View view;


    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int SELECT_IMAGE_REQUEST = 0x0011;
    private ArrayList<Image> mSelectImages = new ArrayList<>();
    private RecyclerView mSelectedImageRv;
    private TextView mDragTip;
    private SelectedImageAdapter mAdapter;
    private TextView btnSelect;


    private static final int PERMISSION_REQUEST_CODE2 = 1;
    private static final int SELECT_IMAGE_REQUEST2 = 100;
    private ArrayList<Image> mSelectImages2 = new ArrayList<>();
    private RecyclerView mSelectedImageRv2;
    private TextView mDragTip2;
    private SelectedImageAdapter mAdapter2;
    private TextView btnSelect2;

    private CheckBox checkBox;
    private BusinessMo businessMo;
    private PdUtil pdUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        context = FragmentBusinessIden.this.getContext();
        view = inflater.inflate(R.layout.fg_business01, null);
        spUtils = new SPUtils(context, "db_user");
        pdUtil = new PdUtil(context);
        initView(view);
        return view;
    }

    //初始化控件
    private void initView(View view) {

        //上一步
        view.findViewById(R.id.bt_pri).setOnClickListener(this);

        view.findViewById(R.id.btn_send).setOnClickListener(this);

        //装在店铺类型
        radioGroup = view.findViewById(R.id.dep_radio);

        //获取店铺类型
        getDepType();

        et_name = view.findViewById(R.id.et_name);
        et_jj = view.findViewById(R.id.et_jj);

        et_pc = view.findViewById(R.id.et_pc);

        checkBox = view.findViewById(R.id.checkbox);
        view.findViewById(R.id.bu_position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PositionActivityTen.class);
                startActivity(intent);
            }
        });

        //门店照
        mDragTip = view.findViewById(R.id.drag_tip);
        mSelectedImageRv = view.findViewById(R.id.rv_selected_image);
        mSelectedImageRv.setLayoutManager(new GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false));
        mSelectedImageRv.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 1)));
        btnSelect = view.findViewById(R.id.btn_select);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(PERMISSION_REQUEST_CODE, "selected_images", SELECT_IMAGE_REQUEST, mSelectImages);
            }
        });

        //营业执照
        mDragTip2 = view.findViewById(R.id.drag_tip2);
        mSelectedImageRv2 = view.findViewById(R.id.rv_selected_image2);
        mSelectedImageRv2.setLayoutManager(new GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false));
        mSelectedImageRv2.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 1)));
        btnSelect2 = view.findViewById(R.id.btn_select2);
        btnSelect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(PERMISSION_REQUEST_CODE2, "selected_images2", SELECT_IMAGE_REQUEST2, mSelectImages2);
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
                        typeList = JsonUtil.string2Obj(str, List.class, DepTypeMo.class);
                        addview(radioGroup);
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

    /**
     * 动态添加 RadioButton
     *
     * @param radiogroup
     */
    public void addview(RadioGroup radiogroup) {
        int index = 0;
        for (DepTypeMo depTypeMo : typeList) {
            final RadioButton rb = new RadioButton(context);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//这里设置radiogroup的宽高
            rb.setId(depTypeMo.getId());
            params.setMargins(20, 0, 0, 0);
            rb.setText(depTypeMo.getName());
            rb.setTextColor(context.getResources().getColor(R.color.colorDb3));
            rb.setButtonDrawable(R.drawable.zf_select);
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.showShort(context, rb.getText().toString());
                    depType = String.valueOf(rb.getId());
                }
            });
            radiogroup.addView(rb, index, params);
            index++;
        }
    }

    /**
     * 判断
     */
    private void judge() {
        String name = et_name.getText().toString();
        String jj = et_jj.getText().toString();
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(jj)) {
            shouw("请完善必填信息");
        } else if (StringUtils.isEmpty(lat)
                || StringUtils.isEmpty(lon)
                || StringUtils.isEmpty(province)
                || StringUtils.isEmpty(city)
                || StringUtils.isEmpty(county)) {
            shouw("请选择店铺位置信息");
        } else if (mSelectImages == null || mSelectImages.size() < 1) {
            shouw("请上传门店照");
        } else if (mSelectImages2 == null || mSelectImages2.size() < 1) {
            shouw("请上传营业执照");
        } else if (StringUtils.isEmpty(depType)) {
            shouw("未选择店铺经营类型");
        } else if (!checkBox.isChecked()) {
            shouw("未同意海风暴协议");
        } else {
            pdUtil.showLoding("正在上传资料");
            String str = (String) spUtils.getkey("SJRZ", "");
            businessMo = JsonUtil.string2Obj(str, BusinessMo.class);
            businessMo.setName(name);
            businessMo.setSynopsis(jj);
            businessMo.setFoodType(depType);
            businessMo.setAgreedAgreement("1");
            businessMo.setProductionAddress(et_pc.getText().toString());
            businessMo.setProductionProvince(province);
            businessMo.setProductionCity(city);
            businessMo.setProductionCounty(county);
            businessMo.setLon(lon);
            businessMo.setLat(lat);

            List<File> list = new ArrayList<>();
            for (int i = 0; i < mSelectImages.size(); i++) {
                if (StringUtils.isEmpty(mSelectImages.get(i).getPath())) {
                    continue;
                }
                File file = new File(mSelectImages.get(i).getPath());

                try {
                    String size = formatFileSize(getFileSize(file));
                    Log.e("hello", "门店照 "+i+"个文件的大小为："+size);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                list.add(file);
            }

            List<File> list2 = new ArrayList<>();
            for (int i = 0; i < mSelectImages2.size(); i++) {
                if (StringUtils.isEmpty(mSelectImages2.get(i).getPath())) {
                    continue;
                }
                File file = new File(mSelectImages2.get(i).getPath());


                try {
                    String size = formatFileSize(getFileSize(file));
                    Log.e("hello", "营业执照 "+i+"个文件的大小为："+size);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                list2.add(file);
            }

            businessMo.setStoreImgs(list);
            businessMo.setThreeCertificates(list2);

            startForMyServer();
        }
    }

    public long getFileSize(File f) throws Exception {

        long l = 0;
        if (f.exists()) {

            FileInputStream mFIS = new FileInputStream(f);

            l = mFIS.available();

        } else {

            f.createNewFile();
        }
        return l;

    }

    public String formatFileSize(long fSize) {

        DecimalFormat df = new DecimalFormat("#.00");

        String fileSizeString = "";

        if (fSize < 1024) {

            fileSizeString = df.format((double) fSize) + "B";

        } else if (fSize > 104875) {

            fileSizeString = df.format((double) fSize / 1024) + "K";
        } else if (fSize > 1073741824) {

            fileSizeString = df.format((double) fSize / 104875) + "M";

        } else {

            fileSizeString = df.format((double) fSize / 1073741824) + "G";

        }

        return fileSizeString;

    }

    public static void setAddress(String address) {
        et_pc.setText(address);
    }


    private void selectImage(int code, String arrName, int requestCode, ArrayList<Image> list) {
        int isPermission1 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int isPermission2 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (isPermission1 == PackageManager.PERMISSION_GRANTED && isPermission2 == PackageManager.PERMISSION_GRANTED) {
            startActivity(arrName, requestCode, list);
        } else {
            //申请权限
            ActivityCompat.requestPermissions(FragmentBusinessIden.this.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }

    private void startActivity(String arrName, int requestCode, ArrayList<Image> list) {
        Intent intent = new Intent(context, SelectImageActivity.class);
        intent.putExtra("name", arrName);
        intent.putParcelableArrayListExtra(arrName, list);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity("selected_images", SELECT_IMAGE_REQUEST, mSelectImages);
            } else {
                //申请权限
                ActivityCompat.requestPermissions(FragmentBusinessIden.this.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                Toast.makeText(context, "需要您的存储权限!", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PERMISSION_REQUEST_CODE2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity("selected_images2", SELECT_IMAGE_REQUEST2, mSelectImages2);
            } else {
                //申请权限
                ActivityCompat.requestPermissions(FragmentBusinessIden.this.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                Toast.makeText(context, "需要您的存储权限!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE_REQUEST && data != null) {
                ArrayList<Image> selectImages = data.getParcelableArrayListExtra(SelectImageActivity.EXTRA_RESULT);
                mSelectImages.clear();
                mSelectImages.addAll(selectImages);
                if (mSelectImages.size() > 1) {
                    mDragTip.setVisibility(View.VISIBLE);
                    if (mSelectImages.size() < 9) {
                        btnSelect.setText("继续添加");
                    } else {
                        btnSelect.setText("最多只能添加九张图哦");
                    }
                }
                mAdapter = new SelectedImageAdapter(context, mSelectImages, R.layout.selected_image_item);
                mSelectedImageRv.setAdapter(mAdapter);
                mItemTouchHelper.attachToRecyclerView(mSelectedImageRv);
            }

            if (requestCode == SELECT_IMAGE_REQUEST2 && data != null) {
                ArrayList<Image> selectImages = data.getParcelableArrayListExtra(SelectImageActivity.EXTRA_RESULT);
                mSelectImages2.clear();
                mSelectImages2.addAll(selectImages);
                if (mSelectImages2.size() > 1) {
                    mDragTip2.setVisibility(View.VISIBLE);
                    if (mSelectImages2.size() < 9) {
                        btnSelect2.setText("继续添加");
                    } else {
                        btnSelect2.setText("最多只能添加九张图哦");
                    }
                }
                mAdapter2 = new SelectedImageAdapter(context, mSelectImages2, R.layout.selected_image_item);
                mSelectedImageRv2.setAdapter(mAdapter2);
                mItemTouchHelper2.attachToRecyclerView(mSelectedImageRv2);
            }


        }
    }

    private ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

            // 获取触摸响应的方向   包含两个 1.拖动dragFlags 2.侧滑删除swipeFlags
            // 代表只能是向左侧滑删除，当前可以是这样ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT
            int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            int dragFlags;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            } else {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            }
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        /**
         * 拖动的时候不断的回调方法
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //获取到原来的位置
            int fromPosition = viewHolder.getAdapterPosition();
            //获取到拖到的位置
            int targetPosition = target.getAdapterPosition();
            if (fromPosition < targetPosition) {
                for (int i = fromPosition; i < targetPosition; i++) {
                    Collections.swap(mSelectImages, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > targetPosition; i--) {
                    Collections.swap(mSelectImages, i, i - 1);
                }
            }
            mAdapter.notifyItemMoved(fromPosition, targetPosition);
            return true;
        }

        /**
         * 侧滑删除后会回调的方法
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            mSelectImages.remove(position);
            mAdapter.notifyItemRemoved(position);
        }
    });

    private ItemTouchHelper mItemTouchHelper2 = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

            // 获取触摸响应的方向   包含两个 1.拖动dragFlags 2.侧滑删除swipeFlags
            // 代表只能是向左侧滑删除，当前可以是这样ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT
            int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            int dragFlags;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            } else {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            }
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        /**
         * 拖动的时候不断的回调方法
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //获取到原来的位置
            int fromPosition = viewHolder.getAdapterPosition();
            //获取到拖到的位置
            int targetPosition = target.getAdapterPosition();
            if (fromPosition < targetPosition) {
                for (int i = fromPosition; i < targetPosition; i++) {
                    Collections.swap(mSelectImages2, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > targetPosition; i--) {
                    Collections.swap(mSelectImages2, i, i - 1);
                }
            }
            mAdapter2.notifyItemMoved(fromPosition, targetPosition);
            return true;
        }

        /**
         * 侧滑删除后会回调的方法
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            mSelectImages2.remove(position);
            mAdapter2.notifyItemRemoved(position);
        }
    });


    private void shouw(String name) {
        Snackbar snackbar = Snackbar.make(view, name, Snackbar.LENGTH_SHORT);
        View mView = snackbar.getView();
        mView.setBackgroundColor(Color.GRAY);
        TextView text = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(Color.WHITE);
        text.setTextSize(15);
        snackbar.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_pri:
                viewPager.setCurrentItem(0);
                break;
            case R.id.btn_send:
                judge();
                break;
            default:
                break;
        }
    }

    private void startForMyServer() {
        Map<String, Object> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token", ""));
        map.put("name", businessMo.getName());
        map.put("userName", businessMo.getUserName());
        map.put("phone", businessMo.getPhone());
        map.put("idcard", businessMo.getIdcard());
        map.put("longitude", businessMo.getLon());
        map.put("latitude", businessMo.getLat());
        map.put("productionProvince", businessMo.getProductionProvince());
        map.put("productionCity", businessMo.getProductionCity());
        map.put("productionCounty", businessMo.getProductionCounty());
        map.put("productionAddress", businessMo.getProductionAddress());
        map.put("idcartFacial", businessMo.getIdcartFacial());
        map.put("idcartBehind", businessMo.getIdcartBehind());
        map.put("foodType", businessMo.getFoodType());
        map.put("threeCertificates", businessMo.getThreeCertificates());
        map.put("agreedAgreement", businessMo.getAgreedAgreement());
        map.put("email", businessMo.getEmail());
        map.put("synopsis", businessMo.getSynopsis());
        map.put("storeImgs", businessMo.getStoreImgs());

        OkHttp3Utils.getInstance(DyUrl.BASE).upLoadFile3(DyUrl.addDept, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return ;
                }

                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")) {
                        if (object.optInt("code") == 500) {
                            ToastUtil.showShort(context, object.optString("msg"));
                        } else {
                            viewPager.setCurrentItem(3);
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

}
