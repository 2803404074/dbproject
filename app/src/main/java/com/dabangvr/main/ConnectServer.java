package com.dabangvr.main;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.mina.ConnectionConfig;
import com.example.mina.ConnectionManager;

import java.net.URISyntaxException;

public class ConnectServer extends Service {
    private ConnectionThread thread;
    private volatile String address;
    private volatile String name;
    private volatile String head;
    private volatile String create;
    private volatile String isFirst;
    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("tag", "start thread to connect");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        address = intent.getStringExtra("address");
        name = intent.getStringExtra("name");
        head = intent.getStringExtra("head");
        create = intent.getStringExtra("create");
        isFirst = intent.getStringExtra("isFirst");
        thread = new ConnectionThread("mina", getApplicationContext(),address,name,head,create,isFirst);
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.disConnect();
        thread = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class ConnectionThread extends HandlerThread {
        boolean isConnected;
        ConnectionManager manager;

        public ConnectionThread(String name, Context context,String address,String userName,String head,String create,String isFirst) {
            super(name);
            ConnectionConfig config = new ConnectionConfig.Builder(context)
                    .setIp("47.107.128.89").setPort(2866)/*47.107.128.89*/
                    .setReadBufferSize(10240).setConnectionTimeout(10000)
                    .builder();
            manager = new ConnectionManager(config,address,userName,head,create,isFirst);
        }
        @Override
        protected void onLooperPrepared() {
            while (true) {
                isConnected = manager.connect();
                 if (isConnected) {
                    Log.e("tag", "connect successfully.");
                    break;
                }
                try {
                    Log.e("tag", "connect fail.");
                    Thread.sleep(3000);
                } catch (Exception e) {
                    Log.e("tag", "fail with error");
                }
            }
        }

        public void disConnect() {
            manager.disConnect();
        }
    }

}