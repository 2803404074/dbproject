package com.dabangvr.my.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.base.BaseFragment;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.contens.ParameterContens;
import com.dabangvr.main.MainActivity;
import com.dabangvr.model.MenuMo;
import com.dabangvr.my.activity.LoginActivity;
import com.dabangvr.my.activity.MyMessageActivity;
import com.dabangvr.my.activity.MyOrtherActivity;
import com.dabangvr.my.activity.MyScActivity;
import com.dabangvr.my.activity.MyYhjActivity;
import com.dabangvr.my.activity.SbActivity;
import com.dabangvr.my.activity.StartOpenShopActivity;
import com.dabangvr.my.activity.StartOpenZhuBoActivity;
import com.dabangvr.my.activity.UserMessActivity;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils2;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.wxapi.AppManager;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.OkHttp3Utils;
import Utils.TObjectCallback;
import bean.UserMess;
import butterknife.BindView;
import butterknife.OnClick;
import config.DyUrl;

/**
 * 个人中心
 */
public class FragmentMy extends BaseFragment {

    //头像
    @BindView(R.id.sdvHead)
    SimpleDraweeView sdvHead;
    //昵称
    @BindView(R.id.tv_name)
    TextView tvName;
    //动态数量
    @BindView(R.id.tv_dyNum)
    TextView tvDyNum;
    //关注数量
    @BindView(R.id.tv_gzNum)
    TextView tvGzNum;
    //粉丝数量
    @BindView(R.id.tv_fsNum)
    TextView tvFsNum;
    //跳币数量
    @BindView(R.id.tv_tbNum)
    TextView tvTbNum;

    //功能列表
    @BindView(R.id.server_recy)
    RecyclerView recyclerViewServer;

    private BaseLoadMoreHeaderAdapter adapter;

    private List<MenuMo> serverData = new ArrayList<>();


    @Override
    public int layoutId() {
        return R.layout.fragment_my;
    }

    @Override
    public void initView() {

        recyclerViewServer.setLayoutManager(new GridLayoutManager(getContext(), 4));
        adapter = new BaseLoadMoreHeaderAdapter<MenuMo>(getContext(), recyclerViewServer, serverData, R.layout.item_my_server) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, MenuMo o) {
                holder.setText(R.id.tv_tag, o.getTitle());
                holder.setImageByUrl(R.id.iv_icon, o.getIconUrl());
            }
        };
        recyclerViewServer.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (serverData.get(position).getJumpUrl()) {
                    case ParameterContens.yhq:
//                        ToastUtil.showShort(getContext(), "优惠券更新中");
                        startActivity(new Intent(getContext(), MyYhjActivity.class));
                        break;
                    case ParameterContens.spsc:
                        startActivity(new Intent(getContext(), MyScActivity.class));
                        break;
                    case ParameterContens.wdpt:
                        ToastUtil.showShort(getContext(), "我的拼团更新中");
                        break;
                    case ParameterContens.wdqb:
                        startActivity(new Intent(getContext(), SbActivity.class));
                        break;
                    case ParameterContens.sjrz:
                        startActivity(new Intent(getContext(), StartOpenShopActivity.class));
                        break;//商家入驻
                    case ParameterContens.zbsq:
                        startActivity(new Intent(getContext(), StartOpenZhuBoActivity.class));
                        break;
                    case ParameterContens.kf:
                        ToastUtil.showShort(getContext(), "服务更新中");
                        break;
                }
            }
        });
    }

    @Override
    public void initData() {
        setLoaddingView(true);
        //初始化用户信息
        UserMess userMess = SPUtils2.instance(getContext()).getObj("userMo",UserMess.class);
        if (userMess != null) {
            sdvHead.setImageURI(userMess.getHeadUrl());
            tvName.setText(userMess.getNickName());
        } else {
            ToastUtil.showShort(getContext(), "获取用户信息失败，请重新登陆");
            startActivity(new Intent(getContext(), LoginActivity.class));
            AppManager.getAppManager().finishActivity(MainActivity.class);
        }
        //初始化菜单
        Map<String,String>map = new HashMap<>();
        map.put("mallSpeciesId","8");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPostForm(DyUrl.getChannelMenuList, map, getToken(), new TObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) throws JSONException {
                JSONObject object = new JSONObject(result);
                String str = object.optString("channelMenuList");
                serverData = JsonUtil.string2Obj(str, List.class, MenuMo.class);
                if (serverData != null && serverData.size() > 0) {
                    adapter.updateDataa(serverData);
                }
                setLoaddingView(false);
            }
            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(), msg);
                setLoaddingView(false);
            }
        });
    }

    @OnClick({R.id.tv_order, R.id.tv_dfk, R.id.tv_dfh, R.id.tv_dsh, R.id.tv_dpj, R.id.tv_tkth, R.id.tv_name, R.id.sdvHead, R.id.tv_set,R.id.iv_message})
    public void onTouchClick(View view) {
        switch (view.getId()) {
            case R.id.tv_order:
                startActivity(new Intent(getContext(), MyOrtherActivity.class));
                break;//全部订单
            case R.id.tv_dfk:
                Intent intent = new Intent(getContext(), MyOrtherActivity.class);
                intent.putExtra("position", 1);
                startActivity(intent);
                break;//待付款
            case R.id.tv_dfh:
                Intent intent1 = new Intent(getContext(), MyOrtherActivity.class);
                intent1.putExtra("position", 2);
                startActivity(intent1);
                break;//待发货
            case R.id.tv_dsh:
                Intent intent2 = new Intent(getContext(), MyOrtherActivity.class);
                intent2.putExtra("position", 3);
                startActivity(intent2);
                break;//待收货
            case R.id.tv_dpj:
                Intent intent3 = new Intent(getContext(), MyOrtherActivity.class);
                intent3.putExtra("position", 4);
                startActivity(intent3);
                break;//待评价
            case R.id.tv_tkth:
                Intent intent4 = new Intent(getContext(), MyOrtherActivity.class);
                intent4.putExtra("position", 5);
                startActivity(intent4);
                break;//退款退货
            case R.id.tv_name:
                startActivity(new Intent(getContext(), UserMessActivity.class));
                break;//昵称点击
            case R.id.sdvHead:
                startActivity(new Intent(getContext(), UserMessActivity.class));
                break;//头像点击
            case R.id.tv_set:
                startActivity(new Intent(getContext(), UserMessActivity.class));
                break;//设置点击
            case R.id.iv_message:
                startActivity(new Intent(getContext(), MyMessageActivity.class));
                break;//消息点击
            default:break;
        }
    }
}
