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
import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.dep.activity.DepMessActivity;
import com.dabangvr.home.fragment.hxxq.CommentView;
import com.dabangvr.home.fragment.hxxq.DeatilsView;
import com.dabangvr.home.fragment.hxxq.GoodsView;
import com.dabangvr.home.fragment.hxxq.TopMessView;
import com.dabangvr.home.weight.ShoppingSelectDialog;
import com.dabangvr.home.weight.ShoppingSelectHome;
import com.dabangvr.model.Goods;
import com.dabangvr.model.GoodsComment;
import com.dabangvr.model.goods.GoodsDetails;
import com.dabangvr.util.BottomImgSize;
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.LoginTipsDialog;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.widget.GridDividerItemDecoration;
import com.example.hxxq.CustomScrollView;
import com.google.gson.Gson;
import com.rey.material.app.BottomSheetDialog;
import com.sackcentury.shinebuttonlib.ShineButton;

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
import Utils.TObjectCallback;
import butterknife.BindView;
import config.DyUrl;
import okhttp3.Call;

/**
 * 海鲜-商品详情页
 */
public class HxxqLastActivity extends BaseNewActivity implements View.OnClickListener, TopMessView.TimeCall {

    private int type;//页面类型，0普通，1拼团，2秒杀

    private GoodsDetails mData;//商品信息
    private List<GoodsComment> commentMoList;
    public static HxxqLastActivity instants;

    //立即购买按钮
    @BindView(R.id.hx_shop)
    TextView tvBuy;

    //拼团/秒杀购买按钮
    @BindView(R.id.hx_shop_pt)
    TextView tvBuyGroup;

    //统一商品库存
    public static String unifiedStok;

    //统一商品价钱
    public static String unifiedPrice;

    //其他商品
    private RecyclerView goodsRecy;
    private BaseLoadMoreHeaderAdapter goodsAdapter;
    private List<Goods> goodsData = new ArrayList<>();
    private int page;

    //收藏动画控件
    @BindView(R.id.bt_collect)
    ShineButton btCollect;

    //收藏文字（用于点击收藏变色）
    @BindView(R.id.tv_collection)
    TextView tvCollect;

    //店铺按钮
    @BindView(R.id.ll_dep)
    LinearLayout llDep;

    //服务按钮
    @BindView(R.id.ll_server)
    LinearLayout llServer;


