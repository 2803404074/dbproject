package com.dabangvr.home.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.home.adapter.HxxqAdapter;
import com.dabangvr.home.weight.ShoppingSelectView;
import com.dabangvr.model.CommentBean;
import com.dabangvr.model.GGobject;
import com.dabangvr.model.GoodsMo;
import com.dabangvr.model.ProductInfoVoList;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoadingDialog;
import com.dabangvr.util.LoginTipsDialog;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.TextUtil;
import com.dabangvr.util.ToastUtil;
import com.rey.material.app.BottomSheetDialog;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.DyUrl;
import okhttp3.Call;

/**
 * 海鲜-商品详情页
 */
public class HxxqActivity extends BaseActivity implements View.OnClickListener, DialogInterface.OnDismissListener {

    private HxxqAdapter adapter;
    private int mDistance = 0;
    private int maxDistance = 255;
    private RelativeLayout layout;
    private LinearLayout title_mess;
    private ImageView back;
    private boolean isFinish = false;
    public static boolean isGG = false;
    public static HxxqActivity instants;
    private LocalBroadcastManager broadcastManager;
    private IntentFilter intentFilter;
    private BroadcastReceiver mReceiver;

    private TextView buyNow;//立即购买
    private int showDialogType;//区别底部弹出框的类型

    private int buyType;//意图传过来的商品类型，0是普通商品，1是拼团商品，2是秒杀商品

    private String goodsId;//意图传过来的商品id
    private RecyclerView recyclerView;//页面数据列表
    private LoadingDialog loadingDialog;
    private String bottomDilogImgUrl;

    private TextView number;//底部弹窗-商品的数量
    private TextView priceCount;//底部弹窗-商品的总价

    private String sellingPrice;//普通默认价钱
    private String groAndKillPrice;//团购和秒杀的默认价钱

