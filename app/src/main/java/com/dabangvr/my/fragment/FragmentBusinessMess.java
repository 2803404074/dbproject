package com.dabangvr.my.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.ToastUtil;
import com.example.camera.CameraActivity;
import com.example.model.BusinessMo;
import com.example.mylibrary.ViewPagerSlide;
import com.example.utils.IDCardValidate;

import org.apache.commons.lang.StringUtils;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 *
 */

@SuppressLint("ValidFragment")
public class FragmentBusinessMess extends Fragment implements View.OnClickListener {
    private Context context;
    private SPUtils spUtils;
    private TextView errmsg;//身份证错误信息提示
    private ViewPagerSlide viewPager;

    public static   String path01;//身份证正面
    public static String path02;//身份证反面
    private static ImageView imageView1;
    private static ImageView imageView2;
    private EditText name, number, phone, email;

    public static TextView textView;//身份证上传与否提示


    public FragmentBusinessMess(ViewPagerSlide viewPager) {
        this.viewPager = viewPager;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        context = FragmentBusinessMess.this.getContext();
        View view = inflater.inflate(R.layout.fg_business02, null);
        spUtils = new SPUtils(context, "db_user");
        initView(view);
        return view;
    }

    //初始化控件
    private void initView(View view) {
        textView = view.findViewById(R.id.iden_status);//身份证上传状态显示
        imageView1 = view.findViewById(R.id.img1);
        imageView2 = view.findViewById(R.id.img2);
        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);

        errmsg = view.findViewById(R.id.errmsg);
        name = view.findViewById(R.id.name);
        number = view.findViewById(R.id.number);
        phone = view.findViewById(R.id.phones);
        email = view.findViewById(R.id.emails);
        view.findViewById(R.id.bt_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                judge();
            }
        });
    }

    private void judge() {
        String namex = name.getText().toString();
        String phonex = phone.getText().toString();
        String numberx = number.getText().toString();
        String emailx = email.getText().toString();

        if (StringUtils.isEmpty(namex)
                || StringUtils.isEmpty(phonex)
                || StringUtils.isEmpty(numberx)
                || StringUtils.isEmpty(emailx)) {
            ToastUtil.showShort(context, "请完善必填信息");
        } else if (StringUtils.isEmpty(path01)
                || StringUtils.isEmpty(path02)) {
            ToastUtil.showShort(context, "未上传身份证");
        } else {
            boolean isTG = IDCardValidate.validate_effective(numberx);
            if (isTG) {
                errmsg.setVisibility(View.GONE);
                BusinessMo businessMo = new BusinessMo();
                businessMo.setUserName(namex);
                businessMo.setPhone(phonex);
                businessMo.setIdcard(numberx);
                businessMo.setEmail(emailx);
                businessMo.setIdcartFacial(new File(path01));
                businessMo.setIdcartBehind(new File(path02));
                String strx = JsonUtil.obj2String(businessMo);
                spUtils.put("SJRZ", strx);
                viewPager.setCurrentItem(1);
            } else {
                errmsg.setText("无效的身份证信息，请从新输入");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img1:
                identityPositive();
                break;
            case R.id.img2:
                identityBack();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }


    }

    public static void setImg(int index, String path){
        switch (index){
            case 0:
                path01 = path;
                imageView1.setImageBitmap(BitmapFactory.decodeFile(path));
                break;
            case 1:
                path02 = path;
                imageView2.setImageBitmap(BitmapFactory.decodeFile(path));
                break;
        }
    }
    /**
     * 拍摄证件照片
     *
     * @param type 拍摄证件类型
     */
    private void takePhoto(int type) {
        if (ActivityCompat.checkSelfPermission(FragmentBusinessMess.this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FragmentBusinessMess.this.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if (ContextCompat.checkSelfPermission(FragmentBusinessMess.this.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FragmentBusinessMess.this.getActivity(), new String[]{Manifest.permission.CAMERA}, 0x12);
            return;
        }
        CameraActivity.navToCamera(FragmentBusinessMess.this.getContext(), type,0);
    }

    /**
     * 身份证正面
     */
    private void identityPositive() {
        takePhoto(CameraActivity.TYPE_ID_CARD_FRONT);
    }

    /**
     * 身份证反面
     */
    private void identityBack() {
        takePhoto(CameraActivity.TYPE_ID_CARD_BACK);
    }


}
