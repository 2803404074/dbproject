package com.dabangvr.common.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dabangvr.common.adapter.ShoppingCarAdapter;
import com.dabangvr.R;
import com.dabangvr.model.ShoppingCarDataBean;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.RoundCornerDialog;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.ToastUtil;

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
import okhttp3.Call;

/**
 * 购物车
 */
public class CartActivity extends BaseActivity implements View.OnClickListener{


    private TextView tvTitlebarCenter;
    private TextView tvTitlebarRight;
    private ExpandableListView elvShoppingCar;
    private ImageView ivSelectAll;
    private LinearLayout llSelectAll;
    private Button btnOrder;
    private Button btnDelete;
    private TextView tvTotalPrice;
    private RelativeLayout rlTotalPrice;
    private RelativeLayout rl;
    private ImageView ivNoContant;
    private RelativeLayout rlNoContant;
    private String token;
    //TextView tvTitlebarLeft;

    private List<ShoppingCarDataBean.DatasBean> datas;
    private Context context;
    private ShoppingCarAdapter shoppingCarAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);

    }

    @Override
    public int setLayout() {
        return R.layout.activity_cart;
    }

    @Override
    public void initView() {
        context = this;
        token = getSPKEY(CartActivity.this,"token");
        tvTitlebarCenter = findViewById(R.id.tv_titlebar_center);
        tvTitlebarRight = findViewById(R.id.tv_titlebar_right);
        elvShoppingCar = findViewById(R.id.elv_shopping_car);
        ivSelectAll = findViewById(R.id.iv_select_all);
        llSelectAll = findViewById(R.id.ll_select_all);
        btnOrder = findViewById(R.id.btn_order);
        btnDelete = findViewById(R.id.btn_delete);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        rlTotalPrice = findViewById(R.id.rl_total_price);
        rl = findViewById(R.id.rl);
        ivNoContant = findViewById(R.id.iv_no_contant);
        rlNoContant = findViewById(R.id.rl_no_contant);
        findViewById(R.id.tv_titlebar_left).setOnClickListener(this);
        findViewById(R.id.tv_titlebar_right).setOnClickListener(this);
        initExpandableListView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_titlebar_left:
                finish();
                break;
            case R.id.tv_titlebar_right://编辑
                String edit = tvTitlebarRight.getText().toString().trim();
                if (edit.equals("编辑")) {
                    tvTitlebarRight.setText("完成");
                    rlTotalPrice.setVisibility(View.GONE);
                    btnOrder.setVisibility(View.GONE);
                    btnDelete.setVisibility(View.VISIBLE);
                } else {
                    tvTitlebarRight.setText("编辑");
                    rlTotalPrice.setVisibility(View.VISIBLE);
                    btnOrder.setVisibility(View.VISIBLE);
                    btnDelete.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initData() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, token);
        map.put("page", "1");
        map.put("limit", "10");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoods2CartList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    ToastUtil.showShort(CartActivity.this, "获取失败");
                    return;
                }

                try {
                    JSONObject object = new JSONObject(result);
                    if(500 == object.optInt("code")){
                        ToastUtil.showShort(context,object.optString("msg"));
                        return ;
                    }
                    int errno = object.optInt("errno");
                    if(errno == 1){
                        //ToastUtil.showShort(CartActivity.this,result);
                    }
                    if( errno== 500){
                        ToastUtil.showShort(context,object.optString("errmsg"));
                        return ;
                    }
                    if(errno == 0){
                        if(500 == object.optInt("code")){
                            return ;
                        }
                        ShoppingCarDataBean bean = JsonUtil.string2Obj(result, ShoppingCarDataBean.class);
                        datas = bean.getData().getGoods2CartList();
                        initExpandableListViewData(datas);
                    }else {
                        initExpandableListViewData(null);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });


        //使用Gson解析购物车数据，
        //ShoppingCarDataBean为bean类，Gson按照bean类的格式解析数据
        /**
         * 实际开发中，通过请求后台接口获取购物车数据并解析
         */
