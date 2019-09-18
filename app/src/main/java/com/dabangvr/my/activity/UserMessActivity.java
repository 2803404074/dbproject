package com.dabangvr.my.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.base.plush.SPTAG;
import com.dabangvr.common.activity.AddressActivity;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.main.MainActivity;
import com.dabangvr.util.CacheUtil;
import com.dabangvr.util.CameraUtil;
import com.dabangvr.util.DiaLogUtil;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoginTipsDialog;
import com.dabangvr.util.SPUtils2;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.wxapi.AppManager;
import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import bean.UserMess;
import cn.jpush.android.api.JPushInterface;
import config.DyUrl;
import okhttp3.Call;

/**
 * 个人中心-设置
 */
public class UserMessActivity extends BaseNewActivity implements View.OnClickListener, DiaLogUtil.DiaLogClickListener {
    private static final int REQ_1 = 1;//打开相机
    private static final int REQ_2 = 2;//打开相册
    private static final int REQ_4 = 4;//相册后启动裁剪程序
    private Context context;
    Dialog dg;
    private SimpleDraweeView draweeView;
    private static final String IMAGE_FILE_LOCATION = "file:///" + Environment.getExternalStorageDirectory().getPath() + "/temp.jpg";
    private Uri imageUris = Uri.parse(IMAGE_FILE_LOCATION);
    private Uri imageUri;
    public static File tempFile;
    private Uri headUri;
    private Bitmap bitmapCamera;//截切后的bitmap

    private TextView set_sex;//性别
    private TextView tvTAG;//个性签名
    private TextView tvNickName;//昵称
    private TextView tvPhone;//手机号
    private TextView tvNow;//常住地
    private TextView tvCache;//缓存

    private AlertDialog alertDialog;

