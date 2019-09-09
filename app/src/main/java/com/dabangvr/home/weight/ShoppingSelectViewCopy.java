package com.dabangvr.home.weight;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.home.activity.OrderActivity;
import com.dabangvr.model.GGobject;
import com.dabangvr.model.ProductInfoVoList;
import com.dabangvr.model.Specifications;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.ToastUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import Utils.PdUtil;
import config.DyUrl;
import okhttp3.Call;

/*
 * 描述:TODO 商品规格选择View
 */
public class ShoppingSelectViewCopy extends LinearLayout {

    private boolean hasGG = false;
    private boolean isFinish = false;
    private boolean OK;
    private int faterArr[];
    private String sunArr[];

    private String id;
    private String anchorId;

    private TextView number; //数量
    private TextView countPrice;//总价
    private TextView stock;//库存
    private TextView tvJG;//加购
    private TextView tvBuy;//购买

    private double lastPrice;//选完规格后的价钱
    private int productId = -1;//选完规格后的id
    private List<ProductInfoVoList> productInfo;

    private SPUtils spUtils;
    private PdUtil pdUtil;
    /**
     * 数据源
     */
    private List<GGobject> list;
    /**
     * 上下文
     */
    private Context context;

    /**
     * 规格标题栏的文本间距
     */
    private int titleMargin = 8;
    /**
     * 整个商品属性的左右间距
     */
    private int flowLayoutMargin = 16;
    /**
     * 属性按钮的高度
     */
    private int buttonHeight = 25;
    /**
     * 属性按钮之间的左边距
     */
    private int buttonLeftMargin = 10;
    /**
     * 属性按钮之间的上边距
     */
    private int buttonTopMargin = 8;
    /**
     * 文字与按钮的边距
     */
    private int textPadding = 10;
    /**
     * 选择后的回调监听
     */
    private OnSelectedListener listener;


    public ShoppingSelectViewCopy(Context context) {
        super(context);
        initView(context);
    }

    public ShoppingSelectViewCopy(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        this.context = context;
        spUtils = new SPUtils(context,"db_user");
        pdUtil = new PdUtil(context);
    }

