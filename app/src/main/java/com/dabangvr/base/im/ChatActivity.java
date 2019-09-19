package com.dabangvr.base.im;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.util.ToastUtil;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.List;

import butterknife.BindView;

public class ChatActivity extends BaseNewActivity {
    @BindView(R.id.tv_toUsername)
    TextView tv_toUsername;

    @BindView(R.id.recy_chat)
    RecyclerView recyclerView;

    @BindView(R.id.btn_send)
    Button btn_send;

    @BindView(R.id.et_content)
    EditText et_content;

    private int chatType = 1;
    private String toChatUsername;

    private List<EMMessage> msgList;
    private EMConversation conversation;
    protected int pagesize = 20;


    private ChatAdapter chatAdapter;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat);
    }


    @Override
    public void initView() {
        toChatUsername = this.getIntent().getStringExtra("username");
        tv_toUsername.setText(toChatUsername);

        getAllMessage();
        msgList = conversation.getAllMessages();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(getContext(),msgList) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, EMMessage message,int position) {
                if (chatAdapter.getItemViewType(position) == 0){
                    EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                    holder.setText(R.id.tv_chatcontent,txtBody.getMessage());
                }else {
                    EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                    holder.setText(R.id.tv_chatcontent,txtBody.getMessage());
                }
            }
        };
        recyclerView.setAdapter(chatAdapter);

        btn_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = et_content.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                setMesaage(content);
            }

        });
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    @Override
    public void initData() {

    }

    @Override
    public int setLayout() {
        return R.layout.activity_chat;
    }

    protected void getAllMessage() {
        // 获取当前conversation对象

        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername,
                EaseCommonUtils.getConversationType(chatType), true);
        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
        }
    }

    private void setMesaage(String content) {
        // 创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        // 如果是群聊，设置chattype，默认是单聊
        if (chatType == Constant.CHATTYPE_GROUP)
            message.setChatType(ChatType.GroupChat);
        // 发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        msgList.add(message);
        chatAdapter.upData(msgList);
        et_content.setText("");
        et_content.clearFocus();
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {

            for (EMMessage message : messages) {
                String username = null;
                // 群组消息
                if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }
                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals(toChatUsername)) {
                    msgList.addAll(messages);
                    chatAdapter.upData(msgList);
                    if (msgList.size() > 0) {
                        et_content.setSelection(chatAdapter.getItemCount() - 1);
                    }
                }
            }
            // 收到消息
            ToastUtil.showShort(getContext(),"收到消息");
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            // 收到透传消息
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            // 收到已读回执
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            // 收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            // 消息状态变动
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }
}