    private LocalBroadcastManager broadcastManager;
    private IntentFilter intentFilter;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_mess;
    }

    @Override
    public void initView() {
        //初始化控件
        draweeView = findViewById(R.id.mess_drawee_img);
        //我的收获地址
        findViewById(R.id.linear_address).setOnClickListener(this);
        findViewById(R.id.linear_nick).setOnClickListener(this);
        findViewById(R.id.linear_sex).setOnClickListener(this);
        findViewById(R.id.linear_czd).setOnClickListener(this);
        findViewById(R.id.linear_band_phone).setOnClickListener(this);
        findViewById(R.id.linear_redis).setOnClickListener(this);
        findViewById(R.id.linear_check_update).setOnClickListener(this);
        findViewById(R.id.linear_dz).setOnClickListener(this);
        findViewById(R.id.tv_logout).setOnClickListener(this);
        findViewById(R.id.backe).setOnClickListener(this);

        //个性签名
        tvTAG = findViewById(R.id.my_set_gxqm);
        tvTAG.setOnClickListener(this);

        //性别
        set_sex = findViewById(R.id.set_sex);
        //昵称
        tvNickName = findViewById(R.id.set_nc);
        //手机号
        tvPhone = findViewById(R.id.set_phone);
        //常住地
        tvNow = findViewById(R.id.set_live_now);
        //缓存
        tvCache = findViewById(R.id.set_cache);

    }

    @Override
    public void initData() {
        //获取缓存
        try {
            tvCache.setText(CacheUtil.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //填充用户信息数据
        String str = (String) SPUtils2.instance(this).getkey("user","");
        UserMess userMess = JsonUtil.string2Obj(str, UserMess.class);
        draweeView.setImageURI(userMess.getHeadUrl());

        //个性签名
        tvTAG.setText(userMess.getAutograph());

        //性别
        set_sex.setText(userMess.getSex());

        //昵称
        tvNickName.setText(userMess.getNickName());

        //手机号
        tvPhone.setText(userMess.getMobile());

        //常住地
        tvNow.setText(userMess.getPermanentResidence());

    }

    /**
     * 底部弹出框
     *
     * @param view
     */
    public void open_cam(View view) {
        DiaLogUtil du = new DiaLogUtil(context, R.layout.layout_dhk, this);
        dg = du.getDialog();
        dg.show();
    }

    /**
     * 相机，相册，剪切，的回调activity处理
     * 其中剪切 后 需要网络请求
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQ_1: {
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startPhotoZoom(imageUri);
                }
                break;
            }
            case REQ_2: {
                if (resultCode == RESULT_OK) {
                    startPhotoZoom(data.getData());
                }
                break;
            }
            case REQ_4: {
                if (resultCode == RESULT_OK) {
                    try {
                        bitmapCamera = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(imageUris));
                        this.headUri = imageUris;

                        Uri uri = bitmap2uri(UserMessActivity.this, bitmapCamera);
                        draweeView.setImageURI(uri);
                        if (bitmapCamera != null) {
                            File file = null;   //图片地址
                            try {
                                file = new File(new URI(headUri.toString()));
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                            LoginTipsDialog.senData(getSPKEY(this, "token"), null, "headUrl", file);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public static Uri bitmap2uri(Context c, Bitmap b) {
        File path = new File(c.getCacheDir() + File.separator + System.currentTimeMillis() + ".jpg");
        try {
            OutputStream os = new FileOutputStream(path);
            b.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
            return Uri.fromFile(path);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_address: {
                Intent intent = new Intent(this, AddressActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_open_right, R.anim.activity_open_left);
                break;
            }
            case R.id.linear_nick: {
                LoginTipsDialog.showEdit(this, getSPKEY(this, "token"), tvNickName, "昵称", "nickName");
                break;
            }
            case R.id.linear_sex: {
                String sex = set_sex.getText().toString();
                if (sex.equals("男")) {
                    LoginTipsDialog.senData(getSPKEY(this, "token"), set_sex, "sex", "女");
                } else {
                    LoginTipsDialog.senData(getSPKEY(this, "token"), set_sex, "sex", "男");
                }
                break;
            }
            case R.id.linear_czd: {
                LoginTipsDialog.showEdit(this, getSPKEY(this, "token"), tvNow, "常住地", "permanentResidence");
                break;
            }
            case R.id.linear_band_phone: {
                Intent intent = new Intent(this, SetPhoneActivity.class);
                intent.putExtra("status", "edit");
                startActivity(intent);
                break;
            }
            case R.id.linear_redis: {
                show(1, "清除缓存会导致应用所加载数据清空，但不包括您的个人信息，是否确定？");
                break;
            }
            case R.id.linear_check_update: {
                break;
            }
            case R.id.linear_dz: {
                break;
            }
            case R.id.my_set_gxqm: {
                LoginTipsDialog.showEdit(context, getSPKEY(this, "token"), null, "个性签名", "autograph");
                break;
            }
            case R.id.backe: {
                finish();
                break;
            }
            case R.id.tv_logout: {
                show(0, "确定要退出吗？");
                break;
            }
        }
    }

    //退出登陆
    private void myLogout() {
        String token = getSPKEY(UserMessActivity.this, "token");
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, token);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.LOGOUT, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                ToastUtil.showShort(context, "退出成功");
                //移除本地缓存
                SPUtils2.instance(getContext()).remove("token");
                SPUtils2.instance(getContext()).remove("isLogin");
                JPushInterface.removeLocalNotification(UserMessActivity.this, SPTAG.SEQUENCE);
                Intent intent = new Intent(UserMessActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                AppManager.getAppManager().finishActivity(MainActivity.class);
            }

            @Override
            public void onFailed(Call call, IOException e) {
                //ToastUtil.showShort(context,"退出成功");
                //移除本地缓存
                SPUtils2.instance(getContext()).remove("token");
                SPUtils2.instance(getContext()).remove("isLogin");
                Intent intent = new Intent(UserMessActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                AppManager.getAppManager().finishActivity(MainActivity.class);
            }
        });

    }


    //打开相册方法
    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQ_2);
    }

    /**
     * 打开相机方法
     */
    private void openCamera() {
        //獲取系統版本
        int currentapiVersion = Build.VERSION.SDK_INT;
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        if (CameraUtil.hasSdcard()) {
            SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    filename + ".jpg");
            if (currentapiVersion < 24) {
                // 从文件中创建uri
                imageUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
        }
        startActivityForResult(intent, REQ_1);
    }


    //剪切、压缩后的方法
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        //该参数可以不设定用来规定裁剪区的宽高比
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 2);
        //该参数设定为你的imageView的大小
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        //是否返回bitmap对象
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUris);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片的格式
        intent.putExtra("noFaceDetection", true); // 头像识别
        startActivityForResult(intent, REQ_4);
    }

    /**
     * dialog点击事件的回调
     *
     * @param view
     */
    @Override
    public void click(View view) {
        switch (view.getId()) {
            case R.id.takePhoto: {
                openCamera();//打开相机
                dg.dismiss();
                break;
            }
            case R.id.choosePhoto: {
                openAlbum();//打开相册方法
                dg.dismiss();
                break;
            }
        }
    }

    /**
     * 权限申请状态回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(context, "申请失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(context, "申请失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //处理通知或状态
    @Override
    protected void onResume() {
        super.onResume();
        broadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_PHONE");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String result = intent.getStringExtra("phone");
                tvPhone.setText(result);
            }
        };
        broadcastManager.registerReceiver(mReceiver, intentFilter);
    }

    private void show(final int index, String mess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (index == 0) {
            builder.setTitle("海风暴");
        }
        builder.setMessage(mess);
        builder.setIcon(R.mipmap.application);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (index == 0) {
                    myLogout();
                } else {
                    CacheUtil.clearAllCache(UserMessActivity.this);
                    try {
                        tvCache.setText("缓存已清除");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {//添加普通按钮
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }
}
