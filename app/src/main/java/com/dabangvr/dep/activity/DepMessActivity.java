package com.dabangvr.dep.activity;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.dep.model.DepCart;
import com.dabangvr.home.weight.ShoppingSelectWM;
import com.dabangvr.util.CheckPage;
import com.dabangvr.util.GlideLoadUtils;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.TextUtil;
import com.dabangvr.util.ToastUtil;
import com.example.gangedrecyclerview.CheckListener;
import com.example.gangedrecyclerview.DepSortBean;
import com.example.gangedrecyclerview.GoodsDetails;
import com.example.gangedrecyclerview.ItemHeaderDecoration;
import com.example.gangedrecyclerview.RvListener;
import com.example.gangedrecyclerview.SortAdapter;
import com.example.gangedrecyclerview.SortDetailFragment;
import com.example.gangedrecyclerview.recy.HideScrollListener;
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
 *店铺详情
 */
public class DepMessActivity extends BaseActivity implements CheckListener, View.OnClickListener, SortDetailFragment.onClickGoods, HideScrollListener {

    private double startPrice;
    private boolean isSeller = false;//是否营业中，1为营业中
    private int height;//门店信息控件高度，用于隐藏
    private RelativeLayout layout;
    private RelativeLayout rlTopName;

    private TextView tvTopName;
    private TextView tvAddress;
    private TextView tvStartPrice;
    private TextView tvStartPriceTips;

    private ImageView ivTopBack;
    private ImageView ivDep;


    private String depId;
    private List<DepCart> mDepCart;
    private PdUtil pdUtil;

    private RecyclerView rvSort;
    private SortAdapter mSortAdapter;
    private SortDetailFragment mSortDetailFragment;
    private Context mContext;
    private LinearLayoutManager mLinearLayoutManager;
    private int targetPosition;//点击左边某一个具体的item的位置
    private boolean isMoved;
    private DepSortBean depSortBean;

    private boolean IS_LOADED = false;
    private ShoppingSelectWM selectView;
    private TextView number;//底部弹窗的数量控件
    private TextView countPrice;

    private TextView tvAllPrice;//总价
    private TextView tvGoodsNum;//添加了多少个商品

    private List<GoodsDetails.ProductInfoVoList> productInfoVoLists;

    private TextView tvComFirmOrder;//去结算按钮

    private BaseLoadMoreHeaderAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_dep_mess;
    }

    @Override
    protected void initView() {
        mContext = this;
        pdUtil = new PdUtil(this);

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.back2).setOnClickListener(this);

        //门店信息控件
        layout = findViewById(R.id.dep_top);

        rlTopName = findViewById(R.id.name_top);
        tvTopName = findViewById(R.id.dep_name);

        tvAddress = findViewById(R.id.dep_address);//地址

        tvStartPrice = findViewById(R.id.tv_start_price);

        tvStartPriceTips = findViewById(R.id.tv_start_tips);

        //购物车按钮
        findViewById(R.id.cart).setOnClickListener(this);

        //去结算按钮
        tvComFirmOrder = findViewById(R.id.go_order);
        tvComFirmOrder.setOnClickListener(this);
        tvComFirmOrder.setClickable(false);
        tvComFirmOrder.setBackgroundColor(getResources().getColor(R.color.colorAccentNo));

        rvSort = (RecyclerView) findViewById(R.id.rv_sort);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        rvSort.setLayoutManager(mLinearLayoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        rvSort.addItemDecoration(decoration);

        tvAllPrice = findViewById(R.id.all_price);
        tvGoodsNum = findViewById(R.id.goods_num);

        ivDep = findViewById(R.id.dep_logo);


    }

    @Override
    protected void initData() {
        depId = getIntent().getStringExtra("depId");
        Map<String, String> map = new HashMap<>();
        map.put("deptId",depId);
        map.put(DyUrl.TOKEN_NAME,getSPKEY(this,"token"));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getDeptGoodsList, map,
                new GsonObjectCallback<String>(DyUrl.BASE) {
                    @Override
                    public void onUi(String result) {
                        if (StringUtils.isEmpty(result)) return ;

                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.optInt("errno") == 0) {
                                if (object.optInt("code") == 500) {
                                    return ;
                                }
                                //店铺信息
                                JSONObject dataObj = object.optJSONObject("data");

                                JSONObject depVo = dataObj.optJSONObject("deptVo");

                                GlideLoadUtils.getInstance().glideLoad(DepMessActivity.this,depVo.optString("storeImgs"),ivDep);
                                tvTopName.setText(TextUtil.isNull2Url(depVo.optString("name")));

                                //地址
                                String address = TextUtil.isNull2Url(depVo.optString("productionProvince"))
                                        +TextUtil.isNull2Url(depVo.optString("productionCity"))
                                        +TextUtil.isNull2Url(depVo.optString("productionCounty"))
                                        +TextUtil.isNull2Url(depVo.optString("productionAddress"));
                                tvAddress.setText(address);

                                //起送价delivery_price
                                startPrice = Double.valueOf(TextUtil.isNull(depVo.optString("deliveryPrice")));
                                tvStartPrice.setText(String.valueOf(startPrice));

                                String str = object.optString("data");
                                Gson gson = new Gson();
                                depSortBean = gson.fromJson(str, DepSortBean.class);

                                if (null != depSortBean){
                                    List<DepSortBean.CategoryOneArrayBean> categoryOneArray = depSortBean.getCategoryOneArray();
                                    if (null != categoryOneArray && categoryOneArray.size()>0){
                                        List<String> list = new ArrayList<>();
                                        //初始化左侧列表数据
                                        for (int i = 0; i < categoryOneArray.size(); i++) {
                                            list.add(categoryOneArray.get(i).getName());
                                        }
                                        mSortAdapter = new SortAdapter(mContext, list, new RvListener() {
                                            @Override
                                            public void onItemClick(int id, int position) {
                                                if (mSortDetailFragment != null) {
                                                    isMoved = true;
                                                    targetPosition = position;
                                                    setChecked(position, true);
                                                }
                                            }
                                        });
                                        rvSort.setAdapter(mSortAdapter);
                                        createFragment();
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
     * 商品分类显示
     */
    public void createFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mSortDetailFragment = new SortDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("right", depSortBean.getCategoryOneArray());
        mSortDetailFragment.setArguments(bundle);
        mSortDetailFragment.setListener(this);
        mSortDetailFragment.setOnClickGoods(this);

        mSortDetailFragment.setListenerx(this);

        fragmentTransaction.add(R.id.lin_fragment, mSortDetailFragment);
        fragmentTransaction.commit();
    }

    private void setChecked(int position, boolean isLeft) {
        Log.d("p-------->", String.valueOf(position));
        if (isLeft) {
            mSortAdapter.setCheckedPosition(position);
            //此处的位置需要根据每个分类的集合来进行计算
            int count = 0;
            for (int i = 0; i < position; i++) {
                if (null != depSortBean.getCategoryOneArray()){
                    if (null != depSortBean.getCategoryOneArray().get(i).getCategoryTwoArray()){
                        count += depSortBean.getCategoryOneArray().get(i).getCategoryTwoArray().size();
                    }
                }
            }
            count += position;
            mSortDetailFragment.setData(count);
            ItemHeaderDecoration.setCurrentTag(String.valueOf(targetPosition));//凡是点击左边，将左边点击的位置作为当前的tag
        } else {
            if (isMoved) {
                isMoved = false;
            } else
                mSortAdapter.setCheckedPosition(position);
            ItemHeaderDecoration.setCurrentTag(String.valueOf(position));//如果是滑动右边联动左边，则按照右边传过来的位置作为tag

        }
        moveToCenter(position);

    }

    //将当前选中的item居中
    private void moveToCenter(int position) {
        //将点击的position转换为当前屏幕上可见的item的位置以便于计算距离顶部的高度，从而进行移动居中
        View childAt = rvSort.getChildAt(position - mLinearLayoutManager.findFirstVisibleItemPosition());
        if (childAt != null) {
            int y = (childAt.getTop() - rvSort.getHeight() / 2);
            rvSort.smoothScrollBy(0, y);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void check(int position, boolean isScroll) {
        setChecked(position, isScroll);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.back2:
                onShow();
                break;
            case R.id.cart:
                showCartData();
                break;
            case R.id.go_order:
                if (!isSeller){
                    ToastUtil.showShort(this,"商家打烊啦，下次早点来哦");
                }else {
                    pdUtil.showLoding("正在获取订单");
                    comfirmOrder();
                }
                break;
            default:
                break;
        }
    }

    private void comfirmOrder() {
        Map<String, String> map = new HashMap<>();
        map.put("deptId", depId);
        if (StringUtils.isEmpty(getSPKEY(this, "token"))) {
            show(this, 0, "登录后才能下单哦", "去登陆");
            return;
        }
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this, "token"));
        map.put("goodsList", JsonUtil.obj2String(mDepCart));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.confirmGoods2Delivery, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")) {
                        if (500 == object.optInt("code")) {
                            ToastUtil.showShort(DepMessActivity.this, "该商家休息中。。。");
                            pdUtil.desLoding();
                            return ;
                        }
                        Intent intent = new Intent(DepMessActivity.this, OrderDepActivity.class);
                        intent.putExtra("type", "buy");
                        intent.putExtra("payOrderSnType", "orderSnTotal");
                        intent.putExtra("dropType", 1);
                        startActivity(intent);
                    }
                    pdUtil.desLoding();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }

    private void sendMessage(int index, String price,int size) {
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putInt("id", index);
        bundle.putString("price", price);
        bundle.putInt("speSize", size);
        message.setData(bundle);
        message.sendToTarget();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (!IS_LOADED) {
                IS_LOADED = true;
                Bundle bundle = msg.getData();
                setDate(bundle.getInt("id"), bundle.getString("price"),bundle.getInt("speSize"));
            }
            return false;
        }
    });

    private void setDate(int index, String price,int size) {
        //数量
        int shuliang = Integer.parseInt(number.getText().toString());
        if (index == 1) {
            if (shuliang < 2) {
                IS_LOADED = false;
                return;
            }
            shuliang--;

        } else {
            shuliang++;
        }
        number.setText(String.valueOf(shuliang));
        if (productInfoVoLists == null || productInfoVoLists.size() < 1) {
            //没产品，直接数量*基本价钱
            double pp = shuliang * Double.valueOf(price);
            countPrice.setText("" + String.format("%.2f", pp));
        } else {
            //有产品，判断是否有规格
            if (size>0){//size 规格数组大小
                String sPrice = selectView.getLastPrice();
                double lPrice = shuliang * Double.valueOf(sPrice);
                countPrice.setText("" + String.format("%.2f", lPrice));
            }else {
                double pp = shuliang * Double.valueOf(price);
                countPrice.setText("" + String.format("%.2f", pp));
            }
        }
        IS_LOADED = false;
    }

    @Override
    public void show(String id,
                     String name,
                     String listUrl,
                     String price,
                     String remainingInventory,
                     List<GoodsDetails.GoodsSpecVoList> specVoLists,
                     List<GoodsDetails.ProductInfoVoList> productInfoVoLists) {

        final String idx = id;
        final String pricex = price;
        final String namex = name;
        final String listUrlx = listUrl;
        final List<GoodsDetails.GoodsSpecVoList> specVoListsx = specVoLists;
        final List<GoodsDetails.ProductInfoVoList> productInfoVoListsx = productInfoVoLists;


        this.productInfoVoLists = productInfoVoListsx;
        //初始化底部弹窗
        final BottomSheetDialog bottomInterPasswordDialog = new BottomSheetDialog(this);

        //初始化 - 底部弹出框布局
        View view = LayoutInflater.from(this).inflate(R.layout.dep_bottom_dilog, null);

        //商品图片
        ImageView imageView = view.findViewById(R.id.imageView3);

        Glide.with(mContext).load(listUrlx).into(imageView);

        TextView tvName = view.findViewById(R.id.dep_goods_name);
        tvName.setText(namex);

        //价钱
        countPrice = view.findViewById(R.id.count_money);
        countPrice.setText(pricex);

        //加减控件
        TextView sub = view.findViewById(R.id.di_iv_sub);//减
        TextView add = view.findViewById(R.id.di_iv_add);//加
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(1, pricex,specVoListsx.size());
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(0, pricex,specVoListsx.size());
            }
        });


        //数量
        number = view.findViewById(R.id.di_iv_num);//数量


        //确定按钮
        TextView hxxq_dilog_ok = view.findViewById(R.id.hxxq_dilog_ok);

        hxxq_dilog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //加入当前购物车
                String cid = "";
                double lastPrice = 0.00;

                if (null != selectView && specVoListsx != null) {//如果有产品，则价钱为产品价钱
                    cid = String.valueOf(selectView.getProductId());
                    lastPrice = Double.valueOf(selectView.getLastPrice());
                } else {
                    lastPrice = Double.valueOf(pricex);//否则是默认价格*数量
                }
                if (null == mDepCart) {
                    mDepCart = new ArrayList<>();
                }
                //加入当前购物车的基本信息
                mDepCart.add(new DepCart(idx, namex, cid, number.getText().toString(), String.valueOf(lastPrice), listUrlx));


                //显示一共添加了多少件商品
                tvGoodsNum.setText(String.valueOf(mDepCart.size()));

                //计算所有添加的总价
                double allPrice = 0.00;
                for (int i = 0; i < mDepCart.size(); i++) {
                    //每个商品的价钱*数量*总商品 = 总价钱
                    allPrice += Integer.parseInt(mDepCart.get(i).getNumber()) * Double.valueOf(mDepCart.get(i).getPrice());
                }

                //显示一共多少钱
                tvAllPrice.setText(String.format("%.2f", allPrice));


                //获取购物车的商品，如果购物车的   商品价钱*数量 > 起送价，则结算按钮可以点击
                if (null != mDepCart || mDepCart.size() > 0) {
                    double cartPrice = 0.00;//设置默认购物车总价

                    //循环叠加购物车的商品*数量的价钱，计算最终的总价是否大于起送价
                    for (int i=0;i<mDepCart.size();i++){
                        cartPrice += Double.valueOf(mDepCart.get(i).getPrice()) * Integer.parseInt(mDepCart.get(i).getNumber());
                    }
                    if (cartPrice>startPrice){
                        tvComFirmOrder.setClickable(true);
                        tvComFirmOrder.setBackgroundColor(getResources().getColor(R.color.colorSendName1));
                        tvStartPriceTips.setVisibility(View.GONE);
                    }else {
                        tvStartPriceTips.setVisibility(View.VISIBLE);
                    }
                }
                bottomInterPasswordDialog.dismiss();


            }
        });

        //规格信息(如果存在规格)
        if (null != specVoListsx && specVoListsx.size() > 0) {
            hxxq_dilog_ok.setBackgroundColor(getResources().getColor(R.color.colorAccentNo));
            selectView = view.findViewById(R.id.v_home);//规格控件
            selectView.setTextViewAndGGproject(0, number, countPrice, hxxq_dilog_ok, productInfoVoListsx);
            selectView.setData(specVoListsx);//规格数组
            hxxq_dilog_ok.setClickable(false);
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
    }


    private void showCartData() {
        if (null == mDepCart || mDepCart.size() < 1) {
            ToastUtil.showShort(this, "请选择商品");
            return;
        }
        //初始化底部弹窗
        BottomSheetDialog bottomInterPasswordDialog = new BottomSheetDialog(this);

        //初始化 - 底部弹出框布局
        View view = LayoutInflater.from(this).inflate(R.layout.recy_demo, null);
        RecyclerView recyclerView = view.findViewById(R.id.ms_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseLoadMoreHeaderAdapter<DepCart>(this, recyclerView, mDepCart, R.layout.dep_cart_dialog_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, final DepCart o) {
                holder.setImageByUrl(R.id.img, o.getUrl());
                holder.setText(R.id.name, o.getName());
                holder.setText(R.id.number, "x" + o.getNumber());
                holder.setText(R.id.price, o.getPrice());
                holder.setText(R.id.all_price,String.format("%.2f", Double.valueOf(o.getPrice()) * Integer.parseInt(o.getNumber())));
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DepMessActivity.this);
                builder.setTitle("删除");
                builder.setMessage("要删除"+mDepCart.get(position).getName()+"吗?");
                builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDepCart.remove(position);
                        adapter.notifyDataSetChanged();

                        //展示购物车数量的textview
                        tvGoodsNum.setText(String.valueOf(mDepCart.size()));

                        double pp = 0.00;
                        for (int i=0;i<mDepCart.size();i++){
                            pp += Integer.parseInt(mDepCart.get(i).getNumber()) * Double.valueOf(mDepCart.get(i).getPrice());
                        }
                        tvAllPrice.setText(String.valueOf(pp));
                        if (pp<startPrice){
                            tvStartPriceTips.setVisibility(View.VISIBLE);
                            tvComFirmOrder.setBackgroundColor(getResources().getColor(R.color.colorAccentNo));
                            tvComFirmOrder.setClickable(false);
                        }

                    }
                });
                builder.setNegativeButton("取消",null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        //adapter.updateData(mDepCart);

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
    }

    @Override
    public void onHide() {
        rlTopName.setVisibility(View.VISIBLE);
        //隐藏动画
        //layout.animate().translationY(-layout.getHeight()).setInterpolator(new AccelerateInterpolator(3));
        performAnim2(true,height);
    }

    @Override
    public void onShow() {
        rlTopName.setVisibility(View.GONE);
        // 显示动画--属性动画
        //layout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3));
        performAnim2(false,height);
    }

    /**
     *
     * @param show  true 隐藏   false显示
     * @param height  控件高度
     */
    private void performAnim2(boolean show,int height) {
        //View是否显示的标志
        //属性动画对象
        ValueAnimator va;
        if (show) {
            //隐藏view，高度从height变为0
            va = ValueAnimator.ofInt(height, 0);
        } else {
            //显示view，高度从0变到height值
            va = ValueAnimator.ofInt(0, height);
        }
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int h = (Integer) valueAnimator.getAnimatedValue();
                //动态更新view的高度
                layout.getLayoutParams().height = h;
                layout.requestLayout();
            }
        });
        va.setDuration(500);
        //开始动画
        va.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final RelativeLayout layout = findViewById(R.id.dep_top);
        layout.post(new Runnable() {
            @Override
            public void run() {
                height = layout.getHeight();
            }
        });
    }


}
