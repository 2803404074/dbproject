package com.dabangvr.home.fragment.hxxq;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.common.activity.CommentActivity;
import com.dabangvr.common.activity.CommentListActivity;
import com.dabangvr.model.GoodsComment;
import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.commons.lang.StringUtils;

import java.util.Random;

import main.CommentMo;

public class CommentView extends LinearLayout {

    private View include_item;

    //评论人头像
    private SimpleDraweeView sdCommentHead;

    //评论人名称
    private TextView tvCommentName;

    //评论时间
    private TextView tvCommentDate;

    //评论内容
    private TextView tvCommentContent;

    //评论的第一张图片
    private ImageView ivCommentImg1;

    //评论的第二张图片
    private ImageView ivCommentImg2;

    //评论的第三张图片
    private ImageView ivCommentImg3;

    //销售量
    private TextView tvSalesNum;

    //评论量
    private TextView tvCommentSize;


    private Context context;

    private String goodsId;

    public CommentView(Context context) {
        this(context, null);
        this.context = context;
    }

    public CommentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.hxxq_view_comment, this, true);

        include_item = view.findViewById(R.id.include_item);

        //销售量
        tvSalesNum = view.findViewById(R.id.sales_num);

        //评论量
        tvCommentSize = view.findViewById(R.id.hx_comment_count);

        //评论人头像
        sdCommentHead = view.findViewById(R.id.hx_lv_icon);

        //评论人名称
        tvCommentName = view.findViewById(R.id.hx_lv_name);

        //评论时间
        tvCommentDate = view.findViewById(R.id.comment_date);

        //评论内容
        tvCommentContent = view.findViewById(R.id.hx_lv_content);

        //评论的第一张图片
        ivCommentImg1 = view.findViewById(R.id.com_img1);

        //评论的第二张图片
        ivCommentImg2 = view.findViewById(R.id.com_img2);

        //评论的第三张图片
        ivCommentImg3 = view.findViewById(R.id.com_img3);

        //查看更多评论
        view.findViewById(R.id.hx_see_comment).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentListActivity.class);
                intent.putExtra("goodsId",goodsId);
                context.startActivity(intent);
            }
        });
    }

    public void setSaleNum(String salesSize){
        tvSalesNum.setText(setText(salesSize));//销售量
    }

    public void setCommentNum(String commentSize){
        tvCommentSize.setText(setText(commentSize));//评论量
    }
    public void setView(GoodsComment commentMo){
        tvCommentName.setText(setText(commentMo.getNickName()));//昵称
        tvCommentDate.setText(setText(commentMo.getCommentData()));//评论日期
        tvCommentContent.setText(setText(commentMo.getCommentContent()));//评论内容
        sdCommentHead.setImageURI(commentMo.getHeadUrl());//头像

        //评论内容图片
        if (null !=commentMo.getCommentImg() && commentMo.getCommentImg().size()>0){
            for (int i=0;i<commentMo.getCommentImg().size();i++){
                if (i==0){
                    Glide.with(context).load(commentMo.getCommentImg().get(i)).into(ivCommentImg1);
                }else if (i==1){
                    Glide.with(context).load(commentMo.getCommentImg().get(i)).into(ivCommentImg2);
                }else {
                    Glide.with(context).load(commentMo.getCommentImg().get(i)).into(ivCommentImg3);
                }
            }
        }
    }

    public void setViewShow(boolean isShow) {
        if (isShow){
            include_item.setVisibility(VISIBLE);
        }else {
            include_item.setVisibility(GONE);
        }
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    private String setText(String tv){
        String text = "0";
        if (StringUtils.isEmpty(tv)|| tv.equals("0")||tv.equals("null")){
            return text;
        }else {
            return tv;
        }
    }
}
