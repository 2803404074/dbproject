package com.dabangvr.lbroadcast.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.lbroadcast.ui.CameraPreviewFrameView;
import com.dabangvr.lbroadcast.widget.BarrageView;
import com.dabangvr.main.ConnectServer;
import com.dabangvr.model.Goods;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.SPUtils;
import com.dabangvr.util.ToastUtil;
import com.example.mina.ConnectionManager;
import com.example.mina.SessionManager;
import com.example.widget.DKDragView;
import com.example.widget.OnDragViewClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.http.DnspodFree;
import com.qiniu.android.dns.local.AndroidDnsServer;
import com.qiniu.android.dns.local.Resolver;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.WatermarkSetting;
import com.qiniu.pili.droid.streaming.microphone.AudioMixer;
import com.rey.material.app.BottomSheetDialog;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import Utils.PdUtil;
import bean.UserMess;
import config.DyUrl;
import config.GiftUrl;
import main.CommentMo;
import okhttp3.Call;
import qqmusic.MusicMo;

import static com.qiniu.pili.droid.streaming.AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC;

public class HWCameraStreamingActivity extends Activity implements
        StreamingStateChangedListener,
        View.OnClickListener, OnDragViewClickListener, DKDragView.onDragViewClickListener {

    private MessageBroadcastReceiver receiver = new MessageBroadcastReceiver();

    private volatile boolean isFace = true;

    //K歌选完歌曲后的回调
    public static final int K_SONG_REQ = 666;
    //申请录音权限
    private static final int GET_RECODE_AUDIO = 1;
    private static String[] PERMISSION_AUDIO = {
            Manifest.permission.RECORD_AUDIO
    };

    private CameraStreamingSetting cameraStreamingSetting;
    private static final String TAG = "LiveAcivitys";
    private MediaStreamingManager mMediaStreamingManager;
    private StreamingProfile mProfile;
    private CameraPreviewFrameView cameraPreviewFrameView;
    private WatermarkSetting watermarksetting;

    private List<CommentMo> list = new ArrayList<>();
    private BaseLoadMoreHeaderAdapter adapter;
    private RecyclerView recyclerView;
    private int dataSize;//消息数量，始终将最新消息显示在recycelview的底部

    private EditText editText;//评论输入框
    private TextView tvSend;//发送按钮
    private TextView userNumber;//共有多少人观看

    private boolean isOK = false;//推流成功标志

    private String publishUrl;//推流地址
    private String tag;//直播间
    private Intent intent;
    private SPUtils spUtils;
    private PdUtil pdUtil;
    private UserMess mess;

    private RecyclerView recyGoods;
    private BaseLoadMoreHeaderAdapter adapterGoods;
    private List<Goods> listGoods;

    private BottomSheetDialog bottomInterPasswordDialog;

    //评论框
    private LinearLayout linearLayout;

    //功能列表
    private LinearLayout functionLayout;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 111) {
                ArrayList data = msg.getData().getParcelableArrayList("data");
                dataSize++;
                recyclerView.smoothScrollToPosition(dataSize);
                adapter.addAll(data);
            }
            return false;
        }
    });

    //功能定义
    private int mp = 0;
    private int mb = 0;
    private int hr = 0;

    //音乐播放
    private MediaPlayer mediaPlayer;


    private BarrageView barrageView;
    private List<String> barrageViews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swcamera_streaming);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        spUtils = new SPUtils(this, "db_user");
        String str = (String) spUtils.getkey("user", "");
        mess = JsonUtil.string2Obj(str, UserMess.class);
        pdUtil = new PdUtil(this);
        pdUtil.showLoding("正在初始化...");
        //申请录音权限
        verifyAudioPermissions(this);

        registerBroadcast();

        //启动服务
        intent = new Intent(this, ConnectServer.class);


        initView();
    }

    /*
     * 申请录音权限*/
    private void verifyAudioPermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION_AUDIO,
                    GET_RECODE_AUDIO);
        }
    }

    public void registerBroadcast() {
        IntentFilter filter = new IntentFilter(ConnectionManager.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }


    public void initView() {
        //弹幕控件
        barrageView = findViewById(R.id.barrageview);
        barrageViews = new ArrayList<>();

        //功能列表
        functionLayout = findViewById(R.id.function_menu);

        //点击展开功能列表
        ImageView dragView = (ImageView) findViewById(R.id.start_menu_img);
        dragView.setOnClickListener(this);

        findViewById(R.id.my_ll_goods).setOnClickListener(this);

        //美颜
        findViewById(R.id.mei_yan).setOnClickListener(this);
        //切换
        findViewById(R.id.rotation).setOnClickListener(this);
        //播放歌曲
        findViewById(R.id.start_music).setOnClickListener(this);
        //K歌
        findViewById(R.id.start_k_song).setOnClickListener(this);

        //推流地址
        publishUrl = getIntent().getStringExtra("stream_publish_url");
        //播放地址
        tag = getIntent().getStringExtra("tag");

        //主播信息
        String user = (String) spUtils.getkey("user", "");
        UserMess mess = JsonUtil.string2Obj(user, UserMess.class);
        SimpleDraweeView head = (SimpleDraweeView) findViewById(R.id.auth_img);
        if (!StringUtils.isEmpty(mess.getHeadUrl())) {
            Uri uri = Uri.parse(mess.getHeadUrl());
            head.setImageURI(uri);
        }

        TextView name = findViewById(R.id.auth_name);
        name.setText(mess.getNickName());

        TextView fanse = findViewById(R.id.auth_fanse);
        fanse.setText(mess.getFansNumber());

        //多少个用户观看
        userNumber = findViewById(R.id.user_number);

        //评论图片按钮
        findViewById(R.id.iv_comment).setOnClickListener(this);

        //评论视图
        linearLayout = findViewById(R.id.comment_view);

        //直播的商品
        recyGoods = findViewById(R.id.recy_goods);
        LinearLayoutManager goodsManager = new LinearLayoutManager(this);
        goodsManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyGoods.setLayoutManager(goodsManager);

        String goods = getIntent().getStringExtra("goods");
        if (!StringUtils.isEmpty(goods)) {
            listGoods = JsonUtil.string2Obj(goods, List.class, Goods.class);

            adapterGoods = new BaseLoadMoreHeaderAdapter<Goods>(this, recyGoods, listGoods, R.layout.zhibo_main_dilog) {
                @Override
                public void convert(Context mContext, BaseRecyclerHolder holder, Goods o) {
                    //holder.setText(R.id.count_money, o.getSellingPrice());
                    holder.setImageByUrl(R.id.hx_dilog_img, o.getListUrl());
                }
            };
            recyGoods.setAdapter(adapterGoods);
        }


        findViewById(R.id.iv_back).setOnClickListener(this);
        cameraPreviewFrameView = findViewById(R.id.cameraPreview_surfaceView);
        mProfile = new StreamingProfile();
        try {
            mProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_MEDIUM1)/*视频质量*/
                    .setAudioQuality(StreamingProfile.AUDIO_QUALITY_HIGH1)/*音频质量*/
                    .setQuicEnable(false)//RMPT or QUIC
                    .setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.PORT)//横竖屏   ENCODING_ORIENTATION.PORT 之后，播放端会观看竖屏的画面；反之
                    .setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_720)
                    .setBitrateAdjustMode(StreamingProfile.BitrateAdjustMode.Auto)//自适应码率
                    .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY)
                    .setDnsManager(getMyDnsManager())
                    .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))
                    .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000))
                    .setPublishUrl(publishUrl);//设置推流地址

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//当前竖屏；横屏SCREEN_ORIENTATION_LANDSCAPE

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        cameraStreamingSetting = new CameraStreamingSetting();
        cameraStreamingSetting.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT) // 摄像头切换,默认前置，后置是BACK
                .setContinuousFocusModeEnabled(true)//开启对焦
                .setFocusMode(CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_VIDEO)//自动对焦
                .setBuiltInFaceBeautyEnabled(true)//开启美颜
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.0f, 0.0f, 0.0f))// 磨皮，美白，红润 取值范围为[0.0f, 1.0f]
                .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_4_3);
        mMediaStreamingManager = new MediaStreamingManager(this, cameraPreviewFrameView, SW_VIDEO_WITH_SW_AUDIO_CODEC);

        //质量与性能
        StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(44100, 48 * 1024);

        StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(20, 2000 * 1024, 60, StreamingProfile.H264Profile.HIGH);

        StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);

        mProfile.setAVProfile(avProfile);

        mMediaStreamingManager.prepare(cameraStreamingSetting, mProfile);
        mMediaStreamingManager.setStreamingStateListener(this);

        watermarksetting = new WatermarkSetting(this);
