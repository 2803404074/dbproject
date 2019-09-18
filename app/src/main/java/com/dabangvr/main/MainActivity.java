package com.dabangvr.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.dynamic.activity.DynamicActivity;
import com.dabangvr.home.activity.XrflActivity;
import com.dabangvr.home.interf.ChangeRadioButtonCallBack;
import com.dabangvr.lbroadcast.activity.PlayZhiBoActivity;
import com.dabangvr.lbroadcast.fragment.page.FragmentZhiboCopy;
import com.dabangvr.lbroadcast.fragment.page.ZhiBoPage;
import com.dabangvr.my.StatusBarUtil;
import com.dabangvr.my.activity.ApplyAnchorActivity;
import com.dabangvr.my.activity.LoginActivity;
import com.dabangvr.my.fragment.FragmentMy;
import com.dabangvr.util.BottomImgSize;
import com.dabangvr.util.CheckPage;
import com.dabangvr.util.DialogUtilT;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils2;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.video.play.VideoRecordActivity;
import com.dabangvr.video.utils.PermissionChecker;
import com.dabangvr.video.utils.ToastUtils;
import com.dabangvr.view.home.CartFragment;
import com.dabangvr.wxapi.AppManager;

import bean.UserMess;
import butterknife.BindView;

public class MainActivity extends BaseNewActivity implements RadioGroup.OnCheckedChangeListener, ChangeRadioButtonCallBack {

    @BindView(R.id.main_orther)
    TextView tvShow;

    private Dialog mShareDialog;
    private RadioGroup radioGroup;
    private FragmentManager fragmentManager;
    private FragmentZhiboCopy fg_01;
    private FragmentMy fg_04;
    private BottomImgSize bis;
    private RadioButton radioButton_00, radioButton_01, radioButton_03, radioButton_04;

    private ImageView imgCent;

    private int height;
    private FrameLayout fg_content;

