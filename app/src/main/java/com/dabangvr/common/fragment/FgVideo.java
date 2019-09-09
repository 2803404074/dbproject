package com.dabangvr.common.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dabangvr.common.weight.MmediaController;
import com.dabangvr.common.weight.VideoView;
import com.dabangvr.R;
import com.dabangvr.common.activity.MyFragment;

/**
 * 商品详情页播放视频的fragment
 */
@SuppressLint("ValidFragment")
public class FgVideo extends Fragment {
    private VideoView player;
    private RelativeLayout playerParent;
    private String videoUrl;
    private TextView showView;


    public FgVideo(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public static FgVideo newInstance(String videoUrl) {
        FgVideo fragment = new FgVideo(videoUrl);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_fragment, container, false);
        initView(view);
        return view;
    }



    private void initView(View view) {
        //player = (VideoView) view.findViewById(R.id.paly_video);
        showView = view.findViewById(R.id.show_view);
        playerParent = (RelativeLayout)view.findViewById(R.id.player_parent);
        //设置页和当前页一致时加载，防止预加载
//        if (isFirst && mTabPos == mSerial) {
//            isFirst = false;
//            sendMessage();
//        }
        MmediaController mmediaController = new MmediaController(FgVideo.this.getActivity());
        player.setVideoPath(videoUrl);
        player.start();
        mmediaController
                .setPlayerParent(playerParent)
                .setPlayer(player)
                .setText(showView)
                .build();
    }
}
