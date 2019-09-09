package com.dabangvr.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import com.dabangvr.R;
import com.dabangvr.my.activity.LoginActivity;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.DyUrl;
import okhttp3.Call;

/**
 * 简单提示和修改用户信息提示
 */
public class LoginTipsDialog {

    public static String getToken(Context context){
        SPUtils spUtils = new SPUtils(context,"db_user");
        return (String) spUtils.getkey("token","");
    }

    public static void removeToken(Context context){
        SPUtils spUtils = new SPUtils(context,"db_user");
        spUtils.remove("token");
    }


    //其他提示
    public static void ortehrTips(final Activity context, String mess){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setIcon(R.mipmap.application)
                .setMessage(mess)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {//添加普通按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    //其他提示
    public static void LoginTips(final Activity context, String mess){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setIcon(R.mipmap.application)
                .setMessage(mess)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        context.finish();
                    }
                }).create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    //其他提示
    public static void finishTips(final Activity context, String mess){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setIcon(R.mipmap.application)
                .setMessage(mess)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.finish();
                    }
                }).create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    /**
     *  长按修改用户信息
     * @param editKey 修改的名称
     *
     */
    public static void showEdit(final Context context, final String token, final TextView textView, String title, final String editKey){
        final EditText et = new EditText(context);
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(R.mipmap.application)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        senData(token,textView,editKey,et.getText().toString());
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {//添加普通按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create();
        alertDialog.show();
    }


    public static void senData(String token,final TextView textView, String editKey, final Object editValue) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME,token);
        if (editValue instanceof File){
            map.put("headUrl",editValue);
        }else {
            map.put(editKey,editValue);
        }
        OkHttp3Utils.getInstance(DyUrl.BASE).upLoadFile3(DyUrl.update, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if(StringUtils.isEmpty(result)){
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    if(0 == object.optInt("errno")){
                        if(null!=textView){
                            textView.setText((String)editValue);
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
}
