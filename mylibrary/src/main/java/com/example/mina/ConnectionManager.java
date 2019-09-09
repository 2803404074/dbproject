package com.example.mina;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class ConnectionManager {
    public static final String BROADCAST_ACTION="com.xxx.mina.common";
    public static final String MESSAGE = "message";
    private ConnectionConfig config;
    private WeakReference<Context> context;
    private NioSocketConnector connection;
    private IoSession session;
    private InetSocketAddress address;

    private String userAddress;
    private String userName;
    private String head;
    private String create;
    private String isFirst;

    public ConnectionManager(ConnectionConfig config,String userAddres ,String userName,String head,String create,String isFirst){
        this.config = config;
        this.userAddress = userAddres;
        this.userName = userName;
        this.head = head;
        this.create = create;
        this.isFirst = isFirst;
        this.context = new WeakReference<Context>(config.getContext());
        init();
    }

    private void init(){
        address = new InetSocketAddress(config.getIp(),config.getPort());
        connection = new NioSocketConnector();
        connection.setDefaultRemoteAddress(address);
        connection.getSessionConfig().setReadBufferSize(config.getReadBufferSize());
        connection.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"),
                        LineDelimiter.WINDOWS.getValue(), LineDelimiter.WINDOWS.getValue())));
//        connection.getFilterChain().addLast("codec", new ProtocolCodecFilter(
//                new ObjectSerializationCodecFactory()));
        connection.setHandler(new DefaultHandler(context.get()));
    }

    public boolean connect(){
        try {
            ConnectFuture future = connection.connect();
            future.awaitUninterruptibly();
            session = future.getSession();
            if(session!=null){
                //设置连接名
                JSONObject o = new JSONObject();
                o.put(isFirst,isFirst);
                o.put("create",create);
                o.put("name",userName);
                o.put("address",userAddress);
                o.put("head",head);
//                o.put("create_auth","create_auth");
//                o.put("connect","connect");
                session.write(o);//直播流
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("tag",e.getMessage());
            return false;
        }
        return session == null ? false:true;
    }

    public void disConnect(){
        connection.dispose();
        connection = null;
        session = null;
        address = null;
        context = null;
    }
    private static class DefaultHandler extends IoHandlerAdapter{
        private Context context;
        public DefaultHandler(Context context) {
            this.context = context;
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            //put session to sessionmanager for send message
            SessionManager.getInstance().setSession(session);
        }

        @Override
        public void messageReceived(IoSession session, Object message)
                throws Exception {
            if(context != null){
                Log.e("tag", "接收消息："+message.toString());
                Intent intent = new Intent(BROADCAST_ACTION);
                intent.putExtra(MESSAGE, message.toString());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
            super.messageReceived(session, message);
        }

        @Override
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception
        {
            Log.e("tag", "client端发送信息：" + message.toString());
        }
    }
}
