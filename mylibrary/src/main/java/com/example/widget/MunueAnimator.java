package com.example.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import com.example.mylibrary.R;

import java.util.List;

public class MunueAnimator {
    private List<TextView> mTextViewList;
    private MediaPlayer music;
    private Context context;
    private IsShow isMenuOpen;
    public interface IsShow{
        void isShow(boolean is);
    }
    public MunueAnimator(List<TextView> mTextViewList, Context context,IsShow isMenuOpen) {
        this.mTextViewList = mTextViewList;
        this.context = context;
        this.isMenuOpen = isMenuOpen;
    }

    public  void showOpenAnim(int dp) {
        for (int i=0;i<mTextViewList.size();i++){
            mTextViewList.get(i).setVisibility(View.VISIBLE);
        }
        if(music == null){
            music= MediaPlayer.create(context, R.raw.load_music);
        }
        music.start();
        //for循环来开始小图标的出现动画
        for (int i = 0; i < mTextViewList.size(); i++) {
            AnimatorSet set = new AnimatorSet();
            //标题1与x轴负方向角度为20°，标题2为100°，转换为弧度
            double a = -Math.cos(20 * Math.PI / 180 * (i * 2 + 1));
            double b = -Math.sin(20 * Math.PI / 180 * (i * 2 + 1));
            double x = a * dip2px(dp);
            double y = b * dip2px(dp);

            set.playTogether(
                    ObjectAnimator.ofFloat(mTextViewList.get(i), "translationX", (float) (x * 0.25), (float) x),
                    ObjectAnimator.ofFloat(mTextViewList.get(i), "translationY", (float) (y * 0.25), (float) y)
                    , ObjectAnimator.ofFloat(mTextViewList.get(i), "alpha", 0, 1).setDuration(2000)
            );
            set.setInterpolator(new BounceInterpolator());
            set.setDuration(500).setStartDelay(100);
            set.start();

            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    //菜单状态置打开
                    isMenuOpen.isShow(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }

        //转动加号大图标本身45°
//        ObjectAnimator rotate = ObjectAnimator.ofFloat(imgPublish, "rotation", 0, 90).setDuration(300);
//        rotate.setInterpolator(new BounceInterpolator());
//        rotate.start();
    }

    //关闭扇形菜单的属性动画，参数与打开时相反
    public void showCloseAnim(int dp) {


        //for循环来开始小图标的出现动画
        for (int i = 0; i < mTextViewList.size(); i++) {
            AnimatorSet set = new AnimatorSet();
            double a = -Math.cos(20 * Math.PI / 180 * (i * 2 + 1));
            double b = -Math.sin(20 * Math.PI / 180 * (i * 2 + 1));
            double x = a * dip2px(dp);
            double y = b * dip2px(dp);

            set.playTogether(
                    ObjectAnimator.ofFloat(mTextViewList.get(i), "translationX", (float) x, (float) (x * 0.25)),
                    ObjectAnimator.ofFloat(mTextViewList.get(i), "translationY", (float) y, (float) (y * 0.25)),
                    ObjectAnimator.ofFloat(mTextViewList.get(i), "alpha", 1, 0).setDuration(2000)
            );
//      set.setInterpolator(new AccelerateInterpolator());
            set.setDuration(500);
            set.start();

            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    for (int i=0;i<mTextViewList.size();i++){
                        mTextViewList.get(i).setVisibility(View.GONE);
                    }

                    //菜单状态置关闭
                    isMenuOpen.isShow(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }


        //转动加号大图标本身45°
//        ObjectAnimator rotate = ObjectAnimator.ofFloat(imgPublish, "rotation", 0, 90).setDuration(300);
//        rotate.setInterpolator(new BounceInterpolator());
//        rotate.start();
    }

    private int dip2px(int value) {
        float density = context.getResources()
                .getDisplayMetrics().density;
        return (int) (density * value + 0.5f);
    }

}
