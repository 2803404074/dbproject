package com.dabangvr.home.fragment.hxxq;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.ViewPagerTransform;
import com.dabangvr.model.goods.GoodsDetails;
import com.dabangvr.model.goods.ParameterMo;
import com.dabangvr.util.CountDownUtil;
import com.dabangvr.util.DialogUtilT;
import com.dabangvr.util.TextUtil;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Unbinder;

import static com.dabangvr.home.activity.HxxqLastActivity.unifiedPrice;
import static com.dabangvr.home.activity.HxxqLastActivity.unifiedStok;

public class TopMessView extends LinearLayout {

    private Context context;
    private ViewPagerTransform viewPager;

    private TimeCall timeCall;

    //滑动轮播图变化的翻页数字
    private TextView tvImgNum;

    //销售价
    private TextView tvPrice;
    //市场价
    private TextView tvMarketPrice;
    //商品标题
    private TextView tvGoodsName;
    //商品副标题
    private TextView tvGoodsTitle;
    //销量
    private TextView tvSalseNum;
    //已选的规格
    private TextView tvProduct;
    //倒计时
    private TextView tvTime;
    //倒计时视图（用于普通商品隐藏，秒杀拼团商品显示）
    private LinearLayout llTimeView;
    //邮费
    private TextView tvYfPrice;
    //服务
    private TextView tvServer;
    //服务弹窗的数据
    private List<ParameterMo>mServerData = new ArrayList<>();

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

    private void init(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.hxxq_view_top, this, true);

        //3D画廊
        viewPager = (ViewPagerTransform) view.findViewById(R.id.viewpager);
        viewPager.setPageMargin(20);

        //滑动时候变化的数字（）
        tvImgNum = view.findViewById(R.id.tv_imgNum);

        //倒计时
        llTimeView = view.findViewById(R.id.llTime);
        tvTime = view.findViewById(R.id.tvTime);

        //商品标题
        tvGoodsName = view.findViewById(R.id.tv_name);

        //商品副标题
        tvGoodsTitle = view.findViewById(R.id.tv_title);

        //销售价
        tvPrice = view.findViewById(R.id.tv_price);

        //市场价
        tvMarketPrice = view.findViewById(R.id.m_price);

        //销量
        tvSalseNum = view.findViewById(R.id.tv_salseNum);

        //已选的规格
        tvProduct = view.findViewById(R.id.tv_product);

        //邮费
        tvYfPrice = view.findViewById(R.id.tv_yf);

        //服务
        tvServer = view.findViewById(R.id.tv_server);

        tvServer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtilT dialogUtilT = new DialogUtilT(context) {
                    @Override
                    public void convert(BaseRecyclerHolder holder) {
                        RecyclerView serverRecyc =  holder.getView(R.id.recy_server);
                        serverRecyc.setLayoutManager(new LinearLayoutManager(context));
                        BaseLoadMoreHeaderAdapter serverAdapter = new BaseLoadMoreHeaderAdapter<ParameterMo>
                                (context,serverRecyc,mServerData,R.layout.key_value_img_item) {
                            @Override
                            public void convert(Context mContext, BaseRecyclerHolder holder, ParameterMo o) {
                                holder.setText(R.id.tv_key,o.getKey());
                                holder.setText(R.id.tv_value,o.getValue());
                            }
                        };
                        serverRecyc.setAdapter(serverAdapter);
                    }
                };
                dialogUtilT.show(R.layout.dialog_server);
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

                    final ImageView iv =  view.findViewById(R.id.hxxq_img);

                    imgList.add((ImageView)view.findViewById(R.id.hxxq_img));

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

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                i++;
                tvImgNum.setText(i+"/"+imgAndVideo.size());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

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

         //初始滑动下标
        tvImgNum.setText(1+"/"+mData.getImgList().size());
        //-----------------------分类信息----------0普通，1拼团，2秒杀------------
        //设置价钱
        switch (type) {
            case 0:
                llTimeView.setVisibility(View.GONE);//隐藏倒计时视图
                tvPrice.setText("¥" + TextUtil.isNull(mData.getSellingPrice()));//团购价
                break;
            case 1:
                llTimeView.setVisibility(View.VISIBLE);//显示倒计时视图
                tvPrice.setText("¥" + TextUtil.isNull(mData.getGroupPrice()));//团购价
                //团购倒计时
                dowTime(mData.getEndTime());
                break;
            case 2:
                llTimeView.setVisibility(View.VISIBLE);//显示倒计时视图
                tvPrice.setText("¥" + TextUtil.isNull(mData.getSecondsPrice()));//秒杀价
                //秒杀倒计时
                dowTime(mData.getSecondsEndTime());
                break;
        }

        //---------------------公共的信息---------------
        //商品名称（信息）
        tvGoodsName.setText(mData.getName());
        //商品标题
        tvGoodsTitle.setText(StringUtils.isEmpty(mData.getTitle())?"该接口中有title字段，但是没有值":mData.getTitle());

        //商品市场价
        tvMarketPrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG ); //中间横线
        tvMarketPrice.setText("￥ " + mData.getMarketPrice());
        tvMarketPrice.getPaint().setAntiAlias(true);// 抗锯齿

        //销量
        tvSalseNum.setText("已售"+mData.getSalesVolume());

        //默认规格
        tvProduct.setText("默认规格....");

        //邮费
        tvYfPrice.setText(StringUtils.isEmpty(mData.getLogisticsPrice())?"免邮":mData.getLogisticsPrice());

        //服务
        tvServer.setText("服务支持。。。。");

        //服务模拟数据
        mServerData.add(new ParameterMo("5天发货",""));
        mServerData.add(new ParameterMo("8天退货","8天退货，退货邮费卖家承担"));
        mServerData.add(new ParameterMo("破损包退","所有商品在谦手时候如有商品破损变形等，商家承诺进行退款处理"));
        mServerData.add(new ParameterMo("运险费","爱上大大说asd阿萨"));
        mServerData.add(new ParameterMo("公益宝贝","萨的方式打撒旦盛大的啊啊撒啊阿斯顿阿萨奥迪"));
        mServerData.add(new ParameterMo("蚂蚁花呗","爱上大大撒旦阿大湿答答asd阿大啊打上单啊打上单安师大打色大时代的asdasd是哒是哒打色"));
        mServerData.add(new ParameterMo("信用卡支付","爱上大大撒旦阿大湿答答asd阿大啊打上单啊打上单安师大打色大时代的asdasd是哒是哒打色按时打撒打色asd"));
    }
}
