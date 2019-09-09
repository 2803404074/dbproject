package com.dabangvr.lbroadcast.fragment.page;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.lbroadcast.activity.LbroaTypeActivity;
import com.dabangvr.lbroadcast.activity.MyZbActivity;
import com.dabangvr.lbroadcast.activity.PlayActivity;
import com.dabangvr.model.GwBean;
import com.dabangvr.util.BannerStart;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.youth.banner.Banner;

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
import config.DyUrl;
import config.GiftUrl;
import okhttp3.Call;

/**
 * 直播娱乐页面
 */
@SuppressLint("ValidFragment")
public class ZhiBoPage extends Fragment {

    private BaseLoadMoreHeaderAdapter typeAdapter;
    private RecyclerView typeRecyclerView;
    private List<FragmentGw.ZhiboPageMo> typeData = new ArrayList<>();

    private BaseLoadMoreHeaderAdapter adapter;
    private RecyclerView recyclerView;
    private List<GwBean> mData = new ArrayList<>();

    private int page = 1;
    private boolean IS_LOADED = false;
    private static int mSerial = 0;
    private int mTabPos = 0;//第几个商品类型
    private String cId = "";//类型id
    private boolean isFirst = true;
    private Context context;
    private SPUtils spUtils;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("tag", "onCreateView()方法执行");
        context = ZhiBoPage.this.getContext();
        spUtils = new SPUtils(context,"db_user");
        View view = inflater.inflate(R.layout.zb_page, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        //设置页和当前页一致时加载，防止预加载
        if (isFirst && mTabPos == mSerial) {
            isFirst = false;
            sendMessage();
        }

        typeRecyclerView = view.findViewById(R.id.recy_type);
        typeRecyclerView.setLayoutManager(new GridLayoutManager(context,6));

        recyclerView = view.findViewById(R.id.recy);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(context,2));
        adapter = new BaseLoadMoreHeaderAdapter<GwBean>(context,recyclerView,mData,R.layout.zb_gw_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, GwBean o) {

                //封面图
                holder.setImageByUrl(R.id.iv_live_bg,o.getSnapshotPlayURL());


                //观看数量
                holder.setText(R.id.tv_see_count,o.getLookCount());

                if (null != o.getLiveGoodsList() && o.getLiveGoodsList().size()>0){
                    //商品图片
                    holder.setImageByUrl(R.id.iv_goods_img,o.getLiveGoodsList().get(0).getListUrl());
                    //商品标题
                    holder.setText(R.id.tv_goods_name,o.getLiveGoodsList().get(0).getName());
                    //商品价钱
                    holder.setText(R.id.tv_goods_price,"￥"+o.getLiveGoodsList().get(0).getSellingPrice());
                }else {
                    holder.getView(R.id.ll_live_goods).setVisibility(View.GONE);
                }

                //直播标题
                holder.setText(R.id.title,o.getLiveTitle());
                //主播名称
                holder.setText(R.id.tv_auth,o.getAnchorName());
                //主播头像

                SimpleDraweeView draweeView = holder.getView(R.id.drawee_img);
                draweeView.setImageURI(o.getHeadUrl());
            }
        };
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(ZhiBoPage.this.getContext(), PlayActivity.class);
                intent.putExtra("all_anchor",JsonUtil.obj2String(mData));
                intent.putExtra("fansTag","");
                intent.putExtra("name",mData.get(position).getAnchorName());
                intent.putExtra("head",mData.get(position).getHeadUrl());
                intent.putExtra("fanse",mData.get(position).getFans());
                intent.putExtra("anchorId",mData.get(position).getAnchorId());
                intent.putExtra("path",mData.get(position).getHlsPlayURL());
                intent.putExtra("tag",mData.get(position).getTag());
                intent.putExtra("userId",mData.get(position).getUserId());
                startActivity(intent);
            }
        });


        tvSeeCount1 = view.findViewById(R.id.tv_see_count1);
        ivLiveBg1 = view.findViewById(R.id.iv_live_bg1);
        ivGoodsImg1 = view.findViewById(R.id.iv_goods_img1);
        tvGoodsName1 = view.findViewById(R.id.tv_goods_name1);
        tvGoodsPrice1 = view.findViewById(R.id.tv_goods_price1);
        title1 = view.findViewById(R.id.title1);
        draweeImg1 = view.findViewById(R.id.drawee_img1);
        tvAuth1 = view.findViewById(R.id.tv_auth1);

        tvSeeCount = view.findViewById(R.id.tv_see_count);
        ivLiveBg = view.findViewById(R.id.iv_live_bg);
        ivGoodsImg = view.findViewById(R.id.iv_goods_img);
        tvGoodsName = view.findViewById(R.id.tv_goods_name);
        tvGoodsPrice = view.findViewById(R.id.tv_goods_price);
        title = view.findViewById(R.id.title);
        draweeImg = view.findViewById(R.id.drawee_img);
        tvAuth = view.findViewById(R.id.tv_auth);

        llOne = view.findViewById(R.id.ll_one);
        llTow = view.findViewById(R.id.ll_tow);
        llNo = view.findViewById(R.id.ll_no);
    }


    private TextView tvSeeCount1;
    private ImageView ivLiveBg1;
    private ImageView ivGoodsImg1;
    private TextView tvGoodsName1;
    private TextView tvGoodsPrice1;
    private TextView title1;
    private SimpleDraweeView draweeImg1;
    private TextView tvAuth1;

    private TextView tvSeeCount;
    private ImageView ivLiveBg;
    private ImageView ivGoodsImg;
    private TextView tvGoodsName;
    private TextView tvGoodsPrice;
    private TextView title;
    private SimpleDraweeView draweeImg;
    private TextView tvAuth;
    private LinearLayout llLiveGoods1;
    private LinearLayout llOne;
    private LinearLayout llTow;
    private LinearLayout llNo;


    /**
     * 获取数据
     * @param isLoad 是否是加载更多 true 是
     */
    private void setDate(final boolean isLoad) {

        //分类
        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(0),"精选",R.drawable.zb_one));
        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(1),"关注",R.drawable.zb_tow));
        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(2),"娱乐",R.drawable.zb_three));
        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(3),"海滩",R.drawable.zb_four));
        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(4),"美女",R.drawable.zb_one));

        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(5),"出海",R.drawable.zb_three));
        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(6),"海鲜",R.drawable.zb_tow));
        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(7),"音乐",R.drawable.zb_four));
        typeData.add(new FragmentGw.ZhiboPageMo(String.valueOf(8),"交友",R.drawable.zb_one));


        typeAdapter = new BaseLoadMoreHeaderAdapter<FragmentGw.ZhiboPageMo>(context,typeRecyclerView,typeData,R.layout.zhibo_type_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, FragmentGw.ZhiboPageMo o) {
                TextView textView = holder.getView(R.id.tv_title);
                textView.setBackgroundResource(o.getBackground());
                textView.setText(o.getName());
            }
        };
        typeRecyclerView.setAdapter(typeAdapter);

        typeAdapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, LbroaTypeActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });




        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
        map.put("playType","0");//cId
        ToastUtil.showShort(getContext(),""+cId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(GiftUrl.getFocusLiveRecords, map,
                new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result))return;

                try {
                    final JSONObject object = new JSONObject(result);
                    if (0 ==  object.optInt("errno")){
                        if (object.optInt("code") == 500)return;

                        String str =  object.optString("data");
                        mData = JsonUtil.string2Obj(str,List.class,GwBean.class);
                        if (mData!=null){
                            if (mData.size() == 0){
                                // TODO: 2019/8/23  设置主播显示
                                llNo.setVisibility(View.GONE);
                            }

                            if (mData.size()>1 && mData.size()<2){
                                llTow.setVisibility(View.VISIBLE);
                                llTow.setClickable(false);
                                final GwBean o = mData.get(0);


                                //封面图
                                Glide.with(context).load(o.getSnapshotPlayURL()).into(ivLiveBg1);

                                //观看数量
                                tvSeeCount1.setText(o.getLookCount());

                                if (null != o.getLiveGoodsList() && o.getLiveGoodsList().size()>0){
                                    //商品图片
                                    Glide.with(context).load(o.getLiveGoodsList().get(0).getListUrl()).into(ivGoodsImg1);
                                    //商品标题
                                    tvGoodsName1.setText(o.getLiveGoodsList().get(0).getName());
                                    //商品价钱
                                    tvGoodsPrice1.setText("￥"+o.getLiveGoodsList().get(0).getSellingPrice());
                                }else {
                                    llLiveGoods1.setVisibility(View.GONE);
                                }

                                //直播标题
                                title1.setText(o.getLiveTitle());
                                //主播名称
                                tvAuth1.setText(o.getAnchorName());
                                //主播头像
                                draweeImg1.setImageURI(o.getHeadUrl());


                                mData.remove(o);
                                adapter.updateData(mData);

                                llOne.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ZhiBoPage.this.getContext(), PlayActivity.class);
                                        intent.putExtra("all_anchor",JsonUtil.obj2String(mData));
                                        intent.putExtra("fansTag","");
                                        intent.putExtra("name",o.getAnchorName());
                                        intent.putExtra("head",o.getHeadUrl());
                                        intent.putExtra("fanse",o.getFans());
                                        intent.putExtra("anchorId",o.getAnchorId());
                                        intent.putExtra("path",o.getHlsPlayURL());
                                        intent.putExtra("tag",o.getTag());
                                        intent.putExtra("userId",o.getUserId());
                                        startActivity(intent);
                                    }
                                });
                            }
                            if (mData.size()>2){
                                llTow.setClickable(true);
                                GwBean o = mData.get(0);
                                final GwBean o2 = mData.get(1);

                                //封面图
                                String testUrl = "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3982262708,3993837289&fm=26&gp=0.jpg";
                                String testUrl2 = "http://upload.mnw.cn/2015/0807/1438931855592.jpg";
                                //o.getSnapshotPlayURL()
                                Glide.with(context).load(testUrl).into(ivLiveBg1);
                                Glide.with(context).load(testUrl2).into(ivLiveBg);

                                //观看数量
                                tvSeeCount1.setText(o.getLookCount());
                                tvSeeCount.setText(o2.getLookCount());


                                if (null != o.getLiveGoodsList() && o.getLiveGoodsList().size()>0){
                                    //商品图片
                                    Glide.with(context).load(o.getLiveGoodsList().get(0).getListUrl()).into(ivGoodsImg1);
                                    Glide.with(context).load(o2.getLiveGoodsList().get(0).getListUrl()).into(ivGoodsImg);
                                    //商品标题
                                    tvGoodsName1.setText(o.getLiveGoodsList().get(0).getName());
                                    tvGoodsName.setText(o2.getLiveGoodsList().get(0).getName());
                                    //商品价钱
                                    tvGoodsPrice1.setText("￥"+o.getLiveGoodsList().get(0).getSellingPrice());
                                    tvGoodsPrice.setText("￥"+o2.getLiveGoodsList().get(0).getSellingPrice());
                                }else {
                                    llLiveGoods1.setVisibility(View.GONE);
                                }

                                //直播标题
                                title1.setText(o.getLiveTitle());
                                title.setText(o2.getLiveTitle());
                                //主播名称
                                tvAuth1.setText(o.getAnchorName());
                                tvAuth.setText(o2.getAnchorName());
                                //主播头像
                                draweeImg1.setImageURI(o.getHeadUrl());
                                draweeImg.setImageURI(o2.getHeadUrl());

                                mData.remove(o);
                                mData.remove(o2);
                                adapter.updateData(mData);
                                String testUrl3 = "http://i2.17173.itc.cn/2015/live/04/24/xiuchang.jpg";
                                for (int i = 0; i < mData.size(); i++) {
                                    if (i%2 == 0){
                                        mData.get(i).setSnapshotPlayURL(testUrl);
                                    }
                                    if (i%3 == 0){
                                        mData.get(i).setSnapshotPlayURL(testUrl2);
                                    }
                                    if (i%5 == 0){
                                        mData.get(i).setSnapshotPlayURL(testUrl3);
                                    }
                                }

                                llTow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ZhiBoPage.this.getContext(), PlayActivity.class);
                                        intent.putExtra("all_anchor",JsonUtil.obj2String(mData));
                                        intent.putExtra("fansTag","");
                                        intent.putExtra("name",o2.getAnchorName());
                                        intent.putExtra("head",o2.getHeadUrl());
                                        intent.putExtra("fanse",o2.getFans());
                                        intent.putExtra("anchorId",o2.getAnchorId());
                                        intent.putExtra("path",o2.getHlsPlayURL());
                                        intent.putExtra("tag",o2.getTag());
                                        intent.putExtra("userId",o2.getUserId());
                                        startActivity(intent);
                                    }
                                });
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

    public ZhiBoPage(int serial) {
        mSerial = serial;
    }

    public void sendMessage() {
        Message message = handler.obtainMessage();
        message.sendToTarget();
    }

    public void setTabPos(int mTabPos, String cId) {
        this.mTabPos = mTabPos;
        this.cId = cId;
    }
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (!IS_LOADED) {
                IS_LOADED = true;
                //这里执行加载数据的操作
                page = 1;
                setDate(false);
            }
        }
    };
}
