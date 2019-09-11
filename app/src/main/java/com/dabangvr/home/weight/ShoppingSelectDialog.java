package com.dabangvr.home.weight;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.model.goods.GoodsDetails;
import com.dabangvr.util.SPUtils2;
import com.dabangvr.util.ScreenUtils;
import com.dabangvr.util.TextUtil;
import com.dabangvr.util.ToastUtil;
import com.rey.material.app.BottomSheetDialog;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

import Utils.OkHttp3Utils;
import Utils.TObjectCallback;
import config.DyUrl;

public class ShoppingSelectDialog{
    private Context mContext;
    private BottomSheetDialog dialog;
    private ShoppingSelectNews selectView;
    private OnClickAddCartOrConfirmListener listener;

    public interface OnClickAddCartOrConfirmListener{
        void showLoadingView();

        void addCartOk(String mess);

        void confirmOk(String mess);

        void error(String msg);
    }

    private int type;//0普通，1拼团，2秒杀
    public ShoppingSelectDialog(Context mContext,int type) {
        this.mContext = mContext;
        this.type = type;
    }

    public void setListener(OnClickAddCartOrConfirmListener listener) {
        this.listener = listener;
    }

    public void showDialog(final GoodsDetails mData){
        dialog = new BottomSheetDialog(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.hxxq_bottom_dilog, null);
        //商品预览图
        ImageView diaLogo = view.findViewById(R.id.hx_dilog_img);
        //商品初始价钱
        TextView diaPrice = view.findViewById(R.id.count_money);
        //如果内有规格，直接使用默认价钱
        if (mData.getSpecList() == null || mData.getSpecList().size() == 0){
            if (type == 0) {//加购和普通购买使用普通价钱
                diaPrice.setText(mData.getSellingPrice());
            }
            if (type == 1) {//拼团使用拼团价钱
                diaPrice.setText(mData.getGroupPrice());
            }
            if (type == 2) {//秒杀使用秒杀价钱
                diaPrice.setText(mData.getSecondsPrice());
            }
        }

        //商品市场价
        TextView diaMarketPrice = view.findViewById(R.id.tv_mPrice);
        diaMarketPrice.setText("¥"+mData.getMarketPrice());
        diaMarketPrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
        diaMarketPrice.getPaint().setAntiAlias(true);

        //已选规格的库存数量
        final TextView diaStock = view.findViewById(R.id.tv_Stock);

        //商品已选规格名称
        TextView diaCheck = view.findViewById(R.id.tv_check);
        if (mData.getProductInfoList()!= null && mData.getProductInfoList().size()>0){
            diaCheck.setText(TextUtil.isNull2Url(mData.getProductInfoList().get(0).getName()));
        }else {
            diaCheck.setText("标准");
            diaStock.setText(mData.getRemainingInventory());
        }



        //数量
        final TextView diaNum = view.findViewById(R.id.tv_num);
        //加监听
        view.findViewById(R.id.tv_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果所选的规格库存 大于当前数量，则可以往上加
                if (Integer.parseInt(diaStock.getText().toString()) > Integer.parseInt(diaNum.getText().toString())){
                    int nowNum = Integer.parseInt(diaNum.getText().toString());
                    nowNum++;
                    diaNum.setText(String.valueOf(nowNum));
                }else {
                    ToastUtil.showShort(mContext,"库存不足");
                }
            }
        });

        //减监听
        view.findViewById(R.id.tv_sub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果大于1,则可以减
                int nowNum = Integer.parseInt(diaNum.getText().toString());
                if (nowNum>1){
                    nowNum--;
                    diaNum.setText(String.valueOf(nowNum));
                }else {
                    ToastUtil.showShort(mContext,"不能再少啦~~");
                }
            }
        });

        //加入购物车
        view.findViewById(R.id.tv_addCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddCart(mData.getGoodsId(),diaNum.getText().toString(),selectView.getProductId());
            }
        });

        //立即购买
        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConfirm(mData.getGoodsId(),diaNum.getText().toString(),selectView.getProductId());
            }
        });


        //规格
        selectView = view.findViewById(R.id.v_home);
        selectView.setData(mData.getSpecList());//规格数组
        selectView.setTextViewAndGGproject(1,diaLogo,diaPrice,diaStock,mData.getProductInfoList());

        int hight = (int) (Double.valueOf(ScreenUtils.getScreenHeight(mContext)) / 1.3);
        dialog.contentView(view)
                .heightParam(hight)
                .inDuration(200)
                .outDuration(200)
                .cancelable(true)
                .show();
    }

    private void startAddCart(String goodsId,String num,String productId) {
        listener.showLoadingView();
        addCartAndConfirm(0,DyUrl.addToCart,goodsId,num,productId);
    }

    private void startConfirm(String goodsId,String num,String productId) {
        listener.showLoadingView();
        addCartAndConfirm(1,DyUrl.confirmGoods,goodsId,num,productId);
    }


    /**
     *  加入购物车或确认购买
     * @param index 0 加入购物车 ；1立即购买
     * @param url
     * @param goodsId
     * @param num
     * @param productId
     */
    private void addCartAndConfirm(final int index, String url, String goodsId, String num, String productId){
        Map<String,Object>map = new HashMap<>();
        map.put("goodsId",goodsId);
        map.put("number",num);
        if (!StringUtils.isEmpty(productId)){
            map.put("productId",productId);
        }
        String token = (String) SPUtils2.instance(mContext).getkey("token","");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPostJson(url, map, token, new TObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (index == 0){
                    listener.addCartOk(result);
                }
                if (index == 1){
                    listener.confirmOk(result);
                }
            }
            @Override
            public void onFailed(String msg) {
                listener.error(msg);
            }
        });
    }

    public void desDialogView(){
        if (dialog!=null){
            dialog.dismiss();
        }
    }
}
