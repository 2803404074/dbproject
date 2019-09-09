package com.example.gangedrecyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gangedrecyclerview.recy.HideScrollListener;
import com.example.potholibrary.R;
import com.rey.material.app.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class SortDetailFragment extends BaseFragment<SortDetailPresenter, String> implements CheckListener, DialogInterface.OnDismissListener {
    private RecyclerView mRv;
    private ClassifyDetailAdapter mAdapter;
    private GridLayoutManager mManager;
    private List<RightBean> mDatas = new ArrayList<>();
    private ItemHeaderDecoration mDecoration;
    private boolean move = false;
    private int mIndex = 0;
    private CheckListener checkListener;


    public interface onClickGoods {
        void show(
                String id,
                String name,
                String listUrl,
                String price,
                String remainingInventory,
                List<GoodsDetails.GoodsSpecVoList> specVoLists,
                List<GoodsDetails.ProductInfoVoList> productInfoVoLists);
    }

    private onClickGoods onClickGoods;


    public void setOnClickGoods(onClickGoods onClickGoods) {
        this.onClickGoods = onClickGoods;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sort_detail;
    }

    @Override
    protected void initCustomView(View view) {
        mRv = (RecyclerView) view.findViewById(R.id.rv);

    }

    @Override
    protected void initListener() {
        mRv.addOnScrollListener(new RecyclerViewListener());
    }

    @Override
    protected SortDetailPresenter initPresenter() {
        showRightPage(1);
        //每个条目设置显示数量
        mManager = new GridLayoutManager(mContext, 1);
        //通过isTitle的标志来判断是否是title
        mManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mDatas.get(position).isTitle() ? 1 : 1;
            }
        });
        mRv.setLayoutManager(mManager);
        mAdapter = new ClassifyDetailAdapter(mContext, mDatas, new RvListener() {
            @Override
            public void onItemClick(int id, int position) {
                String content = "";
                if (id == R.id.root) {
                    content = "title";

                } else if (id == R.id.content) {
                    content = "content";
                }
//                Snackbar snackbar = Snackbar.make(mRv, "当前点击的是" + content + ":" + mDatas.get(position).getName(), Snackbar.LENGTH_SHORT);
//                View mView = snackbar.getView();
//                mView.setBackgroundColor(Color.BLUE);
//                TextView text = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
//                text.setTextColor(Color.WHITE);
//                text.setTextSize(25);
//                snackbar.show();

                if (!mDatas.get(position).isTitle()) {
                    onClickGoods.show(mDatas.get(position).getId(),
                            mDatas.get(position).getName(),
                            mDatas.get(position).getListUrl(),
                            mDatas.get(position).getSellingPrice(),
                            mDatas.get(position).getRemainingInventory(),
                            mDatas.get(position).getDeliveryGoodsSpecVoList(),
                            mDatas.get(position).getDeliveryProductInfoVoList());
                }
                //showBottomDialog();

            }
        });

        mRv.setAdapter(mAdapter);
        mDecoration = new ItemHeaderDecoration(mContext, mDatas);
        mRv.addItemDecoration(mDecoration);
        mDecoration.setCheckListener(checkListener);
        initData();
        return new SortDetailPresenter();
    }


    private void initData() {
        ArrayList<DepSortBean.CategoryOneArrayBean> rightList = getArguments().getParcelableArrayList("right");

        //设置分类
        for (int i = 0; i < rightList.size(); i++) {
            RightBean head = new RightBean(rightList.get(i).getName());
            //头部设置为true
            head.setTitle(true);
            head.setTitleName(rightList.get(i).getName());
            head.setTag(String.valueOf(i));
            mDatas.add(head);

            //设置商品
            List<DepSortBean.CategoryOneArrayBean.CategoryTwoArrayBean> categoryTwoArray = rightList.get(i).getCategoryTwoArray();
            if (null != categoryTwoArray && categoryTwoArray.size()>0){
                for (int j = 0; j < categoryTwoArray.size(); j++) {
                    RightBean body = new RightBean(categoryTwoArray.get(j).getName());
                    body.setTag(String.valueOf(i));
                    String name = rightList.get(i).getName();
                    String id = categoryTwoArray.get(j).getId();
                    String listUrl = categoryTwoArray.get(j).getListUrl();

                    String price = categoryTwoArray.get(j).getDeliverPrice();
                    String pPrice = categoryTwoArray.get(j).getPackagingPrice();
                    String sPrice = categoryTwoArray.get(j).getStartingPrice();
                    String rPrice = categoryTwoArray.get(j).getSellingPrice();
                    String salesNum = categoryTwoArray.get(j).getSalesVolume();
                    String remainingInventory = categoryTwoArray.get(j).getRemainingInventory();
                    String marketPrice = categoryTwoArray.get(j).getMarketPrice();

                    body.setDeliverPrice(price);
                    body.setPackagingPrice(pPrice);
                    body.setStartingPrice(sPrice);
                    body.setSalesVolume(salesNum);
                    body.setSellingPrice(rPrice);
                    body.setRemainingInventory(remainingInventory);
                    body.setRemainingInventory(marketPrice);


                    body.setTitleName(name);
                    body.setId(id);
                    body.setListUrl(listUrl);

                    body.setDeliveryGoodsSpecVoList(categoryTwoArray.get(j).getDeliveryGoodsSpecVoList());
                    body.setDeliveryProductInfoVoList(categoryTwoArray.get(j).getDeliveryProductInfoVoList());

                    mDatas.add(body);
                }
            }
        }

        mAdapter.notifyDataSetChanged();
        mDecoration.setData(mDatas);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void refreshView(int code, String data) {

    }

    public void setData(int n) {
        mIndex = n;
        mRv.stopScroll();
        smoothMoveToPosition(n);
    }

    @Override
    protected void getData() {

    }

    public void setListener(CheckListener listener) {
        this.checkListener = listener;
    }

    private void smoothMoveToPosition(int n) {
        int firstItem = mManager.findFirstVisibleItemPosition();
        int lastItem = mManager.findLastVisibleItemPosition();
        Log.d("first--->", String.valueOf(firstItem));
        Log.d("last--->", String.valueOf(lastItem));
        if (n <= firstItem) {
            mRv.scrollToPosition(n);
        } else if (n <= lastItem) {
            Log.d("pos---->", String.valueOf(n) + "VS" + firstItem);
            int top = mRv.getChildAt(n - firstItem).getTop();
            Log.d("top---->", String.valueOf(top));
            mRv.scrollBy(0, top);
        } else {
            mRv.scrollToPosition(n);
            move = true;
        }
    }


    @Override
    public void check(int position, boolean isScroll) {
        checkListener.check(position, isScroll);

    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    private HideScrollListener listener;

    public void setListenerx(HideScrollListener listener){
        this.listener = listener;
    }

    private class RecyclerViewListener extends RecyclerView.OnScrollListener {

        private static final int THRESHOLD = 20;
        private int distance = 0;
        private boolean visible = true;//是否可见

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (move && newState == RecyclerView.SCROLL_STATE_IDLE) {
                move = false;
                int n = mIndex - mManager.findFirstVisibleItemPosition();
                Log.d("n---->", String.valueOf(n));
                if (0 <= n && n < mRv.getChildCount()) {
                    int top = mRv.getChildAt(n).getTop();
                    Log.d("top--->", String.valueOf(top));
                    mRv.smoothScrollBy(0, top);
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (move) {
                move = false;
                int n = mIndex - mManager.findFirstVisibleItemPosition();
                if (0 <= n && n < mRv.getChildCount()) {
                    int top = mRv.getChildAt(n).getTop();
                    mRv.scrollBy(0, top);
                }
            }

            if (distance > THRESHOLD && visible) {
                //隐藏动画
                visible = false;
                listener.onHide();
                distance = 0;
            } else if (distance < -20 && !visible) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                if(firstCompletelyVisibleItemPosition==0){
                    //显示动画
                    visible = true;
                    listener.onShow();
                    distance = 0;
                }
            }

            if (visible && dy > 0 || (!visible && dy < 0)) {
                distance += dy;
            }

        }
    }


}
