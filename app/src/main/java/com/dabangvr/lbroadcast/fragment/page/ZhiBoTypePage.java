package com.dabangvr.lbroadcast.fragment.page;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.lbroadcast.activity.MyZbActivity;
import com.dabangvr.lbroadcast.activity.PlayActivity;
import com.dabangvr.model.GwBean;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;

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
public class ZhiBoTypePage extends Fragment {

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
        context = ZhiBoTypePage.this.getContext();
        spUtils = new SPUtils(context,"db_user");
        View view = inflater.inflate(R.layout.recy_demo, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        //设置页和当前页一致时加载，防止预加载
        if (isFirst && mTabPos == mSerial) {
            isFirst = false;
            sendMessage();
        }
        recyclerView = view.findViewById(R.id.ms_recycler_view);
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
                Intent intent = new Intent(ZhiBoTypePage.this.getContext(), PlayActivity.class);
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
    }

    /**
     * 获取数据
     * @param isLoad 是否是加载更多 true 是
     */
    private void setDate(final boolean isLoad) {
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
        map.put("playType",cId);
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
                        if (mData!=null && mData.size()>0){
                            String testUrl = "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3982262708,3993837289&fm=26&gp=0.jpg";
                            String testUrl2 = "http://upload.mnw.cn/2015/0807/1438931855592.jpg";
                            String testUrl3 = "http://i2.17173.itc.cn/2015/live/04/24/xiuchang.jpg";
                            for (int i = 0; i < mData.size() ; i++) {
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
                            adapter.updateData(mData);
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

    public ZhiBoTypePage(int serial) {
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
