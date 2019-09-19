package com.dabangvr.main;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.base.plush.SPTAG;
import com.dabangvr.model.MenuMo;
import com.dabangvr.model.TypeBean;
import com.dabangvr.my.activity.LoginActivity;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils2;
import com.dabangvr.util.ToastUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import Utils.TObjectCallback;
import bean.UserMess;
import cn.jpush.android.api.JPushInterface;
import config.DyUrl;
import okhttp3.Call;

/**
 * 欢迎页
 *  （1）如果第一次进入app：
 *              1.5秒的启动页 -> 登陆 -> 闪屏 -> 首页
 *  （2）退出登陆再登陆：
 *              1.5秒的启动页 -> 首页
 *  （3）版本更新后再次打开app：
 *              1.5秒的启动页 -> 闪屏 -> 首页
 *
 * 逻辑简述：判断是否是第一次安装app
 *
 *
 */
public class WellcomActivity extends AppCompatActivity{
    private TextView text_version;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        checkUser();
        //获取首页数据
        getMenu();
        getType();
        setContentView(R.layout.activity_wellcom);
        text_version = this.findViewById(R.id.text_version);
        text_version.setText("V" + getVersion());

    }

    private void checkUser() {
        String token = (String) SPUtils2.instance(this).getkey("token","");
        if (StringUtils.isEmpty(token)){
            goTActivity(LoginActivity.class);
        }else {
            getUserInfo(token);
            getType();
            getMenu();
        }
    }

    private void goTActivity(final Class T){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WellcomActivity.this,T);
                startActivity(intent);
                finish();
            }
        },1500);


    }

    protected void getUserInfo(String token) {
        HashMap<String, Object> map = new HashMap<>();
        OkHttp3Utils.getInstance(DyUrl.BASE).doPostJson(DyUrl.USER_INFO, map, token, new TObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                UserMess userMess = JsonUtil.string2Obj(result, UserMess.class);
                if (userMess!=null){
                    JPushInterface.setAlias(WellcomActivity.this, SPTAG.SEQUENCE,String.valueOf(userMess.getId()));
                    SPUtils2.instance(WellcomActivity.this).put("user",result);
                    SPUtils2.instance(WellcomActivity.this).put("token",userMess.getToken());
                    goTActivity(MainActivity.class);
                }else {
                    goTActivity(LoginActivity.class);
                }
            }
            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(WellcomActivity.this,"登录失败,请重新登录");
                SPUtils2.instance(WellcomActivity.this).remove("user");
                SPUtils2.instance(WellcomActivity.this).remove("token");
                goTActivity(LoginActivity.class);
            }
        });
    }

    /**
     * 获取首页渠道数据
     */
    private void getMenu() {
        Map<String, String> map = new HashMap<>();
        map.put("mallSpeciesId", "1");
        map.put("parentId", "1");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getChannelMenuList, map,
                new GsonObjectCallback<String>(DyUrl.BASE) {
                    @Override
                    public void onUi(String msg) {
                        try {
                            JSONObject object = new JSONObject(msg);
                            int code = object.optInt("errno");
                            if (code == 0) {//成功
                                JSONObject data = object.optJSONObject("data");
                                String str = data.optString("channelMenuList");
                                SPUtils2.instance(WellcomActivity.this).put("menuList",str);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //请求失败
                    @Override
                    public void onFailed(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(WellcomActivity.this,"网络连接超时");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(WellcomActivity.this,"网络连接超时");
                            }
                        });
                    }
                });
    }

    /**
     * 获取首页分类列表
     */
    private void getType() {
        Map<String, String> map = new HashMap<>();
        map.put("parentId", "1");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoodsCategoryList, map,
                new GsonObjectCallback<String>(DyUrl.BASE) {
                    //主线程处理
                    @Override
                    public void onUi(String msg) {
                        try {
                            JSONObject object = new JSONObject(msg);
                            int code = object.optInt("errno");
                            if (code == 0) {//成功
                                JSONObject data = object.optJSONObject("data");
                                String str = data.optString("goodsCategoryList");
                                SPUtils2.instance(WellcomActivity.this).put("typeList",str);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //请求失败
                    @Override
                    public void onFailed(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(WellcomActivity.this,"网络连接超时");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(WellcomActivity.this,"网络连接超时");
                            }
                        });
                    }
                });
    }

    //获取版本号
    private String getVersion() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException eArp) {
        }
        return "";
    }

    @Override
    protected void onDestroy() {
        OkHttp3Utils.desInstance();
        super.onDestroy();
    }
}