    @BindView(R.id.hx_jg)
    RadioButton jg;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);

        //设置状态栏透明myfragment用到
        //StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_hxxq_last;
    }


    @Override
    public void initView() {
        type = getIntent().getIntExtra("type",0);
        realTabLayout = findViewById(R.id.tablayout_real);
        scrollView = findViewById(R.id.scrollView);
        container = findViewById(R.id.container);
        instants = this;
        //返回
        findViewById(R.id.backe).setOnClickListener(this);

        //收藏
        btCollect.init(this);

        jg.setOnClickListener(this);
        if (type != 0){
            jg.setVisibility(View.GONE);
        }
        //立即购买
        tvBuy.setOnClickListener(this);
        //拼团购买
        tvBuyGroup = findViewById(R.id.hx_shop_pt);
        tvBuyGroup.setOnClickListener(this);
        goodsRecy = findViewById(R.id.orther_recy);
        GridLayoutManager manager = new GridLayoutManager(this,2);
        goodsRecy.addItemDecoration(new GridDividerItemDecoration(DensityUtil.dip2px(this,7), ContextCompat.getColor(this,R.color.white)));
        goodsRecy.setLayoutManager(manager);
        goodsRecy.setNestedScrollingEnabled(false);
        goodsAdapter = new BaseLoadMoreHeaderAdapter<Goods>(this, goodsRecy, goodsData, R.layout.new_release_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, Goods o) {
                holder.setImageByUrl(R.id.new_item_img, o.getListUrl());
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

        //收藏点击
        btCollect.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                if (null == mData)return;
                if (checked) {
                    tvCollect.setTextColor(getResources().getColor(R.color.colorDb3));
                } else {
                    tvCollect.setTextColor(getResources().getColor(R.color.colorGray6));
                }
                collectionGoods();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backe: {
                finish();
                break;
            }
            case R.id.hx_shop: {//普通购买
                showBottomDialog(type);
                break;
            }
            case R.id.hx_shop_pt: {//拼团购买
                showBottomDialog(type);
                break;
            }
            case R.id.hx_jg: {//加购
                showBottomDialog(type);
                break;
            }
            default:
                break;
        }
    }

    /**
     * 收藏
     */
    private void collectionGoods() {
        Map<String, Object> map = new HashMap<>();
        map.put("goodsId",mData.getGoodsId());
        OkHttp3Utils.getInstance(DyUrl.BASE).doPostJson(DyUrl.getGoodsCollectSave, map,getToken(this),new TObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                //设置收藏动画
            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(),msg);
            }
        });
    }

    private ShoppingSelectDialog dialogView;
    /**
     * 底部弹窗购买
     * @param index 0普通商品，1拼团购买，2秒杀购买
     */
    private void showBottomDialog(final int index) {
        dialogView = new ShoppingSelectDialog(this,index);
        dialogView.setListener(new ShoppingSelectDialog.OnClickAddCartOrConfirmListener() {
            //加入购物车或确认订单时的回掉，显示加载动画
            @Override
            public void showLoadingView() {
                setLoaddingView(true);
            }
            //加入购物车后的回掉，释放加载动画
            @Override
            public void addCartOk(String mess) {
                setLoaddingView(false);
                dialogView.desDialogView();
            }
            //确认订单后的回掉,跳转订单页面
            @Override
            public void confirmOk(String mess) {
                setLoaddingView(false);
                dialogView.desDialogView();
                Intent intent = new Intent(getContext(), OrderActivity.class);
                startActivity(intent);
            }

            @Override
            public void error(String msg) {
                setLoaddingView(false);
                dialogView.desDialogView();
            }
        });
        dialogView.showDialog(mData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        topMessView.desTop();//释放第一部分视图资源
    }

    /**
     * 购买按钮 以及初始化库存和价钱信息
     */
    private void setGoodsData() {
        if (type == 1 || type == 2){
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
    }

    /**
     * 评论信息，详细信息
     */
    private TopMessView topMessView;

    private void setAdapter() {
        int type = getIntent().getIntExtra("type", 0);
        //0普通，1拼团，2秒杀
        if (type == 1 || type == 2){
            jg.setClickable(false);
        }
        //商品基本信息视图
        topMessView = new TopMessView(this,this);
        topMessView.setMess(type, mData);
        topMessView.setLoadingListener(new TopMessView.LoadingListener() {
            @Override
            public void showAndHideView(boolean isShow) {
                setLoaddingView(isShow);
            }
        });

        //评论视图
        CommentView commentView = new CommentView(this);
        commentView.setBaseMess(mData.getGoodsId(),mData.getDeptLogo(),mData.getDeptName(),commentMoList);

        //商品详细信息视图
        DeatilsView deatilsView = new DeatilsView(this);
        deatilsView.setWebView(mData.getGoodsDesc());

        //推荐视图
        GoodsView goodsView = new GoodsView(this);

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
                if (y < 250 && y>120) {
                    //.mutate()方法不会通知其他控件跟着改变background
                    realTabLayout.getBackground().mutate().setAlpha(y);
                    realTabLayout.setVisibility(View.VISIBLE);
                }else if(y < 120){
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

    @Override
    public void initData() {
        setLoaddingView(true);
        String id = getIntent().getStringExtra("id");
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME,getSPKEY(this,"token"));
        map.put("goodsId", id);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoodsDetails, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            //主线程处理
            @Override
            public void onUi(String newsBean) {

                setLoaddingView(false);
                if (StringUtils.isEmpty(newsBean)) {
                    ToastUtil.showShort(HxxqLastActivity.this, "获取数据失败");
                }
                try {
                    JSONObject object = new JSONObject(newsBean);
                    int err = object.optInt("errno");
                    if (1 == err){
                        LoginTipsDialog.finishTips(HxxqLastActivity.this, "该产品已下架");
                    }
                    if (err == 0) {
                        if (object.optInt("code") == 500) {
                            finish();
                            return;
                        }
                        JSONObject object1 = object.optJSONObject("data");
                        String str = object1.optString("goodsDetails");
                        Gson gson = new Gson();
                        mData = gson.fromJson(str, GoodsDetails.class);

                        String commentVoList = object1.optString("commentVoList");

                        commentMoList = JsonUtil.string2Obj(commentVoList,List.class,GoodsComment.class);

                        //设置商品详细信息
                        if ( null != mData){
                            setGoodsData();
                            //设置滑动
                            setAdapter();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    setLoaddingView(false);
                }
            }

            //请求失败
            @Override
            public void onFailed(Call call, IOException e) {
                jg.setClickable(false);
                setLoaddingView(false);
            }


            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                Looper.prepare();
                ToastUtil.showShort(HxxqLastActivity.this,"电波无法到达，请检查您的网络~~");
                Looper.loop();
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

    //获取推荐商品
    public void setOrtherData(final boolean isLoad) {
        if (isLoad) {
            page ++;
        } else {
            page = 1;
        }
        Map<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(page));
        map.put("categoryId",mData.getCategoryId());
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
                        goodsData = JsonUtil.string2Obj(str,List.class,Goods.class);
                        if (isLoad) {
                            if (goodsData.size()>0){
                                goodsAdapter.addAll(goodsData);
                            }
                        } else {
                            if (goodsData.size()>0){
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
     * @param b
     */
    @Override
    public void isEnd(boolean b) {
        if (b){
            tvBuyGroup.setClickable(false);
            tvBuyGroup.setBackgroundResource(R.color.colorGray2);
            tvBuyGroup.setText("活动已结束");
            jg.setClickable(false);
        }
    }
}
