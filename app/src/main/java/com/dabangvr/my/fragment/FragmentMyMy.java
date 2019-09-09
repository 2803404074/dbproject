package com.dabangvr.my.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.MyFragment;
import com.dabangvr.my.activity.LoginActivity;
import com.dabangvr.my.activity.JfActivity;
import com.dabangvr.my.activity.MyMessageActivity;
import com.dabangvr.my.activity.MyScActivity;
import com.dabangvr.my.activity.SbActivity;
import com.dabangvr.my.activity.StartOpenShopActivity;
import com.dabangvr.my.activity.StartOpenZhuBoActivity;
import com.dabangvr.my.activity.UserMessActivity;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.ToastUtil;

import org.apache.commons.lang.StringUtils;

import bean.UserMess;

@SuppressLint("ValidFragment")
public class FragmentMyMy extends MyFragment implements View.OnClickListener {

    private Context context;
    private int userType;

    private UserMess userMess;



    public FragmentMyMy(int userType) {
        this.userType = userType;
    }

    public static FragmentMyMy newInstance(int userType) {
        FragmentMyMy fragment = new FragmentMyMy(userType);
        return fragment;
    }

    @Override
    protected int setContentView() {
        context = FragmentMyMy.this.getContext();
        return R.layout.my_my;
    }

    @Override
    protected void lazyLoad() {
        findViewById(R.id.rl_msg_id).setOnClickListener(this);
        findViewById(R.id.rl_cols_id).setOnClickListener(this);
        findViewById(R.id.rl_certifi_id).setOnClickListener(this);
        TextView type = findViewById(R.id.my_zb);
        if (userType == 1){
            type.setText("我的钱包");
        }
        findViewById(R.id.rl_anchor_id).setOnClickListener(this);

        //鲜币
        findViewById(R.id.rl_conis_id).setOnClickListener(this);

        findViewById(R.id.ll_setting_id).setOnClickListener(this);


    }

    private void show(String mess) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("海风暴")
                .setMessage(mess)
                .setIcon(R.mipmap.application)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {//添加普通按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                }).create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        String token = getSPKEY(context, "token");
        if (StringUtils.isEmpty(token)) {
            show("您未登陆，去登陆?");
        } else {
            switch (v.getId()) {
                case R.id.rl_msg_id:          //消息
                    Intent intent = new Intent(context, MyMessageActivity.class);
                    startActivity(intent);
                    break;

                case R.id.rl_cols_id:             //收藏
                    Intent cintent = new Intent(context, MyScActivity.class);
                    startActivity(cintent);
                    break;
                case R.id.rl_certifi_id:          //商家认证
                    Intent asintent = new Intent(context, StartOpenShopActivity.class);
                    startActivity(asintent);
                    break;

                case R.id.rl_anchor_id:          //主播认证
                    if (userType == 1) {
                        Intent lasintent = new Intent(context, JfActivity.class);
                        lasintent.putExtra("integral", getUser().getIntegral());
                        startActivity(lasintent);
                    } else {
                        Intent lvintent = new Intent(context, StartOpenZhuBoActivity.class);
                        startActivity(lvintent);
                    }
                    break;

                case R.id.rl_conis_id:          //鲜币
                    Intent consintent = new Intent(context, SbActivity.class);
                    consintent.putExtra("diamond", getUser().getDiamond());
                    startActivity(consintent);
                    break;
                case R.id.ll_setting_id:        //设置
                    String str = getSPKEY(context, "user");
                    if (!StringUtils.isEmpty(str)) {
                        userMess = JsonUtil.string2Obj(str, UserMess.class);
                    }
                    if (isNoLin()) {
                        ToastUtil.showShort(context, "网络不可用");
                        return;
                    }
                    Intent uintent = new Intent(context, UserMessActivity.class);
                    uintent.putExtra("user", JsonUtil.obj2String(userMess));
                    startActivity(uintent);
                    break;
                default:
                    break;
            }
        }
    }


    private UserMess getUser(){
        return JsonUtil.string2Obj(getSPKEY(context,"user"), UserMess.class);
    }
}
