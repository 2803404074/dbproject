package com.dabangvr.my.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.lbroadcast.widget.MediaController;
import com.dabangvr.main.MainActivity;
import com.dabangvr.main.MyApplication;
import com.dabangvr.main.WellComePageActivity;
import com.dabangvr.util.SPUtils2;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.wxapi.AppManager;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.DyUrl;
import okhttp3.Call;

import static com.dabangvr.base.resources.ThirdParty.QQ_APP_ID;

public class LoginActivity extends BaseNewActivity implements View.OnClickListener {

    @BindView(R.id.video_view)
    PLVideoTextureView mVideoView;

    private Tencent mTencent;
    private BaseUiListener mIUiListener; //第三方登陆处理后的UI回调接口
    private UserInfo mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_login;
    }

    //初始化控件
    @Override
    public void initView() {
        mTencent = Tencent.createInstance(QQ_APP_ID, LoginActivity.this.getApplicationContext());

        //微信登录
        findViewById(R.id.wechat_login).setOnClickListener(this);
        //qq
        findViewById(R.id.qq_login).setOnClickListener(this);
        //手机
        findViewById(R.id.phone_login).setOnClickListener(this);
        //账号
        //findViewById(R.id.account_login).setOnClickListener(this);
    }

    @Override
    public void initData() {
        MediaController mMediaController = new MediaController(LoginActivity.this);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        mVideoView.setMirror(true);
        mVideoView.setVideoPath(DyUrl.APP_LOGIN_VIDEO);
        mVideoView.setLooping(true);//重复播放
        mVideoView.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qq_login:
                setLoaddingView(true);
                qqLogin();//qq登陆
                break;
            case R.id.wechat_login:
                setLoaddingView(true);
                wechatLogin();
                break;
            case R.id.phone_login:
                //手机登陆页面
                Intent intent = new Intent(this, PhoneActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * qq登陆方法
     */
    private void qqLogin() {
        mIUiListener = new BaseUiListener();
        //all表示获取所有权限
        mTencent.login(LoginActivity.this, "all", mIUiListener);
    }

    /**
     * 微信登陆
     */
    private void wechatLogin() {
        if (MyApplication.api == null) {
            MyApplication.api = WXAPIFactory.createWXAPI(this, MyApplication.APP_ID, true);
        }
        if (!MyApplication.api.isWXAppInstalled()) {
            setLoaddingView(false);
            ToastUtil.showShort(this, "您手机尚未安装微信，请安装后再登录");
            return;
        }
        MyApplication.api.registerApp(MyApplication.APP_ID);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_xb_live_state";//官方说明：用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
        MyApplication.api.sendReq(req);
    }

    /**
     * qq登陆结果UI回调处理
     */
    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            Toast.makeText(LoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
            JSONObject obj = (JSONObject) response;
            try {
                final String openID = obj.getString("openid");
                String accessToken = obj.getString("access_token");
                String expires = obj.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);
                QQToken qqToken = mTencent.getQQToken();//这个应该是qq登陆成功后构造一个返回信息方便序列化
                mUserInfo = new UserInfo(getApplicationContext(), qqToken);
                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        JSONObject objectResult = null;
                        try {
                            objectResult = new JSONObject(response.toString());
                            String uName = objectResult.optString("nickname");//获取第三方返回的昵称
                            String icon = objectResult.optString("figureurl_2");//第三方头像
                            String type = "qq";//登陆类型
                            senMyServer(openID, uName, icon, type);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(UiError uiError) {
                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        setLoaddingView(false);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "登录取消", Toast.LENGTH_SHORT).show();
                        setLoaddingView(false);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 在调用Login的Activity或者Fragment中重写onActivityResult方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void senMyServer(final String openID, final String uName, final String icon, final String type) {
        HashMap<String, String> map = new HashMap<>();
        map.put("openId", openID);
        map.put("nickName", uName);
        map.put("icon", icon);
        map.put("loginType", type);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.LOGIN, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    int code = object.optInt("code");
                    if (errno == 0){
                        if (code == 500) {
                            setLoaddingView(false);
                            return ;
                        }
                        JSONObject data = object.optJSONObject("data");
                        if (null != data){
                            String token = data.optString("token");
                            SPUtils2.instance(LoginActivity.this).put("token",token);
                            String userStr = data.optString("user");
                            SPUtils2.instance(getContext()).put("user",userStr);
                            //如果是第一次登陆，则跳到闪屏
                            boolean isNews = (boolean) SPUtils2.instance(LoginActivity.this).getkey("isNews",true);
                            if (isNews){
                                Intent intent = new Intent(LoginActivity.this, WellComePageActivity.class);
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                            }
                            finish();
                        }else {
                            ToastUtil.showShort(LoginActivity.this,"网络繁忙，请稍候再试试？");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
                setLoaddingView(false);
            }
        });
    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

}