//        watermarksetting.setResourceId(R.mipmap.ic_launcher)
//                .setAlpha(100)
//                .setSize(WatermarkSetting.WATERMARK_SIZE.MEDIUM)
//                .setCustomPosition(0.5f, 0.5f);
        watermarksetting.setInJustDecodeBoundsEnabled(false);
        mMediaStreamingManager.updateWatermarkSetting(watermarksetting);

        AudioMixer audioMixer = mMediaStreamingManager.getAudioMixer();


        //
        editText = findViewById(R.id.edit_text);
        tvSend = findViewById(R.id.tv_send);
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (StringUtils.isEmpty(editText.getText().toString())) {
                    ToastUtil.showShort(HWCameraStreamingActivity.this, "请输入");
                } else {
                    sand(editText.getText().toString());
                    editText.setText("");
                }
            }
        });

        //消息
        recyclerView = findViewById(R.id.recy);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        adapter = new BaseLoadMoreHeaderAdapter<CommentMo>(this, recyclerView, list, R.layout.zb_comment_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, CommentMo commentMo) {
                TextView auth = holder.getView(R.id.auth_name);
                TextView content = holder.getView(R.id.auth_content);
                //0服务，1主播信息，2主播退出，3用户信息，4用户退出
                if (commentMo.getType().equals("0")) {
                    auth.setTextColor(getResources().getColor(R.color.zhiBo));
                } else if (commentMo.getType().equals("1")) {
                    auth.setTextColor(getResources().getColor(R.color.black));
                } else if (commentMo.getType().equals("2")) {
                    auth.setTextColor(getResources().getColor(R.color.colorZi));
                } else if (commentMo.getType().equals("3")) {
                    auth.setTextColor(getResources().getColor(R.color.colorOrag));
                } else if (commentMo.getType().equals("100")) {
                    auth.setTextColor(getResources().getColor(R.color.colorDb3));
                    content.setTextColor(getResources().getColor(R.color.colorDb3));
                    content.setTextSize(25);
                }
                auth.setText(commentMo.getName());
                holder.setText(R.id.auth_content, commentMo.getMess());

                SimpleDraweeView head = holder.getView(R.id.drawee_img);
                if (!StringUtils.isEmpty(commentMo.getHead())) {
                    Uri uri = Uri.parse(commentMo.getHead());
                    head.setImageURI(uri);
                }
            }
        };

        recyclerView.setAdapter(adapter);

        pdUtil.desLoding();
    }

    @Override
    public void onStateChanged(StreamingState streamingState, Object o) {
        switch (streamingState) {
            case PREPARING:
                setData("0", "","大邦直播", "准备", "http://test.fuxingsc.com/upload/20190413/16210074362f64.png");
                Log.d(TAG, "onStateChanged: ===>" + "准备");
                break;
            case READY:
                startStreaming();
                break;
            case CONNECTING:
                setData("0", "","大邦直播", "连接", "http://test.fuxingsc.com/upload/20190413/16210074362f64.png");
                Log.d(TAG, "onStateChanged: ===>" + "连接");
                break;
            case STREAMING:
                if (!isOK) {
                    setData("0", "","大邦直播", "开始直播", "http://test.fuxingsc.com/upload/20190413/16210074362f64.png");
                    //开启服务
                    intent.putExtra("address", tag);//主播间
                    intent.putExtra("name", mess.getNickName());
                    intent.putExtra("head", mess.getHeadUrl());
                    intent.putExtra("isFirst", "create_auth");
                    startService(intent);
                }
                isOK = true;
                //Log.d(TAG, "onStateChanged: ===>" + "已发送");
                break;
            case SHUTDOWN:
                Log.d(TAG, "onStateChanged: ===>" + "推流结束");
                break;
            case IOERROR:
                setData("0", "","大邦直播", "IO异常", "http://test.fuxingsc.com/upload/20190413/16210074362f64.png");
                Log.d(TAG, "onStateChanged: ===>" + "IO异常" + o.toString());
                break;
            case SENDING_BUFFER_EMPTY:
                Log.d(TAG, "onStateChanged: ===>" + "发送缓冲区为空");
                break;
            case SENDING_BUFFER_FULL:
                Log.d(TAG, "onStateChanged: ===>" + "发送缓冲区满");
                break;
            case AUDIO_RECORDING_FAIL:
                setData("0", "","大邦直播", "录音失败", "http://test.fuxingsc.com/upload/20190413/16210074362f64.png");
                Log.d(TAG, "onStateChanged: ===>" + "录音失败");
                break;
            case OPEN_CAMERA_FAIL:
                setData("0", "","大邦直播", "相机打开失败", "http://test.fuxingsc.com/upload/20190413/16210074362f64.png");
                Log.d(TAG, "onStateChanged: ===>" + "相机打开失败");
                break;
            case DISCONNECTED:
                setData("0", "","大邦直播", "断开连接", "http://test.fuxingsc.com/upload/20190413/16210074362f64.png");
                Log.d(TAG, "onStateChanged: ===>" + "断开连接");
                break;
        }
    }

    protected void setData(String type,String gift, String auth, String mess, String head) {

        //有客户下单通知
        if (type.equals("100")){
            barrageViews.add(auth+"下了一单，获得10点积分");
            barrageView.setData(barrageViews);
            barrageView.start();
        }else if (type.equals("99")){//礼物
            barrageViews.add(auth+"送了一个"+mess);
            barrageView.setData(barrageViews);
            barrageView.setType(gift);
            barrageView.start();

            CommentMo commentMo = new CommentMo();
            commentMo.setType(type);
            commentMo.setName(auth);
            commentMo.setMess("赠送--"+mess);
            commentMo.setHead(head);
            Bundle bundle = new Bundle();
            ArrayList arr = new ArrayList();
            arr.add(commentMo);
            bundle.putStringArrayList("data", arr);
            Message message = new Message();
            message.what = 111;
            message.setData(bundle);
            handler.sendMessage(message);
        }else {
            CommentMo commentMo = new CommentMo();
            commentMo.setType(type);
            commentMo.setName(auth);
            commentMo.setMess(mess);
            commentMo.setHead(head);
            Bundle bundle = new Bundle();
            ArrayList arr = new ArrayList();
            arr.add(commentMo);
            bundle.putStringArrayList("data", arr);
            Message message = new Message();
            message.what = 111;
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }

    /**
     * @author:
     * @create
     * @Description: 开始推流
     */
    protected void startStreaming() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMediaStreamingManager.startStreaming();
                Log.d(TAG, "run: ===>" + "推流");
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaStreamingManager.resume();
        barrageView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaStreamingManager.pause();
        barrageView.onPause();
    }

    /**
     * @author:
     * @create at: 2018/11/14  10:57
     * @Description: 防止 Dns 被劫持
     */
    private static DnsManager getMyDnsManager() {
        IResolver r0 = null;
        IResolver r1 = new DnspodFree();
        IResolver r2 = AndroidDnsServer.defaultResolver();
        try {
            r0 = new Resolver(InetAddress.getByName("119.29.29.29"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new DnsManager(NetworkInfo.normal, new IResolver[]{r0, r1, r2});
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_comment:
                ValueAnimator valueAnimator = null;
                linearLayout.measure(0, 0);
                int width = linearLayout.getMeasuredWidth();
                if (linearLayout.getVisibility() == View.INVISIBLE) {
                    linearLayout.setVisibility(View.VISIBLE);
                    valueAnimator = ValueAnimator.ofInt(0, width).setDuration(500);
                    //弹出软键盘、
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, 0);
                } else {
                    linearLayout.setVisibility(View.INVISIBLE);
                    valueAnimator = ValueAnimator.ofInt(width, 0).setDuration(500);
                }


                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        linearLayout.getLayoutParams().width = (int) animation.getAnimatedValue();
                        linearLayout.requestLayout();
                    }
                });
                valueAnimator.start();
                break;

            //美颜
            case R.id.mei_yan:
                setMyDialog(0);
                break;
            case R.id.rotation: //切换摄像头
                changeCamera();
                break;
            case R.id.start_music://播放音乐
                setMyDialog(1);
                break;
            case R.id.start_k_song://K歌
