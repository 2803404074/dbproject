package com.dabangvr.home.fragment.hxxq;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.model.GoodsComment;
import com.dabangvr.model.goods.GoodsDetails;
import com.dabangvr.model.goods.ParameterMo;
import com.dabangvr.util.TextUtil;
import com.dabangvr.util.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
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

    //评论量
    private TextView tvCommentSize;

    private Context context;

    private String goodsId;

    //商家头像
    private SimpleDraweeView depHead;

    //商家名称
    private TextView tvDepName;

    //参数列表
    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;
    private List<ParameterMo>mData = new ArrayList<>();
    private List<ParameterMo>subList = new ArrayList<>();//默认加载的三条参数

    //点击展开的更多参数列表的控件
    private LinearLayout llAction;

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

        //评论量
        tvCommentSize = view.findViewById(R.id.tv_comment_num);

        //评论人头像
        sdCommentHead = view.findViewById(R.id.sdvHead);

        //评论人名称
        tvCommentName = view.findViewById(R.id.tv_name);

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

        //商家头像
        depHead = view.findViewById(R.id.hx_lv_icon);

        //商家名称
        tvDepName = view.findViewById(R.id.tvDepName);

        //参数列表
        recyclerView = view.findViewById(R.id.recy_details);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        //点击展开参数列表的控件
        llAction = view.findViewById(R.id.ll_action);
        llAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                llAction.setVisibility(GONE);
                subList = mData.subList(0,mData.size()-1);
                adapter.updateDataa(subList);
            }
        });

        //进店
        view.findViewById(R.id.llDepGo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showShort(context,"期待店铺的开张");
            }
        });

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

    public void setViewShow(boolean isShow) {
        if (isShow){
            include_item.setVisibility(VISIBLE);
        }else {
            include_item.setVisibility(GONE);
        }
    }

    public void setBaseMess(String goodsId,String depUrl,String depName, List<GoodsComment> commentMoList) {
        this.goodsId = goodsId;

        //评论数量
        String size = commentMoList == null ? "0":String.valueOf(commentMoList.size());
        tvCommentSize.setText("商品评价（"+size+")");

        //最新的一个评论
        if (null != commentMoList && commentMoList.size()>0){
            setViewShow(true);
            GoodsComment commentMo = commentMoList.get(0);
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
        }else {
            setViewShow(false);
        }
        //商家头像
        depHead.setImageURI(depUrl);
        //商家名称
        tvDepName.setText(TextUtil.isNull2Url(depName));

        //参数列表
        //模拟参数数据
        mData.add(new ParameterMo("品牌","海跳跳"));
        mData.add(new ParameterMo("产地","广西东兴"));
        mData.add(new ParameterMo("生产条件","临海"));
        mData.add(new ParameterMo("历史","历史悠久，鱼露是一种传统的海鲜调味料，在我国以及亚洲各国已有长期的生产历史，它以低值鱼、虾为主要原料酿造而成"));
        mData.add(new ParameterMo("网络类型","4G全网通"));
        mData.add(new ParameterMo("售后服务","全国联保"));
        mData.add(new ParameterMo("分辨率","2048*4096"));
        mData.add(new ParameterMo("颜色","彩色"));
        mData.add(new ParameterMo("生产企业","广西大邦全景广告有限公司"));

        if (null != mData && mData.size()>3){
            subList = mData.subList(0,3);
        }
        adapter = new BaseLoadMoreHeaderAdapter<ParameterMo>(context,recyclerView,subList,R.layout.key_value_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, ParameterMo o) {
                holder.setText(R.id.tv_key,o.getKey());
                holder.setText(R.id.tv_value,o.getValue());
                if (subList.get(subList.size()-1).getKey().equals(o.getKey())){
                    holder.getView(R.id.view_line).setVisibility(GONE);
                }
            }
        };
        recyclerView.setAdapter(adapter);
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
