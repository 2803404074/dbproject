package com.dabangvr.dynamic.page;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.model.DynamicMo;
import com.dabangvr.my.activity.AuthMessActivity;
import com.dabangvr.util.DateUtil;
import com.dabangvr.util.GlideLoadUtils;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.TextUtil;
import com.example.model.CommentListMo;
import com.facebook.drawee.view.SimpleDraweeView;
import com.rey.material.app.BottomSheetDialog;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.DyUrl;
import config.GiftUrl;
import okhttp3.Call;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * 动态
 */
@SuppressLint("ValidFragment")
public class DynamicPage extends Fragment {
    private String token;
    private BaseLoadMoreHeaderAdapter adapter;
    private RecyclerView recyclerView;
    private List<DynamicMo> mData = new ArrayList<>();
    private int page = 1;
    private SPUtils spUtils;
    private boolean IS_LOADED = false;
    private static int mSerial = 0;
    private int mTabPos = 0;//第几个商品类型
    private String typeId = "";//类型id
    private boolean isFirst = true;
    private Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = DynamicPage.this.getContext();
        spUtils = new SPUtils(context,"db_user");
        setToken("");
        View view = inflater.inflate(R.layout.dynamic_page, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        //设置页和当前页一致时加载，防止预加载
        if (isFirst && mTabPos == mSerial) {
            isFirst = false;
            sendMessage();
        }
        recyclerView = view.findViewById(R.id.recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new BaseLoadMoreHeaderAdapter<DynamicMo>(context,recyclerView,mData,R.layout.dynamic_item) {
            @Override
            public void convert(final Context mContext, BaseRecyclerHolder holder, final DynamicMo o) {
                SimpleDraweeView draweeView = holder.getView(R.id.auth_head);
                draweeView.setImageURI(TextUtil.isNull2Url(o.getHeadUrl()));
                draweeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, AuthMessActivity.class);
                        intent.putExtra("authId",o.getAnchorId());
                        intent.putExtra("userId",o.getUserId());
                        startActivity(intent);
                    }
                });

                holder.setText(R.id.auth_name,TextUtil.isNull2Url(o.getNickName()));

                holder.setText(R.id.auth_data, DateUtil.stampToDate(TextUtil.isNull(o.getSendTime())));

                holder.setText(R.id.tv_content,o.getContent());

                final TextView tvDz = holder.getView(R.id.dz_num);
                tvDz.setText(TextUtil.isNull(o.getPraisedNumber()));


                final CheckBox checkBox  = holder.getView(R.id.iv_dz);
                if (o.getGreatTag().equals("-1")){//未点赞
                    checkBox.setChecked(false);
                }else {
                    checkBox.setChecked(true);
                }
//                holder.getView(R.id.ll_dz).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        sendDz(o.getId());
//                        int dzNum = Integer.parseInt(tvDz.getText().toString());
//                        if (checkBox.isChecked()){
//                            tvDz.setText(String.valueOf(dzNum-1));
//                            checkBox.setChecked(false);
//                        }else {
//                            tvDz.setText(String.valueOf(dzNum+1));
//                            checkBox.setChecked(true);
//                        }
//                    }
//                });

                holder.setText(R.id.comment_num,TextUtil.isNull(o.getCommentNumber()));


                //关注
                final CheckBox tvFollow = holder.getView(R.id.tv_follow);
                if (typeId.equals("100")){
                    tvFollow.setVisibility(View.GONE);
                }
                if (o.getFollowTag().equals("1")){//已经关注
                    tvFollow.setText("取消关注");
                    tvFollow.setChecked(true);
                }else {
                    tvFollow.setText("关注");
                    tvFollow.setChecked(false);
                }

