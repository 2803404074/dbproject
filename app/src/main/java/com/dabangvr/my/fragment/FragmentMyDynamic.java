package com.dabangvr.my.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.model.DynamicMo;
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

@SuppressLint("ValidFragment")
public class FragmentMyDynamic extends Fragment {

    private Context context;
    private BaseLoadMoreHeaderAdapter adapter;
    private RecyclerView recyclerView;
    private List<DynamicMo> mData = new ArrayList<>();
    private int page = 1;

    private SPUtils spUtils;
    private String uId;//用户id

    public static FragmentMyDynamic newInstance(String uId) {
        FragmentMyDynamic fragment = new FragmentMyDynamic();
        Bundle args = new Bundle();
        args.putString("uId", uId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = FragmentMyDynamic.this.getContext();
        if (getArguments() != null) {
            uId = getArguments().getString("uId");
        }
        spUtils = new SPUtils(context,"db_user");
        View view = inflater.inflate(R.layout.dynamic_page, container, false);
        initView(view);
        return view;
    }

    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new BaseLoadMoreHeaderAdapter<DynamicMo>(context,recyclerView,mData,R.layout.item_dynamic) {
            @Override
            public void convert(final Context mContext, BaseRecyclerHolder holder, final DynamicMo o) {
                SimpleDraweeView draweeView = holder.getView(R.id.auth_head);
                draweeView.setImageURI(TextUtil.isNull2Url(o.getHeadUrl()));

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
                //测试占时注释
//                holder.getView(R.id.ll_dz).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
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
                ImageView imgDele = holder.getView(R.id.tv_follow);
//                checkBox1.setText("关注");
//                checkBox1.setTextSize(12);

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

        initData();
    }

    private void initData() {
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
        map.put("page",String.valueOf(page));
        map.put("userId",uId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getSayList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result))return;

                try {
                    JSONObject object = new JSONObject(result);
                    if (0==object.optInt("errno")){
                        if (500 == object.optInt("code"))return;
                        String dataStr = object.optString("data");
                        mData = JsonUtil.string2Obj(dataStr,List.class, DynamicMo.class);
                        if (null!=mData && mData.size()>0){
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

    //评论
    private void sendComment(String content,String sayId) {
        Map<String,String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
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

}
