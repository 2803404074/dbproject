package com.dabangvr.common.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.dabangvr.R;

@SuppressLint("ValidFragment")
public class FgImg extends Fragment {
    private boolean IS_LOADED = false;
    private static int mSerial = 0;
    private int mTabPos = 0;
    private ImageView imageView;
    private String imgUrl;
    private Context context;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Log.e("tag", "IS_LOADED=" + IS_LOADED);
            if (!IS_LOADED) {
                IS_LOADED = true;
                //这里执行加载数据的操作
                setDate();
                Log.e("tag", "我是page" + (mTabPos + 1));
            }
            return;
        }

        ;
    };

    private void setDate() {
        Glide.with(context).load(imgUrl).into(imageView);
    }

    public FgImg(int serial,String imgUrl) {
        mSerial = serial;
        this.imgUrl = imgUrl;
    }
    public void des(){
        this.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("tag", "onCreate()方法执行");

    }

    public void sendMessage() {
        Message message = handler.obtainMessage();
        message.sendToTarget();
    }

    public void setTabPos(int mTabPos) {
        this.mTabPos = mTabPos;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("tag", "onCreateView()方法执行");
        View view = inflater.inflate(R.layout.img_fragment, container, false);
        context = FgImg.this.getContext();
        imageView= view.findViewById(R.id.hxxq_img);
        //设置页和当前页一致时加载，防止预加载
//        if (isFirst && mTabPos == mSerial) {
//            isFirst = false;
//            sendMessage();
//        }
        Glide.with(context).load(imgUrl).into(imageView);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("tag", "onDestroyView()方法执行");
    }
}
