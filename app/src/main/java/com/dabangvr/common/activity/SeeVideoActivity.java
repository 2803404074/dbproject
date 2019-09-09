package com.dabangvr.common.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.lbroadcast.widget.MediaController;
import com.dabangvr.util.TextUtil;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.widget.PLVideoTextureView;

public class SeeVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private PLVideoTextureView mVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_video);
        initView();
    }

    private void initView() {
        findViewById(R.id.back).setOnClickListener(this);
        //视频播放器
        mVideoView = (PLVideoTextureView) findViewById(R.id.video_view);

        //缓冲等待控件
        View loadingView = findViewById(R.id.loading_view);

        //播放控制器
        MediaController mMediaController = new MediaController(this);

        mVideoView.setMediaController(mMediaController);
        mVideoView.setBufferingIndicator(loadingView);


        AVOptions options = new AVOptions();
        int codec = AVOptions.MEDIA_CODEC_AUTO; //硬解优先，失败后自动切换到软解
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_FAST_OPEN, 1);

        // 默认的缓存大小，单位是 ms
        // 默认值是：2000
        options.setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, 2000);

        // 最大的缓存大小，单位是 ms
        // 默认值是：4000
        options.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 4000);
        mVideoView.setAVOptions(options);

        String path = getIntent().getStringExtra("path");
        String title = getIntent().getStringExtra("title");
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(TextUtil.isNull2Url(title));
        mVideoView.setVideoPath(path);
        mVideoView.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mVideoView.stopPlayback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
                default:break;
        }
    }
}