    private CartFragment cartFragment03;
    private ZhiBoPage zhiBoPage01;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            //解决Android5.0以上，状态栏设置颜色后变灰的问题
            StatusBarUtil.setTransparentForWindow(this);
        }
    }

    @Override
    public int setLayout() {
        return R.layout.activity_main;
    }

    //初始化控件
    @Override
    public void initView() {
        fragmentManager = getSupportFragmentManager();
        radioGroup = findViewById(R.id.main_radiogrop);
        radioGroup.setOnCheckedChangeListener(this);
        radioButton_00 = findViewById(R.id.main_home);
        fg_content = findViewById(R.id.fg_content);
        radioButton_01 = findViewById(R.id.main_zhibo);
        imgCent = (ImageView) findViewById(R.id.iv_cent_id);
        radioButton_03 = findViewById(R.id.main_video);
        radioButton_04 = findViewById(R.id.main_my);

        radioButton_00.setChecked(true);


        bis = new BottomImgSize<>(this);
        bis.setImgSize(69, 69, 1, radioButton_00, R.drawable.main_home);
        bis.setImgSize(69, 69, 1, radioButton_01, R.drawable.main_zhibo);
//            bis.setImgSize(80, 80, 1, tvShow, R.drawable.main_orther);
        bis.setImgSize(69, 69, 1, radioButton_03, R.drawable.main_cart);
        bis.setImgSize(69, 69, 1, radioButton_04, R.drawable.main_my);
        imgCent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        ViewTreeObserver viewTreeObserver = radioGroup.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                radioGroup.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                height = radioGroup.getMeasuredHeight();
                fg_content.setPadding(0, 0, 0, height);
            }
        });
    }


    private DialogUtilT dialogUtilT;
    @Override
    public void initData() {
        //判断是否是8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //判断8.0是否有通知权限
            if(!NotificationManagerCompat.from(this).areNotificationsEnabled()){
                //是否之前弹窗过并且点击了不再提醒
                boolean isNotify = (boolean) SPUtils2.instance(MainActivity.this).getkey("no_notify",false);
                if (isNotify){
                    return;
                }
                //没有该权限则弹窗提示
                dialogUtilT = new DialogUtilT(this) {
                    @Override
                    public void convert(BaseRecyclerHolder holder) {
                        holder.getView(R.id.tv_go).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CheckPage.gotoNotificationSetting(MainActivity.this);
                            }
                        });
                    }
                };
                dialogUtilT.setCallBack(new DialogUtilT.CloseCallBack() {
                    @Override
                    public void close() {
                        SPUtils2.instance(MainActivity.this).put("no_notify",true);
                    }
                });
                dialogUtilT.show(R.layout.dialog_set);
            }else {
                if (dialogUtilT!=null){
                    dialogUtilT.des();
                }
            }
        }

        String user = getSPKEY(this,"user");
        UserMess userMess = JsonUtil.string2Obj(user, UserMess.class);
        if (true){
            dialogUtilT = new DialogUtilT(this) {
                @Override
                public void convert(BaseRecyclerHolder holder) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), XrflActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            };
            dialogUtilT.show(R.layout.user_new_dialog);
        }
    }

    /**
     * RadioButton点击监听
     *
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.main_home: {
                changeFragment(0);
                break;
            }
            case R.id.main_zhibo: {
                changeFragment(1);
                break;
            }
            case R.id.main_video: {
                changeFragment(3);
                break;
            }
            case R.id.main_my: {
                changeFragment(4);
                break;
            }
        }
    }


    public void changeFragment(int index) {

        // 调用FragmentTransaction中的方法来处理这个transaction
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        hideFragments(beginTransaction);

        // 根据不同的标签页执行不同的操作
        switch (index) {
            case 1:
                if (zhiBoPage01 == null) {
                    zhiBoPage01 = new ZhiBoPage(0);
                    beginTransaction.add(R.id.fg_content, zhiBoPage01);
                } else {
                    beginTransaction.show(zhiBoPage01);
                }
                break;
            case 0:
                if (fg_01 == null) {
                    fg_01 = FragmentZhiboCopy.newInstance(1);
                    beginTransaction.add(R.id.fg_content, fg_01);
                } else {
                    beginTransaction.show(fg_01);
                }
                break;
            case 3: //购物车
                if (cartFragment03 == null) {
                    cartFragment03 = CartFragment.newInstance(3);
                    beginTransaction.add(R.id.fg_content, cartFragment03);
                } else {
                    beginTransaction.show(cartFragment03);
                }
                break;
            case 4:
                if (fg_04 == null) {
                    fg_04 = new FragmentMy();
                    beginTransaction.add(R.id.fg_content, fg_04);
                } else {
                    beginTransaction.show(fg_04);
                }
                break;
            default:
                break;
        }
        //需要提交事务
        beginTransaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {

        /*****/
        if (zhiBoPage01 != null) {
            transaction.hide(zhiBoPage01);
        }

        if (fg_01 != null) {
            transaction.hide(fg_01);
        }
        if (cartFragment03 != null) {
            transaction.hide(cartFragment03);
        }
        if (fg_04 != null) {
            transaction.hide(fg_04);
        }
    }

    private void showDialog() {
        if (mShareDialog == null) {
            initShareDialog();
        }
        mShareDialog.show();
    }

    /**
     * 初始化分享弹出框
     */
    private void initShareDialog() {
        mShareDialog = new Dialog(this, R.style.dialog);
        mShareDialog.setCanceledOnTouchOutside(true);
        mShareDialog.setCancelable(true);
        Window window = mShareDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.share_animation);
        View view = View.inflate(this, R.layout.main_dilog, null);

        TextView textView1 = view.findViewById(R.id.dilog_tv1);
        TextView textView2 = view.findViewById(R.id.dilog_tv2);
        TextView textView3 = view.findViewById(R.id.dilog_tv3);
        bis.setImgSize(150, 150, 1, textView1, R.drawable.dilog_main_zhibo);
        bis.setImgSize(150, 150, 1, textView2, R.drawable.dilog_main_vedio);
        bis.setImgSize(150, 150, 1, textView3, R.drawable.dilog_main_dynamic);

        view.findViewById(R.id.dilog_main_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShareDialog != null && mShareDialog.isShowing()) {
                    mShareDialog.dismiss();
                }
            }
        });
        //开直播
        view.findViewById(R.id.dilog_tv1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getSPKEY(MainActivity.this, "isAnchor").equals("1")) {//是否已经主播认证
                    Intent intent = new Intent(MainActivity.this, PlayZhiBoActivity.class);
                    startActivity(intent);
                } else {
                    show(MainActivity.this, 1, "只有通过实名认证才能开直播哦", "去认证");
                }
                if (mShareDialog != null && mShareDialog.isShowing()) {
                    mShareDialog.dismiss();
                }

            }
        });

        //短视频
        view.findViewById(R.id.dilog_tv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSPKEY(MainActivity.this, "isAnchor").equals("1")) {//是否已经主播认证
                    if (isPermissionOK()) {
                        jumpToCaptureActivity();
                    }
                } else {
                    show(MainActivity.this, 1, "只有通过实名认证才能发视频哦", "去认证");
                }

                if (mShareDialog != null && mShareDialog.isShowing()) {
                    mShareDialog.dismiss();
                }
            }
        });

        //动态
        view.findViewById(R.id.dilog_tv3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getSPKEY(MainActivity.this, "isAnchor").equals("1")) {//是否已经主播认证
                    Intent intent = new Intent(MainActivity.this, DynamicActivity.class);
                    startActivity(intent);
                } else {
                    show(MainActivity.this, 1, "只有通过实名认证才能发动态哦", "去认证");
                }
                if (mShareDialog != null && mShareDialog.isShowing()) {
                    mShareDialog.dismiss();
                }
            }
        });
        window.setContentView(view);
        window.setLayout(1000, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
    }

    public void jumpToCaptureActivity() {
        Intent intent = new Intent(MainActivity.this, VideoRecordActivity.class);
        startActivity(intent);
    }

    private boolean isPermissionOK() {
        PermissionChecker checker = new PermissionChecker(this);
        boolean isPermissionOK = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checker.checkPermission();
        if (!isPermissionOK) {
            ToastUtils.s(this, "Some permissions is not approved !!!");
        }
        return isPermissionOK;
    }

    @Override
    public void change(int index) {
        switch (index) {
            case 0:
                radioButton_00.setChecked(true);
                break;
            case 1:
                radioButton_01.setChecked(true);
                break;
            case 3:
                radioButton_03.setChecked(true);
                break;
            case 4:
                radioButton_04.setChecked(true);
                break;
            default:
                break;
        }
        changeFragment(index);
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void show(final Activity activity, final int index, String mess, String btnStr) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle("海风暴")
                .setMessage(mess)
                .setIcon(R.mipmap.application)
                .setPositiveButton(btnStr, new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (index == 0) {
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                        }
                        if (index == 1) {
                            Intent intent = new Intent(activity, ApplyAnchorActivity.class);
                            activity.startActivity(intent);
                        }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CheckPage.NOTIFY_RESULT_CODE){
            if (dialogUtilT!=null){
                dialogUtilT.des();
            }
        }
    }
}

