package com.dabangvr.dynamic.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.common.activity.BaseActivity;
import com.dabangvr.common.adapter.CheckAdapter;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.model.ZBGoods;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.PreImg;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.StatusBarUtil;
import com.dabangvr.util.ToastUtil;
import com.dbvr.imglibrary.model.Image;
import com.dbvr.imglibrary.ui.SelectImageActivity;
import com.dbvr.imglibrary.ui.adapter.SelectedImageAdapter;
import com.dbvr.imglibrary.utils.TDevice;
import com.dbvr.imglibrary.widget.recyclerview.SpaceGridItemDecoration;
import com.qiniu.android.http.ResponseInfo;

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
import config.QiNiu;
import okhttp3.Call;

public class DynamicActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private CheckAdapter checkAdapter;
    private List<ZBGoods> list = new ArrayList<>();

    private PdUtil pdUtil;
    private SPUtils spUtils;
    private EditText etContent;

    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int SELECT_IMAGE_REQUEST = 0x0011;
    private ArrayList<Image> mSelectImages = new ArrayList<>();
    private List<String>mPath = new ArrayList<>();
    private RecyclerView mSelectedImageRv;
    private TextView mDragTip;
    private SelectedImageAdapter mAdapter;
    private TextView btnSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_dynamic;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity();
            } else {
                //申请权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                Toast.makeText(this, "需要您的存储权限!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE_REQUEST && data != null) {
                ArrayList<Image> selectImages = data.getParcelableArrayListExtra(SelectImageActivity.EXTRA_RESULT);
                mSelectImages.clear();
                mSelectImages.addAll(selectImages);
                if (mSelectImages.size() > 1) {
                    mDragTip.setVisibility(View.VISIBLE);
                    if (mSelectImages.size() < 9) {
                        btnSelect.setText("继续添加");
                    } else {
                        btnSelect.setText("最多只能添加九张图哦");
                    }
                }
                mAdapter = new SelectedImageAdapter(this, mSelectImages, R.layout.selected_image_item);
                mSelectedImageRv.setAdapter(mAdapter);
                mItemTouchHelper.attachToRecyclerView(mSelectedImageRv);
            }
        }
    }

    @Override
    protected void initView() {
        pdUtil = new PdUtil(this);
        spUtils = new SPUtils(this, "db_user");
        mDragTip = findViewById(R.id.drag_tip);
        etContent = findViewById(R.id.et_content);
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

//        recyclerView = findViewById(R.id.ms_recycler_view);
//        GridLayoutManager manager = new GridLayoutManager(DynamicActivity.this, 3);
//        recyclerView.setLayoutManager(manager);
//
//        checkAdapter = new CheckAdapter(this, list);
//        recyclerView.setAdapter(checkAdapter);
//
//        checkAdapter.setItemClickListener(new CheckAdapter.RecyclerViewOnItemClickListener() {
//            @Override
//            public void onItemClickListener(View view, int position) {
//                checkAdapter.setSelectItem(position);
//            }
//        });


        findViewById(R.id.tv_report).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);

    }

    @Override
    protected void initData() {
//        Map<String, String> map = new HashMap<>();
//        map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token", ""));
//        map.put("limit", "30");
//        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getLiveGoodgsList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
//            @Override
//            public void onUi(String result) {
//                if (StringUtils.isEmpty(result)) {
//                    return ;
//                }
//                try {
//                    JSONObject object = new JSONObject(result);
//                    int errno = object.optInt("errno");
//                    if (0 == errno) {
//                        String dataArr = object.optString("data");
//                        List<ZBGoods> zbGoods = JsonUtil.string2Obj(dataArr, List.class, ZBGoods.class);
//                        if (zbGoods != null && zbGoods.size() > 0) {
//                            list = zbGoods;
//                            checkAdapter.updateData(list);
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailed(Call call, IOException e) {
//
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_report:
                //判断说说内容
                if (StringUtils.isEmpty(etContent.getText().toString())) {
                    ToastUtil.showShort(this,"发表内容不能留空哦");
                    return;
                }
                pdUtil.showLoding("正在上传");
                rePort();

                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    private void rePort() {
//        //获取选中的商品列表
//        List<String> mList = new ArrayList<>();
//        Map<Integer, Boolean> goodsmap = checkAdapter.getMap2();
//        for (Integer integer : goodsmap.keySet()) {
//            mList.add(list.get(integer).getId());
//        }
//
//        //序列化商品列表
//        String str = "";
//        if (mList.size() > 0) {
//            for (int i = 0; i < mList.size(); i++) {
//                if (i == mList.size() - 1) {
//                    str += mList.get(i);
//                } else {
//                    str += mList.get(i) + ",";
//                }
//            }
//        }

        //有图
        if (mSelectImages.size()>0){
            for (int i = 0; i < mSelectImages.size(); i++) {
                if (StringUtils.isEmpty(mSelectImages.get(i).getPath())) {
                    continue;
                }
                mPath.add(mSelectImages.get(i).getPath());
            }
            getQiniuToken();
        }else {
            sendDynamic("");
        }
    }



    private void sendDynamic(String mPaths){
        Map<String, Object> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(this,"token"));
        map.put("centent", etContent.getText().toString());
//        if (!StringUtils.isEmpty(str)){
//            map.put("goodsId", str);
//        }
        if (!StringUtils.isEmpty(mPaths)){
            map.put("imgUrl", mPaths);
        }
        OkHttp3Utils.getInstance(DyUrl.BASE).upLoadFile3(DyUrl.say, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")) {
                        if (object.optInt("code") == 500) {
                            ToastUtil.showShort(DynamicActivity.this,"升级中");
                            pdUtil.desLoding();
                            return ;
                        }
                        show("发表成功");
                    }
                    pdUtil.desLoding();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }

    private void getQiniuToken() {
        Map<String,String>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME,getSPKEY(this,"token"));
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getQiNiuToken, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result))return;
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")){
                        JSONObject data = object.optJSONObject("data");
                        String domain = data.optString("domain");
                        domain = "http://image.vrzbgw.com/";
                        String upLoadToken  = data.optString("upLoadToken");
                        final QiNiu qiNiu = QiNiu.getInstance();
                        qiNiu.initUploadManager(domain);
                        qiNiu.setmData(mPath);
                        for (Image image : mSelectImages){
                            qiNiu.startUpload(image.getPath(), upLoadToken, new QiNiu.UploadCallBack() {
                                @Override
                                public void success(List<String> url) {
                                    sendDynamic(JsonUtil.obj2String(url));
                                    qiNiu.desQiniu();
                                }
                                @Override
                                public void fail(String key, ResponseInfo info) {
                                    qiNiu.desQiniu();
                                }
                            });
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

    private void show(String mess) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("海风暴")
                .setMessage(mess)
                .setIcon(R.mipmap.application)
                .setPositiveButton("继续发表", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setNeutralButton("返回", new DialogInterface.OnClickListener() {//添加普通按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).create();
        alertDialog.show();
    }

}