    public void getView() {

        if (list.size() < 1) {
            isFinish = true;
            return;
        }
        hasGG = true;
        faterArr = new int[list.size()];
        sunArr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            faterArr[i] = list.get(i).getId();
        }
        for (GGobject attr : list) {
            //设置规格分类的标题
            TextView textView = new TextView(context);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            int margin = dip2px(context, titleMargin);
            textView.setText(attr.getName());
            textView.setTextColor(context.getResources().getColor(R.color.colorWhite));
            params.setMargins(margin, margin, margin, margin);
            textView.setLayoutParams(params);
            addView(textView);
            //设置一个大规格下的所有小规格
            FlowLayout layout = new FlowLayout(context, attr.getGoodsSpecList());

            layout.setTitle(attr.getName());
            layout.setPadding(dip2px(context, flowLayoutMargin), 0, dip2px(context, flowLayoutMargin), 0);
            //设置选择监听
//            if (listener != null) {
//                layout.setListener(listener);
//            }
            layout.setListener(new OnSelectedListener() {
                @Override
                public void onSelected(int tagId, String id) {
                    for (int i = 0; i < faterArr.length; i++) {
                        if (tagId == faterArr[i]) {
                            sunArr[i] = id;
                        }
                    }
                    //开始计算
                    startJs();
                }
            });

            for (int k = 0; k < attr.getGoodsSpecList().size(); k++) {
                Specifications smallAttr = attr.getGoodsSpecList().get(k);
                //属性按钮
                RadioButton button = new RadioButton(context);
                //默认选中第一个
                /*if (i == 0) {
                    button.setChecked(true);
                }*/
                //设置按钮的参数
                LayoutParams buttonParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                        dip2px(context, buttonHeight));
                //设置文字的边距
                int padding = dip2px(context, textPadding);
                button.setPadding(padding, 0, padding, 0);
                //设置margin属性，需传入LayoutParams否则会丢失原有的布局参数
                MarginLayoutParams marginParams = new MarginLayoutParams(buttonParams);
                marginParams.leftMargin = dip2px(context, buttonLeftMargin);
                marginParams.topMargin = dip2px(context, buttonTopMargin);

                button.setLayoutParams(marginParams);
                button.setGravity(Gravity.CENTER);
                button.setBackgroundResource(R.drawable.tv_sel);
                button.setButtonDrawable(android.R.color.transparent);
                button.setText(smallAttr.getValue());//设置规格内容
                button.setId(Integer.parseInt(smallAttr.getId()));//设置规格ID
                button.setTag(attr.getId());
                layout.addView(button);
            }
            addView(layout);
        }
    }

    private void startJs() {
        String num = number.getText().toString();//数量
        String lastStr = "";
        for (int i = 0; i < faterArr.length; i++) {
            if (StringUtils.isEmpty(sunArr[i])) {
                OK = false;
            } else {
                OK = true;
            }
        }
        if (OK) {
            for (int i = 0; i < sunArr.length; i++) {
                //只有一个规格
                if (sunArr.length == 1) {
                    lastStr = sunArr[0];
                    break;
                }

                //最后一个规格
                if (i == sunArr.length - 1) {
                    lastStr += sunArr[i];
                    break;
                }

                lastStr += sunArr[i] + "_";

            }

            //循环规格对象，如果和lastStr一样则存在
            for (int i = 0; i < productInfo.size(); i++) {
                if (lastStr.equals(productInfo.get(i).getGoodsSpecIds())) {
                    lastPrice = Double.valueOf(productInfo.get(i).getRetailPrice());
                    double price = lastPrice * Integer.parseInt(num);
                    countPrice.setText(String.format("%.2f", price));//总价
                    //库存
                    stock.setText(productInfo.get(i).getNumber());
                    //设置productId
                    productId = productInfo.get(i).getId();
                    isFinish = true;
                    if (Integer.parseInt(productInfo.get(i).getNumber()) < 1) {
                        ToastUtil.showShort(context, "该规格的产品卖完啦。。");
                        isFinish = false;
                    }
                    break;
                } else {

                }
            }
        }


    }

    public String getLastPrice() {
        return String.valueOf(lastPrice);
    }

    public int getProductId() {
        return productId;
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setData(List<GGobject> data) {
        list = data;
        getView();
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * @param number      数量控件
     * @param countPrice  总价控件
     * @param stock       库存控件
     * @param productInfo 规格对象
     */
    public void setTextViewAndGGproject(TextView number, TextView countPrice, TextView stock, final TextView tvJG, TextView tvBuy,
                                        List<ProductInfoVoList> productInfo,String id,String anchorId) {
        this.number = number;
        this.countPrice = countPrice;
        this.stock = stock;
        this.tvJG = tvJG;
        this.tvBuy = tvBuy;
        this.productInfo = productInfo;
        this.id = id;
        this.anchorId = anchorId;

        tvJG.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFinish || !hasGG) {
                    pdUtil.showLoding("加购中。。");
                    requestHttp(0,DyUrl.addToCart);
                } else{
                    ToastUtil.showShort(context, "还没选完规格");
                }
            }
        });

        tvBuy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFinish) {
                    //确认订单 ->跳转订单页面
                    pdUtil.showLoding("正在确认订单..");
                    requestHttp(1,DyUrl.confirmGoods);
                } else {
                    ToastUtil.showShort(context, "还没选完规格");
                }
            }
        });
    }

    /**\
     * 加购、直接购买统一请求
     * @param index 0 加购； 1直接购买
     */
    private void requestHttp(final int index,final String url) {
        Map<String, String> map = new HashMap<>();
        if(index == 1){
            map.put("initiateType","301");
        }
        if(productId != -1){
            map.put("productId", String.valueOf(productId));//有规格的情况下
        }
        map.put("number", number.getText().toString());
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
        map.put("goodsId", id);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(url, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (500 == object.optInt("code")) {
                        ToastUtil.showShort(context, "服务异常");
                        return ;
                    }
                    int err = object.optInt("errno");
                    if (err == 0) {
                        if (index == 0) {//加入购物车
                            ToastUtil.showShort(context, "加购成功");
                        }
                        if (index == 1) {//确认订单
                            Intent intent = new Intent(context, OrderActivity.class);
                            intent.putExtra("type", "live");
                            intent.putExtra("anchorId",anchorId);
                            intent.putExtra("payOrderSnType", "orderSnTotal");
                            intent.putExtra("dropType", 1);
                            context.startActivity(intent);
                        }
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

}
