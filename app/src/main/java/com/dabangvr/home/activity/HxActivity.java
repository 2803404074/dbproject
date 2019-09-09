package com.dabangvr.home.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.WithHint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.activity.CartActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.home.activity.two.LeftListAdapter;
import com.dabangvr.home.activity.two.LeftSortBean;
import com.dabangvr.home.activity.two.RightBean;
import com.dabangvr.home.activity.two.RightListAdapter;
import com.dabangvr.home.activity.two.ScrollTopLayoutManager;
import com.dabangvr.model.GoodsCategoryJson;
import com.dabangvr.model.TypeBean;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.ToastUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import config.DyUrl;
import okhttp3.Call;

/**
 * 海鲜跳转的页面
 */
public class HxActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener {
    private Unbinder mUnbinder;
    @BindView(R.id.mRvLeft)
    RecyclerView mRvLeft;
    @BindView(R.id.mRvRight)
    RecyclerView mRvRight;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    private View mRightFooterView;

    private List<LeftSortBean> mLeftSortBeans;
    private LeftListAdapter mLeftAdapter;

    private List<RightBean> mRightBeans;
    private RightListAdapter mRightAdapter;
    public static final int COLUMN_NUM = 3;
    private boolean isSelectLeftList;
    public static HxActivity instant;
    private List<GoodsCategoryJson> list = new ArrayList<>();
    ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_hx;
    }

    @Override
    protected void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("page", "1");
        map.put("limit", "10000");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.CategoryList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (!StringUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (0 == object.optInt("errno")) {
                            JSONObject data = object.optJSONObject("data");
                            String listStr = data.optString("goodsCategoryJson");
                            list = JsonUtil.string2Obj(listStr, List.class, GoodsCategoryJson.class);
                            initLeftSortRecyclerView();
                            initRightRecyclerView();
                            for (GoodsCategoryJson dataBean : list) {
                                mLeftSortBeans.add(new LeftSortBean(dataBean.getName()));
                                mRightBeans.add(new RightBean(dataBean.getName()));
                                if (dataBean.getChildren() == null || dataBean.getChildren().size() <= 0) {
                                    continue;
                                }
                                for (GoodsCategoryJson.ChildrenBean listBean : dataBean.getChildren()) {
                                    mRightBeans.add(new RightBean(listBean.getName(), listBean.getId(), listBean.getCategoryImg(), dataBean.getName(), dataBean.getName()));
                                }
                            }
                            mLeftAdapter.notifyDataSetChanged();
                            mRightAdapter.notifyDataSetChanged();
                            mTvTitle.setText(mLeftSortBeans.get(0).getTypeName());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
            }
        });

    }


    private void initLeftSortRecyclerView() {
        mLeftSortBeans = new ArrayList<>();
        mRvLeft.setLayoutManager(new LinearLayoutManager(this));
        mRvLeft.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        ((SimpleItemAnimator) mRvLeft.getItemAnimator()).setSupportsChangeAnimations(false);
        mLeftAdapter = new LeftListAdapter(R.layout.adapter_left_sort, mLeftSortBeans);
        mRvLeft.setAdapter(mLeftAdapter);

        mLeftAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                isSelectLeftList = true;
                mLeftAdapter.setSelectPosition(position);

                //选中左边分类，使得右边滚动到指定位置
                LeftSortBean sortBean = mLeftSortBeans.get(position);
                int rightPosition = findRightItemPositionBySortName(sortBean.getTypeName());
                if (rightPosition != -1) {
                    mRvRight.smoothScrollToPosition(rightPosition);
                    mTvTitle.setText(sortBean.getTypeName());
                }
            }
        });
    }

    /**
     * 跟据分组名获取分组名在右侧的位置
     *
     * @param name
     * @return
     */
    private int findRightItemPositionBySortName(String name) {
        RightBean bean = null;
        for (int i = 0; i < mRightBeans.size(); i++) {
            bean = mRightBeans.get(i);
            if (TextUtils.equals(name, bean.getName())) {
                return i;
            }
        }
        return -1;
    }

    private void initRightRecyclerView() {
        mRightFooterView = LayoutInflater.from(this).inflate(R.layout.footer_empty, null);
        mRightBeans = new ArrayList<>();
        mRightAdapter = new RightListAdapter(mRightBeans, this);
        mRvRight.setLayoutManager(new ScrollTopLayoutManager(this, COLUMN_NUM));
        mRightAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                RightBean bean = mRightBeans.get(position);
                return bean != null && bean.getItemType() == RightBean.TYPE_TITLE ? COLUMN_NUM : 1;
            }
        });
        mRvRight.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isSelectLeftList = false;
                    changeLeftListSelectState();

                    LinearLayoutManager manager = (LinearLayoutManager) mRvRight.getLayoutManager();
                    int lastCompleteVisiblePosition = manager.findLastCompletelyVisibleItemPosition();
                    if (lastCompleteVisiblePosition == mRightBeans.size() - 1 && mRightAdapter.getFooterLayoutCount() == 0) {
                        //已经滑动到最后一个完全显示的item并且之前没有添加过底部填充
                        View lastItemView = manager.findViewByPosition(findLastGroupTitlePositionInRightLists());
                        if (lastItemView == null) {
                            return;
                        }
                        //设置填充高度，使得最后一个分类标题能够置顶悬浮
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, lastItemView.getTop());
                        mRightFooterView.setLayoutParams(params);
                        mRightAdapter.setFooterView(mRightFooterView);
                        mRvRight.scrollBy(0, lastItemView.getTop());
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isSelectLeftList) {
                    changeLeftListSelectState();
                }
            }
        });
        mRightAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                RightBean rightBean = mRightBeans.get(position);
                Toast.makeText(getApplicationContext(), rightBean.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        mRvRight.setAdapter(mRightAdapter);
    }

    /**
     * 改变左侧列表选中状态
     */
    private void changeLeftListSelectState() {
        LinearLayoutManager manager = (LinearLayoutManager) mRvRight.getLayoutManager();
        int firstVisiblePosition = manager.findFirstVisibleItemPosition();
        RightBean bean = mRightAdapter.getItem(firstVisiblePosition);
        if (bean != null) {
            int leftSelectPos = findLeftItemPositionBySortName(bean.getGroupName());
            mLeftAdapter.setSelectPosition(leftSelectPos);
            mRvLeft.smoothScrollToPosition(leftSelectPos);
            if (isChangeGroup(firstVisiblePosition)) {
                mTvTitle.setText(bean.getGroupName());
            }
        }
    }

    /**
     * 获取右侧列表最后一个分组白标题的位置
     *
     * @return
     */
    private int findLastGroupTitlePositionInRightLists() {
        RightBean bean = null;
        for (int i = mRightBeans.size() - 1; i >= 0; i--) {
            bean = mRightBeans.get(i);
            if (bean.getItemType() == RightBean.TYPE_TITLE) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据f分组名名获取左侧列表的位置
     *
     * @param name
     * @return
     */
    private int findLeftItemPositionBySortName(String name) {
        LeftSortBean bean = null;
        for (int i = 0; i < mLeftSortBeans.size(); i++) {
            bean = mLeftSortBeans.get(i);
            if (TextUtils.equals(name, bean.getTypeName())) {
                return i;
            }
        }
        return -1;
    }


    private boolean isChangeGroup(int position) {
        GridLayoutManager manager = (GridLayoutManager) mRvRight.getLayoutManager();
        RightBean bean = mRightAdapter.getItem(position);
        int column = manager.getSpanCount();
        boolean isChanged = false;
        int dataSize = mRightBeans.size();

        //右侧列表往下滑动时，若第一个可见的是标题之前的item列表，表示可以改变分组名
        if (bean != null && bean.getItemType() == RightBean.TYPE_ITEM) {
            for (int i = 1; i <= column; i++) {
                if (position + i < dataSize) {
                    isChanged = isChanged || mRightBeans.get(position + i).getItemType() == RightBean.TYPE_TITLE;
                }
            }
        } else if (bean != null && bean.getItemType() == RightBean.TYPE_TITLE) {
            //从下往上滑动，第一个可见的是标题
            isChanged = true;
        }

        return isChanged;
    }

    @Override
    protected void initView() {
        instant = this;
        //返回
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //搜索
        findViewById(R.id.ll_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HxActivity.this, SearchActivity.class));
            }
        });

        //消息
        findViewById(R.id.mess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建弹出式菜单对象（最低版本11）
                PopupMenu popup = new PopupMenu(HxActivity.this, v);//第二个参数是绑定的那个view
                //获取菜单填充器
                MenuInflater inflater = popup.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.main_menu, popup.getMenu());
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(HxActivity.this);
                //显示(这一行代码不要忘记了)
                popup.show();
            }
        });
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart: {
                Intent intent = new Intent(HxActivity.this, CartActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.action_mess: {
                break;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 88) {
            if (resultCode == 88) {

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
