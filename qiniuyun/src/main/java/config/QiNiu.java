package config;

import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 七牛初始化
 * 七牛上传实例
 * create by huang shi hao
 */
public class QiNiu {
    private UploadManager uploadManager;

    protected String domainName;//域名(从服务器获得)

    private List<String> mData;//多图上传 传过来的列表

    private List<String>resultList = new ArrayList<>();//多图上传成功后的图片列表


    public QiNiu() {
    }

    public void desQiniu(){
        mData = null;
        domainName = null;
    }
    private static class QiNiuHolder {
        private final static QiNiu INSTANCE = new QiNiu();
    }

    public static QiNiu getInstance() {
        return QiNiuHolder.INSTANCE;
    }

    public void initUploadManager(String domainName){
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
        this.domainName = domainName;

    }
    //初始化
    public UploadManager init(String domainName) {

        return uploadManager;
    }

    public void setmData(List<String> mData){
        this.mData = mData;
    }

    /**
     * http://image.vrzbgw.com/headImagec1e435ec-0eb0-4569-8d47-6996e1b95bb9
     * 上传
     *
     * @param o       文件类型
     * @param qiNiuToken 七牛云上传token凭证
     */
    public void startUpload(Object o, String qiNiuToken, final UploadCallBack callBack) {
       if (o instanceof String) {
            uploadManager.put((String) o, UUID.randomUUID().toString(), qiNiuToken, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    if(info.isOK()){
                        String img = response.optString("key");
                        String imgUrl = domainName+ img;
                        resultList.add(imgUrl);
                        if(resultList.size() == mData.size()){
                            callBack.success(resultList);
                        }
                    }else {
                        String allKeys = "";
                        for (int i=0;i<resultList.size();i++){
                            allKeys+=resultList.get(i);
                        }
                        callBack.fail(allKeys,info);
                    }
                }
            }, null);
        } else if (o instanceof File) {
            uploadManager.put((File) o, UUID.randomUUID().toString(), qiNiuToken, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    if(info.isOK()){
                        String img = response.optString("key");
                        String imgUrl = domainName+ img;
                        resultList.add(imgUrl);
                        if(resultList.size() == mData.size()){
                            callBack.success(resultList);
                        }
                    }else {
                        String allKeys = "";
                        for (int i=0;i<resultList.size();i++){
                            allKeys+=resultList.get(i);
                        }
                        callBack.fail(allKeys,info);
                    }
                }
            }, null);
        } else if (o instanceof byte[]) {
            uploadManager.put((byte[]) o, UUID.randomUUID().toString(), qiNiuToken, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    if(info.isOK()){
                        String img = response.optString("key");
                        String imgUrl = domainName + img;
                        resultList.add(imgUrl);
                        if(resultList.size() == mData.size()){
                            callBack.success(resultList);
                        }
                    }else {
                        String allKeys = "";
                        for (int i=0;i<resultList.size();i++){
                            allKeys+=resultList.get(i);
                        }
                        callBack.fail(allKeys,info);
                    }
                }
            }, null);
        }
    }//send end！


    public interface UploadCallBack {
        void success(List<String> url);
        void fail(String key, ResponseInfo info);
    }

}
