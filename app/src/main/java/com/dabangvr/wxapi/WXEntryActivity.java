package com.dabangvr.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dabangvr.main.MainActivity;
import com.dabangvr.main.MyApplication;
import com.dabangvr.my.activity.LoginActivity;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.SPUtils2;
import com.dabangvr.util.ToastUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.DyUrl;
import okhttp3.Call;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI mWeixinAPI;
    private static final String APP_SECRET = "93e6fe9703ba9e1b571a039bab64ef69";
    public static final String WEIXIN_APP_ID = "wx2351c48134140a3c";
    private SPUtils spUtils;
    public static WXEntryActivity instants;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instants = this;
//        mWeixinAPI = WXAPIFactory.createWXAPI(this, WEIXIN_APP_ID, true);
        mWeixinAPI = MyApplication.api;
        mWeixinAPI.handleIntent(this.getIntent(), this);
        spUtils = new SPUtils(this, "db_user");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWeixinAPI.handleIntent(intent, this);//必须调用此句话
    }

    //微信发送的请求将回调到onReq方法
    @Override
    public void onReq(BaseReq req) {
        Log.d("wechat", "onReq");
    }

    //发送到微信请求的响应结果
    @Override
    public void onResp(BaseResp resp) {
        Log.d("wechat", "onResp");
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (resp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
                    finish();
                    return;
                }
                String code = ((SendAuth.Resp) resp).code;
                getAccessToken(code);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                ToastUtil.showShort(WXEntryActivity.this, "取消");
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                ToastUtil.showShort(WXEntryActivity.this, "请求拒绝");
                finish();
                break;
            default:
                //发送返回
                finish();
                break;
        }
    }

    //66f9d1f7e2328f65339cdec1d993bd79
    //获取授权信息，即获取access_token 和 openId
    private void getAccessToken(String code) {
        String loginUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WEIXIN_APP_ID + "&secret=" + APP_SECRET + "&code=" + code + "&grant_type=authorization_code";
        OkHttp3Utils.getInstance("").doGet(loginUrl, new GsonObjectCallback<String>("") {
            @Override
            public void onUi(String result) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String accessToken = jsonObject.optString("access_token");
                    String openId = jsonObject.getString("openid");

                    //获取用户信息
                    getUserInfo(accessToken, openId);

                } catch (JSONException e) {
                    e.printStackTrace();
                    finish();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                    finish();
            }
        });
    }

    /**
     * 获取用户信息
     *
     * @param acc
     * @param openId
     */
    private void getUserInfo(String acc, String openId) {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + acc + "&openid=" + openId + "";
        OkHttp3Utils.getInstance("").doGet(url, new GsonObjectCallback<String>("") {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (StringUtils.isEmpty(result)) {
                        finish();
                        return ;
                    }
                    String openId = object.optString("openid");
                    String nickname = object.optString("nickname");
                    String sex = object.optString("sex");
                    String headimgurl = object.optString("headimgurl");

                    //登陆后端
                    senMyServer(openId, nickname, headimgurl, "wechat");
                } catch (JSONException e) {
                    e.printStackTrace();
                    finish();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                    finish();
            }
        });
    }

    private void senMyServer(final String openID, final String uName, final String icon, final String type) {
        OkHttp3Utils.desInstance();
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
                            finish();
                            return ;
                        }
                        JSONObject data = object.optJSONObject("data");
                        String userStr = data.optString("user");
                        SPUtils2.instance(WXEntryActivity.this).put("user",userStr);
                        String token = data.optString("token");
                        spUtils.put("isLogin","true");
                        spUtils.put("token",token);
                        Intent intent = new Intent(WXEntryActivity.this,MainActivity.class);
                        startActivity(intent);
                        AppManager.getAppManager().finishActivity(LoginActivity.class);
                        finish();//结束当前页面
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    finish();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                Toast.makeText(WXEntryActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
                //loadingDialog.dismiss();
                finish();
            }
        });
    }

}
