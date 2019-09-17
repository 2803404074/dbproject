package Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bean.Goods;
import config.DyUrl;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import static java.lang.String.valueOf;

/**
 * 1. 类的用途 封装OkHttp3的工具类 用单例设计模式
 */

public class OkHttp3Utils {
    /**
     * 懒汉 安全 加同步
     * 私有的静态成员变量 只声明不创建
     * 私有的构造方法
     * 提供返回实例的静态方法
     */

    private static OkHttp3Utils okHttp3Utils = null;
    private String BASE_PATH;

    private OkHttp3Utils(String BASE_PATH) {
        this.BASE_PATH = BASE_PATH;
    }

    public static OkHttp3Utils getInstance(String BASE_PATH) {
        if (okHttp3Utils == null) {
            //加同步安全
            synchronized (OkHttp3Utils.class) {
                if (okHttp3Utils == null) {
                    okHttp3Utils = new OkHttp3Utils(BASE_PATH);
                }
            }

        }

        return okHttp3Utils;
    }

    public static OkHttp3Utils desInstance() {
        if (okHttp3Utils != null) {
            //加同步安全
            synchronized (OkHttp3Utils.class) {
                if (okHttp3Utils != null) {
                    okHttp3Utils = null;
                }
            }
        }
        return okHttp3Utils;
    }



    public String getBASE_PATH() {
        return BASE_PATH;
    }

    public void setBASE_PATH(String path){
        this.BASE_PATH = path;
    }

    private static OkHttpClient okHttpClient = null;

    public synchronized static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
                    //添加OkHttp3的拦截器
                    .writeTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS)
                    .build();
        }
        return okHttpClient;
    }

    private static Handler mHandler = null;

    public synchronized static Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler();
        }

        return mHandler;
    }

    /**
     * get请求
     * 参数1 url
     * 参数2 回调Callback
     */

    public void doGet(String url, Callback callback) {

        //创建OkHttpClient请求对象
        OkHttpClient okHttpClient = getOkHttpClient();
        //补全请求地址
        String requestUrl = BASE_PATH + url;
        //创建Request
        Request request = new Request.Builder().url(url).build();
        //得到Call对象
        Call call = okHttpClient.newCall(request);
        //执行异步请求
        call.enqueue(callback);
    }

    /**
     * post请求
     * 参数1 url
     * 参数2 回调Callback
     */

    public void doPost(String url, Map<String, String> params, Callback callback) {

        //创建OkHttpClient请求对象
        OkHttpClient okHttpClient = getOkHttpClient();
        //3.x版本post请求换成FormBody 封装键值对参数

        FormBody.Builder builder = new FormBody.Builder();

        //遍历集合
        if (params != null) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }

        //补全请求地址
        String requestUrl = BASE_PATH + url;

        //创建Request
        Request request = new Request.Builder().url(requestUrl).post(builder.build()).build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    /**
     * post请求上传文件
     * 参数1 url
     * 参数2 回调Callback
     */
    public static void uploadPic(String url, File file, String fileName) {
        //创建OkHttpClient请求对象
        OkHttpClient okHttpClient = getOkHttpClient();
        //创建RequestBody 封装file参数
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        //创建RequestBody 设置类型等
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", fileName, fileBody).build();

        //创建Request
        Request request = new Request.Builder().url(url).post(requestBody).build();

        //得到Call
        Call call = okHttpClient.newCall(request);
        //执行请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //上传成功回调 目前不需要处理
            }
        });

    }


    /**
     * 携带参数文件上传
     *
     * @param url
     * @param map
     * @param file
     */
    public void upLoadFile(String url, Map<String, Object> map, File file, Callback callback) {
        OkHttpClient client = getOkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (file != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("file", file.getName(), body);
        }
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
            }
        }
        String requestUrl = BASE_PATH + url;

        Request request = new Request.Builder().url(requestUrl).post(requestBody.build()).build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void upLoadFile3(String url, Map<String, Object> map,Callback callback) {
        OkHttpClient client = getOkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (map != null) {
            for (Map.Entry entry : map.entrySet()) {
                if(entry.getValue() instanceof File){
                    File file = (File) entry.getValue();
                    RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
                    requestBody.addFormDataPart(valueOf(entry.getKey()), file.getName(), body);
                }else if(entry.getValue() instanceof java.util.List){
                    List<File>list = (List<File>) entry.getValue();
                    for (int i=0;i<list.size();i++){
                        RequestBody body = RequestBody.create(MediaType.parse("image/*"), list.get(i));
                        requestBody.addFormDataPart(valueOf(entry.getKey()), list.get(i).getName(), body);
                    }
                }else {
                    requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
                }
            }

        }
        String requestUrl = BASE_PATH + url;
        Request request = new Request.Builder().url(requestUrl).post(requestBody.build()).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }


    /**
     * Post请求发送JSON数据
     * 参数一：请求Url
     * 参数二：请求的JSON
     * 参数三：请求回调
     */
    public  void doPostJson(String url, Map<String,Object> map,String token, Callback callback) {
        Gson gson = new Gson();
        String jsonParams = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams);
        Request request = new Request.Builder().url(BASE_PATH + url).addHeader(DyUrl.TOKEN_NAME,token).post(requestBody).build();
        Call call = getOkHttpClient().newCall(request);
        call.enqueue(callback);
    }

    /**
     * 下载文件 以流的形式把apk写入的指定文件 得到file后进行安装
     * 参数一：请求Url
     * 参数二：保存文件的路径名
     * 参数三：保存文件的文件名
     */
    public static void download(final Context context, final String url, final String saveDir) {
        Request request = new Request.Builder().url(url).build();
        Call call = getOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("xxx", e.toString());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    //apk保存路径
                    final String fileDir = isExistDir(saveDir);
                    //文件
                    File file = new File(fileDir, getNameFromUrl(url));
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //apk下载完成后 调用系统的安装方法
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    context.startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) is.close();
                    if (fos != null) fos.close();


                }
            }
        });

    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    public static String isExistDir(String saveDir) throws IOException {
        // 下载位置
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
            if (!downloadFile.mkdirs()) {
                downloadFile.createNewFile();
            }
            String savePath = downloadFile.getAbsolutePath();
            Log.e("savePath", savePath);
            return savePath;
        }
        return null;
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    private static String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

}

