package com.dabangvr.lbroadcast.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.CartActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.home.activity.OrderActivity;
import com.dabangvr.home.weight.ShoppingSelectViewCopy;
import com.dabangvr.lbroadcast.mo.PlayGiftMo;
import com.dabangvr.lbroadcast.widget.BarrageView;
import com.dabangvr.lbroadcast.widget.MediaController;
import com.dabangvr.main.ConnectServer;
import com.dabangvr.model.GoodsAll;
import com.dabangvr.model.GwBean;
import com.dabangvr.my.activity.AuthMessActivity;
import com.dabangvr.my.activity.MyOrtherActivity;
import com.dabangvr.util.GlideLoadUtils;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.util.WXPayUtils;
import com.example.mina.ConnectionManager;
import com.example.mina.SessionManager;
import com.example.widget.MunueAnimator;
import com.facebook.drawee.view.SimpleDraweeView;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnCompletionListener;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.PLOnPreparedListener;
import com.pili.pldroid.player.PLOnVideoSizeChangedListener;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;

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
import Utils.PdUtil;
import bean.UserMess;
import config.DyUrl;
import config.GiftUrl;
import main.CommentMo;
import okhttp3.Call;

public class PlayActivity extends AppCompatActivity implements
        PLOnPreparedListener,
        PLOnInfoListener,
        PLOnCompletionListener,
        PLOnVideoSizeChangedListener,
        PLOnErrorListener, View.OnClickListener{

    public static PlayActivity instance;
    private PLVideoTextureView mVideoView;
    private SPUtils spUtils;
    private PdUtil pdUtil;
    private Intent intent;//开启服务
    private UserMess mess;


    private LinearLayout linearLayout;
    private volatile String path;//播放地址
    private String tag;//直播间
    private String anchorId;//主播id
    private String userId;//主播的用户id

    private MessageBroadcastReceiver receiver = new MessageBroadcastReceiver();

    private TextView tvFollow;
    //评论
    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;
    private List<CommentMo> list = new ArrayList<>();

    //其他主播头像
    private RecyclerView recyHead;
    private BaseLoadMoreHeaderAdapter adapterHead;
    private List<GwBean> listHead = new ArrayList<>();

    private int dataSize;//消息数量，始终将最新消息显示在recycelview的底部
    private EditText editText;//评论输入框


    private ImageView imgPublish;
    private TextView textView1;
    private TextView textView2;

    private boolean isMenuOpen = false;
    private MunueAnimator animator;

    private List<TextView> textViews = new ArrayList<>();

    private TextView name;
    private TextView fans;
    private SimpleDraweeView head;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 111) {
                ArrayList data = msg.getData().getParcelableArrayList("data");
                dataSize++;
                recyclerView.smoothScrollToPosition(dataSize);
                adapter.addAll(data);
            }
            return false;
        }
    });

    //商品列表
    private RecyclerView recyGoods;
    private BaseLoadMoreHeaderAdapter adapterGoods;
    private List<GoodsAll> listGoods = new ArrayList<>();


    //自定义输入的金额
    private EditText etMoney;
    private TextView tvAuthMoney;

    private LinearLayout llGift;
    private BaseLoadMoreHeaderAdapter giftAdapter;
    private List<PlayGiftMo>mData = new ArrayList<>();
    private RecyclerView giftRecy;


    private BarrageView barrageView;
    private List<String> barrageViews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_play);

        spUtils = new SPUtils(this, "db_user");
        pdUtil = new PdUtil(this);
        pdUtil.showLoding("加载中");
        //注册服务
        intent = new Intent(this, ConnectServer.class);
        //注册广播
        registerBroadcast();

        findViewById(R.id.play_layout).setOnClickListener(this);
        //视频播放器
        mVideoView = (PLVideoTextureView) findViewById(R.id.video_view);

        //缓冲等待控件
        View loadingView = findViewById(R.id.loading_view);

        //播放控制器
        MediaController mMediaController = new MediaController(this);

        mVideoView.setMediaController(mMediaController);
        mVideoView.setBufferingIndicator(loadingView);

        //播放状态监听
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnVideoSizeChangedListener(this);
        mVideoView.setOnErrorListener(this);

        //mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_ORIGIN);
        //mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_FIT_PARENT);
        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        //mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_16_9);
        //mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_4_3);

        //mVideoView.setDisplayOrientation(90); // 旋转90度
        mVideoView.setMirror(true);


        setAvOption(mVideoView);

        anchorId = getIntent().getStringExtra("anchorId");
        userId = getIntent().getStringExtra("userId");
        path = getIntent().getStringExtra("path");
        tag = getIntent().getStringExtra("tag");


        initView();//初始化主播信息

        commentView();//初始化评论信息

        initGoodsView();//初始化直播商品信息

        if (pdUtil.isShow()) {
            pdUtil.desLoding();
        }
    }

    private void initGoodsView() {
        recyGoods = findViewById(R.id.recy_goods);
        LinearLayoutManager manager2 = new LinearLayoutManager(this);
        manager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyGoods.setLayoutManager(manager2);

        adapterGoods = new BaseLoadMoreHeaderAdapter<GoodsAll>(this, recyGoods, listGoods, R.layout.zhibo_dilog) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, final GoodsAll o) {
                holder.setText(R.id.goods_name, o.getName());
                holder.setImageByUrl(R.id.hx_dilog_img, o.getListUrl());
                holder.setText(R.id.count_money, o.getSellingPrice());
                holder.setText(R.id.stock, o.getRemainingInventory());

                ShoppingSelectViewCopy selectView = null;

                if (null != o.getSpecList() && o.getSpecList().size()>0){
                    selectView = holder.getView(R.id.v);
                    selectView.setData(o.getSpecList());
                    selectView.setTextViewAndGGproject(
                            (TextView) holder.getView(R.id.di_iv_num),
                            (TextView) holder.getView(R.id.count_money),
                            (TextView) holder.getView(R.id.stock),
                            (TextView) holder.getView(R.id.dialog_jg),
                            (TextView) holder.getView(R.id.dialog_buy),
                            o.getProductInfoList(), o.getId(),
                            anchorId);
                }

                //数量
                final TextView num = holder.getView(R.id.di_iv_num);
                //价钱
                final TextView price = holder.getView(R.id.count_money);

                final ShoppingSelectViewCopy finalSelectView = selectView;

                holder.getView(R.id.di_iv_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int number = Integer.parseInt(num.getText().toString());
                        number += 1;
                        num.setText(String.valueOf(number));
                        if (null != finalSelectView){
                            price.setText(finalSelectView.getLastPrice());
                        }else {
                            price.setText(String.valueOf(Double.valueOf(o.getSellingPrice())*number));
                        }

                    }
                });

                holder.getView(R.id.di_iv_sub).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int number = Integer.parseInt(num.getText().toString());
                        if (number > 1) {
                            number -= 1;
                        }
                        num.setText(String.valueOf(number));
                        if (null != finalSelectView){
                            price.setText(finalSelectView.getLastPrice());
                        }else {
                            price.setText(String.valueOf(Double.valueOf(o.getSellingPrice())*number));
                        }
                    }
                });

                holder.getView(R.id.dialog_buy).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String,String>map = new HashMap<>();
                        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
                        map.put("goodsId", o.getId());
                        map.put("number", num.getText().toString());
                        map.put("buyType", "1");
                        if (null != o.getProductInfoList()) {
                            if (o.getProductInfoList().size() > 0) {
                                map.put("productId", String.valueOf(finalSelectView.getProductId()));
                            }
                        }
                        getOrder(map);
                    }
                });


                holder.getView(R.id.dialog_jg).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addCart(o.getId(),num.getText().toString());
                    }
                });
            }
        };
        recyGoods.setAdapter(adapterGoods);
        Map<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token", ""));
        map.put("anchorId", anchorId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getAnchorLiveGoodsList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (errno == 0) {
                        if (object.optInt("code") == 500) {
                            ToastUtil.showShort(PlayActivity.this, object.optString("msg"));
                            return;
                        }
                        String goodsListStr = object.optString("data");
                        List<GoodsAll> goods = JsonUtil.string2Obj(goodsListStr, List.class, GoodsAll.class);
                        if (goods != null && goods.size() > 0) {
                            listGoods = goods;
                            adapterGoods.updateData(listGoods);
                        }
                    }
                    if (pdUtil.isShow()) {
                        pdUtil.desLoding();
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

    private void addCart(String id,String number) {
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
        map.put("goodsId", id);
        map.put("number", number);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.addToCart, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result))return;

                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")){
                        if (500 == object.optInt("code"))return;
                        ToastUtil.showShort(PlayActivity.this,"加购成功");
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
     * 获取确认订单
     */
    private void getOrder(Map<String,String> map) {
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.confirmGoods, map, new GsonObjectCallback<String>(DyUrl.BASE){
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")){
                        if (500 == object.optInt("code")) {
                            ToastUtil.showShort(PlayActivity.this, "服务异常");
                            return;
                        }
                        Intent intent = new Intent(PlayActivity.this, OrderActivity.class);
                        intent.putExtra("type", "buy");
                        intent.putExtra("payOrderSnType", "orderSnTotal");
                        intent.putExtra("dropType", 1);
                        intent.putExtra("lbroadcast",true);
                        intent.putExtra("tag",tag);
                        startActivity(intent);
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


    private void setUserInfo(String namex, String ifanse, String ihead) {
        if (!StringUtils.isEmpty(namex)) {
            name.setText(namex);
        }
        if (!StringUtils.isEmpty(ihead)) {
            Uri uri = Uri.parse(ihead);
            head.setImageURI(uri);
        }
        if (!StringUtils.isEmpty(ifanse)) {
            fans.setText(ifanse);
        }
    }

    private void initView() {
        //返回
        findViewById(R.id.iv_back).setOnClickListener(this);

        //评论图片按钮
        findViewById(R.id.iv_comment).setOnClickListener(this);

        //评论视图
        linearLayout = findViewById(R.id.ll_comment);

        //关注按钮
        tvFollow = findViewById(R.id.play_follow);
        tvFollow.setOnClickListener(this);
        String fansTag = getIntent().getStringExtra("fansTag");
        if(!StringUtils.isEmpty(fansTag)){
            if(fansTag.equals("1")){
                tvFollow.setText("取消关注");
            }
        }

        //主播信息
        name = findViewById(R.id.auth_name);
        fans = findViewById(R.id.auth_fanse);
        head = findViewById(R.id.auth_img);
        head.setOnClickListener(this);
        String iname = getIntent().getStringExtra("name");
        String ihead = getIntent().getStringExtra("head");
        String ifanse = getIntent().getStringExtra("fanse");
        setUserInfo(iname, ifanse, ihead);

        imgPublish = (ImageView) findViewById(R.id.img_publish);
        textView1 = (TextView) findViewById(R.id.tv_1);
        textView2 = (TextView) findViewById(R.id.tv_2);
        textView1.setOnClickListener(this);
        textView2.setOnClickListener(this);
        textViews.add(textView1);
        textViews.add(textView2);

        animator = new MunueAnimator(textViews, PlayActivity.this, new MunueAnimator.IsShow() {
            @Override
            public void isShow(boolean is) {
                isMenuOpen = is;
            }
        });
        imgPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuOpen) {
                    animator.showCloseAnim(80);

                } else {
                    animator.showOpenAnim(80);
                }
            }
        });

        //其他直播列表
        String all = getIntent().getStringExtra("all_anchor");
        listHead = JsonUtil.string2Obj(all, List.class, GwBean.class);
        //观看用户的头像
        recyHead = findViewById(R.id.recy_top_head);
        LinearLayoutManager headManager = new LinearLayoutManager(this);
        headManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyHead.setLayoutManager(headManager);

        adapterHead = new BaseLoadMoreHeaderAdapter<GwBean>(this, recyHead, listHead, R.layout.head_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, GwBean o) {
                SimpleDraweeView view = holder.getView(R.id.drawee_img);
                Uri uri = Uri.parse(o.getHeadUrl());
                view.setImageURI(uri);
            }
        };
        recyHead.setAdapter(adapterHead);

        adapterHead.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (listHead.get(position).getTag() == tag) {
                    return;
                }
                changeRoom();
                path = listHead.get(position).getRtmpPlayURL();
                tag = listHead.get(position).getTag();
                anchorId = listHead.get(position).getAnchorId();
                userId = listHead.get(position).getUserId();

                mVideoView.setVideoPath(path);
                mVideoView.start();


                setUserInfo(listHead.get(position).getAnchorName()
                        , listHead.get(position).getFans()
                        , listHead.get(position).getHeadUrl());

            }
        });


        //礼物控件
        llGift = findViewById(R.id.ll_gift);
        giftRecy = findViewById(R.id.recy_gift);
        giftRecy.setLayoutManager(new GridLayoutManager(this,4));
        giftAdapter = new BaseLoadMoreHeaderAdapter<PlayGiftMo>(this,giftRecy,mData,R.layout.play_gift_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, PlayGiftMo o) {
                holder.setImageByUrl(R.id.iv_gift,o.getGiftUrl());
                holder.setText(R.id.tv_num,o.getGiftCoins());
            }
        };
        giftRecy.setAdapter(giftAdapter);

        giftAdapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
                View viewNum = View.inflate(PlayActivity.this, R.layout.play_gift_num, null);

                TextView tvName = viewNum.findViewById(R.id.tv_gift_name);
                ImageView ivUrl = viewNum.findViewById(R.id.iv_gift_url);

                tvName.setText(mData.get(position).getGiftName());
                GlideLoadUtils.getInstance().glideLoad(PlayActivity.this,mData.get(position).getGiftUrl(),ivUrl);

                builder.setIcon(R.mipmap.application);//设置对话框icon
                final AlertDialog dialog = builder.create();
                dialog.setView(viewNum);
                viewNum.findViewById(R.id.tv_num_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //打赏
                        startDs(mData.get(position).getId(),mData.get(position).getGiftName(),mData.get(position).getTag());
                        dialog.dismiss();

                        //隐藏礼物控件
                        llGift.setVisibility(View.GONE);
                    }
                });
                dialog.show();
            }
        });


        //充值弹窗
        findViewById(R.id.ll_cz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(PlayActivity.this, R.style.BottomDialogStyle);
                View viewM= View.inflate(PlayActivity.this, R.layout.play_cz_dialog, null);


                //10充值
//                viewM.findViewById(R.id.tv_money_01).setOnClickListener(this);

                viewM.findViewById(R.id.tv_money_01).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startCz("10");
                    }
                });

                //50充值
                viewM.findViewById(R.id.tv_money_02).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startCz("50");
                    }
                });
                //100充值
                viewM.findViewById(R.id.tv_money_03).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startCz("100");
                    }
                });
                //200充值
                viewM.findViewById(R.id.tv_money_04).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startCz("200");
                    }
                });
                //自定义充值
                tvAuthMoney = viewM.findViewById(R.id.tv_money_05);
                tvAuthMoney.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (StringUtils.isEmpty(etMoney.getText().toString())){
                            ToastUtil.showShort(PlayActivity.this,"请输入金额");
                            return;
                        }

                        if (Integer.parseInt(etMoney.getText().toString())<10){
                            ToastUtil.showShort(PlayActivity.this,"至少充值100个鲜币哦~~");
                            return;
                        }
                        startCz(tvAuthMoney.getText().toString());
                    }
                });

                etMoney = viewM.findViewById(R.id.et_money);

                etMoney.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(delayRun!=null){
                            //每次editText有变化的时候，则移除上次发出的延迟线程
                            handler.removeCallbacks(delayRun);
                        }
                        //延迟800ms，如果不再输入字符，则执行该线程的run方法
                        handler.postDelayed(delayRun, 500);
                    }
                });



                viewM.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
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

        initGiftData();


        //弹幕控件
        barrageView = findViewById(R.id.barrageview);
        barrageViews = new ArrayList<>();
    }


    private boolean isShowTips = true;// 第一次送礼，弹窗提示积分
    /**
     * 打赏
     */
    private void startDs(String giftId, final String giftName, final String type){
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
        map.put("giftId",giftId);
        map.put("anchorId",anchorId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(GiftUrl.rewardGift, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)){
                    return;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")){
                        if (500 == object.optInt("code"))return;

                        if (isShowTips){
                            //弹窗动画
                            AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
                            View viewNum = View.inflate(PlayActivity.this, R.layout.play_gift_success, null);
                            final AlertDialog dialog = builder.create();
                            dialog.setView(viewNum);
                            //dialog.getWindow().getAttributes().windowAnimations = R.style.dialogWindowAnim;
                            viewNum.findViewById(R.id.tv_num_ok).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                            isShowTips = false;
                        }

                        //消息
                        sandMess(giftName,type);


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
     * 支付成功后回调
     */

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            tvAuthMoney.setText(etMoney.getText().toString());
        }
    };


    public void tvSand(View view) {
        if (!StringUtils.isEmpty(editText.getText().toString())) {
            sand(editText.getText().toString());
            editText.setText("");
        }
    }

    public void share(View view) {

    }

    /**
     * 评论控件
     */
    private void commentView() {
        editText = findViewById(R.id.et_comment);

        recyclerView = findViewById(R.id.recy);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        adapter = new BaseLoadMoreHeaderAdapter<CommentMo>(this, recyclerView, list, R.layout.zb_comment_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, CommentMo commentMo) {
                TextView auth = holder.getView(R.id.auth_name);
                TextView content = holder.getView(R.id.auth_content);
                if (commentMo.getType().equals("0")) {
                    auth.setTextColor(getResources().getColor(R.color.zhiBo));
                } else if (commentMo.getType().equals("1")) {
                    auth.setTextColor(getResources().getColor(R.color.black));
                } else if (commentMo.getType().equals("2")) {
                    auth.setTextColor(getResources().getColor(R.color.colorZi));
                } else if (commentMo.getType().equals("3")) {
                    auth.setTextColor(getResources().getColor(R.color.colorOrag));
                } else if (commentMo.getType().equals("100")){
                    auth.setTextColor(getResources().getColor(R.color.colorDb3));
                    content.setTextColor(getResources().getColor(R.color.colorDb3));
                    content.setTextSize(25);
                }
                SimpleDraweeView head = holder.getView(R.id.drawee_img);
                if (!StringUtils.isEmpty(commentMo.getHead())) {
                    Uri uri = Uri.parse(commentMo.getHead());
                    head.setImageURI(uri);
                }
                auth.setText(commentMo.getName());
                holder.setText(R.id.auth_content, commentMo.getMess());
            }
        };

        recyclerView.setAdapter(adapter);

    }

    /**
     * 切换房间
     */
    private void changeRoom() {
        JSONObject o = new JSONObject();
        try {
            o.put("address", tag);
            o.put("change", "change");
            SessionManager.getInstance().writeToServer(o);
            list.clear();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送评论
     *
     * @param et
     */
    private void sand(String et) {
        JSONObject o = new JSONObject();
        try {
            o.put("sand", "sand");
            o.put("name", mess.getNickName());
            o.put("head", mess.getHeadUrl());
            o.put("address", tag);
            o.put("type", "3");
            o.put("mess", et);
            SessionManager.getInstance().writeToServer(o);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送弹幕
     * @param giftName 礼物名称
     * @param type 礼物类型   ： 礼物名称首字母
     */
    private void sandMess(String giftName,String type) {
        JSONObject o = new JSONObject();
        try {
            o.put("sand", "sand");
            o.put("name", mess.getNickName());
            o.put("head", mess.getHeadUrl());
            o.put("address", tag);
            o.put("type", "99");
            o.put("gift",type);
            o.put("mess", giftName);
            SessionManager.getInstance().writeToServer(o);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 注册广播
     * 接收其他人的消息
     */
    public void registerBroadcast() {
        IntentFilter filter = new IntentFilter(ConnectionManager.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    /**
     * 销毁服务
     */
    public void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    /**
     * 将消息展示在控件上
     *
     * @param type
     * @param auth
     * @param mess
     */
    protected void setData(String type, String gift,String auth, String mess, String head) {
        //有客户下单通知
        if (type.equals("100")){
            barrageViews.add(auth+"下了一单，获得10点积分");
            barrageView.setData(barrageViews);
            barrageView.start();
        }else if (type.equals("99")){//烟花
            barrageViews.add(auth+"送了一个"+mess);
            barrageView.setData(barrageViews);
            barrageView.setType(gift);
            barrageView.start();

            CommentMo commentMo = new CommentMo();
            commentMo.setType(type);
            commentMo.setName(auth);
            commentMo.setMess("赠送--"+mess);
            commentMo.setHead(head);
            Bundle bundle = new Bundle();
            ArrayList arr = new ArrayList();
            arr.add(commentMo);
            bundle.putStringArrayList("data", arr);
            Message message = new Message();
            message.what = 111;
            message.setData(bundle);
            handler.sendMessage(message);
        }else {
            CommentMo commentMo = new CommentMo();
            commentMo.setType(type);
            commentMo.setName(auth);
            commentMo.setMess(mess);
            commentMo.setHead(head);
            Bundle bundle = new Bundle();
            ArrayList arr = new ArrayList();
            arr.add(commentMo);
            bundle.putStringArrayList("data", arr);
            Message message = new Message();
            message.what = 111;
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.auth_img:
                mVideoView.stopPlayback();
                Intent intentA = new Intent(this,AuthMessActivity.class);
                intentA.putExtra("authId",anchorId);
                intentA.putExtra("userId",userId);
                startActivity(intentA);
                break;
            case R.id.tv_1:
                Intent intent = new Intent(PlayActivity.this, CartActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_2:
                Intent intent2 = new Intent(PlayActivity.this, MyOrtherActivity.class);
                startActivity(intent2);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.play_follow:
                follow();
                break;
            case R.id.iv_comment:
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, 0);
                break;
            case R.id.play_layout:
                if (recyGoods.getVisibility() == View.VISIBLE) {
                    recyGoods.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 充值
     */
    private void startCz(String money){
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
        map.put("price",money);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(GiftUrl.investDiamond, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)){
                    return;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (errno == 0) {
                        if (500 == object.optInt("code")) {
                            return ;
                        }
                        //吊起微信;
                        toWXPay(object.optJSONObject("data"));
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
     * 微信支付
     *
     * @param object
     */
    private void toWXPay(JSONObject object) {
        spUtils.put("payType","Recharge");
        WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
        builder.setAppId(object.optString("appid"))
                .setPartnerId(object.optString("partnerid"))
                .setPrepayId(object.optString("prepayid"))
                .setPackageValue(object.optString("package"))
                .setNonceStr(object.optString("noncestr"))
                .setTimeStamp(object.optString("timestamp"))
                .setSign(object.optString("sign"))
                .build().toWXPayNotSign(PlayActivity.this);
        ToastUtil.showShort(this, "正在打开微信...");
    }

    private void follow(){
        pdUtil.showLoding("关注");
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
        map.put("anchorId",anchorId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.updateFans, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if(StringUtils.isEmpty(result)){
                    return ;
                }

                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if(errno == 0){
                        if(500 == object.optInt("code")){
                            return ;
                        }
                        tvFollow.setText("已关注");
                    }else {
                        ToastUtil.showShort(PlayActivity.this,"关注失败,服务异常");
                    }

                    if(pdUtil.isShow()){
                        pdUtil.desLoding();
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
     * 接收mina服务消息
     */
    private class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("tag", "广播接收：" + intent.getStringExtra(ConnectionManager.MESSAGE));
            try {
                String str = intent.getStringExtra(ConnectionManager.MESSAGE);
                JSONObject object = new JSONObject(str);
                String type = object.optString("type");
                String name = object.optString("name");
                String mess = object.optString("mess");
                String head = object.optString("head");
                String gift = object.optString("gift");
                setData(type,gift, name, mess, head);

                String exit = object.optString("exit");
                if(!StringUtils.isEmpty(exit)){
                    if(exit.equals("exit")){
                        unregist();
                        show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void unregist() {
        stopService(new Intent(this, ConnectServer.class));
        unregisterBroadcast();
        stopService(intent);
        mVideoView.stopPlayback();
    }

    private void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this)
                .setMessage("主播已离开房间")
                .setPositiveButton("去看其他产品", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        stopService(new Intent(PlayActivity.this, ConnectServer.class));
                        unregisterBroadcast();
                        stopService(intent);
                        mVideoView.stopPlayback();
                        finish();
                    }
                })
                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        stopService(new Intent(PlayActivity.this, ConnectServer.class));
                        unregisterBroadcast();
                        stopService(intent);
                        mVideoView.stopPlayback();
                        finish();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 设置播放参数
     */
    private void setAvOption(PLVideoTextureView mVideoView) {
        AVOptions options = new AVOptions();

// 解码方式:
// codec＝AVOptions.MEDIA_CODEC_HW_DECODE，硬解
// codec=AVOptions.MEDIA_CODEC_SW_DECODE, 软解
        int codec = AVOptions.MEDIA_CODEC_AUTO; //硬解优先，失败后自动切换到软解
// 默认值是：MEDIA_CODEC_SW_DECODE
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);

// 准备超时时间，包括创建资源、建立连接、请求码流等，单位是 ms
// 默认值是：无
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);

// 读取视频流超时时间，单位是 ms
// 默认值是：10 * 1000
        //options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);

// 当前播放的是否为在线直播，如果是，则底层会有一些播放优化
// 默认值是：0
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);


// 快开模式，启用后会加快该播放器实例再次打开相同协议的视频流的速度
        options.setInteger(AVOptions.KEY_FAST_OPEN, 1);

// 默认的缓存大小，单位是 ms
// 默认值是：2000
        options.setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, 2000);

// 最大的缓存大小，单位是 ms
// 默认值是：4000
        options.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 4000);

        mVideoView.setAVOptions(options);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        tag = intent.getStringExtra("tag");
        mVideoView.setVideoPath(path);
        mVideoView.start();
    }

    @Override
    public void onPrepared(int i) {

    }

    /**
     * 播放状态回调
     *
     * @param what
     * @param i1
     */
    @Override
    public void onInfo(int what, int i1) {
        switch (what) {
            case MEDIA_INFO_CONNECTED:
                if (isSuccess){
                    startConnectServer();
                }
                break;//连接成功
            case MEDIA_INFO_BUFFERING_START:
                break;//开始缓冲
        }
    }

    private boolean isSuccess = true;

    private void startConnectServer(){
        String str = (String) spUtils.getkey("user", "");
        mess = JsonUtil.string2Obj(str, UserMess.class);
        intent.putExtra("address", tag);//主播间
        if (mess == null) {
            intent.putExtra("name", "路人甲");
        } else {
            intent.putExtra("name", mess.getNickName());
            intent.putExtra("head", mess.getHeadUrl());
        }
        intent.putExtra("isFirst", "connect");
        startService(intent);
        isSuccess = false;
    }
    @Override
    public void onCompletion() {

    }

    @Override
    public void onVideoSizeChanged(int i, int i1) {

    }

    @Override
    public boolean onError(int i) {
        Log.e("tag", "" + i);
        switch (i) {
            case MEDIA_ERROR_UNKNOWN:
                Log.e("tag", "未知错误");
                break;
            case ERROR_CODE_OPEN_FAILED:
                Log.e("tag", "播放器打开失败");
                break;
            case ERROR_CODE_IO_ERROR:
                Log.e("tag", "网络异常");
                break;
            case ERROR_CODE_PLAYER_DESTROYED:
                Log.e("tag", "播放器已被销毁，需要再次 setVideoURL 或 prepareAsync");
                break;
            case ERROR_CODE_PLAYER_VERSION_NOT_MATCH:
                Log.e("tag", "so 库版本不匹配，需要升级");
                break;
            case ERROR_CODE_PLAYER_CREATE_AUDIO_FAILED:
                Log.e("tag", "AudioTrack 初始化失败，可能无法播放音频");
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, ConnectServer.class));
        unregisterBroadcast();
        stopService(intent);
        mVideoView.stopPlayback();
        barrageView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barrageView.onResume();
//        mVideoView.setVideoPath(path);
//        mVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barrageView.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (linearLayout.getVisibility() == View.VISIBLE) {
            linearLayout.setVisibility(View.INVISIBLE);
            return;
        }
        stopService(new Intent(this, ConnectServer.class));
        unregisterBroadcast();
        stopService(intent);
        mVideoView.stopPlayback();
    }

    /**
     * 显示商品规格列表
     *
     * @param view
     */
    public void showGoods(View view) {
        if (recyGoods.getVisibility() == View.GONE) {
            recyGoods.setVisibility(View.VISIBLE);
        } else {
            recyGoods.setVisibility(View.GONE);
        }
    }



    /**
     * 显示礼物布局
     * @param view
     */
    public void showGift(final View view){
        if (llGift.getVisibility() == View.VISIBLE){
            llGift.setVisibility(View.GONE);
        }else {
            llGift.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 礼物数据
     */
    private void initGiftData(){
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(GiftUrl.getLiveGiftList, null, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {

                if (!StringUtils.isEmpty(result)){
                    try {
                        JSONObject object= new JSONObject(result);
                        if (0 == object.optInt("errno")){
                            if (500 == object.optInt("code"))return;
                            String data = object.optString("data");
                            mData = JsonUtil.string2Obj(data,List.class,PlayGiftMo.class);
                            giftAdapter.updateData(mData);
                        }
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