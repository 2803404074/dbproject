package com.dabangvr.home.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.dep.activity.DepMessActivity;
import com.dabangvr.home.fragment.hxxq.CommentView;
import com.dabangvr.home.fragment.hxxq.DeatilsView;
import com.dabangvr.home.fragment.hxxq.GoodsView;
import com.dabangvr.home.fragment.hxxq.TopMessView;
import com.dabangvr.home.weight.ShoppingSelectHome;
import com.dabangvr.model.Goods;
import com.dabangvr.model.GoodsComment;
import com.dabangvr.model.goods.GoodsDetails;
import com.dabangvr.util.BottomImgSize;
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.GlideRoundedCornersTransform;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoginTipsDialog;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.widget.GridDividerItemDecoration;
import com.example.hxxq.CustomScrollView;
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
import Utils.PdUtil;
import config.DyUrl;
import okhttp3.Call;

/**
 * 海鲜-商品详情页
 */
public class HxxqLastActivity extends BaseActivity implements View.OnClickListener, TopMessView.TimeCall {

    private int type;//页面类型，0普通，1拼团，2秒杀

    private String jfNum;//商品积分量

    private GoodsDetails mData;//商品信息
    private List<GoodsComment> commentMoList;

    private PdUtil pdUtil;
    public static Bitmap bottomDialogImg;
    public static HxxqLastActivity instants;
    private BottomSheetDialog bottomInterPasswordDialog;

    private GoodsView goodsView;
    //规格控件
    private ShoppingSelectHome selectView;
    private TextView number;//底部弹窗的数量控件


    private TextView countPrice;

    //立即购买按钮
    private TextView tvBuy;

    //拼团/秒杀购买按钮
    private TextView tvBuyGroup;

    //底部弹窗 的库存
    private TextView bottomTvStok;

    //统一商品库存
    public static String unifiedStok;

    //统一商品价钱
    public static String unifiedPrice;


    //其他商品
    private RecyclerView goodsRecy;
    private BaseLoadMoreHeaderAdapter goodsAdapter;
    private List<Goods> goodsData = new ArrayList<>();
    private int page;

    //底部按钮
    private CheckBox CbCollection;
    private CheckBox sc;
    private RadioButton jg;

    /**
     * 占位tablayout，用于滑动过程中去确定实际的tablayout的位置
     */
    //private TabLayout holderTabLayout;
    /**
     * 实际操作的tablayout，
     */
    //private LinearLayout llTop;
    private TabLayout realTabLayout;
    private CustomScrollView scrollView;
    private LinearLayout container;
    private String[] tabTxt = {"商品", "评论", "详情", "推荐"};

    private List<LinearLayout> anchorList = new ArrayList<>();

