package config;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import Utils.PdUtil;
import okhttp3.Call;

/**
 * 七牛初始化
 * 七牛上传实例
 * create by huangshihao
 */
public class QiNiuUpload {
    private PdUtil pdUtil;
    private Context context;
    private UploadManager uploadManager;
    private volatile boolean isSuccess = false;//上传成功与否标志
    protected String domainName;//域名(从服务器获得)

    private int size;//需要上传多少张图片

    private List<String>resultList = new ArrayList<>();//多图上传成功后的图片列表

    public QiNiuUpload(Context context,int size) {
        this.context = context;
        this.size = size;
    }

    //初始化
    public UploadManager init(String domainName,PdUtil pdUtil) {
        Configuration config = new Configuration.Builder()
                /*.chunkSize(512 * 1024) */       // 分片上传时，每片的大小。 默认256K
                .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                .connectTimeout(10)           // 链接超时。默认10秒
                .useHttps(false)               // 是否使用https上传域名
                .responseTimeout(60)          // 服务器响应超时。默认60秒
                /*.recorder(recorder)*/           // recorder分片上传时，已上传片记录器。默认null
                /*.recorder(recorder, keyGen)*/   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(FixedZone.zone0)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();
// 重用uploadManager。一般地，只需要创建一个uploadManager对象
        uploadManager = new UploadManager(config);

        //域名
        this.domainName = domainName;
        this.pdUtil = pdUtil;
        return uploadManager;
    }

    /**
     * 上传
     *
     * @param o       图片,支持uri、url、file。当前使用file
     * @param Q_TOKEN 七牛云上传token凭证
     * @param token   服务会话
     */
    public boolean send(final int type, Object o, String Q_TOKEN, final String token) {
        if (o == null) {
            return false;
        } else if (o instanceof String) {
            uploadManager.put((String) o, UUID.randomUUID().toString(), Q_TOKEN, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    if(info.isOK()){
                        String img = response.optString("key");
                        String imgUrl = "http://test.fuxingsc.com/"+ img;
                        if(type == 0){//多图上传
                            resultList.add(imgUrl);
                            if(resultList.size() == size){
                                Intent intent = new Intent("android.intent.action.AUTH_OK");
                                String listStr = JsonUtil.obj2String(resultList);
                                intent.putExtra("resultList",listStr);
                                intent.putExtra("type",type);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            }
                        }else {//单图上传
                            Intent intent = new Intent("android.intent.action.AUTH_OK");
                            intent.putExtra("resultList",imgUrl);
                            intent.putExtra("type",type);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }

                    }
                }
            }, null);
        } else if (o instanceof File) {
            uploadManager.put((File) o, UUID.randomUUID().toString(), Q_TOKEN, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                }
            }, null);
        } else if (o instanceof byte[]) {
            uploadManager.put((byte[]) o, UUID.randomUUID().toString(), Q_TOKEN, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                }
            }, null);
        }
        return isSuccess;
    }//send end！

}
