package com.dabangvr.common.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.home.activity.HxActivity;
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
import config.DyUrl;
import okhttp3.Call;

public class CommentActivity extends BaseActivity implements View.OnClickListener {

    private Utils.PdUtil pd;
    private AlertDialog alertDialog;

    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int SELECT_IMAGE_REQUEST = 0x0011;
    private ArrayList<Image> mSelectImages = new ArrayList<>();
    private RecyclerView mSelectedImageRv;
    private TextView mDragTip;
    private SelectedImageAdapter mAdapter;
    private TextView btnSelect;

    private EditText editText;
    private String goodsId;
    private String orderId;

    private CheckBox cb01;
    private CheckBox cb02;
    private CheckBox cb03;
    private CheckBox cb04;
    private CheckBox cb05;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_comment;
    }

    @Override
    protected void initView() {
        pd = new Utils.PdUtil(this);

        //返回
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //发起评论
        findViewById(R.id.click).setOnClickListener(this);

        //评论内容
        editText = findViewById(R.id.edt_text);

        //五个星星，代表分数5
        cb01 = findViewById(R.id.cm_ck01);
        cb02 = findViewById(R.id.cm_ck02);
        cb03 = findViewById(R.id.cm_ck03);
        cb04 = findViewById(R.id.cm_ck04);
        cb05 = findViewById(R.id.cm_ck05);


        mDragTip = findViewById(R.id.drag_tip);
        mSelectedImageRv = findViewById(R.id.rv_selected_image);
        mSelectedImageRv.setNestedScrollingEnabled(false);
        mSelectedImageRv.setLayoutManager(new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false));
        mSelectedImageRv.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 1)));
        btnSelect = findViewById(R.id.btn_select);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }

    @Override
    protected void initData() {
        ImageView img = findViewById(R.id.img);
        TextView name = findViewById(R.id.mess);
        TextView price = findViewById(R.id.price);

        goodsId = getIntent().getStringExtra("goodsId");
        orderId = getIntent().getStringExtra("orderId");

        if (!StringUtils.isEmpty(getIntent().getStringExtra("url"))){
            Glide.with(this).load(getIntent().getStringExtra("url")).into(img);
        }
        name.setText(getIntent().getStringExtra("name"));
        price.setText(getIntent().getStringExtra("price"));
        getIntent().getStringExtra("");
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.click:{
                judge();
                break;
            }
        }
    }

    private void judge() {
        if (StringUtils.isEmpty(editText.getText().toString())){
            ToastUtil.showShort(this,"评论内容不能留空哦~~~");
            return;
        }

        if (!cb01.isChecked() && !cb02.isChecked() && !cb03.isChecked() && !cb04.isChecked() && !cb05.isChecked()){
            ToastUtil.showShort(this,"请为商品打个分吧~~~");
            return;
        }
        pd.showLoding("评论");

        //评论的图片
        List<File> list = null;
        if (null != mSelectImages && mSelectImages.size()>0){
            list = new ArrayList<>();
            for (int i=0;i<mSelectImages.size();i++){
                if (null!=mSelectImages.get(i).getPath()){
                    list.add(new File(mSelectImages.get(i).getPath()));
                }
            }
        }

        //评论的评分
        int grade =0;
        List<CheckBox>cbList = new ArrayList<>();
        cbList.add(cb01);
        cbList.add(cb02);
        cbList.add(cb03);
        cbList.add(cb04);
        cbList.add(cb05);
        for (int j=0;j<cbList.size();j++){
            if (cbList.get(j).isChecked()){
                grade++;
            }
        }
        senMyServer(String.valueOf(grade),list);
    }


    /**
     *
     * @param grade 一颗星代表一分，传1
     */
    private void senMyServer(String grade,List<File> list) {
        Map<String,Object>map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME,getSPKEY(this,"token"));
        map.put("goodsId",goodsId);
        map.put("orderId",orderId);
        map.put("commentContent",editText.getText().toString());
        map.put("grade",grade);

        if (null != list && list.size()>0){
            map.put("commentImg",list);
        }
        OkHttp3Utils.getInstance(DyUrl.BASE).upLoadFile3(DyUrl.getCommentSave, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if(StringUtils.isEmpty(result)){
                    return ;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int errno = object.optInt("errno");
                    if(errno == 0){
                        if(500 == object.optInt("code")){
                            ToastUtil.showShort(CommentActivity.this,object.optString("msg"));
                            pd.desLoding();
                            return ;
                        }
                        pd.desLoding();
                        alertDialog = new AlertDialog.Builder(CommentActivity.this)
                                .setTitle("海风暴")
                                .setMessage("评论成功")
                                .setIcon(R.mipmap.application)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(CommentActivity.this, HxActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .setNeutralButton("取消", new DialogInterface.OnClickListener() {//添加普通按钮
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                        if (alertDialog.isShowing()) {
                                            alertDialog.dismiss();
                                        }
                                    }
                                }).create();
                        alertDialog.show();
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



}