//        Gson gson = new Gson();
//        ShoppingCarDataBean shoppingCarDataBean = gson.fromJson(shoppingCarData, ShoppingCarDataBean.class);

    }

    /**
     * 初始化ExpandableListView
     * 创建数据适配器adapter，并进行初始化操作
     */
    private void initExpandableListView() {
        shoppingCarAdapter = new ShoppingCarAdapter(context,token, llSelectAll, ivSelectAll, btnOrder, btnDelete, rlTotalPrice, tvTotalPrice);
        elvShoppingCar.setAdapter(shoppingCarAdapter);

        //删除的回调
        shoppingCarAdapter.setOnDeleteListener(new ShoppingCarAdapter.OnDeleteListener() {
            @Override
            public void onDelete() {

                initDelete();
                /**
                 * 实际开发中，在此请求删除接口，删除成功后，
                 * 通过initExpandableListViewData（）方法刷新购物车数据。
                 * 注：通过bean类中的DatasBean的isSelect_shop属性，判断店铺是否被选中；
                 *                  GoodsBean的isSelect属性，判断商品是否被选中，
                 *                  （true为选中，false为未选中）
                 */
            }
        });

        //修改商品数量的回调
        shoppingCarAdapter.setOnChangeCountListener(new ShoppingCarAdapter.OnChangeCountListener() {
            @Override
            public void onChangeCount(String goods_id,int count) {
                /**
                 * 实际开发中，在此请求修改商品数量的接口，商品数量修改成功后，
                 * 通过initExpandableListViewData（）方法刷新购物车数据。
                 */
                HashMap<String, String> map = new HashMap<>();
                map.put(DyUrl.TOKEN_NAME, token);
                map.put("cartId", goods_id);
                map.put("number", String.valueOf(count));
                OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.updateNumber2Cart, map, new GsonObjectCallback<String>(DyUrl.BASE) {
                    @Override
                    public void onUi(String result) {
                        if (StringUtils.isEmpty(result)) {
                            ToastUtil.showShort(CartActivity.this, "获取失败");
                            return ;
                        }
                        try {
                            JSONObject object = new JSONObject(result);
                            int errno = object.optInt("errno");
                            if(errno == 0){
                                if(500 == object.optInt("code")){
                                    return ;
                                }
                                ToastUtil.showShort(context,"操作成功");
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
        });
    }

    /**
     * 初始化ExpandableListView的数据
     * 并在数据刷新时，页面保持当前位置
     *
     * @param datas 购物车的数据
     */
    private void initExpandableListViewData(List<ShoppingCarDataBean.DatasBean> datas) {


        if (datas != null && datas.size() > 0) {
            //刷新数据时，保持当前位置
            shoppingCarAdapter.setData(datas);

            //使所有组展开
            for (int i = 0; i < shoppingCarAdapter.getGroupCount(); i++) {
                elvShoppingCar.expandGroup(i);
            }

            //使组点击无效果
            elvShoppingCar.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    return true;
                }
            });

            tvTitlebarRight.setVisibility(View.VISIBLE);
            tvTitlebarRight.setText("编辑");
            rlNoContant.setVisibility(View.GONE);
            elvShoppingCar.setVisibility(View.VISIBLE);
            rl.setVisibility(View.VISIBLE);
            rlTotalPrice.setVisibility(View.VISIBLE);
            btnOrder.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.GONE);
        } else {
            tvTitlebarRight.setVisibility(View.GONE);
            rlNoContant.setVisibility(View.VISIBLE);
            elvShoppingCar.setVisibility(View.GONE);
            rl.setVisibility(View.GONE);
        }
    }

    /**
     * 判断是否要弹出删除的dialog
     * 通过bean类中的DatasBean的isSelect_shop属性，判断店铺是否被选中；
     * GoodsBean的isSelect属性，判断商品是否被选中，
     */
    private void initDelete() {
        //判断是否有店铺或商品被选中
        //true为有，则需要刷新数据；反之，则不需要；
        boolean hasSelect = false;
        //创建临时的List，用于存储没有被选中的购物车数据
        List<ShoppingCarDataBean.DatasBean> datasTemp = new ArrayList<>();

        List<String>isSelectCartId = new ArrayList<>();

        for (int i = 0; i < datas.size(); i++) {
            List<ShoppingCarDataBean.DatasBean.GoodsBean> goods = datas.get(i).getGcvList();
            boolean isSelect_shop = datas.get(i).getIsSelect_shop();
            for (int y = 0; y < goods.size(); y++) {
                ShoppingCarDataBean.DatasBean.GoodsBean goodsBean = goods.get(y);
                boolean isSelect = goodsBean.getIsSelect();
                if (isSelect) {
                    isSelectCartId.add(String.valueOf(goodsBean.getId()));
                }
            }

            if (isSelect_shop) {
                hasSelect = true;
                //跳出本次循环，继续下次循环。
                continue;
            } else {
                datasTemp.add(datas.get(i));
                datasTemp.get(datasTemp.size() - 1).setGcvList(new ArrayList<ShoppingCarDataBean.DatasBean.GoodsBean>());
            }

            for (int y = 0; y < goods.size(); y++) {
                ShoppingCarDataBean.DatasBean.GoodsBean goodsBean = goods.get(y);
                boolean isSelect = goodsBean.getIsSelect();

                if (isSelect) {
                    hasSelect = true;
                } else {
                    datasTemp.get(datasTemp.size() - 1).getGcvList().add(goodsBean);
                }
            }
        }

        if (hasSelect) {
            showDeleteDialog(datasTemp,isSelectCartId);
        } else {
            ToastUtil.showShort(context, "请选择要删除的商品");
        }
    }

    /**
     * 展示删除的dialog（可以自定义弹窗，不用删除即可）
     *
     * @param datasTemp
     */
    private void showDeleteDialog(final List<ShoppingCarDataBean.DatasBean> datasTemp, final List<String>cartIdArr) {
        View view = View.inflate(context, R.layout.dialog_two_btn, null);
        final RoundCornerDialog roundCornerDialog = new RoundCornerDialog(context, 0, 0, view, R.style.RoundCornerDialog);
        roundCornerDialog.show();
        roundCornerDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        roundCornerDialog.setOnKeyListener(keylistener);//设置点击返回键Dialog不消失

        TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
        TextView tv_logout_confirm = (TextView) view.findViewById(R.id.tv_logout_confirm);
        TextView tv_logout_cancel = (TextView) view.findViewById(R.id.tv_logout_cancel);
        tv_message.setText("确定要删除商品吗？");

        //确定
        tv_logout_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundCornerDialog.dismiss();

                for (int i=0;i<cartIdArr.size();i++){
                    System.out.println("删除了:"+cartIdArr.get(i));
                }

                //请求后端
                sendServer(datasTemp,cartIdArr);

            }
        });
        //取消
        tv_logout_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundCornerDialog.dismiss();
            }
        });
    }

    private void sendServer(final List<ShoppingCarDataBean.DatasBean> datasTemp, List<String> cartIdArr) {
        Map<String,String>map = new HashMap<>();
        map.put("cartIds",JsonUtil.obj2String(cartIdArr));
        map.put(DyUrl.TOKEN_NAME,getSPKEY(this,"token"));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.delete2Cart, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) return ;

                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getInt("errno") == 0){
                        datas = datasTemp;
                        initExpandableListViewData(datas);
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

    DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return true;
            } else {
                return false;
            }
        }
    };

}