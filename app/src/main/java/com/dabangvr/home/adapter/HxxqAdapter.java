package com.dabangvr.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.fragment.FgImg;
import com.dabangvr.common.fragment.FgVideo;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.common.weight.SimpleFragmentPagerAdapter;
import com.dabangvr.common.weight.onLoadMoreListener;
import com.dabangvr.model.Goods;
import com.dabangvr.model.GoodsMo;
import com.dabangvr.util.CountDownUtil;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.MyWebViewClient;
import com.dabangvr.util.TextUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
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
import okhttp3.Call;

public class HxxqAdapter extends RecyclerView.Adapter<BaseRecyclerHolder>{
    private Context context;
    private int buyType; //0 1 2
    private int COMMENT_FIRST = 0;
    private int COMMENT_SECOND = 1;
    private GoodsMo goodsMo;
    private List<Fragment> mFragments;
    private FragmentManager fragmentManager;
    private String endTime;//拼团结束时间
    private String kucun;


    private int page;
    private RecyclerView goodsRecy;
    private BaseLoadMoreHeaderAdapter goodsAdapter;
    private List<Goods> goodsData = new ArrayList<>();

    public HxxqAdapter(Context context, String kucun,int buyType,FragmentManager fragmentManager, GoodsMo goodsMo, String endTime) {
        this.context = context;
        this.kucun = kucun;
        this.buyType = buyType;
        this.fragmentManager = fragmentManager;
        this.goodsMo = goodsMo;
        this.endTime = endTime;
    }

    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == COMMENT_FIRST) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.hxxq_top, viewGroup, false);
            return BaseRecyclerHolder.getRecyclerHolder(context, view);
        }
        if (viewType == COMMENT_SECOND) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.hxxq_bottom, viewGroup, false);
            return BaseRecyclerHolder.getRecyclerHolder(context, view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final BaseRecyclerHolder holder, int position) {
        if (position == COMMENT_FIRST) {
            //设置显示图片和视频
//            TabLayout tabLayout = holder.getView(R.id.hxxq_tablayout);
//            ViewPager viewPager = holder.getView(R.id.hxxq_viewpager);
            //setImgAndVideoView(tabLayout, viewPager, goodsMo.getImgList());

            //商品信息
            String info = "";
            if (!StringUtils.isEmpty(goodsMo.getDescribe()) && !goodsMo.getDescribe().equals("null")) {
                holder.setText(R.id.hx_info,goodsMo.getDescribe());
            } else if (!StringUtils.isEmpty(goodsMo.getName()) && !goodsMo.getName().equals("null")) {
                holder.setText(R.id.hx_info,goodsMo.getName());
            } else {
                holder.setText(R.id.hx_info,info);
            }

            //市场价
            if (!goodsMo.getMarketPrice().equals("null")) {
                TextView marketPrice =  holder.getView(R.id.hx_money_y);
                marketPrice.setText(goodsMo.getMarketPrice());
                marketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }


            //实际价
            if (!goodsMo.getSellingPrice().equals("null")) {
                holder.setText(R.id.hx_money_s,goodsMo.getSellingPrice());
            }

            //评论数量
            if (StringUtils.isEmpty(goodsMo.getCommentListSize()) || goodsMo.getCommentListSize().equals("null")) {
                holder.setText(R.id.hx_comment_count,"0");
            } else {
                holder.setText(R.id.hx_comment_count,goodsMo.getCommentListSize());
            }

            //库存
            if (kucun.equals("null") || StringUtils.isEmpty(kucun)) {
                holder.setText(R.id.kucun,"剩余:0");
            } else {
                holder.setText(R.id.kucun,"剩余:"+ kucun);
            }

            //倒计时
            if (buyType == 0) {//普通商品隐藏倒计时
                holder.getView(R.id.is_ms_or_pt).setVisibility(View.GONE);
            } else {
                holder.getView(R.id.is_ms_or_pt).setVisibility(View.VISIBLE);

                //如果是拼团商品，则时间不为空才倒计时
                if (!StringUtils.isEmpty(endTime) && !endTime.equals("null")) {
                    CountDownUtil downUtil = new CountDownUtil();
                    downUtil.start(Long.parseLong(endTime), new CountDownUtil.OnCountDownCallBack() {
                        @Override
                        public void onProcess(int day, int hour, int minute, int second) {
                            holder.setText(R.id.hx_countdown_timer,day + "天 " + hour + "时 " + minute + "分 " + second + "秒");
                        }

                        @Override
                        public void onFinish() {
                            holder.setText(R.id.hx_countdown_timer,"活动结束啦");
                            //发送广播
                            Intent intent = new Intent("android.intent.action.END_TIME_FINISH");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }
                    });

                } else {//否则隐藏倒计时控件
                    holder.getView(R.id.hx_countdown_timer).setVisibility(View.GONE);
                }
            }
        }
        if (position == COMMENT_SECOND) {
            //详情图片
            if (!TextUtil.isEmpty(goodsMo.getGoodsDesc())) {
                WebView webView = holder.getView(R.id.hx_bom_web_view);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存
                webView.getSettings().setUserAgentString(System.getProperty("http.agent"));
                webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把html中的内容放大webview等宽的一列中
                webView.getSettings().setAppCacheEnabled(true);
                webView.getSettings().setDomStorageEnabled(true);
                webView.setWebViewClient(new MyWebViewClient(webView));
                webView.loadData(goodsMo.getGoodsDesc(), "text/html", "UTF-8");
            }


            //其他商品
            getGoodsListFromHttp(false);

            goodsRecy = holder.getView(R.id.orther_recy);
            GridLayoutManager manager = new GridLayoutManager(context,2);
            goodsRecy.setLayoutManager(manager);
            goodsAdapter = new BaseLoadMoreHeaderAdapter<Goods>(context,goodsRecy,goodsData,R.layout.new_release_item) {
                @Override
                public void convert(Context mContext, BaseRecyclerHolder holder, Goods o) {
                    holder.setImageByUrl(R.id.new_item_img,o.getListUrl());
                    holder.setText(R.id.new_item_msg,o.getName());
                    holder.setText(R.id.new_item_salse,o.getSellingPrice());
                }
            };
            goodsRecy.setAdapter(goodsAdapter);
            goodsRecy.addOnScrollListener(new onLoadMoreListener() {
                @Override
                protected void onLoading(int countItem, int lastItem) {
                    getGoodsListFromHttp(true);
                }
            });


        }
    }

    /**
     * 根据position的不同选择不同的布局类型
     */
    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0: {
                return COMMENT_FIRST;
            }
            case 1:
                return COMMENT_SECOND;
        }
        return position;
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return 2;
    }




    private void setImgAndVideoView(TabLayout tableLayout, ViewPager viewPager, JSONArray tabTitles) {
        final String[] list = new String[tabTitles.length()];
        mFragments = new ArrayList<Fragment>();
        //遍历资源后缀名，如果是图片则fragment的数组添加的是展示图片的fragment，如果是视频则展示的是视频的fragment
        for (int i = 0; i < tabTitles.length(); i++) {
            try {
                String imgAndVideo = (String) tabTitles.get(i);
                String prefix = imgAndVideo.substring(imgAndVideo.lastIndexOf(".") + 1);
                if (prefix.equals("jpg") || prefix.equals("png") || prefix.equals("gif")) {
                    FgImg fgImg = new FgImg(0, imgAndVideo);
                    fgImg.setTabPos(i);
                    mFragments.add(fgImg);
                    list[i] = prefix;
                }
                if (prefix.equals("mp4")) {
                    FgVideo fgVideo = new FgVideo(imgAndVideo);
                    mFragments.add(fgVideo);
                    list[i] = prefix;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //适配tablayout和viewpager联动
        final SimpleFragmentPagerAdapter mAdapter = new SimpleFragmentPagerAdapter(fragmentManager, mFragments, list);
        viewPager.setAdapter(mAdapter);
        tableLayout.setupWithViewPager(viewPager);

        //设置当前显示哪个标签页
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                //滑动监听加载数据，一次只加载一个标签页
                String name = list[position];
                String prefix = name.substring(name.lastIndexOf(".") + 1);
                if (prefix.equals("jpg") || prefix.equals("png") || prefix.equals("gif")) {
                    ((FgImg) mAdapter.getItem(position)).sendMessage();
                }
                if (prefix.equals("mp4")) {
                    //((FgVideo) mAdapter.getItem(position)).sendMessage();
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    private void getGoodsListFromHttp(final boolean isLoad) {
        if (isLoad){
            page++;
        }else {
            page = 1;
        }
        Map<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(page));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getLiveGoodgsList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (0 == errno) {
                        String dataArr = object.optString("data");
                        List<Goods> goods = JsonUtil.string2Obj(dataArr, List.class, Goods.class);
                        if (goods != null && goods.size() > 0) {
                            if (isLoad) {
                                goodsAdapter.addAll(goods);
                            } else {
                                goodsAdapter.updateData(goods);
                            }
                            goodsData = goodsAdapter.getData();
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
}
