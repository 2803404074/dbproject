package com.dabangvr.home.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.model.goods.GoodsDetails;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/*
 * 描述:TODO 商品规格选择View
 */
public class ShoppingSelectNews<T> extends LinearLayout {
    private int faterArr[];
    private String sunArr[];

    private int type;//类型
    private TextView countPrice;//规格对应的价钱
    private TextView tvStock;//规格对应的库存
    private ImageView ivLogo;//规格对应的图片

    private double lastPrice;//选完规格后的价钱
    private String productId;//选完规格后的id
    private List<GoodsDetails.ProductInfoVoList> productInfo;
    /**
     * 数据源
     */
    private List<GoodsDetails.GoodsSpecVoList> list;
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
    private int flowLayoutMargin = 5;
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
    private int buttonTopMargin = 5;
    /**
     * 文字与按钮的边距
     */
    private int textPadding = 10;

    public ShoppingSelectNews(Context context) {
        super(context);
        initView(context);
    }

    public ShoppingSelectNews(Context context, AttributeSet attrs) {
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
            faterArr[i] = Integer.parseInt(list.get(i).getId());
        }
        for (GoodsDetails.GoodsSpecVoList attr : list) {
            //设置规格分类的标题
            TextView textView = new TextView(context);
            TextView line = new TextView(context);
            line.setBackgroundColor(getResources().getColor(R.color.colorGray1));
            int lineHeight = dip2px(context, titleMargin);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            LayoutParams lineParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, lineHeight);
            line.setLayoutParams(lineParams);

            int margin = dip2px(context, titleMargin);
            textView.setText(attr.getName());
            textView.setTextSize(20);
            params.setMargins(margin, margin, margin, margin);
            textView.setLayoutParams(params);

            addView(textView);
            //设置一个大规格下的所有小规格
            FlowLayoutHome layout = new FlowLayoutHome(context, attr.getGoodsSpecList());

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

            if (null == attr.getGoodsSpecList() || attr.getGoodsSpecList().size() == 0) {
                return;
            }
            for (int k = 0; k < attr.getGoodsSpecList().size(); k++) {
                GoodsDetails.GoodsSpecVoList.GoodsSpecList smallAttr = attr.getGoodsSpecList().get(k);
                //属性按钮
                RadioButton button = new RadioButton(context);
                //默认选中第一个
                if (k == 0) {
                    button.setChecked(true);
                    for (int i = 0; i < faterArr.length; i++) {
                        if (Integer.parseInt(attr.getId()) == faterArr[i]) {
                            sunArr[i] = attr.getGoodsSpecList().get(k).getId();
                        }
                    }
                }
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
                button.setBackgroundResource(R.drawable.selector_item);
                button.setButtonDrawable(android.R.color.transparent);
                button.setText(smallAttr.getValue());//设置规格内容
                button.setId(Integer.parseInt(smallAttr.getId()));//设置规格ID
                button.setTag(attr.getId());
                layout.addView(button);
            }
            addView(layout);
        }
    }

    private String lastStr = "";//选择后的规格字符串----->   123_456
    private void startJs() {
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
        if (null != productInfo && productInfo.size()>0) {
            for (int i = 0; i < productInfo.size(); i++) {
                if (lastStr.equals(productInfo.get(i).getGoodsSpecIds())) {
                    //选完规格后的价钱。区分普通的规格价钱、拼团的规格价钱、秒杀的规格价钱。
                    if (type == 0) {//0普通商品
                        if (!StringUtils.isEmpty(productInfo.get(i).getRetailPrice())) {
                            lastPrice = Double.valueOf(productInfo.get(i).getRetailPrice());
                        }
                    }
                    if (type == 1) {//拼团商品
                        if (!StringUtils.isEmpty(productInfo.get(i).getGroupPrice())) {
                            lastPrice = Double.valueOf(productInfo.get(i).getGroupPrice());
                        }
                    }
                    if (type == 2){//秒杀商品
                        if (!StringUtils.isEmpty(productInfo.get(i).getSecondsPrice())) {
                            lastPrice = Double.valueOf(productInfo.get(i).getSecondsPrice());
                        }
                    }
                    //设置价钱
                    countPrice.setText(String.format("%.2f", lastPrice));

                    //设置库存
                    tvStock.setText(productInfo.get(i).getNumber());

                    //设置规格图片
                    Glide.with(context.getApplicationContext()).load(productInfo.get(i).getPictureUrl()).into(ivLogo);

                    //设置productId
                    productId = productInfo.get(i).getId();
                    break;
                }
            }
        } else {
            //ToastUtil.showShort(context, "该规格的产品卖完啦");
        }
    }

    public String getProductId() {
        return productId;
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setData(List<GoodsDetails.GoodsSpecVoList> data) {
        list = data;
        getView();
    }

    /**
     * @param type        选择购买的类型   0普通，1拼团，2秒杀
     * @param imageView   规格对应的商品图片
     * @param countPrice  总价控件
     * @param productInfo 规格对象
     */
    public void setTextViewAndGGproject(int type, ImageView imageView, TextView countPrice,TextView tvStock, List<GoodsDetails.ProductInfoVoList> productInfo) {
        this.type = type;
        this.ivLogo = imageView;
        this.countPrice = countPrice;
        this.tvStock = tvStock;
        this.productInfo = productInfo;
        startJs();
    }
}
