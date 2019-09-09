package com.dabangvr.video.zb;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.dabangvr.R;
import com.dabangvr.base.BaseFragment;
import com.dabangvr.model.Goods;
import com.dabangvr.util.BannerStart;
import com.dabangvr.util.DensityUtil;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.video.adapter.BGANormalRefreshViewHolder;
import com.dabangvr.video.adapter.ItemOnClickListener;
import com.dabangvr.video.adapter.ThreadUtil;
import com.dabangvr.view.home.SpacesItemDecoration;
import com.dabangvr.widget.GridDividerItemDecoration;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;


public class VideoSonZBragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    @BindView(R.id.recycler_goods_id)
    RecyclerView recyclerViewGoods;
    @BindView(R.id.refreshLayout)
    BGARefreshLayout mRefreshLayout;
    private int intPage = 1;

    private VideoHeaderAdapter videoHeaderAdapter;
    private List<Goods> mData = new ArrayList<>();
    private String type;
    private Banner homeBanner;
    private int mNewPageNumber = 0;
    private int mMorePageNumber = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceState = getArguments();
        if (savedInstanceState != null) {
            type = savedInstanceState.getString("type");
        }
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_vieo_son_layout;
    }

    @Override
    public void initView() {
        BGANormalRefreshViewHolder moocStyleRefreshViewHolder = new BGANormalRefreshViewHolder(getActivity(), true);
        moocStyleRefreshViewHolder.setRefreshLayout(mRefreshLayout);
        mRefreshLayout.setRefreshViewHolder(moocStyleRefreshViewHolder);
        initRecycler();

    }

    @Override
    public void initData() {
        mRefreshLayout.setDelegate(this);
        setDate(intPage);
        initBanner();

    }

    /**
     * 初始化轮播图
     */

    private void initBanner() {
        BannerStart.starBanner(getContext(), homeBanner, "1");
    }

    /**
     * 初始化主播列表
     */
    private void initRecycler() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewGoods.setLayoutManager(gridLayoutManager);
        videoHeaderAdapter = new VideoHeaderAdapter(mData, getContext());
        View inflate = View.inflate(getContext(), R.layout.item_good_header_layout, null);
        homeBanner = inflate.findViewById(R.id.home_banner);
        videoHeaderAdapter.addHeaderView(inflate);
        recyclerViewGoods.addItemDecoration(new GridDividerItemDecoration(DensityUtil.dip2px(getContext(), 7), ContextCompat.getColor(getContext(), R.color.color_00d8d6d6)));
        recyclerViewGoods.setAdapter(videoHeaderAdapter);
        videoHeaderAdapter.setItemOnClickListener(new ItemOnClickListener() {
            @Override
            public void onClickListener(int position) {
                Log.d("luhuas", "onClickListener点击主播: " + position);
            }
        });
    }


    /**
     * 获取网络数据
     * <p>
     * 第一个item   my_orther_item_dep
     * 第二个item   my_orther_item_goods
     */
    private void setDate(int page) {
        for (int i = 0; i < 9; i++) {
            Goods goods = new Goods();
            goods.setId("222");
            goods.setListUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565973538408&di=f2e529d512a8fd531fb7a82594332098&imgtype=0&src=http%3A%2F%2Fs1.sinaimg.cn%2Fmw690%2F006fmRRJzy745Jb6KxG00%26690");
            mData.add(goods);
        }
//        videoSonAdapter.setData(mData);
//        final HashMap<String, String> map = new HashMap<>();
//        map.put("page", String.valueOf(page));
//        map.put("limit", "10");
//        map.put("type", String.valueOf(typex));
//        //ToastUtil.showShort(context,"加载的类型是:"+typex+","+type);
//        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoodsLists, map, new GsonObjectCallback<String>(DyUrl.BASE) {
//            //主线程处理
//            @Override
//            public void onUi(String newsBean) {
//                if (StringUtils.isEmpty(newsBean)) {
//                    Toast.makeText(getActivity(), "获取数据失败", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                try {
//                    JSONObject object = new JSONObject(newsBean);
//                    int err = object.optInt("errno");
//                    if (err == 0) {
//                        if (object.optInt("code") == 500) {
//                            //ToastUtil.showShort(context, "服务数据更新中...");
//                            return;
//                        }
//                        JSONObject data = object.optJSONObject("data");
//                        String str = data.optString("goodsList");
////                        mData=JsonUtil.string2Obj(str, List.class, Goods.class);
//                        Collection<? extends Goods> goods = (Collection<? extends Goods>) JsonUtil.string2Obj(str, List.class, Goods.class);
//                        if (goods != null && goods.size() > 0) {
//                            mData.addAll(goods);
//                            videoSonAdapter.setData(mData);
//                        }
//
////                        adapter.updateData(mData);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            //请求失败
//            @Override
//            public void onFailed(Call call, IOException e) {
//
//            }
//        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (homeBanner != null) {
            homeBanner.stopAutoPlay();
        }
    }


    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mNewPageNumber++;
        if (mNewPageNumber > 4) {
            mRefreshLayout.endRefreshing();
            ToastUtil.showShort(getActivity(), "没有最新数据了");
            return;
        }
        final List<Goods> list =new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            Goods goods = new Goods();
            goods.setId("下拉刷新");
            goods.setListUrl("https://img.lovebuy99.com/uploads/allimg/190819/15-1ZQ9205339.jpg");
            list.add(goods);
        }

        ThreadUtil.runInUIThread(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.endRefreshing();
                videoHeaderAdapter.addNewData(list);
                recyclerViewGoods.smoothScrollToPosition(0);
            }
        }, 1500);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        mMorePageNumber++;
        if (mMorePageNumber > 4) {
            mRefreshLayout.endLoadingMore();
            ToastUtil.showShort(getActivity(), "没有更多数据了");
            return false;
        }
        final List<Goods> list =new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            Goods goods = new Goods();
            goods.setId("加载更多");
            goods.setListUrl("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3441742992,2765570575&fm=26&gp=0.jpg");
            list.add(goods);
        }

        ThreadUtil.runInUIThread(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.endLoadingMore();
                videoHeaderAdapter.addMoreData(list);

            }
        }, 1500);


        return true;
    }
}
