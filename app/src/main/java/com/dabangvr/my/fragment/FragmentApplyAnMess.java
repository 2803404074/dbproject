package com.dabangvr.my.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.my.activity.IdentityActivity;
import com.dabangvr.util.DateUtil;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.ToastUtil;
import com.dbvr.imglibrary.model.Image;
import com.dbvr.imglibrary.ui.SelectImageActivity;
import com.dbvr.imglibrary.ui.adapter.SelectedImageAdapter;
import com.dbvr.imglibrary.utils.TDevice;
import com.dbvr.imglibrary.widget.recyclerview.SpaceGridItemDecoration;
import com.dabangvr.common.activity.MyFragment;
import com.example.mylibrary.ViewPagerSlide;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import Utils.PdUtil;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import config.DyUrl;
import okhttp3.Call;

import static android.app.Activity.RESULT_OK;

/**
 * 主播申请
 * 信息填写页面
 */
@SuppressLint("ValidFragment")
public class FragmentApplyAnMess extends MyFragment implements View.OnClickListener {
    private Context context;
    private ViewPagerSlide viewPager;
    private PdUtil pdUtil;
    private SPUtils spUtils;

    private final static int REQUESTCODE = 1;

    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int SELECT_IMAGE_REQUEST = 0x0011;
    private ArrayList<Image> mSelectImages = new ArrayList<>();
    private RecyclerView mSelectedImageRv;
    private TextView mDragTip;
    private SelectedImageAdapter mAdapter;
    private TextView btnSelect;

    private MyCountDownTimer myCountDownTimer;//倒计时
    //姓名
    private EditText etName;

    //手机号
    private EditText etPhone;

    //身份证号
    private EditText etNumber;

    //验证码
    private EditText etCode;

    //点击获取验证码
    private TextView getCode;

    private TextView tvShow;

    //验证码
    private String code;

    //验证成功标志
    private boolean codeFlag = true;

    //身份证是否添加提示控件
    private TextView tvNumberStatus;

    //身份证
    private ImageView img01;
    private ImageView img02;
    private String img01Path;
    private String img02Path;

    //同意协议控件
    private CheckBox cbAgreement;


    public FragmentApplyAnMess(ViewPagerSlide viewPager) {
        this.viewPager = viewPager;
    }


    @Override
    protected int setContentView() {
        return R.layout.fg_applyan_mess;
    }

    @Override
    protected void lazyLoad() {
        context = FragmentApplyAnMess.this.getContext();
        pdUtil = new PdUtil(context);
        initPhone();
        initView();
    }

    private void initPhone() {
        EventHandler eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        };

