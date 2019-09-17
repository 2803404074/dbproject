package com.dabangvr.common.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.home.activity.HxxqLastActivity;
import com.dabangvr.home.activity.OrderActivity;
import com.dabangvr.model.ShoppingCarDataBean;
import com.dabangvr.util.ToastUtil;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.DyUrl;
import okhttp3.Call;


/**
 * 购物车的adapter
 * 因为使用的是ExpandableListView，所以继承BaseExpandableListAdapter
 */
public class ShoppingCarAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final LinearLayout llSelectAll;
    private final ImageView ivSelectAll;
    private final Button btnOrder;
    private final Button btnDelete;
    private final RelativeLayout rlTotalPrice;
    private final TextView tvTotalPrice;
    private List<ShoppingCarDataBean.DatasBean> data;
    private boolean isSelectAll = false;
    private double total_price;
    private String token;

    private SendServerCallback sendServerCallback;

    public void setSendServerCallback(SendServerCallback sendServerCallback) {
        this.sendServerCallback = sendServerCallback;
    }

    public interface SendServerCallback{
        void show();
        void hide();
    }

    public ShoppingCarAdapter(Context context, String token, LinearLayout llSelectAll,
                              ImageView ivSelectAll, Button btnOrder, Button btnDelete,
                              RelativeLayout rlTotalPrice, TextView tvTotalPrice) {
        this.context = context;
        this.token = token;
        this.llSelectAll = llSelectAll;
        this.ivSelectAll = ivSelectAll;
        this.btnOrder = btnOrder;
        this.btnDelete = btnDelete;
        this.rlTotalPrice = rlTotalPrice;
        this.tvTotalPrice = tvTotalPrice;
    }

    /**
     * 自定义设置数据方法；
     * 通过notifyDataSetChanged()刷新数据，可保持当前位置
     *
     * @param data 需要刷新的数据
     */
    public void setData(List<ShoppingCarDataBean.DatasBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        if (data != null && data.size() > 0) {
            return data.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_shopping_car_group, null);

            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        final ShoppingCarDataBean.DatasBean datasBean = data.get(groupPosition);
        //店铺ID
        String store_id = datasBean.getDeptId();
        //店铺名称
        String store_name = datasBean.getDeptName();

        if (store_name != null) {
            groupViewHolder.tvStoreName.setText(store_name);
        } else {
            groupViewHolder.tvStoreName.setText("");
        }

        //店铺内的商品都选中的时候，店铺的也要选中
        for (int i = 0; i < datasBean.getGcvList().size(); i++) {
            ShoppingCarDataBean.DatasBean.GoodsBean goodsBean = datasBean.getGcvList().get(i);
            boolean isSelect = goodsBean.getIsSelect();
            if (isSelect) {
                datasBean.setIsSelect_shop(true);
            } else {
                datasBean.setIsSelect_shop(false);
                break;
            }
        }

        //因为set之后要重新get，所以这一块代码要放到一起执行
        //店铺是否在购物车中被选中
        final boolean isSelect_shop = datasBean.getIsSelect_shop();
        if (isSelect_shop) {
            groupViewHolder.ivSelect.setImageResource(R.mipmap.select);
        } else {
            groupViewHolder.ivSelect.setImageResource(R.mipmap.unselect);
        }

        //店铺选择框的点击事件
        groupViewHolder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datasBean.setIsSelect_shop(!isSelect_shop);

                List<ShoppingCarDataBean.DatasBean.GoodsBean> goods = datasBean.getGcvList();
                for (int i = 0; i < goods.size(); i++) {
                    ShoppingCarDataBean.DatasBean.GoodsBean goodsBean = goods.get(i);
                    goodsBean.setIsSelect(!isSelect_shop);
                }
                notifyDataSetChanged();
            }
        });

        //当所有的选择框都是选中的时候，全选也要选中
        w:
        for (int i = 0; i < data.size(); i++) {
            List<ShoppingCarDataBean.DatasBean.GoodsBean> goods = data.get(i).getGcvList();
            for (int y = 0; y < goods.size(); y++) {
                ShoppingCarDataBean.DatasBean.GoodsBean goodsBean = goods.get(y);
                boolean isSelect = goodsBean.getIsSelect();
                if (isSelect) {
                    isSelectAll = true;
                } else {
                    isSelectAll = false;
                    break w;//根据标记，跳出嵌套循环
                }
            }
        }
        if (isSelectAll) {
            ivSelectAll.setBackgroundResource(R.mipmap.select);
        } else {
            ivSelectAll.setBackgroundResource(R.mipmap.unselect);
        }

        //全选的点击事件
        llSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectAll = !isSelectAll;

                if (isSelectAll) {
                    for (int i = 0; i < data.size(); i++) {
                        List<ShoppingCarDataBean.DatasBean.GoodsBean> goods = data.get(i).getGcvList();
                        for (int y = 0; y < goods.size(); y++) {
                            ShoppingCarDataBean.DatasBean.GoodsBean goodsBean = goods.get(y);
                            goodsBean.setIsSelect(true);
                        }
                    }
                } else {
                    for (int i = 0; i < data.size(); i++) {
                        List<ShoppingCarDataBean.DatasBean.GoodsBean> goods = data.get(i).getGcvList();
                        for (int y = 0; y < goods.size(); y++) {
                            ShoppingCarDataBean.DatasBean.GoodsBean goodsBean = goods.get(y);
                            goodsBean.setIsSelect(false);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });

        //合计的计算
        total_price = 0.0;
        tvTotalPrice.setText("¥0.00");
        for (int i = 0; i < data.size(); i++) {
            List<ShoppingCarDataBean.DatasBean.GoodsBean> goods = data.get(i).getGcvList();
            for (int y = 0; y < goods.size(); y++) {
                ShoppingCarDataBean.DatasBean.GoodsBean goodsBean = goods.get(y);
                boolean isSelect = goodsBean.getIsSelect();
                if (isSelect) {
                    int num = goodsBean.getNumber();
                    String price = goodsBean.getRetailPrice();

                    double v = (double) num;
                    double v1 = Double.parseDouble(price);

                    total_price = total_price + v * v1;

                    //让Double类型完整显示，不用科学计数法显示大写字母E
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    tvTotalPrice.setText("¥" + decimalFormat.format(total_price));
                }
            }
        }

        //去结算的点击事件
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建临时的List，用于存储被选中的商品
                sendServerCallback.show();
                List<ShoppingCarDataBean.DatasBean.GoodsBean> temp = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    List<ShoppingCarDataBean.DatasBean.GoodsBean> goods = data.get(i).getGcvList();
                    for (int y = 0; y < goods.size(); y++) {
                        ShoppingCarDataBean.DatasBean.GoodsBean goodsBean = goods.get(y);
                        boolean isSelect = goodsBean.getIsSelect();
                        if (isSelect) {
                            temp.add(goodsBean);
                        }
                    }
                }

                if (temp != null && temp.size() > 0) {//如果有被选中的
                    /**
                     * 实际开发中，如果有被选中的商品，
                     * 则跳转到确认订单页面，完成后续订单流程。
                     */
                    String ids = "";
                    int index=0;
                    for (int i=0;i<temp.size();i++){
                        if(temp.get(i).getIsSelect()){
                            if(index==0){
                                ids = ""+temp.get(i).getId();
                                index++;
                            }else {
                                ids+=","+temp.get(i).getId();
                            }

                        }
                    }
                    index = 0;
                    getDateFromHttpCart(ids);

                } else {
                    sendServerCallback.hide();
                    ToastUtil.showShort(context, "请选择要购买的商品");
                }
            }
        });

        //删除的点击事件
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 实际开发中，通过回调请求后台接口实现删除操作
                 */
                if (mDeleteListener != null) {
                    mDeleteListener.onDelete();
                }
            }
        });

        return convertView;
    }


    /**
     * 确认订单。
     * 返回是否成功，成功则跳转
     * @param ids
     */
    private void getDateFromHttpCart(String ids) {
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME,token);
        map.put("cartIds",ids);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.confirmGoods2Cart, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if(!StringUtils.isEmpty(result)){
                    Intent intent = new Intent(context, OrderActivity.class);
                    intent.putExtra("dropType",1);
                    context.startActivity(intent);
                }
                sendServerCallback.hide();
            }
            @Override
            public void onFailed(Call call, IOException e) {
                ToastUtil.showShort(context,"获取失败");
                sendServerCallback.hide();
            }
        });
    }
    static class GroupViewHolder {
        ImageView ivSelect;
        TextView tvStoreName;
        LinearLayout ll;

        GroupViewHolder(View view) {
            ivSelect = view.findViewById(R.id.iv_select);
            tvStoreName = view.findViewById(R.id.tv_store_name);
            ll = view.findViewById(R.id.ll);
        }
    }

    //------------------------------------------------------------------------------------------------
    @Override
    public int getChildrenCount(int groupPosition) {
        if (data.get(groupPosition).getGcvList() != null && data.get(groupPosition).getGcvList().size() > 0) {
            return data.get(groupPosition).getGcvList().size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.get(groupPosition).getGcvList().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_shopping_car_child, null);

            childViewHolder = new ChildViewHolder(convertView);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }


        final ShoppingCarDataBean.DatasBean datasBean = data.get(groupPosition);



        //店铺ID
        String store_id = datasBean.getDeptId();
        //店铺名称
        String store_name = datasBean.getDeptName();
        //店铺是否在购物车中被选中
        final boolean isSelect_shop = datasBean.getIsSelect_shop();
        final ShoppingCarDataBean.DatasBean.GoodsBean goodsBean = datasBean.getGcvList().get(childPosition);

        childViewHolder.chileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HxxqLastActivity.class);
                intent.putExtra("id",String.valueOf(goodsBean.getGoodsId()));
                context.startActivity(intent);
            }
        });

        //商品图片
        String goods_image = goodsBean.getListUrl();
        //商品ID
        final String goods_id = String.valueOf(goodsBean.getGoodsId());

        //购物车id
        final String cartId = String.valueOf(goodsBean.getId());
        //商品名称
        String goods_name = goodsBean.getGoodsName();
        //商品价格
        String goods_price = goodsBean.getRetailPrice();
        //商品数量
        String goods_num = String.valueOf(goodsBean.getNumber());
        //商品是否被选中
        final boolean isSelect = goodsBean.getIsSelect();

        Glide.with(context)
                .load(goods_image)
                .into(childViewHolder.ivPhoto);
        if (goods_name != null) {
            childViewHolder.tvName.setText(goods_name);
        } else {
            childViewHolder.tvName.setText("");
        }
        if (goods_price != null) {
            childViewHolder.tvPriceValue.setText(goods_price);
        } else {
            childViewHolder.tvPriceValue.setText("");
        }
        if (goods_num != null) {
            childViewHolder.tvEditBuyNumber.setText(goods_num);
        } else {
            childViewHolder.tvEditBuyNumber.setText("");
        }

        //商品是否被选中
        if (isSelect) {
            childViewHolder.ivSelect.setImageResource(R.mipmap.select);
        } else {
            childViewHolder.ivSelect.setImageResource(R.mipmap.unselect);
        }

        //商品选择框的点击事件
        childViewHolder.ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodsBean.setIsSelect(!isSelect);
                if (!isSelect == false) {
                    datasBean.setIsSelect_shop(false);
                }
                notifyDataSetChanged();
            }
        });

        //加号的点击事件
        childViewHolder.ivEditAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //模拟加号操作
                String num = String.valueOf(goodsBean.getNumber());
                Integer integer = Integer.valueOf(num);
                integer++;
                goodsBean.setNumber(integer);
                notifyDataSetChanged();

                /**
                 * 实际开发中，通过回调请求后台接口实现数量的加减
                 */
                if (mChangeCountListener != null) {
                    mChangeCountListener.onChangeCount(cartId,integer);
                }
            }
        });
        //减号的点击事件
        childViewHolder.ivEditSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //模拟减号操作
                String num = String.valueOf(goodsBean.getNumber());
                Integer integer = Integer.valueOf(num);
                if (integer > 1) {
                    integer--;
                    goodsBean.setNumber(integer);

                    /**
                     * 实际开发中，通过回调请求后台接口实现数量的加减
                     */
                    if (mChangeCountListener != null) {
                        mChangeCountListener.onChangeCount(cartId,integer);
                    }
                } else {
                    ToastUtil.showShort(context, "商品不能再减少了");
                }
                notifyDataSetChanged();
            }
        });

        if (childPosition == data.get(groupPosition).getGcvList().size() - 1) {
            childViewHolder.views.setVisibility(View.GONE);
            childViewHolder.viewLast.setVisibility(View.VISIBLE);
        } else {
            childViewHolder.views.setVisibility(View.VISIBLE);
            childViewHolder.viewLast.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ChildViewHolder {

        ImageView ivSelect;

        ImageView ivPhoto;

        TextView tvName;

        TextView tvPriceKey;

        TextView tvPriceValue;

        ImageView ivEditSubtract;

        TextView tvEditBuyNumber;

        ImageView ivEditAdd;

        View views;

        View viewLast;

        LinearLayout chileView;

        ChildViewHolder(View view) {
            chileView = view.findViewById(R.id.chile_view);
            ivSelect = view.findViewById(R.id.iv_select);
            ivPhoto = view.findViewById(R.id.iv_photo);
            tvName = view.findViewById(R.id.tv_name);
            tvPriceKey = view.findViewById(R.id.tv_price_key);
            tvPriceValue = view.findViewById(R.id.tv_price_value);
            ivEditSubtract = view.findViewById(R.id.iv_edit_subtract);
            tvEditBuyNumber = view.findViewById(R.id.tv_edit_buy_number);
            ivEditAdd = view.findViewById(R.id.iv_edit_add);
            views = view.findViewById(R.id.view);
            viewLast = view.findViewById(R.id.view_last);
        }
    }

    //-----------------------------------------------------------------------------------------------

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //删除的回调
    public interface OnDeleteListener {
        void onDelete();
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        mDeleteListener = listener;
    }

    private OnDeleteListener mDeleteListener;

    //修改商品数量的回调
    public interface OnChangeCountListener {
        void onChangeCount(String cartId, int count);
    }

    public void setOnChangeCountListener(OnChangeCountListener listener) {
        mChangeCountListener = listener;
    }

    private OnChangeCountListener mChangeCountListener;
}
