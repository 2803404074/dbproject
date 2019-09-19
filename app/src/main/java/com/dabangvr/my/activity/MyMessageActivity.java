package com.dabangvr.my.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.base.im.ChatActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.util.StatusBarUtil;
import com.example.mylibrary.MessDetailsActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;


/**
 * 个人中心-消息
 */
public class MyMessageActivity extends BaseNewActivity {

    @BindView(R.id.my_mess_recy)
    RecyclerView recyclerView;

    private List<EMConversation> conversationList = new ArrayList<EMConversation>();
    private BaseLoadMoreHeaderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_my_message;
    }

    @Override
    public void initView() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        conversationList.addAll(loadConversationList());
        adapter = new BaseLoadMoreHeaderAdapter<EMConversation>(this, recyclerView, conversationList, R.layout.my_mess_recyitem) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, EMConversation conversation) {
                String username = conversation.getUserName();
                holder.setText(R.id.name, "与 " + username + " 的会话");
                if (conversation.getUnreadMsgCount() > 0) {
                    // 显示与此用户的消息未读数
                    TextView tvNumber = holder.getView(R.id.unread_msg_number);
                    tvNumber.setText(String.valueOf(conversation.getUnreadMsgCount()));
                    tvNumber.setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.unread_msg_number).setVisibility(View.INVISIBLE);
                }
                if (conversation.getAllMsgCount() != 0) {
                    // 把最后一条消息的内容作为item的message内容
                    EMMessage lastMessage = conversation.getLastMessage();
                    holder.setText(R.id.message, lastMessage.getBody().toString());
                    holder.setText(R.id.time, DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));

                    if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                        holder.getView(R.id.msg_state).setVisibility(View.VISIBLE);
                    } else {
                        holder.getView(R.id.msg_state).setVisibility(View.GONE);
                    }
                }
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                EMConversation conversation = (EMConversation) adapter.getData().get(position);
                String username = conversation.getUserName();
                // 进入聊天页面
                Intent intent = new Intent(MyMessageActivity.this, ChatActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    /**
     * 获取会话列表
     *
     * @return
     */
    protected List<EMConversation> loadConversationList() {
        // 获取所有会话，包括陌生人
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(
                            new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     *
     * @param
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    @Override
    public void initData() {

    }
}
