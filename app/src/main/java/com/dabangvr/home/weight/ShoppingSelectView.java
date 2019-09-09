package com.dabangvr.home.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.home.activity.HxxqActivity;
import com.dabangvr.model.GGobject;
import com.dabangvr.model.ProductInfoVoList;
import com.dabangvr.model.Specifications;
import com.dabangvr.util.ToastUtil;

import org.apache.commons.lang.StringUtils;

import java.util.List;

/*
 * 描述:TODO 商品规格选择View
 */
public class ShoppingSelectView extends LinearLayout {
    private boolean OK;
    private int faterArr[];
    private String sunArr[];

    private int type;
    private TextView number;
    private TextView countPrice;
    private TextView stock;
    private TextView clike;

    private double lastPrice;//选完规格后的价钱
    private int productId;//选完规格后的id
    private List<ProductInfoVoList> productInfo;
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


    public ShoppingSelectView(Context context) {
        super(context);
        initView(context);
    }

    public ShoppingSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        this.context = context;
    }

    public void getView() {
        if (list.size() < 0) {
            return;
        }
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
                    //ToastUtil.showShort(context,"父级id="+tagId+"，规格id="+id);
                    //去判断是否存在规格，然后设置一个方法，去获取选择后的价钱和id，接着activity拿到该价钱再和数量相乘即可
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
            if (null != productInfo){
                for (int i = 0; i < productInfo.size(); i++) {
                    if (lastStr.equals(productInfo.get(i).getGoodsSpecIds())) {
                        //选完规格后的价钱。区分普通的规格价钱、拼团的规格价钱、秒杀的规格价钱。
                        if (type == 1 || type == 2) {
                            if(!StringUtils.isEmpty(productInfo.get(i).getRetailPrice())){
                                lastPrice = Double.valueOf(productInfo.get(i).getRetailPrice());
                            }
                        } else if (type == 3) {
                            if(!StringUtils.isEmpty(productInfo.get(i).getGroupPrice())){
                                lastPrice = Double.valueOf(productInfo.get(i).getGroupPrice());
                            }
                        } else {
                            if(!StringUtils.isEmpty(productInfo.get(i).getSecondsPrice())){
                                lastPrice = Double.valueOf(productInfo.get(i).getSecondsPrice());
                            }
                        }
                        double price = lastPrice * Integer.parseInt(num);
                        countPrice.setText(String.format("%.2f", price));//总价
                        //库存
                        stock.setText(productInfo.get(i).getNumber());
                        clike.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        clike.setClickable(true);
                        HxxqActivity.isGG = true;
                        //设置productId
                        productId = productInfo.get(i).getId();
                        break;
                    } else {
                        clike.setBackgroundColor(getResources().getColor(R.color.colorAccentNo));
                        clike.setClickable(false);
                    }
                }
            }else {
                ToastUtil.showShort(context,"该规格的产品卖完啦");
                clike.setBackgroundColor(getResources().getColor(R.color.colorAccentNo));
                clike.setClickable(false);
            }

        }


        //获取规格对象里面的 xxx_xxx_xx,与aa比较,一样的话就存在规格

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
     * @param type        选择购买的类型   1加购，2单独购买，3拼团购买，4秒杀购买
     * @param number      数量控件
     * @param countPrice  总价控件
     * @param clike       确定按钮
     * @param stock       库存控件
     * @param productInfo 规格对象
     */
    public void setTextViewAndGGproject(int type, TextView number, TextView countPrice, TextView clike,TextView stock, List<ProductInfoVoList> productInfo) {
        this.type = type;
        this.number = number;
        this.countPrice = countPrice;
        this.stock = stock;
        this.productInfo = productInfo;
        this.clike = clike;
        if (productInfo == null || productInfo.size() < 1) {
            clike.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    public void setTextViewAndGGproject(TextView number, TextView countPrice, TextView clike,TextView stock, List<ProductInfoVoList> productInfo) {
        this.type = 1;
        this.number = number;
        this.countPrice = countPrice;
        this.stock = stock;
        this.productInfo = productInfo;
        this.clike = clike;
        if (productInfo == null || productInfo.size() < 1) {
            clike.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

}
