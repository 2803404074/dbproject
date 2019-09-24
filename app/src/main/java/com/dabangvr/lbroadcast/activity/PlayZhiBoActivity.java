package com.dabangvr.lbroadcast.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.model.CheckMo;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.ToastUtil;
import com.dbvr.imglibrary.model.Image;
import com.dbvr.imglibrary.ui.SelectImageActivity;
import com.dbvr.imglibrary.ui.adapter.SelectedImageAdapter;
import com.dbvr.imglibrary.utils.TDevice;
import com.dbvr.imglibrary.widget.recyclerview.SpaceGridItemDecoration;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import Utils.PdUtil;
import config.DyUrl;
import okhttp3.Call;

public class PlayZhiBoActivity extends BaseActivity implements View.OnClickListener {

    private PdUtil pdUtil;
    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int SELECT_IMAGE_REQUEST = 0x0011;
    private ArrayList<Image> mSelectImages = new ArrayList<>();
    private RecyclerView mSelectedImageRv;
    private TextView mDragTip;
    private SelectedImageAdapter mAdapter;
    private TextView btnSelect;
    private EditText editText;//直播内容
    public static PlayZhiBoActivity instants;

    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;
    private List<CheckMo> checkMoList;

    private TextView tvTips;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_play_zhi_bo;
    }

    @Override
    protected void initView() {
        instants = this;
        pdUtil = new PdUtil(this);
        editText = findViewById(R.id.mess);

        tvTips = findViewById(R.id.zb_tips);
        //图片
        mDragTip = findViewById(R.id.drag_tip);
        mSelectedImageRv = findViewById(R.id.rv_selected_image);
        mSelectedImageRv.setLayoutManager(new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false));
        mSelectedImageRv.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 1)));
        btnSelect = findViewById(R.id.btn_select);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        findViewById(R.id.back).setOnClickListener(this);

        findViewById(R.id.start_zhibo).setOnClickListener(this);

        findViewById(R.id.btn_select_goods).setOnClickListener(this);

        recyclerView = findViewById(R.id.recy_goods);
        GridLayoutManager manager = new GridLayoutManager(this,5);
        recyclerView.setLayoutManager(manager);
    }

    @Override
    protected void initData() {

    }

    private void selectImage() {
        int isPermission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int isPermission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (isPermission1 == PackageManager.PERMISSION_GRANTED && isPermission2 == PackageManager.PERMISSION_GRANTED) {
            startActivity();
        } else {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void startActivity() {
        Intent intent = new Intent(this, SelectImageActivity.class);
        intent.putParcelableArrayListExtra("selected_images", mSelectImages);
        startActivityForResult(intent, SELECT_IMAGE_REQUEST);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_zhibo: {
                if (editText.getText().toString().length() < 2) {
                    ToastUtil.showShort(PlayZhiBoActivity.this, "标题太短，请重新设置");
                } else if (null == mSelectImages || mSelectImages.size() == 0) {
                    ToastUtil.showShort(PlayZhiBoActivity.this, "未上传封面");
                } else {
                    //选择的商品
                    String str = "";
                    if (checkMoList !=null && checkMoList.size()>0){
                        List<String>idList = new ArrayList<>();
                        for (int i=0;i<checkMoList.size();i++){
                            idList.add(checkMoList.get(i).getGoodsId());
                        }
                        str = JsonUtil.obj2String(idList);
                    }
                    //开播的封面
                    File f = new File(mSelectImages.get(0).getPath());
                    show("开播流量消耗较大，请注意流量", str, f, editText.getText().toString());
                }
                break;
            }
            case R.id.back:
                finish();
                break;
            case R.id.btn_select_goods:
                Intent intent = new Intent(this, HxActivityChoose.class);
                intent.putExtra("data",JsonUtil.obj2String(checkMoList));
                startActivityForResult(intent,77);
                break;
            default:
                break;
        }
    }

    /**
     * 获取推流地址
     *
     * @param idList 商品id列表
     * @param file   封面地址
     * @param mess   直播内容
     */
    private void getStreamPublishUrl(String idList, File file, String mess) {
        Map<String, Object> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this, "token"));
        map.put("liveTitle", mess);//直播标题
        map.put("goodsIds", idList);
        OkHttp3Utils.getInstance(DyUrl.BASE).upLoadFile(DyUrl.createStream, map, file, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if (errno == 0) {
                        if (object.optInt("code") == 500) {
                            ToastUtil.showShort(PlayZhiBoActivity.this, object.optString("msg"));
                        } else {
//                            String url = object.optString("publishURL");//推流地址
//                            String tag = object.optString("tag");//播放地址（用于存到通讯服务）
//                            Intent intent = new Intent(PlayZhiBoActivity.this, HWCameraStreamingActivity.class);
//                            intent.putExtra("stream_publish_url", url);
//                            intent.putExtra("tag", tag);
//                            String goods = object.optString("goodsVoList");
//                            intent.putExtra("goods", goods);
//                            startActivity(intent);
                        }
                    }
                    if (errno == 1){
                        ToastUtil.showShort(PlayZhiBoActivity.this,object.optString("errmsg"));
                    }
                    pdUtil.desLoding();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }

            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.showShort(PlayZhiBoActivity.this, "请求超时");
                pdUtil.desLoding();
            }
        });
    }

    private void show(final String mess, final String idList, final File file, final String messZ) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("海风暴温馨提醒")
                .setMessage(mess)
                .setIcon(R.mipmap.application)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pdUtil.showLoding("加载中，请稍等");
                        getStreamPublishUrl(idList, file, messZ);
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {//添加普通按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                }).create();
        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE_REQUEST && data != null) {
                ArrayList<Image> selectImages = data.getParcelableArrayListExtra(SelectImageActivity.EXTRA_RESULT);
                if (null != selectImages && selectImages.size() > 0) {
                    mSelectImages.clear();
                    mSelectImages.add(selectImages.get(selectImages.size() - 1));
                    btnSelect.setText("修改封面");
                }
                if (selectImages.size() > 1) {
                    mDragTip.setVisibility(View.VISIBLE);
                }
                mAdapter = new SelectedImageAdapter(this, mSelectImages, R.layout.selected_image_item);
                mSelectedImageRv.setAdapter(mAdapter);
                mItemTouchHelper.attachToRecyclerView(mSelectedImageRv);
            }
        }

        //选择商品后返回
        if (requestCode == 77){
            if (resultCode == 77){
                String str = data.getStringExtra("data");
                checkMoList = JsonUtil.string2Obj(str,List.class, CheckMo.class);
                if (null == checkMoList|| checkMoList.size() == 0)return;
                if (adapter != null){
                    adapter.updateData(checkMoList);
                }else {
                    adapter = new BaseLoadMoreHeaderAdapter<CheckMo>(this,recyclerView,checkMoList,R.layout.play_goods_item) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, CheckMo o) {
                            holder.setImageByUrl(R.id.iv_img,o.getListUrl());
                            holder.setText(R.id.tv_name,o.getName());
                            holder.setText(R.id.tv_price,o.getPrice());
                        }
                    };
                    recyclerView.setAdapter(adapter);
                    mItemTouchHelper2.attachToRecyclerView(recyclerView);
                }
                tvTips.setVisibility(View.VISIBLE);
            }
        }
    }

    private ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

            // 获取触摸响应的方向   包含两个 1.拖动dragFlags 2.侧滑删除swipeFlags
            // 代表只能是向左侧滑删除，当前可以是这样ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT
            int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            int dragFlags;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            } else {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            }
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        /**
         * 拖动的时候不断的回调方法
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //获取到原来的位置
            int fromPosition = viewHolder.getAdapterPosition();
            //获取到拖到的位置
            int targetPosition = target.getAdapterPosition();
            if (fromPosition < targetPosition) {
                for (int i = fromPosition; i < targetPosition; i++) {
                    Collections.swap(mSelectImages, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > targetPosition; i--) {
                    Collections.swap(mSelectImages, i, i - 1);
                }
            }
            mAdapter.notifyItemMoved(fromPosition, targetPosition);
            return true;
        }

        /**
         * 侧滑删除后会回调的方法
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            mSelectImages.remove(position);
            mAdapter.notifyItemRemoved(position);
        }
    });



    private ItemTouchHelper mItemTouchHelper2 = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

            // 获取触摸响应的方向   包含两个 1.拖动dragFlags 2.侧滑删除swipeFlags
            // 代表只能是向左侧滑删除，当前可以是这样ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT
            int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            int dragFlags;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            } else {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            }
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        /**
         * 拖动的时候不断的回调方法
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //获取到原来的位置
            int fromPosition = viewHolder.getAdapterPosition();
            //获取到拖到的位置
            int targetPosition = target.getAdapterPosition();
            if (fromPosition < targetPosition) {
                for (int i = fromPosition; i < targetPosition; i++) {
                    Collections.swap(checkMoList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > targetPosition; i--) {
                    Collections.swap(checkMoList, i, i - 1);
                }
            }
            adapter.notifyItemMoved(fromPosition, targetPosition);
            return true;
        }

        /**
         * 侧滑删除后会回调的方法
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            checkMoList.remove(position);
            adapter.notifyItemRemoved(position);

            if (checkMoList.size()==0){
                tvTips.setVisibility(View.GONE);
            }
        }
    });

}