        SMSSDK.registerEventHandler(eh);
    }


    //初始化控件
    private void initView() {
        spUtils = new SPUtils(context, "db_user");
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etNumber = findViewById(R.id.et_number);
        etCode = findViewById(R.id.et_code);
        getCode = findViewById(R.id.tv_get_code);
        tvShow = findViewById(R.id.tv_show);
        getCode.setOnClickListener(this);

        //生活照
        mDragTip = findViewById(R.id.drag_tip);
        mSelectedImageRv = findViewById(R.id.rv_selected_image);
        mSelectedImageRv.setLayoutManager(new GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false));
        mSelectedImageRv.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 1)));
        btnSelect = findViewById(R.id.btn_select);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        etCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                }else {
                    SMSSDK.submitVerificationCode("86", etPhone.getText().toString(), etCode.getText().toString());
                }
            }
        });

        myCountDownTimer = new MyCountDownTimer(60000, 1000);
        //身份证正反拍照页面的跳转按钮
        findViewById(R.id.img_number).setOnClickListener(this);

        //是否已经添加了身份证，提示
        tvNumberStatus = findViewById(R.id.tv_number_status);

        //身份证
        img01 = findViewById(R.id.img01);
        img02 = findViewById(R.id.img02);

        //同意协议的checkbox
        cbAgreement = findViewById(R.id.cb_agreement);

        //提交
        findViewById(R.id.tv_commit).setOnClickListener(this);

    }

    private void selectImage() {
        int isPermission1 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int isPermission2 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (isPermission1 == PackageManager.PERMISSION_GRANTED && isPermission2 == PackageManager.PERMISSION_GRANTED) {
            startActivity();
        } else {
            //申请权限
            ActivityCompat.requestPermissions(FragmentApplyAnMess.this.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    private void startActivity() {
        Intent intent = new Intent(context, SelectImageActivity.class);
        intent.putParcelableArrayListExtra("selected_images", mSelectImages);
        startActivityForResult(intent, SELECT_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity();
            } else {
                //申请权限
                ActivityCompat.requestPermissions(FragmentApplyAnMess.this.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                Toast.makeText(context, "需要您的存储权限!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            if (requestCode == REQUESTCODE) {
                img01Path = data.getStringExtra("path01");
                img02Path = data.getStringExtra("path02");
                if(!StringUtils.isEmpty(img01Path) && !StringUtils.isEmpty(img02Path)){
                    tvNumberStatus.setTextColor(context.getResources().getColor(R.color.colorDb3));
                    tvNumberStatus.setText("身份证保存成功");
                    img01.setImageBitmap(BitmapFactory.decodeFile(img01Path));
                    img02.setImageBitmap(BitmapFactory.decodeFile(img02Path));
                }
            }
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE_REQUEST && data != null) {
                ArrayList<Image> selectImages = data.getParcelableArrayListExtra(SelectImageActivity.EXTRA_RESULT);
                mSelectImages.clear();
                mSelectImages.addAll(selectImages);
                if (mSelectImages.size() > 1) {
                    mDragTip.setVisibility(View.VISIBLE);
                    if(mSelectImages.size() <9){
                        btnSelect.setText("继续添加");
                    }else {
                        btnSelect.setText("最多只能添加九张图哦");
                    }
                    for (int i=0;i<mSelectImages.size();i++){
                        File f = new File(mSelectImages.get(i).getPath());
                        if (f.exists()){
                            FileInputStream fis = null;
                            try {
                                fis = new FileInputStream(f);
                                if(fis.available()>10485760){
                                    mSelectImages.remove(i);
                                    ToastUtil.showShort(context,"请选择大小为10M以下的图片");
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                fis = null;
                            } catch (IOException e) {
                                e.printStackTrace();
                                fis = null;
                            }
                        }
                    }
                }
                mAdapter = new SelectedImageAdapter(context, mSelectImages, R.layout.selected_image_item);
                mSelectedImageRv.setAdapter(mAdapter);
                mItemTouchHelper.attachToRecyclerView(mSelectedImageRv);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //身份证上传跳转
            case R.id.img_number:
                Intent intent = new Intent(context, IdentityActivity.class);
                startActivityForResult(intent, REQUESTCODE);
                break;
            //提交
            case R.id.tv_commit:
                judge();
                break;
            case R.id.tv_get_code:
                if (DateUtil.isPhoneNumber(etPhone.getText().toString())) {
                    SMSSDK.getVerificationCode("86", etPhone.getText().toString());
                    myCountDownTimer.start();
                } else {
                    ToastUtil.showShort(context, "手机号输入有误,请从新输入");
                }
                break;
            default:
                break;

        }
    }

    /**
     * 判断信息录入
     */
    private void judge() {
        if (!codeFlag) {
            ToastUtil.showShort(context, "手机验证码有误，请重新获取");
            return;
        }
        if (!cbAgreement.isChecked()) {
            ToastUtil.showShort(context, "未同意协议");
        } else if (StringUtils.isEmpty(img01Path) || StringUtils.isEmpty(img02Path)) {
            ToastUtil.showShort(context, "请上传身份证");
        }else if (StringUtils.isEmpty(etName.getText().toString())
                || StringUtils.isEmpty(etPhone.getText().toString())
                || StringUtils.isEmpty(etNumber.getText().toString())) {
        } else {
            pdUtil.showLoding("正在提交");
            List<File>list = new ArrayList<>();
            if(null != mSelectImages){
                for (int i=0;i<mSelectImages.size();i++){
                    if(StringUtils.isEmpty(mSelectImages.get(i).getPath())){
                        continue;
                    }
                    list.add(new File(mSelectImages.get(i).getPath()));
                }
            }
            requestHttp(new File(img01Path),new File(img02Path),list);
        }
    }

    /**
     * 请求后端
     */
    private void requestHttp(File fileFace, File fileBack,List<File>list) {

        Map<String, Object> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token", ""));
        map.put("name", etName.getText().toString());
        map.put("phone", etPhone.getText().toString());
        map.put("idCard", etNumber.getText().toString());
        map.put("idcardFace", fileFace);
        map.put("idcardBack", fileBack);
        map.put("agreedAgreement", "1");
        map.put("userImg",list);
        OkHttp3Utils.getInstance(DyUrl.BASE).upLoadFile3(DyUrl.appAnchor, map,new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int code = object.optInt("code");
                    if (code == 500) {
                        ToastUtil.showShort(context, object.optString("msg"));
                        pdUtil.desLoding();
                        return ;
                    }
                    int errno = object.optInt("errno");
                    if (errno == 0) {
                        if (object.optInt("code")==500){
                            ToastUtil.showShort(context,"服务数据更新中...");
                            return ;
                        }
                        viewPager.setCurrentItem(2);
                    } else if(errno == 1){
                        ToastUtil.showShort(context, object.optString("errmsg"));
                    }else {
                        ToastUtil.showShort(context, object.optString("errmsg"));
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

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                System.out.println("--------result" + event);
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
                    tvShow.setVisibility(View.VISIBLE);
                    tvShow.setText("验证成功");
                    tvShow.setTextColor(context.getResources().getColor(R.color.colorDb3));
                    code = etNumber.getText().toString();
                    codeFlag = true;
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //已经验证
                    Toast.makeText(context, "验证码已经发送", Toast.LENGTH_SHORT).show();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //已经验证
                    //Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
//                    textV.setText(data.toString());
                }

            } else {
                int status = 0;
                try {
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");
                    status = object.optInt("status");
                    if (!TextUtils.isEmpty(des)) {
                        tvShow.setVisibility(View.VISIBLE);
                        tvShow.setText(des);
                        tvShow.setTextColor(getResources().getColor(R.color.colorAccent));
                        codeFlag = false;
                        Toast.makeText(context, des, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                } catch (Exception e) {
                    SMSLog.getInstance().w(e);
                }
            }
            return false;
        }
    });

    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            getCode.setClickable(false);
            getCode.setText(l / 1000 + "秒");

        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            getCode.setText("重新获取");
            //设置可点击
            getCode.setClickable(true);
        }
    }

}
