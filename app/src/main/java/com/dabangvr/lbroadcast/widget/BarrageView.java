package com.dabangvr.lbroadcast.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.util.TextUtil;

import java.util.List;

/**
 * 弹幕，使用属性动画
 */
public class BarrageView extends FrameLayout {
    private String Tag = BarrageView.class.getSimpleName();

    private List<String> mBarrageViewBeanList; //数据源
    private int barrageViewWidth;    //控件宽
    private int barrageViewHeight;  //控件高
    private RelativeLayout.LayoutParams itemLayoutParams;

    private int displayLines = 6;//弹幕行数
    private boolean isRepeat = false;//是否循环显示

    private long animationTime = 3 * 1000L; //动画时间
    private long distanceTime = 3 * 1000L; //两条弹幕间隔时间
    private boolean isRandomDistanTime = true; //是否随机间隔时间

    private int currentIndex; //大当前弹幕索引
    private boolean isStart;//弹幕状态


    private int lastLine; //上一次出现的行数

    private final int CODE_START = 1000;
    private final int CODE_NEXT = 1001;
    private final int CODE_END = 1002;


    private String type;
    public void setType(String type){
        this.type = type;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_START:
                    handler.sendEmptyMessage(CODE_NEXT);
                    break;
                case CODE_NEXT:
                    if (isStart && mBarrageViewBeanList != null && currentIndex < mBarrageViewBeanList.size()) {
                        String item = mBarrageViewBeanList.get(currentIndex);
                        addView(item);
                        currentIndex++;
                        long randomSleepTime;
                        if (isRandomDistanTime) {
                            randomSleepTime = (long) (Math.random() * 5 + 3) * 200L;
                        } else {
                            randomSleepTime = distanceTime;
                        }
                        handler.sendEmptyMessageDelayed(CODE_NEXT, randomSleepTime);
                    } else {
                        handler.sendEmptyMessage(CODE_END);
                    }
                    break;
                case CODE_END:
                    Log.d(Tag, "CODE_END");
                    if (isRepeat) {
                        if (currentIndex != 0) {
                            currentIndex = 0;
                            handler.sendEmptyMessage(CODE_NEXT);
                        }
                    }
                    break;
            }

        }
    };
    private LinearInterpolator linearInterpolator;

    public BarrageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private void addView(String barrageViewBean) {
        final RelativeLayout itemView = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.play_dm, null);
        if (itemLayoutParams == null) {
            itemLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, dip2px(getContext(), 67));
        }
        itemView.setLayoutParams(itemLayoutParams);
        itemView.setY(getItemRamdomY());
        itemView.measure(0, 0);
        int itemViewWidth = itemView.getMeasuredWidth();
        itemView.setX(this.barrageViewWidth);

        //设置文字//设置图片
//        TextView textView = itemView.findViewById(R.id.tv_title);
        ImageView imageView = itemView.findViewById(R.id.iv_img);
        
        int res = R.mipmap.l_fireworks;
        
        if (type.equals("yh")){
            res = R.mipmap.l_fireworks;
        }else if (type.equals("xh")){
            res = R.mipmap.l_flower;
        }else if (type.equals("qq")){
            res = R.mipmap.l_balloon;
        }else if (type.equals("hg")){
            res = R.mipmap.l_hat;
        }else if (type.equals("lbjn")){
            res = R.mipmap.l_cart;
        }else if (type.equals("yt")){//游艇
            res = R.mipmap.l_ailboat;
        }else if (type.equals("fj")){
            res = R.mipmap.l_plane;
        }else if (type.equals("hj")){
            res = R.mipmap.l_rocket;
        }
        Bitmap bitmap = ((BitmapDrawable)getResources().getDrawable(res)).getBitmap();
        imageView.setImageBitmap(bitmap);

//        textView.setText(TextUtil.isNull(barrageViewBean));
        //tvContent.setText(barrageViewBean);
        addView(itemView);

        if (linearInterpolator == null) {
            linearInterpolator = new LinearInterpolator();
        }

        final ObjectAnimator anim = ObjectAnimator.ofFloat(itemView, "translationX", -itemViewWidth);
        anim.setDuration(animationTime);
        anim.setInterpolator(linearInterpolator);
        //释放资源
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                anim.cancel();
                itemView.clearAnimation();
                removeView(itemView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    /**
     * 获得随机的Y轴的值
     */
    private float getItemRamdomY() {
        int currentY;

        //随机选择弹幕出现的行数位置，跟上一条位置不同行
        int randomLine = lastLine;
        while (randomLine == lastLine) {
            randomLine = (int) (Math.random() * displayLines + 1);
        }

        //当前itemView y值
        currentY = barrageViewHeight / displayLines * (randomLine - 1);
        lastLine = randomLine;
        return currentY;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        barrageViewWidth = getWidth();
        barrageViewHeight = getHeight();

    }

    private int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //设置数据
    public void setData(List<String> list) {
        mBarrageViewBeanList = list;
    }

    public void start() {
        isStart = true;
        handler.sendEmptyMessage(CODE_START);
    }


    public void onResume() {
        if (!isStart) {
            isStart = true;
            handler.sendEmptyMessage(CODE_NEXT);
        }
    }

    public void onPause() {
        isStart = false;
        handler.removeMessages(CODE_NEXT);
    }

    public void cancle() {
        isStart = false;
        currentIndex = 0;
        if (mBarrageViewBeanList != null) {
            mBarrageViewBeanList.clear();
        }
        removeAllViews();
        handler.removeMessages(CODE_NEXT);
    }

    public void onDestroy() {
        cancle();
    }
}