    //判读是否是scrollview主动引起的滑动，true-是，false-否，由tablayout引起的
    private boolean isScroll;
    //记录上一次位置，防止在同一内容块里滑动 重复定位到tablayout
    private int lastPos = 0;
    //监听判断最后一个模块的高度，不满一屏时让最后一个模块撑满屏幕
    private ViewTreeObserver.OnGlobalLayoutListener listener;
    private View sj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_hxxq_last;
    }


    @Override
    protected void initView() {
        type = getIntent().getIntExtra("type", 0);
        pdUtil = new PdUtil(this);
        pdUtil.showLoding("正在加载");
        realTabLayout = findViewById(R.id.tablayout_real);
        scrollView = findViewById(R.id.scrollView);
        container = findViewById(R.id.container);
        instants = this;

        //返回
        findViewById(R.id.backe).setOnClickListener(this);

        //底部按钮
        CbCollection = (CheckBox) findViewById(R.id.cb_collection_id);
        sc = findViewById(R.id.hx_sc);
        CbCollection.setOnClickListener(this);
        sc.setOnClickListener(this);
        jg = findViewById(R.id.hx_jg);
        jg.setOnClickListener(this);
        if (type != 0) {
            jg.setVisibility(View.GONE);
        }

        BottomImgSize bis = new BottomImgSize<>(this);
        bis.setImgSize(64, 64, 1, CbCollection, R.drawable.shop_selector);
        bis.setImgSize(64, 64, 1, sc, R.drawable.hxxq_sc_bottom);
//        bis.setImgSize(69, 69, 1, jg, R.drawable.hx_jg);

        //立即购买
        tvBuy = findViewById(R.id.hx_shop);
        tvBuy.setOnClickListener(this);

        //拼团购买
        tvBuyGroup = findViewById(R.id.hx_shop_pt);
        tvBuyGroup.setOnClickListener(this);


        goodsRecy = findViewById(R.id.orther_recy);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        goodsRecy.addItemDecoration(new GridDividerItemDecoration(DensityUtil.dip2px(this, 7), ContextCompat.getColor(this, R.color.white)));
        goodsRecy.setLayoutManager(manager);
        goodsRecy.setNestedScrollingEnabled(false);
        goodsAdapter = new BaseLoadMoreHeaderAdapter<Goods>(this, goodsRecy, goodsData, R.layout.new_release_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, Goods o) {
                holder.setImageByUrl(R.id.new_item_img, o.getListUrl(), GlideRoundedCornersTransform.CornerType.TOP,6f);
                holder.setText(R.id.new_item_msg, o.getName());
                holder.setText(R.id.new_item_salse, o.getSellingPrice());
            }
        };
        goodsRecy.setAdapter(goodsAdapter);

        goodsAdapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(HxxqLastActivity.this, HxxqLastActivity.class);
                Goods goods = (Goods) goodsAdapter.getData().get(position);
                intent.putExtra("id", goods.getId());
                intent.putExtra("type", type);//商品详细页面，1代表拼团类型
                intent.putExtra("assembleId", goods.getAssembleId());
                startActivity(intent);
                finish();
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
            case R.id.hx_shop: {//0立即购买,2单独购买
                if (type == 0) {
                    showBottomDialog(type);
                } else {
                    showBottomDialog(2);
                }
                break;
            }
            case R.id.hx_shop_pt: {//拼团购买,秒杀购买
                if (type == 1) {
                    showBottomDialog(3);
                }
                if (type == 2) {
                    showBottomDialog(4);
                }
                break;
            }
            case R.id.hx_sc: {//收藏
                String token = getSPKEY(this, "token");
                if (StringUtils.isEmpty(token)) {
                    ToastUtil.showShort(this, "登录后才能收藏哦");
                    sc.setChecked(false);
                    return;
                }
                Map<String, String> map = new HashMap<>();
                map.put("goodsId", mData.getId());
                unifiedRequest(map, DyUrl.getGoodsCollectSave, 100);
                break;
            }
            case R.id.hx_jg: {//加购
                showBottomDialog(1);
                break;
            }
            case R.id.cb_collection_id:
                Intent intent = new Intent(this, DepMessActivity.class);
                intent.putExtra("depId", mData.getDeptId());
                this.startActivity(intent);
                break;
            case R.id.di_iv_sub: {//减
                String size = number.getText().toString();
                if (size.equals("1")) {
                    return;
                }
                int num = Integer.parseInt(size) - 1;
                number.setText(String.valueOf(num));
                //sendMessage(1);
                break;
            }
            case R.id.di_iv_add: {//加
                if (Integer.parseInt(number.getText().toString()) > Integer.parseInt(bottomTvStok.getText().toString())) {
                    ToastUtil.showShort(HxxqLastActivity.this, "库存已经没有那么多啦，试试其他的规格吧");
                    return;
                }
                String size = number.getText().toString();
                int num = Integer.parseInt(size) + 1;
                number.setText(String.valueOf(num));
                //sendMessage(0);
                break;
            }
            default:
                break;
        }
    }

    /**
     * 收藏、加购、立即购买统一请求
     *
     * @param map 请求参数
     * @param url 请求地址
     */
    private void unifiedRequest(Map<String, String> map, String url, final int requestType) {
        map.put(DyUrl.TOKEN_NAME, getSPKEY(HxxqLastActivity.this, "token"));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(url, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                pdUtil.desLoding();
                try {

                    JSONObject object = new JSONObject(result);
                    if (500 == object.optInt("code")) {
                        ToastUtil.showShort(HxxqLastActivity.this, "服务异常");

                    }
                    int err = object.optInt("errno");
                    if (err == 0) {
                        if (500 == object.optInt("code")) {
                            ToastUtil.showShort(HxxqLastActivity.this, "获取失败");
                            return;
                        }

                        //0立即购买,，1加购，2单独购买，3拼团购买，4秒杀购买,100收藏
                        if (requestType == 100) {//收藏请求
                            ToastUtil.showShort(HxxqLastActivity.this, "收藏成功");
                        }

                        if (requestType == 0) {//立即购买请求
                            Intent intent = new Intent(HxxqLastActivity.this, OrderActivity.class);
                            intent.putExtra("type", "buy");
                            intent.putExtra("payOrderSnType", "orderSnTotal");
                            intent.putExtra("dropType", 1);
                            startActivity(intent);
                        }

                        if (requestType == 1) {//加入购物车
                            ToastUtil.showShort(HxxqLastActivity.this, "加购成功");
                        }

                        if (requestType == 2) {//单独购买
                            Intent intent = new Intent(HxxqLastActivity.this, OrderActivity.class);
                            intent.putExtra("type", "buy");
                            intent.putExtra("payOrderSnType", "orderSnTotal");
                            intent.putExtra("dropType", 1);
                            startActivity(intent);
                        }
                        if (requestType == 3) {//拼团购买
                            Intent intent = new Intent(HxxqLastActivity.this, OrderActivity.class);
                            intent.putExtra("type", "group2");
                            intent.putExtra("payOrderSnType", "orderSnTotal");
                            intent.putExtra("dropType", 2);
                            startActivity(intent);
                        }
                        if (requestType == 4) {//秒杀购买
                            Intent intent = new Intent(HxxqLastActivity.this, OrderActivity.class);
                            intent.putExtra("type", "seconds");
                            intent.putExtra("payOrderSnType", "orderSnTotal");
                            intent.putExtra("dropType", 3);
                            startActivity(intent);
                        }

                    } else {
                        ToastUtil.showShort(HxxqLastActivity.this, object.optString("errmsg"));
                    }

                    if (null != bottomInterPasswordDialog) {
                        bottomInterPasswordDialog.dismiss();
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
     * @param index 0立即购买,1加购，2单独购买，3拼团购买，4秒杀购买
     */
    private void showBottomDialog(final int index) {

        //初始化底部弹窗
        if (null == bottomInterPasswordDialog) {
            bottomInterPasswordDialog = new BottomSheetDialog(this);

        }
        //初始化 - 底部弹出框布局
        View view = LayoutInflater.from(this).inflate(R.layout.hxxq_bottom_dilog, null);

        //商品图片
        ImageView imageView = view.findViewById(R.id.hx_dilog_img);
        if (null != bottomDialogImg) {
            imageView.setImageBitmap(bottomDialogImg);
        } else {
            Glide.with(HxxqLastActivity.this).load(mData.getListUrl()).into(imageView);
        }
        //库存
        bottomTvStok = view.findViewById(R.id.stock);
        bottomTvStok.setText(unifiedStok);

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
        hxxq_dilog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<>();
                map.put(DyUrl.TOKEN_NAME, getSPKEY(HxxqLastActivity.this, "token"));
                map.put("goodsId", mData.getId());
                map.put("number", number.getText().toString());

                if (null != mData.getProductInfoList()) {
                    if (null != selectView && mData.getProductInfoList().size() > 0) {
                        map.put("productId", String.valueOf(selectView.getProductId()));
                    }
                }
                //0立即购买，1加购，2单独购买，3拼团购买，4秒杀购买
                if (index == 0) {
                    pdUtil.showLoding("获取订单");
                    unifiedRequest(map, DyUrl.confirmGoods, index);
                } else if (index == 1) {
                    unifiedRequest(map, DyUrl.addToCart, index);
                } else if (index == 2) {
                    //301直接购买，302发起拼团，303参加拼团
                    map.put("initiateType", "301");
                    pdUtil.showLoding("获取订单");
                    unifiedRequest(map, DyUrl.confirmGoods2groupbuy, index);
                } else if (index == 3) {
                    //301直接购买，302发起拼团，303参加拼团
                    pdUtil.showLoding("获取拼团订单");
                    map.put("initiateType", "302");
                    unifiedRequest(map, DyUrl.confirmGoods2groupbuy, index);
                } else {//index == 4,秒杀
                    pdUtil.showLoding("获取秒杀订单");
                    unifiedRequest(map, DyUrl.confirmGoods2seconds, index);
                }
            }
        });

        //规格信息(如果存在规格)
        if (null != mData.getSpecList() && mData.getSpecList().size() > 0) {

            selectView = view.findViewById(R.id.v_home);//规格控件
            selectView.setData(mData.getSpecList());//规格数组
            selectView.setTextViewAndGGproject(index, number, countPrice, hxxq_dilog_ok, bottomTvStok, mData.getProductInfoList());
            if (selectView.getHasSpe()) {
                hxxq_dilog_ok.setBackgroundColor(getResources().getColor(R.color.colorAccentNo));
                hxxq_dilog_ok.setClickable(false);
            } else {
                hxxq_dilog_ok.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                hxxq_dilog_ok.setClickable(true);
            }
        } else {
            hxxq_dilog_ok.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            hxxq_dilog_ok.setClickable(true);
        }

        bottomInterPasswordDialog
                .contentView(view)/*加载视图*/
                /*.heightParam(height/2),显示的高度*/
                /*动画设置*/
                .inDuration(200)
                .outDuration(200)
                /*.inInterpolator(new BounceInterpolator())
                .outInterpolator(new AnticipateInterpolator())*/
                .cancelable(true)
                .show();
        bottomInterPasswordDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomInterPasswordDialog = null;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bottomInterPasswordDialog != null) bottomInterPasswordDialog = null;
        if (selectView != null) selectView = null;
    }

    /**
     * 购买按钮 以及初始化库存和价钱信息
     */
    private void setGoodsData() {
        if (type == 1 || type == 2) {
            jg.setClickable(false);
        }
        //-----------------------分类信息----------0普通，1拼团，2秒杀------------
        switch (type) {
            case 0:
                tvBuy.setVisibility(View.VISIBLE);
                tvBuyGroup.setVisibility(View.GONE);
                tvBuy.setText("立即购买");
                unifiedStok = mData.getRemainingInventory();
                unifiedPrice = mData.getSellingPrice();
                break;
            case 1:
                tvBuy.setText("单独购买");
                tvBuyGroup.setText("拼团");
                tvBuy.setVisibility(View.VISIBLE);
                tvBuyGroup.setVisibility(View.VISIBLE);//显示拼团购买按钮

                unifiedStok = mData.getRemainingGroupNumber();

                //如果没有团购价，则默认普通价
                if (null == mData.getGroupPrice() || mData.getGroupPrice().equals("null")) {
                    unifiedPrice = mData.getSellingPrice();
                } else {
                    unifiedPrice = mData.getSecondsPrice();
                }
                break;
            case 2:
                tvBuy.setVisibility(View.GONE);
                tvBuyGroup.setText("秒杀");
                tvBuyGroup.setVisibility(View.VISIBLE);//显示秒杀按钮

                //如果没有秒杀价，则默认普通价
                if (null == mData.getSecondsPrice() || mData.getSecondsPrice().equals("null") || Float.valueOf(mData.getSecondsPrice()) <= 0) {
                    unifiedPrice = mData.getSellingPrice();
                } else {
                    unifiedPrice = mData.getSecondsPrice();
                }
                unifiedStok = mData.getRemainingSecondsNumber();
                break;
        }

        //已收藏标志
        if (!StringUtils.isEmpty(mData.getCollectTag()) && !mData.getCollectTag().equals("null") && mData.getCollectTag().equals("1")) {
            sc.setChecked(true);
        }


        pdUtil.desLoding();
    }

    /**
     * 评论信息，详细信息。其他商品推荐
     */
    // TODO: 2019/9/9 商品詳情添加視圖列表
    private void setAdapter() {
        int type = getIntent().getIntExtra("type", 0);
        if (type == 1 || type == 2) {
            jg.setClickable(false);
        }
        //商品基本信息视图
        TopMessView topMessView = new TopMessView(this, this);
        topMessView.setMess(type, mData);
        topMessView.setJf(jfNum);
        //topMessView.setCall(this);

        //评论视图
        CommentView commentView = new CommentView(this);
        commentView.setGoodsId(mData.getId());
        commentView.setSaleNum(mData.getSalesVolume());
        if (null != commentMoList && commentMoList.size() > 0) {
            commentView.setViewShow(true);
            commentView.setView(commentMoList.get(0));
            commentView.setCommentNum(commentMoList.get(0).getCommentSize());
        } else {
            commentView.setViewShow(false);
        }
        //商品详细信息视图
        DeatilsView deatilsView = new DeatilsView(this);
        deatilsView.setDepId(mData.getDeptId());
        deatilsView.setIvDepHead(mData.getDeptLogo());
        deatilsView.setTvDepName(mData.getDeptName());
        //deatilsView.setTvDepSale(mData.getDepSalseNum());
        deatilsView.setWebView(mData.getGoodsDesc());

        //推荐视图
        goodsView = new GoodsView(this);

        anchorList.add(topMessView);
        anchorList.add(commentView);
        anchorList.add(deatilsView);
        anchorList.add(goodsView);

        container.addView(topMessView);
        container.addView(commentView);
        container.addView(deatilsView);
        container.addView(goodsView);

        for (int i = 0; i < tabTxt.length; i++) {
            realTabLayout.addTab(realTabLayout.newTab().setText(tabTxt[i]));
        }
        listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                container.getViewTreeObserver().removeOnGlobalLayoutListener(listener);

            }
        };
        container.getViewTreeObserver().addOnGlobalLayoutListener(listener);


        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isScroll = true;

                }
                return false;
            }
        });

        realTabLayout.getBackground().mutate().setAlpha(120);
        realTabLayout.setVisibility(View.GONE);

        //监听scrollview滑动
        scrollView.setCallbacks(new CustomScrollView.Callbacks() {
            @Override
            public void onScrollChanged(int x, int y, int oldx, int oldy) {
                int translation = Math.max(y, realTabLayout.getTop());
                if (isScroll) {
                    for (int i = tabTxt.length - 1; i >= 0; i--) {
                        //需要y减去顶部内容区域的高度(具体看项目的高度，这里demo写死的200dp)
                        if (y > anchorList.get(i).getTop() - 100) {
                            setScrollPos(i);
                            break;
                        }
                    }
                }
                if (y < 250 && y > 120) {
                    //.mutate()方法不会通知其他控件跟着改变background
                    realTabLayout.getBackground().mutate().setAlpha(y);
                    realTabLayout.setVisibility(View.VISIBLE);
                } else if (y < 120) {
                    realTabLayout.setVisibility(View.GONE);
                }
            }
        });
        scrollView.setBottomCallbacks(new CustomScrollView.CallbacksBottom() {
            @Override
            public void loadMore() {
                setOrtherData(true);
            }
        });

        //实际的tablayout的点击切换
        realTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isScroll = false;
                int pos = tab.getPosition();
                int top = anchorList.get(pos).getTop();
                //同样这里滑动要加上顶部内容区域的高度(这里写死的高度)
                scrollView.smoothScrollTo(0, top);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        setOrtherData(false);
    }

    private void setScrollViewGoTop() {
        scrollView.fullScroll(View.FOCUS_UP);
    }

    @Override
    protected void initData() {
        String id = getIntent().getStringExtra("id");
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this, "token"));
        map.put("goodsId", id);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoodsDetails, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            //主线程处理
            @Override
            public void onUi(String newsBean) {
                if (StringUtils.isEmpty(newsBean)) {
                    ToastUtil.showShort(HxxqLastActivity.this, "获取数据失败");
                }
                try {
                    JSONObject object = new JSONObject(newsBean);
                    int err = object.optInt("errno");
                    if (1 == err) {
                        LoginTipsDialog.finishTips(HxxqLastActivity.this, "该产品已下架");
                    }
                    if (err == 0) {
                        if (object.optInt("code") == 500) {
                            finish();
                            return;
                        }
                        JSONObject object1 = object.optJSONObject("data");

                        //获取商品积分
                        jfNum = object1.optString("integral");

                        String str = object1.optString("goodsDetails");
                        Gson gson = new Gson();
                        mData = gson.fromJson(str, GoodsDetails.class);

                        String commentVoList = object1.optString("commentVoList");

                        commentMoList = JsonUtil.string2Obj(commentVoList, List.class, GoodsComment.class);

                        //设置商品详细信息
                        if (null != mData) {
                            setGoodsData();
                            //设置滑动
                            setAdapter();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //请求失败
            @Override
            public void onFailed(Call call, IOException e) {
                sc.setClickable(false);
                jg.setClickable(false);
            }


            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                if (pdUtil != null) {
                    pdUtil.desLoding();
                }
                Looper.prepare();
                ToastUtil.showShort(HxxqLastActivity.this, "电波无法到达，请检查您的网络~~");
                Looper.loop();
                sc.setClickable(false);
                jg.setClickable(false);
            }
        });
    }

    private void setScrollPos(int newPos) {
        if (lastPos != newPos) {
            realTabLayout.setScrollPosition(newPos, 0, true);
        }
        lastPos = newPos;
    }


    public void setOrtherData(final boolean isLoad) {
        if (isLoad) {
            page++;
        } else {
            page = 1;
        }
        Map<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(page));
        map.put("categoryId", mData.getCategoryId());
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoodsList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (0 == errno) {
                        if (object.optInt("code") == 500) return;
                        JSONObject object1 = object.optJSONObject("data");
                        String str = object1.optString("goodsList");
                        goodsData = JsonUtil.string2Obj(str, List.class, Goods.class);
                        if (isLoad) {
                            if (goodsData.size() > 0) {
                                goodsAdapter.addAll(goodsData);
                            }
                        } else {
                            if (goodsData.size() > 0) {
                                goodsAdapter.updateData(goodsData);
                            }
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
     * 秒杀或拼团是否已经结束
     *
     * @param b
     */
    @Override
    public void isEnd(boolean b) {
        if (b) {
            tvBuyGroup.setClickable(false);
            tvBuyGroup.setBackgroundResource(R.color.colorGray2);
            tvBuyGroup.setText("活动已结束");
            sc.setClickable(false);
            jg.setClickable(false);
        }
    }
}