//                 Intent intent = new Intent(HWCameraStreamingActivity.this,null);
//                 startActivityForResult(intent,K_SONG_REQ);
                break;
            case R.id.start_menu_img://展开功能列表
                startAnimo();
                break;
            case R.id.my_ll_goods:
                if (recyGoods.getVisibility() == View.VISIBLE) {
                    recyGoods.setVisibility(View.GONE);
                } else {
                    recyGoods.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    private void startAnimo() {
        ValueAnimator valueAnimator = null;
        functionLayout.measure(0, 0);
        int width = functionLayout.getMeasuredWidth();
        if (functionLayout.getVisibility() == View.INVISIBLE) {
            functionLayout.setVisibility(View.VISIBLE);
            valueAnimator = ValueAnimator.ofInt(0, width).setDuration(300);
        } else {
            functionLayout.setVisibility(View.INVISIBLE);
            valueAnimator = ValueAnimator.ofInt(width, 0).setDuration(300);

        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                functionLayout.getLayoutParams().width = (int) animation.getAnimatedValue();
                functionLayout.requestLayout();
            }
        });
        valueAnimator.start();
    }

    /**
     * 切换摄像头
     */
    private void changeCamera() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //mCurrentCamFacingIndex = (mCurrentCamFacingIndex + 1) % CameraStreamingSetting.getNumberOfCameras();
