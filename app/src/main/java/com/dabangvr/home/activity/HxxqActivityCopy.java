package com.dabangvr.home.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.ViewPagerTransform;
import com.dabangvr.common.weight.onLoadMoreListener;
import com.dabangvr.home.weight.ShoppingSelectHome;
import com.dabangvr.model.Goods;
import com.dabangvr.model.goods.GoodsDetails;
import com.dabangvr.util.BottomImgSize;
import com.dabangvr.util.CountDownUtil;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoginTipsDialog;
import com.dabangvr.util.MyWebViewClient;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.TextUtil;
import com.dabangvr.util.ToastUtil;
import com.example.widget.RefreshListener;
import com.example.widget.RefreshScrollView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
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
import main.CommentMo;
import okhttp3.Call;

/**
 * 海鲜-商品详情页
 */
public class HxxqActivityCopy extends BaseActivity implements View.OnClickListener, RefreshListener {


    private GoodsDetails mData;//商品信息
    private List<CommentMo> mCommentData;//评论信息
    private ViewPagerTransform viewPager;
    private Bitmap bottomDialogImg;
    public static HxxqActivityCopy instants;

    //滑动
    private RefreshScrollView refreshScrollView;

    //规格控件
    private ShoppingSelectHome selectView;
    private TextView number;//底部弹窗的数量控件
    private boolean IS_LOADED = false;
    private TextView countPrice;

    //统一商品库存
    private String unifiedStok;

    //统一商品价钱
    private String unifiedPrice;

    //销售价
    private TextView tvPrice;

    //市场价
    private TextView tvMarketPrice;

    //库存
    private TextView tvStock;

    //商品标题
    private TextView tvGoodsName;

    //倒计时
    private TextView tvTime;
    //倒计时视图（用于普通商品隐藏，秒杀拼团商品显示）
    private LinearLayout llTimeView;

    //是否免邮费
    private TextView tvYfPrice;

    //商家地址
    private TextView tvDepAddress;

    //评论数量
    private TextView tvCommentCount;

    //销售量
    private TextView tvSalesNum;

    //评论控件(没有评论就隐藏)
    private LinearLayout llCommentView;

    //评论人头像
    private SimpleDraweeView sdCommentHead;

    //评论人名称
    private TextView tvCommentName;

    //评论时间
    private TextView tvCommentDate;

    //评论内容
    private TextView tvCommentContent;

    //评论的第一张图片
    private ImageView ivCommentImg1;

    //评论的第二张图片
    private ImageView ivCommentImg2;

    //评论的第三张图片
    private ImageView ivCommentImg3;

    //商家头像
    private ImageView ivDepHead;

    //商家名称
    private TextView tvDepName;

    //店铺销量
    private TextView tvDepSale;

    //商品详细的WebView
    private WebView webView;
    //webview视图
    private LinearLayout llWebView;

    //立即购买按钮
    private TextView tvBuy;

    //拼团/秒杀购买按钮
    private TextView tvBuyGroup;

