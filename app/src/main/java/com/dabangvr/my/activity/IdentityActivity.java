package com.dabangvr.my.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.dabangvr.R;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.ToastUtil;
import com.example.camera.CameraActivity;

import org.apache.commons.lang.StringUtils;

public class IdentityActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageView1;
    private ImageView imageView2;
    private String path01;
    private String path02;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity);
        imageView1 = (ImageView) findViewById(R.id.img1);
        imageView2 = (ImageView) findViewById(R.id.img2);
        findViewById(R.id.tv_commit).setOnClickListener(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CameraActivity.REQUEST_CODE) {
            //获取文件路径，显示图片
            if (data != null) {
                String path = data.getStringExtra("result");
                if (!TextUtils.isEmpty(path)) {
                    path01 = path;
                    imageView1.setImageBitmap(BitmapFactory.decodeFile(path));
                }
            }
        }else {//REQUEST_BACK
            if (data != null) {
                String path = data.getStringExtra("result");
                if (!TextUtils.isEmpty(path)) {
                    path02 = path;
                    imageView2.setImageBitmap(BitmapFactory.decodeFile(path));
                }
            }
        }
    }

    /**
     * 拍摄证件照片
     *
     * @param type 拍摄证件类型
     */
    private void takePhoto(int type) {
        if (ActivityCompat.checkSelfPermission(IdentityActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(IdentityActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0x12);
            return;
        }
        CameraActivity.navToCamera(this, type,0);
    }

    /**
     * 身份证正面
     */
    public void identityPositive(View view) {
        takePhoto(CameraActivity.TYPE_ID_CARD_FRONT);
    }

    /**
     * 身份证反面
     */
    public void identityBack(View view) {
        takePhoto(CameraActivity.TYPE_ID_CARD_BACK);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_commit:{
                Intent intent = new Intent();
                intent.putExtra("path01", path01);
                intent.putExtra("path02", path02);
                setResult(100,intent);
                finish();
                break;
            }
            default:break;
        }
    }
}