//                CameraStreamingSetting.CAMERA_FACING_ID facingId;
//                if (mCurrentCamFacingIndex == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK.ordinal()) {
//                    facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK;
//                } else if (mCurrentCamFacingIndex == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT.ordinal()) {
//                    facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT;
//                } else {
//                    facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD;
//                }
//                Log.i(TAG, "switchCamera:" + facingId);
//                mMediaStreamingManager.switchCamera(facingId);
//
//                mIsEncodingMirror = mCameraConfig.mEncodingMirror;
//                mIsPreviewMirror = facingId == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT ? mCameraConfig.mPreviewMirror : false;
//            }
//        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                CameraStreamingSetting.CAMERA_FACING_ID facingId;
                if (isFace) {
                    facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK;
                    isFace = false;
                } else {
                    facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT;
                    isFace = true;
                }
                mMediaStreamingManager.switchCamera(facingId);
            }
        }).start();


        //怎么更新呢,好像没有updataCameraId这个接口

    }


    /**
     * index
     *
     * @param index 美颜0   音乐1    k歌2
     */
    private void setMyDialog(int index) {
        bottomInterPasswordDialog = new BottomSheetDialog(this);

        //初始化 - 底部弹出框布局
        if (index == 0) {
            View bottomView = LayoutInflater.from(this).inflate(R.layout.seekbar_dialog, null);
            SeekBar seekMp = bottomView.findViewById(R.id.sb_mp);
            SeekBar seekMb = bottomView.findViewById(R.id.sb_mb);
            SeekBar seekHr = bottomView.findViewById(R.id.sb_hr);
            seekMp.setProgress(mp);
            seekMb.setProgress(mb);
            seekHr.setProgress(hr);
            seekMp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    CameraStreamingSetting.FaceBeautySetting fbSetting = cameraStreamingSetting.getFaceBeautySetting();
                    //磨皮
                    fbSetting.beautyLevel = progress / 100.0f;
                    mMediaStreamingManager.updateFaceBeautySetting(fbSetting);
                    mp = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            seekMb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    CameraStreamingSetting.FaceBeautySetting fbSetting = cameraStreamingSetting.getFaceBeautySetting();
//                //美白
                    fbSetting.whiten = progress / 100.0f;
                    mMediaStreamingManager.updateFaceBeautySetting(fbSetting);
                    mb = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            seekHr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    CameraStreamingSetting.FaceBeautySetting fbSetting = cameraStreamingSetting.getFaceBeautySetting();
                    //红润
                    fbSetting.redden = progress / 100.0f;
                    mMediaStreamingManager.updateFaceBeautySetting(fbSetting);
                    hr = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            bottomInterPasswordDialog
                    .contentView(bottomView)/*加载视图*/
                    .inDuration(200)
                    .outDuration(200)
                    .cancelable(true)
                    .show();

        } else if (index == 1) {
            Map<String, String> map = new HashMap<>();
            map.put(DyUrl.TOKEN_NAME, (String) spUtils.getkey("token", ""));
            OkHttp3Utils.getInstance(DyUrl.BASE).doPost(GiftUrl.getMusicList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
                @Override
                public void onUi(String result) {
                    try {
                        if (StringUtils.isEmpty(result)) return;
                        JSONObject object = new JSONObject(result);
                        String str = object.optString("data");
                        final List<MusicMo> mList = JsonUtil.string2Obj(str, List.class, MusicMo.class);

                        View bottomView = LayoutInflater.from(HWCameraStreamingActivity.this).inflate(R.layout.music_dialog, null);
                        RecyclerView recyclerView = bottomView.findViewById(R.id.recy);
                        GridLayoutManager manager = new GridLayoutManager(HWCameraStreamingActivity.this, 3);
                        recyclerView.setLayoutManager(manager);
                        BaseLoadMoreHeaderAdapter adapter = new BaseLoadMoreHeaderAdapter<MusicMo>(
                                HWCameraStreamingActivity.this, recyclerView, mList, R.layout.music_dialog_item) {
                            @Override
                            public void convert(Context mContext, BaseRecyclerHolder holder, MusicMo o) {
                                SimpleDraweeView head = holder.getView(R.id.mu_logo);
                                head.setImageURI(o.getImg());
                                holder.setText(R.id.mu_name, o.getName());
                            }
                        };
                        recyclerView.setAdapter(adapter);

                        adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, final int position) {

                                pdUtil.showLoding("正在加载音乐");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mediaPlayer != null) {
                                            if (mediaPlayer.isPlaying()) {
                                                mediaPlayer.stop();
                                            }
                                            mediaPlayer = null;
                                        }
                                        try {
                                            mediaPlayer = new MediaPlayer();
                                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                            mediaPlayer.setDataSource(mList.get(position).getUrl());
                                            mediaPlayer.prepare(); // might take long! (for buffering, etc)
                                            mediaPlayer.start();
                                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                @Override
                                                public void onPrepared(MediaPlayer mp) {
                                                    mediaPlayer.start();
                                                    pdUtil.desLoding();
                                                }
                                            });
                                            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                                @Override
                                                public boolean onError(MediaPlayer mp, int what, int extra) {
                                                    ToastUtil.showShort(HWCameraStreamingActivity.this, "稍等片刻" + what);
                                                    bottomInterPasswordDialog.dismiss();
                                                    return false;
                                                }
                                            });
                                            pdUtil.desLoding();
                                        } catch (IOException e) {
                                            bottomInterPasswordDialog.dismiss();
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                bottomInterPasswordDialog.dismiss();
                            }
                        });

                        bottomInterPasswordDialog
                                .contentView(bottomView)/*加载视图*/
                                .inDuration(200)
                                .outDuration(200)
                                .cancelable(true)
                                .show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(Call call, IOException e) {

                }
            });
        }

    }

    @Override
    public void onDragViewListener(String name, String context) {

    }

    @Override
    public void onClick() {

    }

    /**
     * 接收mina服务消息
     */
    private class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("tag", "广播接收：" + intent.getStringExtra(ConnectionManager.MESSAGE));
            try {
                String str = intent.getStringExtra(ConnectionManager.MESSAGE);
                JSONObject object = new JSONObject(str);
                String type = object.optString("type");
                String name = object.optString("name");
                String mess = object.optString("mess");
                String head = object.optString("head");
                String gift = object.optString("gift");
                setData(type,gift, name, mess, head);

                String number = object.optString("number");
                if (!StringUtils.isEmpty(number)) {
                    userNumber.setText(number);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //setData("0","服务器",intent.getStringExtra(ConnectionManager.MESSAGE));
        }

    }

    private void sand(String et) {
        JSONObject o = new JSONObject();
        try {
            o.put("sand", "sand");
            o.put("name", mess.getNickName());
            o.put("head", mess.getHeadUrl());
            o.put("address", tag);
            o.put("type", "1");
            o.put("mess", et);
            SessionManager.getInstance().writeToServer(o);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    protected void onDestroy() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
        stopService(new Intent(this, ConnectServer.class));
        unregisterBroadcast();
        stopService(intent);

        barrageView.onPause();

    }

    public void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (linearLayout.getVisibility() == View.VISIBLE) {
            linearLayout.setVisibility(View.INVISIBLE);
            return;
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (PlayZhiBoActivity.instants != null) {
            PlayZhiBoActivity.instants.finish();
        }
        stopService(new Intent(this, ConnectServer.class));
        unregisterBroadcast();
        stopService(intent);
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), "再按一次退出直播", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                if (PlayZhiBoActivity.instants != null) {
                    PlayZhiBoActivity.instants.finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