    //其他推荐的商品列表
    private RecyclerView recyclerView;
    private List<Goods> ortherData = new ArrayList<>();
    private BaseLoadMoreHeaderAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_hxxq_copy;
    }

    @Override
    protected void initView() {
        instants = this;
        getIntent().getIntExtra("type", 0);

        //返回
        findViewById(R.id.backe).setOnClickListener(this);

        //滑动
        refreshScrollView = (RefreshScrollView) findViewById(R.id.hxxq_rsv);
        refreshScrollView.setListsner(this);
        //底部按钮
        //收藏
        RadioButton sc = findViewById(R.id.hx_sc);
        RadioButton server = findViewById(R.id.hx_server);
        RadioButton jg= findViewById(R.id.hx_jg);
        BottomImgSize bis = new BottomImgSize<>(this);
        bis.setImgSize(69,69,1,sc,R.drawable.hx_sc);
        bis.setImgSize(69,69,1,server,R.drawable.hx_server);
        bis.setImgSize(69,69,1,jg,R.drawable.hx_jg);

        //立即购买
        tvBuy = findViewById(R.id.hx_shop);
        tvBuy.setOnClickListener(this);

        //拼团购买
        tvBuyGroup = findViewById(R.id.hx_shop_pt);
        tvBuyGroup.setOnClickListener(this);

        //3D画廊
        viewPager = (ViewPagerTransform) findViewById(R.id.viewpager);
        viewPager.setPageMargin(20);


        //商品信息（名称）
        tvGoodsName = findViewById(R.id.hx_info);

        //销售价
        tvPrice = findViewById(R.id.hx_money_s);

        //市场价
        tvMarketPrice = findViewById(R.id.hx_money_y);

        //库存
        tvStock = findViewById(R.id.kucun);

        //销售量
        tvSalesNum = findViewById(R.id.sales_num);

        //倒计时
        tvTime = findViewById(R.id.hx_countdown_timer);
        llTimeView = findViewById(R.id.is_ms_or_pt);

        //是否免邮费
        tvYfPrice = findViewById(R.id.yf_price);

        //商家地址
        tvDepAddress = findViewById(R.id.dep_address);

        //评论数量
        tvCommentContent = findViewById(R.id.hx_comment_count);


        //点击查看所有评论的控件
        findViewById(R.id.hx_see_comment).setOnClickListener(this);

        //评论控件(没有评论就隐藏)
        llCommentView = findViewById(R.id.comment_view);

        //评论人头像
        sdCommentHead = findViewById(R.id.hx_lv_icon);

        //评论人名称
        //tvCommentName = findViewById(R.id.hx_lv_name);

        //评论时间
        tvCommentDate = findViewById(R.id.comment_date);

        //评论内容
        tvCommentContent = findViewById(R.id.hx_lv_content);

        //评论的第一张图片
        ivCommentImg1 = findViewById(R.id.com_img1);

        //评论的第二张图片
        ivCommentImg2 = findViewById(R.id.com_img2);

        //评论的第三张图片
        ivCommentImg3 = findViewById(R.id.com_img3);


        //商家头像
        ivDepHead = findViewById(R.id.dep_head);

        //商家名称
        tvDepName = findViewById(R.id.dep_name);

        //进店点击跳转
        findViewById(R.id.go_dep).setOnClickListener(this);

        //店铺销量
        tvDepSale = findViewById(R.id.dep_sale);

        //商品详细的WebView
        webView = findViewById(R.id.hx_bom_web_view);
        //装载webview的视图（用途隐藏或显示）
        llWebView = findViewById(R.id.hxxq_web_view);

        //其他推荐的商品列表
        recyclerView = findViewById(R.id.orther_recy);

        GridLayoutManager manager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(manager);
        adapter = new BaseLoadMoreHeaderAdapter<Goods>(this, recyclerView, ortherData, R.layout.new_release_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, Goods o) {
                holder.setImageByUrl(R.id.new_item_img, o.getListUrl());
                holder.setText(R.id.new_item_msg, o.getName());
                holder.setText(R.id.new_item_salse, o.getSellingPrice());
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new onLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {

            }
        });


    }

    //图片轮播适配
    private void setViewPagerAdapter(final List<GoodsDetails.ImgList> imgAndVideo) {

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return imgAndVideo.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                View view = null;

                String img = imgAndVideo.get(position).getChartUrl();
                if (!StringUtils.isEmpty(img)) {
                    view = View.inflate(container.getContext(), R.layout.img_fragment, null);
                    final ImageView iv = (ImageView) view.findViewById(R.id.hxxq_img);
                    //iv.setImageResource(position % 2 == 0 ? R.mipmap.app_lunch : R.mipmap.application);
                    //Glide.with(container.getContext()).load(img).into(iv);
                    Glide.with(container.getContext()).asBitmap()
                            .load(img)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap bitmap,
                                                            @Nullable Transition<? super Bitmap> transition) {

                                    if (null != bitmap) {
                                        if (position == 0) {
                                            bottomDialogImg = bitmap;
                                        }
                                        iv.setImageBitmap(bitmap);
                                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewPager.getLayoutParams();
                                        if (bitmap.getHeight() > viewPager.getHeight()) {
                                            lp.height = bitmap.getHeight();
                                        }
                                        if (bitmap.getWidth() > viewPager.getWidth()) {
                                            lp.weight = bitmap.getWidth();
                                        }
                                        lp.rightMargin = 100;
                                        lp.leftMargin = 100;
                                        viewPager.setLayoutParams(lp);
                                    }
                                }
                            });
                    container.addView(view);
                }
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
    }

    /**
     * 获取商品信息
     */
    @Override
    protected void initData() {
        String id = getIntent().getStringExtra("id");
        HashMap<String, String> map = new HashMap<>();
        map.put("goodsId", id);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoodsDetails, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            //主线程处理
            @Override
            public void onUi(String newsBean) {
                if (StringUtils.isEmpty(newsBean)) {
                    ToastUtil.showShort(HxxqActivityCopy.this, "获取数据失败");
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(newsBean);
                    int err = object.optInt("errno");
                    if (err == 0) {
                        if (object.optInt("code") == 500) {
                            return ;
                        }
                        JSONObject object1 = object.optJSONObject("data");
                        String str = object1.optString("goodsDetails");
                        Gson gson = new Gson();
                        mData = gson.fromJson(str, GoodsDetails.class);

                        //评论信息
                        String comStr = object1.optString("commentVoList");
                        mCommentData = JsonUtil.string2Obj(comStr, List.class, CommentMo.class);

                        //设置轮播
                        setViewPagerAdapter(mData.getImgList());

                        //设置商品详细信息
                        setGoodsData();
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

    private void setGoodsData() {
        int type = getIntent().getIntExtra("type", 0);
        //-----------------------分类信息----------0普通，1拼团，2秒杀------------
        switch (type) {
            case 0:
                tvBuy.setText("立即购买");
                tvBuyGroup.setVisibility(View.GONE);//隐藏拼团购买按钮
                llTimeView.setVisibility(View.GONE);//隐藏倒计时视图


                unifiedStok = mData.getRemainingInventory();
                unifiedPrice = mData.getSellingPrice();
                tvPrice.setText(unifiedPrice);//普通价钱
                tvStock.setText(unifiedStok);//普通商品库存
                break;
            case 1:
                tvBuy.setText("单独购买");
                tvBuyGroup.setText("拼团");
                tvBuyGroup.setVisibility(View.VISIBLE);//显示拼团购买按钮
                llTimeView.setVisibility(View.VISIBLE);//显示倒计时视图

                unifiedPrice = mData.getGroupPrice();
                unifiedStok = mData.getRemainingGroupNumber();
                tvPrice.setText(mData.getGroupPrice());//团购价
                tvStock.setText(unifiedStok);//团购商品库存
                //团购倒计时
                dowTime(mData.getEndTime());
                break;
            case 2:
                tvBuy.setVisibility(View.GONE);
                tvBuyGroup.setText("秒杀");
                tvBuyGroup.setVisibility(View.VISIBLE);//显示秒杀按钮
                llTimeView.setVisibility(View.VISIBLE);//显示倒计时视图

                unifiedPrice = mData.getSecondsPrice();
                unifiedStok = mData.getRemainingSecondsNumber();
                tvPrice.setText(unifiedPrice);//秒杀价
                tvStock.setText(unifiedStok);//秒杀商品库存
                //秒杀倒计时
                dowTime(mData.getSecondsEndTime());
                break;
        }

        //---------------------公共的信息---------------
        //商品名称（信息）
        tvGoodsName.setText(mData.getName());
        //商品市场价
        tvMarketPrice.setText(mData.getMarketPrice());
        //商家头像
        Glide.with(this).load(mData.getDeptLogo()).into(ivDepHead);
        //商家名称
        tvDepName.setText(mData.getDeptName());
        //商家销量
        //----

        //商品评论数量
        //tvCommentContent.setText(mCommentData.size());
        //评论人头像
        //sdCommentHead.setImageURI(mCommentData.get(0).getHead());
        //评论人昵称
        //tvCommentName.setText(mCommentData.get(0).getName());
        //评论内容
        //tvCommentContent.setText(mCommentData.get(0).getMess());
        //评论时间
        //tvCommentDate.setText(mCommentData.get(0).getDate());

        //评论携带的图片
//        List<CommentMo.ImgList> comList= mCommentData.get(0).getListUrl();
//        for (int i=0;i<comList.size();i++){
//
//            if (i==0){
//                //评论第一张图
//                Glide.with(this).load(comList.get(i)).into(ivCommentImg1);
//            }else if (i==1){
//                //评论第二张图
//                Glide.with(this).load(comList.get(i)).into(ivCommentImg2);
//            }else {
//                //评论第三张图
//                Glide.with(this).load(comList.get(i)).into(ivCommentImg3);
//            }
//
//
//        }

        //详细页面列表
        if (!TextUtil.isEmpty(mData.getGoodsDesc())) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存
            webView.getSettings().setUserAgentString(System.getProperty("http.agent"));
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把html中的内容放大webview等宽的一列中
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.setWebViewClient(new MyWebViewClient(webView));
            webView.loadData(mData.getGoodsDesc(), "text/html", "UTF-8");
            llWebView.setVisibility(View.VISIBLE);
        }
    }

    //团购/秒杀倒计时
    private void dowTime(String data) {
        CountDownUtil downUtil = new CountDownUtil();
        downUtil.start(Long.parseLong(data), new CountDownUtil.OnCountDownCallBack() {
            @Override
            public void onProcess(int day, int hour, int minute, int second) {
                tvTime.setText(day + "天 " + hour + "时 " + minute + "分 " + second + "秒");
            }

            @Override
            public void onFinish() {
                tvTime.setText("活动已结束");
            }
        });
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
//                if (TextUtil.isEmpty(stockStr) || TextUtil.isEmpty(sellingPrice)){
//                    ToastUtil.showShort(HxxqActivityCopy.this,"该产品已下架");
//                    return;
//                }
                showBottomDialog(2);
                break;
            }
            case R.id.hx_shop_pt: {//拼团购买,秒杀购买
//                if(isFinish){
//                    show();
//                    return;
//                }
//                if (buyType == 1) {
//                    showBottomDialog(3);
//                }
//                if (buyType == 2) {
//                    showBottomDialog(4);
//                }

                break;
            }
            case R.id.hx_sc: {//收藏
//                if (TextUtil.isEmpty(stockStr) || TextUtil.isEmpty(sellingPrice)){
//                    ToastUtil.showShort(HxxqActivityCopy.this,"该产品已下架");
//                    return;
//                }
//                if(isFinish){
//                    return;
//                }
                Map<String, String> map = new HashMap<>();
                unifiedRequest(map, DyUrl.getGoodsCollectSave, 0);
                break;
            }
            case R.id.hx_server: {//客服
                break;
            }
            case R.id.hx_jg: {//加购
//                if (TextUtil.isEmpty(stockStr) || TextUtil.isEmpty(sellingPrice)){
//                    ToastUtil.showShort(HxxqActivityCopy.this,"该产品已下架");
//                    return;
//                }
//                showBottomDialog(1);
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

    private void sendMessage(int index) {
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putInt("id", index);
        message.setData(bundle);
        message.sendToTarget();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (!IS_LOADED) {
                IS_LOADED = true;
                setDate();
            }
            return false;
        }
    });

    /**
     * 加减计算价钱
     *
     */
    private void setDate() {
        Double ddd;
        //数量
        int shuliang = Integer.parseInt(number.getText().toString());

        if (mData.getProductInfoList() != null) {//规格不等于空
            String data = selectView.getLastPrice();
            if (StringUtils.isEmpty(data) || data.equals("0.0")) {
                ddd = shuliang * Double.parseDouble(data);
            } else {
                String price = selectView.getLastPrice();
                double bbb = Double.parseDouble(price);
                ddd = shuliang * bbb;
            }
        }else {
            ddd = shuliang*Double.parseDouble(unifiedPrice);
        }
        countPrice.setText("" + String.format("%.2f", ddd));
        IS_LOADED = false;
    }

    /**
     * 收藏、加购、立即购买统一请求
     *
     * @param map 请求参数
     * @param url 请求地址
     */
    private void unifiedRequest(Map<String, String> map, String url, final int requestType) {
        String token = getSPKEY(this, "token");
        if (StringUtils.isEmpty(token)) {
            LoginTipsDialog.ortehrTips(this, "您未登陆哦，赶紧成为我们的会员吧");
            return;
        }
        map.put(DyUrl.TOKEN_NAME, token);
        map.put("goodsId", mData.getGoodsId());
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(url, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (500 == object.optInt("code")) {
                        ToastUtil.showShort(HxxqActivityCopy.this, "服务异常");
                        return ;
                    }
                    int err = object.optInt("errno");
                    if (err == 0) {

                        //0收藏，1加购，2单独购买，3拼团购买，4秒杀购买
                        if (requestType == 0) {//收藏请求
                            ToastUtil.showShort(HxxqActivityCopy.this, "收藏成功");
                            return ;
                        }
                        if (requestType == 1) {//加入购物车
                            ToastUtil.showShort(HxxqActivityCopy.this, "加购成功");
                        }

                        if (requestType == 2) {//确认订单
                            Intent intent = new Intent(HxxqActivityCopy.this, OrderActivity.class);
                            intent.putExtra("type", "buy");
                            intent.putExtra("payOrderSnType", "orderSnTotal");
                            intent.putExtra("dropType", 1);
                            startActivity(intent);
                        }
                        if (requestType == 3) {//拼团确认订单
                            Intent intent = new Intent(HxxqActivityCopy.this, OrderActivity.class);
                            intent.putExtra("type", "group2");
                            intent.putExtra("payOrderSnType", "orderSnTotal");
                            intent.putExtra("dropType", 2);
                            startActivity(intent);
                        }
                        if (requestType == 4) {//秒杀确认订单
                            Intent intent = new Intent(HxxqActivityCopy.this, OrderActivity.class);
                            intent.putExtra("type", "seconds");
                            intent.putExtra("payOrderSnType", "orderSnTotal");
                            intent.putExtra("dropType", 3);
                            startActivity(intent);
                        }

                    } else {
                        ToastUtil.showShort(HxxqActivityCopy.this, "服务错误" + object.optString("errmsg"));
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
     * 底部弹窗
     *
     * @param index 1加购，2单独购买，3拼团购买，4秒杀购买
     */
    private void showBottomDialog(final int index) {

        //初始化底部弹窗
        BottomSheetDialog bottomInterPasswordDialog = new BottomSheetDialog(this);

        //初始化 - 底部弹出框布局
        View view = LayoutInflater.from(this).inflate(R.layout.hxxq_bottom_dilog, null);

        //商品图片
        ImageView imageView = view.findViewById(R.id.hx_dilog_img);
        if (null != bottomDialogImg) {
            imageView.setImageBitmap(bottomDialogImg);
        } else {
            Glide.with(HxxqActivityCopy.this).load(mData.getListUrl()).into(imageView);
        }
        //库存
        TextView tvStok = view.findViewById(R.id.stock);
        tvStok.setText(unifiedStok);

        //价钱
        countPrice = view.findViewById(R.id.count_money);
        countPrice.setText(unifiedPrice);

        //加减控件
        TextView sub = view.findViewById(R.id.di_iv_sub);//减
        TextView add = view.findViewById(R.id.di_iv_add);//加

        //数量
        number = view.findViewById(R.id.di_iv_num);//数量
        sub.setOnClickListener(this);
        add.setOnClickListener(this);

        //确定按钮
        TextView hxxq_dilog_ok = view.findViewById(R.id.hxxq_dilog_ok);

        //规格信息(如果存在规格)
        if (null != mData.getSpecList() && mData.getSpecList().size() > 0) {
            selectView = view.findViewById(R.id.v_home);//规格控件
            selectView.setData(mData.getSpecList());//规格数组
            selectView.setTextViewAndGGproject(index, number, countPrice, hxxq_dilog_ok, tvStok, mData.getProductInfoList());
            hxxq_dilog_ok.setClickable(false);
        } else {
            hxxq_dilog_ok.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            hxxq_dilog_ok.setClickable(true);
        }

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null !=viewPager){
            viewPager.destroyDrawingCache();
        }
    }

    @Override
    public void startRefresh() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void hintChange(String hint) {

    }

    @Override
    public void setWidthX(int x) {

    }
}
