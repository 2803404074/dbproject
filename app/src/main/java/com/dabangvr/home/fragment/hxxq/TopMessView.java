package com.dabangvr.home.fragment.hxxq;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dabangvr.R;
import com.dabangvr.common.weight.ViewPagerTransform;
import com.dabangvr.model.goods.GoodsDetails;
import com.dabangvr.util.CountDownUtil;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.dabangvr.home.activity.HxxqLastActivity.unifiedPrice;
import static com.dabangvr.home.activity.HxxqLastActivity.unifiedStok;

public class TopMessView extends LinearLayout {

    private Context context;
    private ViewPagerTransform viewPager;


    private TimeCall timeCall;
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

    //商家所在的省份
    private TextView depAddress;

    //积分
    private TextView tvJf;

    public void setJf(String jfNum) {
        tvJf.setText("支持返还积分，下单立即赠送 "+jfNum+" 积分");
    }

    public interface TimeCall{
        void isEnd(boolean b);
    }



    public void setCall(TimeCall timeCall){
        this.timeCall = timeCall;
    }

    private volatile List<ImageView> imgList = new ArrayList<>();

    public TopMessView(Context context,TimeCall timeCall) {
        this(context, (AttributeSet) null);
        this.context = context;
        this.timeCall = timeCall;
    }

    public TopMessView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopMessView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.hxxq_view_top, this, true);
        //3D画廊
        viewPager = (ViewPagerTransform) view.findViewById(R.id.viewpager);
        viewPager.setPageMargin(20);


        //商品信息（名称）
        tvGoodsName = view.findViewById(R.id.hx_info);

        //销售价
        tvPrice = view.findViewById(R.id.hx_money_s);

        //市场价
        tvMarketPrice = view.findViewById(R.id.hx_money_y);

        //库存
        tvStock = view.findViewById(R.id.kucun);


        //倒计时
        tvTime = view.findViewById(R.id.hx_countdown_timer);
        llTimeView = view.findViewById(R.id.is_ms_or_pt);

        //是否免邮费
        tvYfPrice = view.findViewById(R.id.yf_price);

        depAddress = view.findViewById(R.id.dep_address);

        tvJf = view.findViewById(R.id.hx_dz_hhc);

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

                    final ImageView iv =  view.findViewById(R.id.hxxq_img);

                    imgList.add((ImageView)view.findViewById(R.id.hxxq_img));

                    //Glide.with(container.getContext()).load(img).into(imgList.get(position));

                    //heightList.add(imgList.get(position).getHeight());

                    //iv.setImageResource(position % 2 == 0 ? R.mipmap.app_lunch : R.mipmap.application);

                    Glide.with(container.getContext()).asBitmap()
                            .load(img)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap bitmap,
                                                            @Nullable Transition<? super Bitmap> transition) {
                                    iv.setImageBitmap(bitmap);
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

    //团购/秒杀倒计时
    private void dowTime(String data) {
        if (StringUtils.isEmpty(data) || data.equals("null")){
            timeCall.isEnd(true);
            tvTime.setText("活动已结束");
            return;
        }
        CountDownUtil downUtil = new CountDownUtil();
        downUtil.start(Long.parseLong(data), new CountDownUtil.OnCountDownCallBack() {
            @Override
            public void onProcess(int day, int hour, int minute, int second) {
                tvTime.setText(day + "天 " + hour + "时 " + minute + "分 " + second + "秒");
            }
            @Override
            public void onFinish() {
                timeCall.isEnd(true);
                tvTime.setText("活动已结束");
            }
        });
    }


    /**
     * 商品基本信息
     */
    public void setMess(int type, GoodsDetails mData) {

        //设置轮播

         setViewPagerAdapter(mData.getImgList());


        //-----------------------分类信息----------0普通，1拼团，2秒杀------------
        switch (type) {
            case 0:
                llTimeView.setVisibility(View.GONE);//隐藏倒计时视图
                tvPrice.setText("¥" + setText(0,unifiedPrice));//团购价
                tvStock.setText(setText(0,unifiedStok) + "件");//团购商品库存
                break;
            case 1:
                llTimeView.setVisibility(View.VISIBLE);//显示倒计时视图
                tvPrice.setText("¥" + setText(0,unifiedPrice));//团购价
                tvStock.setText(setText(0,unifiedStok) + "件");//团购商品库存
                //团购倒计时
                dowTime(mData.getEndTime());
                break;
            case 2:
                llTimeView.setVisibility(View.VISIBLE);//显示倒计时视图
                tvPrice.setText("¥" + setText(0,unifiedPrice));//秒杀价
                tvStock.setText(setText(0,unifiedStok) + "件");//秒杀商品库存
                //秒杀倒计时
                dowTime(mData.getSecondsEndTime());
                break;
        }

        //---------------------公共的信息---------------
        //商品名称（信息）
        tvGoodsName.setText(mData.getName());
        //商品市场价
        tvMarketPrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG ); //中间横线
        tvMarketPrice.setText("￥ " + mData.getMarketPrice());
        tvMarketPrice.getPaint().setAntiAlias(true);// 抗锯齿


        //邮费
        if (StringUtils.isEmpty(mData.getLogisticsPrice()) || mData.getLogisticsPrice().equals("null") || Float.valueOf(mData.getLogisticsPrice()) <= 0) {
            tvYfPrice.setText("免邮");
        } else {
            tvYfPrice.setText("快递费:" + mData.getLogisticsPrice());
        }

        //地址
        depAddress.setText(setText(1, mData.getDepAddress()));
    }

    /**
     * @param index 0设置数字，1设置字符串
     * @param tv
     * @return
     */
    private String setText(int index, String tv) {
        if (StringUtils.isEmpty(tv) || tv.equals("0") || tv.equals("null")) {
            if (index == 0) return "0";
            else return "该店铺未提供地址";
        } else {
            return tv;
        }
    }
}