    private List<GGobject> gGobjects;//保存规格的对象
    private List<ProductInfoVoList> productInfo;//保存规格的对象
    private ShoppingSelectView selectView;
    private boolean IS_LOADED = false;
    private boolean hasGG = false;
    //计算选中规格后的值
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (!IS_LOADED) {
                IS_LOADED = true;
                //这里执行加载数据的操作
                int index = msg.getData().getInt("id");
                setDate(index);
            }
        }
    };

    private TextView tv2;//拼团购买 按钮


    private TextView stockTv;
    private String stockStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        instants = this;
    }

    @Override
    public int setLayout() {
        return R.layout.activity_hxxq;
    }

    /**
     * 选完规格后通过handel执行下面的setDate()方法
     */
    private void sendMessage(int index) {
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putInt("id", index);
        message.setData(bundle);
        message.sendToTarget();
    }

    @Override
    protected void initView() {
        //判断跳转到当前页面的是什么类型的商品
        Intent intent = getIntent();
        buyType = intent.getIntExtra("type", 0);

        //顶部控件
        layout = findViewById(R.id.hxxq_title);

        //顶部标签
        title_mess = findViewById(R.id.title_mess);
        //获取商品意图传过来的商品id
        goodsId = intent.getStringExtra("id");


        //等待控件,登陆控件
        loadingDialog = new LoadingDialog(this);


        //设置底部三个readiobutton图片的大小以及点击事件
        setImg();

        //显示商品的布局
        recyclerView = findViewById(R.id.hxxq_recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //返回按钮
        back = findViewById(R.id.backe);
        back.setOnClickListener(this);

        //立即购买点击监听
        buyNow = findViewById(R.id.hx_shop);
        buyNow.setOnClickListener(this);

        //拼团购买点击监听
        tv2 = findViewById(R.id.hx_shop_pt);

        //如果是普通商品，则屏蔽掉拼团购买按钮
        //否则显示，并且获取传递过来的拼团id，开始时间和结束时间
        if (buyType == 0) {//0普通，1拼团，2秒杀
            buyNow.setText("立即购买");
            tv2.setVisibility(View.GONE);
            //assembleId = intent.getStringExtra("assembleId");
        } else if (buyType == 1) {//拼团
            tv2.setVisibility(View.VISIBLE);
            //开始倒计时
        } else {//秒杀
            buyNow.setVisibility(View.GONE);
            tv2.setVisibility(View.VISIBLE);
            tv2.setText("秒杀");
        }
        tv2.setOnClickListener(this);

        //收藏点击
        findViewById(R.id.hx_sc).setOnClickListener(this);

        //客服点击
        findViewById(R.id.hx_server).setOnClickListener(this);

        //加购点击
        TextView jg = findViewById(R.id.hx_jg);
        if (buyType == 1 || buyType == 2) {//拼团不显示加购
            jg.setVisibility(View.GONE);
        } else {
            jg.setOnClickListener(this);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mDistance += dy;
                float percent = mDistance * 1f / maxDistance;//百分比
                int alpha = (int) (percent * 255);
//            int argb = Color.argb(alpha, 57, 174, 255);
                setSystemBarAlpha(alpha);
            }
        });

    }

    @Override
    protected void initData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("goodsId", goodsId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoodsDetails, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            //主线程处理
            @Override
            public void onUi(String newsBean) {
                if (StringUtils.isEmpty(newsBean)) {
                    if (loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    ToastUtil.showShort(HxxqActivity.this, "获取数据失败");
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(newsBean);
                    int err = object.optInt("errno");
                    if (err == 0) {
                        if (object.optInt("code") == 500){
                            show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(600);
                                        finish();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            return ;
                        }
                        JSONObject object1 = object.optJSONObject("data");

                        //获取评论列表
                        String commentStr = object1.optString("commentVoList");
                        //评论数量
                        String commentStrSize = object1.optString("commentVoListSize");
                        List<CommentBean> list = JsonUtil.string2Obj(commentStr, List.class, CommentBean.class);

                        JSONObject object2 = object1.optJSONObject("goodsDetails");

                        GoodsMo goodsMo = new GoodsMo();
                        goodsMo.setCommentListSize(commentStrSize);
                        goodsMo.setCommentList(list);
                        goodsMo.setId(object2.optString("id"));//商品id
                        goodsMo.setDepId(object2.optString("deptId"));//店铺id
                        goodsMo.setDescribe(object2.optString("describe"));//商品描述
                        goodsMo.setMarketPrice(object2.optString("marketPrice"));//市场价
                        sellingPrice = object2.optString("sellingPrice");//为了底部弹窗设置的最初价钱
                        //默认库存
                        stockStr = object2.optString("remainingInventory");

                        if (TextUtil.isEmpty(stockStr)){
                            buyNow.setBackgroundColor(getResources().getColor(R.color.colorAccentNo));
                        }

                        if (buyType == 0) {//普通商品
                            goodsMo.setSellingPrice(sellingPrice);//初始化价钱

                        }
                        String endTime = "";
                        if (buyType == 1) {//拼团商品
                            groAndKillPrice = object2.optString("groupPrice");
                            goodsMo.setSellingPrice(groAndKillPrice);//初始化价钱
                            endTime = object2.optString("endTime");
                        }
                        if (buyType == 2) {//秒杀商品
                            groAndKillPrice = object2.optString("secondsPrice");
                            goodsMo.setSellingPrice(groAndKillPrice);//初始化价钱
                            endTime = object2.optString("secondsEndTime");

                        }

                        goodsMo.setName(object2.optString("name"));//商品名称
                        goodsMo.setTitle(object2.optString("title"));//商品标题
                        goodsMo.setImgList(object1.optJSONArray("goodsImgsAndVideo"));//商品资源
                        goodsMo.setGoodsDesc(object2.optString("goodsDesc"));//html

                        //获取商品图片，用于底部弹窗的时候加载各个图片
                        bottomDilogImgUrl = object2.optString("listUrl");

                        //获取规格数组
                        JSONArray array = object2.optJSONArray("goodsSpecVoList");
                        if (array != null && array.length() > 0) {
                            hasGG = true;
                            //获取需要显示的规格
                            gGobjects = JsonUtil.string2Obj(array.toString(), List.class, GGobject.class);
                            //选择完规格后，存在的规格对象
                            String productArray = object2.optString("productInfoVoList");
                            productInfo = JsonUtil.string2Obj(productArray, List.class, ProductInfoVoList.class);
                        }
                        adapter = new HxxqAdapter(HxxqActivity.this, stockStr, buyType, getSupportFragmentManager(), goodsMo, endTime);
                        recyclerView.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //请求失败
            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }

    private void setSystemBarAlpha(int alpha) {
        if (alpha >= 200) {
            title_mess.setVisibility(View.VISIBLE);
            //返回按钮白色
            back.setImageDrawable(getResources().getDrawable(R.drawable.back));
        } else {
            //标题栏渐变。a:alpha透明度 r:红 g：绿 b蓝
//        titlebar.setBackgroundColor(Color.rgb(57, 174, 255));//没有透明效果
//        titlebar.setBackgroundColor(Color.argb(alpha, 57, 174, 255));//透明效果是由参数1决定的，透明范围[0,255]
            layout.getBackground().setAlpha(alpha);
            title_mess.setVisibility(View.INVISIBLE);
            back.setImageDrawable(getResources().getDrawable(R.drawable.back_black));
        }
    }

    private void setImg() {
        RadioButton sc = findViewById(R.id.hx_sc);
        sc.setOnClickListener(this);
        RadioButton server = findViewById(R.id.hx_server);
        server.setOnClickListener(this);
        RadioButton jg = findViewById(R.id.hx_jg);
        jg.setOnClickListener(this);
        Drawable drawableFirst1 = getResources().getDrawable(R.drawable.hx_sc);
        Drawable drawableFirst2 = getResources().getDrawable(R.drawable.hx_server);
        Drawable drawableFirst3 = getResources().getDrawable(R.drawable.hx_jg);
        drawableFirst1.setBounds(0, 0, 69, 69);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        drawableFirst2.setBounds(0, 0, 69, 69);
        drawableFirst3.setBounds(0, 0, 69, 69);
        sc.setCompoundDrawables(null, drawableFirst1, null, null);//只放上面
        server.setCompoundDrawables(null, drawableFirst2, null, null);//只放上面
        jg.setCompoundDrawables(null, drawableFirst3, null, null);//只放上面
    }


    /**
     * 底部菜单点击监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backe: {
                finish();
                break;
            }
            case R.id.hx_shop: {//立即购买,单独购买
                if (TextUtil.isEmpty(stockStr) || TextUtil.isEmpty(sellingPrice)){
                    ToastUtil.showShort(HxxqActivity.this,"该产品已下架");
                    return;
                }
                showBottomDialog(2);
                break;
            }
            case R.id.hx_shop_pt: {//拼团购买,秒杀购买
                if(isFinish){
                    show();
                    return;
                }
                if (buyType == 1) {
                    showBottomDialog(3);
                }
                if (buyType == 2) {
                    showBottomDialog(4);
                }

                break;
            }
            case R.id.hx_sc: {//收藏
                if (TextUtil.isEmpty(stockStr) || TextUtil.isEmpty(sellingPrice)){
                    ToastUtil.showShort(HxxqActivity.this,"该产品已下架");
                    return;
                }
                if(isFinish){
                    return;
                }
                Map<String, String> map = new HashMap<>();
                unifiedRequest(map, DyUrl.getGoodsCollectSave, 0);
                break;
            }
            case R.id.hx_server: {//客服
                break;
            }
            case R.id.hx_jg: {//加购
                if (TextUtil.isEmpty(stockStr) || TextUtil.isEmpty(sellingPrice)){
                    ToastUtil.showShort(HxxqActivity.this,"该产品已下架");
                    return;
                }
                showBottomDialog(1);
                break;
            }
            case R.id.di_iv_sub: {//减
                String size = number.getText().toString();
                if (size.equals("1")) {
                    return;
                }
                int num = Integer.parseInt(size) - 1;
                number.setText(String.valueOf(num));
                sendMessage(1);
                break;
            }
            case R.id.di_iv_add: {//加
                String size = number.getText().toString();
                int num = Integer.parseInt(size) + 1;
                number.setText(String.valueOf(num));
                sendMessage(0);
                break;
            }
        }
    }

    /**
     * 收藏、加购、购买统一请求
     *
     * @param map 请求参数
     * @param url 请求地址
     */
    private void unifiedRequest(Map<String, String> map, String url, final int requestType) {
        String token = getSPKEY(this,"token");
        if (StringUtils.isEmpty(token)) {
            LoginTipsDialog.ortehrTips(this, "您未登陆哦，赶紧成为我们的会员吧");
            return;
        }
        map.put(DyUrl.TOKEN_NAME, token);
        map.put("goodsId", goodsId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(url, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (500 == object.optInt("code")) {
                        ToastUtil.showShort(HxxqActivity.this, "服务异常");
                        return ;
                    }
                    int err = object.optInt("errno");
                    if (err == 0) {

                        //0收藏，1加购，2单独购买，3拼团购买，4秒杀购买
                        if (requestType == 0) {//收藏请求
                            ToastUtil.showShort(HxxqActivity.this, "收藏成功");
                            return ;
                        }
                        if (requestType == 1) {//加入购物车
                            ToastUtil.showShort(HxxqActivity.this, "加购成功");
                        }

                        if (requestType == 2) {//确认订单
                            Intent intent = new Intent(HxxqActivity.this, OrderActivity.class);
                            intent.putExtra("type", "buy");
                            intent.putExtra("payOrderSnType", "orderSnTotal");
                            intent.putExtra("dropType", 1);
                            startActivity(intent);
                        }
                        if (requestType == 3) {//拼团确认订单
                            Intent intent = new Intent(HxxqActivity.this, OrderActivity.class);
                            intent.putExtra("type", "group2");
                            intent.putExtra("payOrderSnType", "orderSnTotal");
                            intent.putExtra("dropType", 2);
                            startActivity(intent);
                        }
                        if (requestType == 4) {//秒杀确认订单
                            Intent intent = new Intent(HxxqActivity.this, OrderActivity.class);
                            intent.putExtra("type", "seconds");
                            intent.putExtra("payOrderSnType", "orderSnTotal");
                            intent.putExtra("dropType", 3);
                            startActivity(intent);
                        }

                    } else {
                        ToastUtil.showShort(HxxqActivity.this, "服务错误" + object.optString("errmsg"));
                    }
                    loadingDialog.dismiss();
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
     * 底部弹窗
     *
     * @param index 1加购，2单独购买，3拼团购买，4秒杀购买
     */
    private void showBottomDialog(final int index) {

        showDialogType = index;
        //初始化底部弹窗
        final BottomSheetDialog bottomInterPasswordDialog = new BottomSheetDialog(this);

        //初始化 - 底部弹出框布局
        View view = LayoutInflater.from(this).inflate(R.layout.hxxq_bottom_dilog, null);
        number = view.findViewById(R.id.di_iv_num);//数量
        priceCount = view.findViewById(R.id.count_money);//总价
        TextView hxxq_dilog_ok = view.findViewById(R.id.hxxq_dilog_ok);//确定按钮

        //库存
        stockTv = view.findViewById(R.id.stock);

        if (!TextUtil.isEmpty(stockStr)){
            stockTv.setText(stockStr);
        }

        if (index == 2 || index == 1) {
            if (!StringUtils.isEmpty(sellingPrice) && !sellingPrice.equals("null")){
                priceCount.setText(sellingPrice);
            }
        } else {
            if (!StringUtils.isEmpty(groAndKillPrice) && !groAndKillPrice.equals("null")){
                priceCount.setText(groAndKillPrice);
            }
        }



        if (!hasGG) {//如果没有规格，确认按钮可点击
            hxxq_dilog_ok.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            hxxq_dilog_ok.setClickable(true);
        } else {
            hxxq_dilog_ok.setClickable(false);
        }
        hxxq_dilog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFinish){
                    return;
                }
                //选完了规格
                if (hasGG && isGG || !hasGG) {

                    //购买数量
                    int stock = 1;

                    if (!StringUtils.isEmpty(number.getText().toString()) && !number.getText().toString().equals("null")){
                        stock = Integer.parseInt(number.getText().toString());
                    }

                    //选择规格后的库存
                    int setStocke = 0;
                    if(!TextUtil.isEmpty(stockTv.getText().toString())){
                        setStocke = Integer.parseInt(stockTv.getText().toString());
                    }
                    if(setStocke <1){
                        ToastUtil.showShort(HxxqActivity.this, "库存不足");
                        return;
                    }
                    if (stock > setStocke || stock < 1) {
                        ToastUtil.showShort(HxxqActivity.this, "库存不足");
                        return;
                    }
                    Map<String, String> map = new HashMap<>();
                    if (null == productInfo) {
                        //map.put("productId","");//没有规格的情况下
                    } else if (index != 0) {//收藏不需要产品id
                        map.put("productId", String.valueOf(selectView.getProductId()));//有规格的情况下
                    }
                    map.put("number", number.getText().toString());
                    //1加购，2单独购买，3拼团购买，4秒杀购买
                    if (index == 1) {
                        unifiedRequest(map, DyUrl.addToCart, index);
                    } else if (index == 2) {
                        //301直接购买，302发起拼团，303参加拼团
                        map.put("initiateType","301");
                        unifiedRequest(map, DyUrl.confirmGoods, index);
                    } else if (index == 3) {
                        //301直接购买，302发起拼团，303参加拼团
                        map.put("initiateType","302");
                        unifiedRequest(map, DyUrl.confirmGoods2groupbuy, index);
                    } else {//index == 4,秒杀
                        unifiedRequest(map, DyUrl.confirmGoods2seconds, index);
                    }
                    bottomInterPasswordDialog.dismiss();
                } else {
                    ToastUtil.showShort(HxxqActivity.this, "请选完规格");
                }
            }
        });


        //规格
        if (hasGG) {
            selectView = view.findViewById(R.id.v_home);//规格控件
            selectView.setData(gGobjects);
            selectView.setTextViewAndGGproject(showDialogType, number, priceCount, hxxq_dilog_ok, stockTv, productInfo);
        }

        //商品图片
        ImageView imageView = view.findViewById(R.id.hx_dilog_img);
        Glide.with(HxxqActivity.this).load(bottomDilogImgUrl).into(imageView);

        //加减控件
        TextView sub = view.findViewById(R.id.di_iv_sub);//减
        TextView add = view.findViewById(R.id.di_iv_add);//加
        sub.setOnClickListener(this);
        add.setOnClickListener(this);

        bottomInterPasswordDialog
                .contentView(view)/*加载视图*/
                /*.heightParam(height/2),显示的高度*/
                /*动画设置*/
                .inDuration(500)
                .outDuration(500)
                .inInterpolator(new BounceInterpolator())
                .outInterpolator(new AnticipateInterpolator())
                .cancelable(true)
                .show();
        bottomInterPasswordDialog.setOnDismissListener(this);
    }

    /**
     * 加减计算价钱
     *
     * @param index
     */
    private void setDate(int index) {
        Double ddd;
        //数量
        int shuliang = Integer.parseInt(number.getText().toString());

        if (productInfo != null) {//规格不等于空
            String data = selectView.getLastPrice();
            if (StringUtils.isEmpty(data) || data.equals("0.0")) {
                ddd = shuliang * Double.parseDouble(data);
            } else {
                String price = selectView.getLastPrice();
                double bbb = Double.parseDouble(price);
                ddd = shuliang * bbb;
            }
            priceCount.setText("" + String.format("%.2f", ddd));
        } else {
            if (showDialogType == 1 || showDialogType == 2) {
                ddd = shuliang * Double.parseDouble(sellingPrice);
            } else {
                ddd = shuliang * Double.parseDouble(groAndKillPrice);
            }

            priceCount.setText("" + String.format("%.2f", ddd));
        }


        IS_LOADED = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.END_TIME_FINISH");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isFinish = true;
            }
        };
        broadcastManager.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        HxxqActivity.isGG = false;
    }


    private void show() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("海风暴")
                .setMessage("该商品已下架")
                .setCancelable(false)
                .setIcon(R.mipmap.application)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create();
        alertDialog.show();


    }
}
