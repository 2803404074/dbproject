package com.dabangvr.lbroadcast.fragment.page;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.dabangvr.R;
import com.dabangvr.common.activity.MyFragment;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.lbroadcast.activity.PlayActivity;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import bean.ZBMain;
import config.DyUrl;
import config.JsonUtil;
import okhttp3.Call;

/**
 * 直播购物页面
 */
public class FragmentGwBack /*extends MyFragment */{

//    private Context context;
//
//    private BaseLoadMoreHeaderAdapter adapter;
//    public static FragmentGwBack newInstance() {
//        FragmentGwBack fragment = new FragmentGwBack();
//        return fragment;
//    }
//    @Override
//    protected int setContentView() {
//        return R.layout.zb_fragment_gw;
//    }
//
//    @Override
//    protected void lazyLoad() {
//        context = FragmentGwBack.this.getContext();
//        spUtils = new SPUtils(context,"db_user");
//        recyclerView = findViewById(R.id.zb_recycler_view_gw);
//        LinearLayoutManager layoutmanager = new LinearLayoutManager(FragmentGwBack.this.getContext());
//        recyclerView.setLayoutManager(layoutmanager);
//
//        adapter = new BaseLoadMoreHeaderAdapter<ZBMain>(FragmentGwBack.this.getContext(),
//                recyclerView,list,R.layout.zb_gw_item) {
//            @Override
//            public void convert(Context mContext, BaseRecyclerHolder holder, ZBMain gwBean) {
//                //封面
//                holder.setImageByUrl(R.id.zbgw_vedio,gwBean.getCoverUrl());
//
//                //头像
//                SimpleDraweeView simpleDraweeView = holder.getView(R.id.zbgw_head);
//                Uri uri = Uri.parse(gwBean.getHeadUrl());
//                simpleDraweeView.setImageURI(uri);
//
//                //直播标题
//                holder.setText(R.id.title,gwBean.getLiveTitle());
//
//                //主播昵称
//                holder.setText(R.id.zbgw_name,gwBean.getAnchorName());
//
//                //主播 主推的商品列表
//                holder.setImageByUrl(R.id.zbgw_img1,gwBean.getLiveGoodsList().get(0).getListUrl());
//                holder.setImageByUrl(R.id.zbgw_img2,gwBean.getLiveGoodsList().get(1).getListUrl());
//                holder.setImageByUrl(R.id.zbgw_img3,gwBean.getLiveGoodsList().get(2).getListUrl());
//            }
//        };
//        recyclerView.setAdapter(adapter);
//
//        /**
//         * 点击事件
//         * 将信息传递到观看直播的页面
//         * 播放地址、房间标识、主播头像、主播昵称、主播粉丝、主播id
//         */
//        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Intent intent = new Intent(context, PlayActivity.class);
//                intent.putExtra("path",list.get(position).getRtmpPlayURL());
//                intent.putExtra("tag",list.get(position).getTag());
//                intent.putExtra("name",list.get(position).getAnchorName());
//                intent.putExtra("head",list.get(position).getHeadUrl());
//                intent.putExtra("fanse",list.get(position).getFans());
//                intent.putExtra("anchorId",list.get(position).getAnchorId());
//                intent.putExtra("all_anchor",JsonUtil.obj2String(list));
//                intent.putExtra("fansTag",list.get(position).getFansTag());
//                startActivity(intent);
//            }
//        });
//
//        initData();
//    }
//
//    private void initData() {
//        Map<String,String>map = new HashMap<>();
//        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token",""));
//        map.put("streamName","shopping");
//        map.put("limit","10");
//        map.put("marker","");
//        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getLiveStreamsList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
//            @Override
//            public void onUi(String result) {
//                if(StringUtils.isEmpty(result)){
//                    return ;
//                }
//                try {
//                    JSONObject object = new JSONObject(result);
//                    int errno = object.optInt("errno");
//                    if(errno == 0){
//
//                        int code = object.optInt("code");
//                        if(code == 500){
//                            ToastUtil.showShort(context,"服务数据更新中");
//                            return ;
//                        }
//                        JSONObject dataObj = object.optJSONObject("data");
//                        String str = dataObj.optString("playList");
//                        List<ZBMain> list = JsonUtil.string2Obj(str,List.class,ZBMain.class);
//                        adapter.updateData(list);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailed(Call call, IOException e) {
//
//            }
//            @Override
//            public void onFailure(Call call, IOException e) {
//                if(e instanceof SocketTimeoutException){//判断超时异常
//
//                }
//                if(e instanceof ConnectException){//判断连接异常
//
//                }
//            }
//
//        });
//    }
}