                holder.getView(R.id.tv_follow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendFollow(o.getAnchorId());
                        if (tvFollow.isChecked()){
                            tvFollow.setText("取消关注");
                        }else {
                            tvFollow.setText("关注");
                        }

                    }
                });

                //图片列表
                RecyclerView recyc = holder.getView(R.id.recy_img);
                if (null!=o.getPicUrl() && o.getPicUrl().size()>0){
                    //recyc.setNestedScrollingEnabled(false);
                    recyc.setLayoutManager(new GridLayoutManager(context,3));
                    final BaseLoadMoreHeaderAdapter adapterImg = new BaseLoadMoreHeaderAdapter<String>(context,recyc,o.getPicUrl(),R.layout.item_img) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, String o) {
                            holder.setImageByUrl(R.id.iv_img,o);
                        }
                    };
                    recyc.setAdapter(adapterImg);
                    adapterImg.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            final Dialog dialog = new Dialog(context, R.style.BottomDialogStyle);
                            View viewM= View.inflate(context, R.layout.img_mach, null);
                            ImageView imageView = viewM.findViewById(R.id.iv_img);
                            GlideLoadUtils.getInstance().glideLoad(context,o.getPicUrl().get(position),imageView);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.setContentView(viewM);
                            dialog.setCanceledOnTouchOutside(true);
                            Window dialogWindow = dialog.getWindow();
                            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                            lp.gravity = Gravity.CENTER;
                            dialogWindow.setAttributes(lp);
                            dialog.show();
                        }
                    });
                }else {
                    recyc.setVisibility(View.GONE);
                }//图片列表end

                //评论列表
                RecyclerView commentRecy = holder.getView(R.id.comment_recy);
                commentRecy.setLayoutManager(new LinearLayoutManager(context));
                List<CommentListMo> moList = o.getCommentVoList();
                if (null ==moList || moList.size()==0){
                    moList = new ArrayList<>();
                }
                final BaseLoadMoreHeaderAdapter commentAdapter = new BaseLoadMoreHeaderAdapter<CommentListMo>(context,commentRecy,moList,R.layout.dy_comment_item) {
                    @Override
                    public void convert(Context mContext, BaseRecyclerHolder holder, CommentListMo o) {
                        holder.setText(R.id.auth_name,o.getNickName()+":");
                        holder.setText(R.id.auth_content,o.getContent());
                    }
                };
                commentRecy.setAdapter(commentAdapter);

                holder.getView(R.id.iv_comment).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final BottomSheetDialog dialog = new BottomSheetDialog(context,R.style.BottomDialogStyle2);
                        final View view = LayoutInflater.from(context).inflate(R.layout.sorf_input, null);
                        final EditText editText = view.findViewById(R.id.et_content);
                        TextView tvSend = view.findViewById(R.id.tv_send);
                        tvSend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!StringUtils.isEmpty(editText.getText().toString())){
                                    sendComment(editText.getText().toString(),o.getId());
                                    dialog.dismiss();
                                    CommentListMo mo = new CommentListMo();
                                    mo.setNickName((String) spUtils.getkey("name",""));
                                    mo.setContent(editText.getText().toString());
                                    commentAdapter.addAll2(mo);
                                    hideInput();
                                }
                            }
                        });
                        dialog.contentView(view)/*加载视图*/
                                /* .heightParam(towHight)*//*,显示的高度*/
                                /*动画设置*/
                                .inDuration(200)
                                .outDuration(200)
                                .cancelable(true)
                                .show();
                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);
    }

    //关注/取消关注
    private void sendFollow(String anchorId) {
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, token);
        map.put("anchorId",anchorId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.updateFans, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (!StringUtils.isEmpty(result)){
                    try {
                        JSONObject object = new JSONObject(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }

    //点赞/取消点赞
    private void sendDz(String sayId) {
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, token);
        map.put("sayId",sayId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(GiftUrl.praisedSay, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (!StringUtils.isEmpty(result)){
                    try {
                        JSONObject object = new JSONObject(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }

    //评论
    private void sendComment(String content,String sayId) {
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, token);
        map.put("centent",content);
        map.put("sayId",sayId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(GiftUrl.commentSay, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (!StringUtils.isEmpty(result)){
                    try {
                        JSONObject object = new JSONObject(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }

    //获取说说列表
    private void setDate(final boolean isLoad) {
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, token);
        map.put("page",String.valueOf(page));
        map.put("type",typeId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getSayList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                Log.d("luhuas", "onUi: "+result);
                if (StringUtils.isEmpty(result))return;

                try {
                    JSONObject object = new JSONObject(result);
                    if (0==object.optInt("errno")){
                        if (500 == object.optInt("code"))return;
                        String dataStr = object.optString("data");
                        mData = JsonUtil.string2Obj(dataStr,List.class, DynamicMo.class);
                        if (isLoad){//true 为加载更多
                            adapter.addAll(mData);
                        }else {
                            adapter.updateData(mData);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }

    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) DynamicPage.this.getContext().getSystemService(INPUT_METHOD_SERVICE);
        View v = DynamicPage.this.getActivity().getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
    public DynamicPage(int serial) {
        mSerial = serial;
    }

    public void sendMessage() {
        Message message = handler.obtainMessage();
        message.sendToTarget();
    }

    public void setToken(String tokenx){
        if (StringUtils.isEmpty(tokenx)){
            token = (String) spUtils.getkey("token","");
        }else {
            token = tokenx;
        }
    }

    public void setTabPos(int mTabPos, String typeId) {
        this.mTabPos = mTabPos;
        this.typeId = typeId;
    }
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (!IS_LOADED) {
                IS_LOADED = true;
                //这里执行加载数据的操作
                page = 1;
                setDate(false);
            }
        }
    };

}
