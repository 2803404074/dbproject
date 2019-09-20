package com.dabangvr.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import bean.UserMess;

/**
 * 保存信息配置类
 * @author
 */
public class SPUtils2 {

    protected static SPUtils2 spUtils;

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor editor;


    /**
     *
     * @param context
     */
    private SPUtils2(Context context) {
        String fileName = "db_user";
        sharedPreferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SPUtils2 instance(Context context){
        if (null == spUtils){
            return new SPUtils2(context.getApplicationContext());
        }
        return spUtils;
    }

    /**
     * 存储
     */
    public synchronized void put(String key, Object object) {
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * 获取保存的数据
     */
    public synchronized Object getkey(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        } else {
            return sharedPreferences.getString(key, null);
        }
    }

    /**
     * 移除某个key值已经对应的值
     */
    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * 查询某个key是否存在
     */
    public Boolean contain(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    /**
     * 保存对象
     */
    public <T> void putObj(String tag, T data) {
        if (null == data)
            return;
        Gson gson = new Gson();
        //change data to json
        String strJson = gson.toJson(data);
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();
    }

    /**
     * 获取对象
     */
    public <T> T getObj(String tag, Class<T> cls) {
        T data = null;
        String strJson = sharedPreferences.getString(tag, null);
        if (null == strJson) {
            return null;
        }
        try {
            Gson gson = new Gson();
            JsonElement jsonElement = new JsonParser().parse(strJson);
            data = gson.fromJson(jsonElement, cls);
        } catch (Exception e) {

        }
        return data;
    }

    /**
     * 获取用户信息
     */
    public UserMess getUser(){
        return getObj("userMo",UserMess.class);
    }


    /**
     * 存储对象----序列化 --私密数据
     */
    public String putObjectByInput(String key, Object obj) {
        if (obj == null) {//判断对象是否为空
            return "";
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = null;
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            // 将对象放到OutputStream中
            // 将对象转换成byte数组，并将其进行base64编码
            String objectStr = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            baos.close();
            oos.close();
            put(key, objectStr);
            return objectStr;
        } catch (Throwable t) {

        }
        return "";
    }

    /**
     * 获取对象---序列化---私密数据
     */
    public Object getObjectByInput(String key) {
        String wordBase64 = sharedPreferences.getString(key,"");
        // 将base64格式字符串还原成byte数组
        if (TextUtils.isEmpty(wordBase64)) {
            return null;
        }
        try {
            byte[] objBytes = Base64.decode(wordBase64.getBytes(), Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(objBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            // 将byte数组转换成product对象
            Object obj = ois.readObject();
            bais.close();
            ois.close();
            return obj;
        } catch (Throwable t) {

        }
        return null;
    }

}