package Utils;

import android.os.Handler;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 类的用途 如果要将得到的json直接转化为集合 建议使用该类
 */

public abstract class TObjectCallback<T> implements Callback {
    private String BASE_PATH;

    public TObjectCallback(String BASE_PATH) {
        this.BASE_PATH = BASE_PATH;
    }

    private Handler handler = OkHttp3Utils.getInstance(BASE_PATH).getHandler();

    //主线程处理
    public abstract void onUi(T result) throws JSONException;

    //主线程处理
    public abstract void onFailed(String msg);

    //请求失败
    @Override
    public void onFailure(final Call call, final IOException e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onFailed(e.getMessage());
            }
        });
    }

    //请求json 并直接返回泛型的对象 主线程处理
    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        final String json = response.body().string();
        try {
            final JSONObject object = new JSONObject(json);
            if (object.optInt("errno")== 0){
                if (object.optInt("code") == 500){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onFailed(object.optString("errmsg"));
                        }
                    });
                    return;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onUi((T) object.optString("data"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String str = object.optString("errmsg");
                        onFailed(str.equals("")||str==null?response.toString():str);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}