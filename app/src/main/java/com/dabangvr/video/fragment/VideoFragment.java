package com.dabangvr.video.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.common.activity.MyFragment;
import com.dabangvr.common.weight.BaseLoadMoreHeaderAdapter;
import com.dabangvr.common.weight.BaseRecyclerHolder;
import com.dabangvr.model.Goods;
import com.dabangvr.util.BottomImgSize;
import com.dabangvr.util.DateUtil;
import com.dabangvr.util.JsonUtil;
import com.dabangvr.util.ToastUtil;
import com.dabangvr.video.fragment.model.CommentMo;
import com.dabangvr.video.fragment.model.PlayMode;
import com.dabangvr.video.fragment.widget.OnViewPagerListener;
import com.dabangvr.video.fragment.widget.PagerLayoutManager;
import com.facebook.drawee.view.SimpleDraweeView;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;
import com.rey.material.app.BottomSheetDialog;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.io.NumberInput;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.DyUrl;
import config.GiftUrl;
import okhttp3.Call;

public class VideoFragment extends MyFragment implements View.OnClickListener {
    private PLVideoTextureView mVideoView;

    private SimpleDraweeView draweeView;
    private TextView tvAuth;
    private TextView tvContent;

    private TextView tvDz;
    private TextView tvComment;
    private TextView tvShare;

    private RecyclerView recyclerView;
    private BaseLoadMoreHeaderAdapter adapter;
    private List<PlayMode> mData = new ArrayList<>();
    private List<PlayMode> mData2 = new ArrayList<>();
    private TextView tvgz;
    private long intPage = 1;

    public static VideoFragment newInstance() {
        VideoFragment fragment = new VideoFragment();
        return fragment;
    }
    @Override
    protected int setContentView() {
        return R.layout.recy_nopadding_demo;
    }

    @Override
    protected void stopLoad() {
        super.stopLoad();
        if (null != mVideoView) {
            mVideoView.stopPlayback();
            mVideoView=null;
        }
    }

    @Override
    protected void stopPlay() {
        super.stopPlay();
        if (null != mVideoView) {
            mVideoView.stopPlayback();
        }
    }

    private PagerLayoutManager manager;
    private int tag = 0;

    @Override
    protected void lazyLoad() {
        if (tag == 0) {
            recyclerView = findViewById(R.id.recy);
            manager = new PagerLayoutManager(VideoFragment.this.getContext(), OrientationHelper.VERTICAL);
            recyclerView.setLayoutManager(manager);
            adapter = new BaseLoadMoreHeaderAdapter<PlayMode>(VideoFragment.this.getContext(), recyclerView, mData, R.layout.fragment_video_item) {
                @Override
                public void convert(Context mContext, BaseRecyclerHolder holder, PlayMode o) {
                    Log.d(TAG, "convert: "+o.getId());
                }
            };
            recyclerView.setAdapter(adapter);

//            商品列表滑到底部后加载更多数据
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (!recyclerView.canScrollVertically(1)) {
                        intPage++;
                        initData(intPage);
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });


            manager.setOnViewPagerListener(new OnViewPagerListener() {
                @Override
                public void onInitComplete(View view) {
                    playVideo(0, view);
                    positionVo = 0;
                    PlayMode playMode = mData.get(positionVo);
                    showMassgse(view, playMode, positionVo);
                }

                @Override
                public void onPageSelected(int position, boolean isBottom, View view) {
                    if (position != positionVo) {
                        playVideo(position, view);
                        positionVo = position;
//                        tvAdd.setVisibility(View.VISIBLE);
                    }
                    //填充头像昵称标题 点赞量评论量分享量等

                    //判断是否已经点赞过了
                    if (mData.get(position).isDz()) {
                        tvDz.setTextColor(getResources().getColor(R.color.colorAccent));
                        tvDz.setClickable(false);
                    } else {
                        tvDz.setTextColor(getResources().getColor(R.color.white));
                        tvDz.setClickable(true);
                    }
                    Log.d(TAG, "onPageSelected: "+position);
                    Log.d(TAG, "onPageSelectedmData: "+mData.size());
//                    if (mData!=null&&mData.size()>0){
//                        if (position==mData.size()-2){
//                            Log.d(TAG, "onPagmDataeSelected: "+mData.size());
//                            intPage++;
//                            initData(intPage);
//                        }
//                    }
                    showMassgse(view, mData.get(position), positionVo);
                }

                @Override
                public void onPageRelease(boolean isNext, int position, View view) {
                    int index = 0;
                    if (isNext) {
                        index = 0;
                    } else {
                        index = 1;
                    }
                    Log.d(TAG, "onPageRelease: "+position);
                    releaseVideo(view);
                }
            });
            initData(intPage);
            tag++;
        }
    }

    private void showMassgse(View view, PlayMode playMode, int positionVo) {
        draweeView = view.findViewById(R.id.drawee_img);
        tvAuth = view.findViewById(R.id.tv_auth);
        tvDz = view.findViewById(R.id.tv_dz);
        tvComment = view.findViewById(R.id.tv_comment);
        tvShare = view.findViewById(R.id.tv_share);
        tvAdd = view.findViewById(R.id.tv_add);
        tvContent = view.findViewById(R.id.tv_content);
        tvDz = view.findViewById(R.id.tv_dz);
        tvComment = view.findViewById(R.id.tv_comment);
        tvShare = view.findViewById(R.id.tv_share);
        tvAdd = view.findViewById(R.id.tv_add);
        tvgz = view.findViewById(R.id.tv_gz);
        //设置点赞评论分享，，头像，昵称，标题
        tvDz.setText(playMode.getPraseCount());
        tvComment.setText(playMode.getCommentCount());
        draweeView.setImageURI(playMode.getHeadUrl());
        tvAuth.setText(playMode.getNickName());
        tvContent.setText(playMode.getTitle());
        BottomImgSize bis = new BottomImgSize<>(VideoFragment.this.getContext());
        bis.setImgSize(120, 120, 1, tvDz, R.mipmap.red_love);
        bis.setImgSize(120, 120, 1, tvComment, R.mipmap.red_message);
        bis.setImgSize(120, 120, 1, tvShare, R.mipmap.white_share);
        tvDz.setOnClickListener(this);
        tvComment.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        draweeView.setOnClickListener(this);
        tvgz.setOnClickListener(this);
    }


    private int positionVo;

    private void initData(long page) {
        final Map<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(VideoFragment.this.getContext(), "token"));
        map.put("page", String.valueOf(page));
        map.put("limit", "2");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(GiftUrl.getLiveShortVideoList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) return;
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")) {
                        if (500 == object.optInt("code")) {
                            return;
                        }
                        String data = object.optString("data");
                        List<PlayMode> playModes = JsonUtil.string2Obj(data, List.class, PlayMode.class);
                        if (playModes !=null&&playModes.size()> 0) {
                            mData.addAll(playModes);
                            Log.d(TAG, "onUmDatai: "+mData.size()+"===="+mData.get(0).getId());
                            Log.d(TAG, "playModes: "+playModes.size());
                            adapter.updateDataa(mData);
                        }
//                        mData = JsonUtil.string2Obj(data, List.class, PlayMode.class);
//                        if (mData != null && mData.size() > 0) {
//                            adapter.updateData(mData);
//                        }
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

    /**
     * 播放视频
     */
    private void playVideo(int position, View view) {
        if (view != null) {
            mVideoView = view.findViewById(R.id.video_view);
            mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
            setAvOption(mVideoView);
            mVideoView.setVideoPath(mData.get(position).getVideoUrl());
            mVideoView.setLooping(true);
            baseUrl = mData.get(position).getVideoUrl();
            mVideoView.start();
        }
    }

    /**
     * 停止播放
     */
    private void releaseVideo(View view) {
        if (view != null) {
            PLVideoTextureView videoView = view.findViewById(R.id.video_view);
            videoView.stopPlayback();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mVideoView) {
            mVideoView.stopPlayback();
        }
        Log.e("niubi", "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mVideoView) {
            mVideoView.stopPlayback();
        }
        Log.e("niubi", "onDestroyView");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("niubi", "onResume");
        if (null != mVideoView) {
            mVideoView.setVideoPath(baseUrl);
            mVideoView.setLooping(true);
            mVideoView.start();
        }
    }


    private String baseUrl = "";

    @Override
    public void onPause() {
        super.onPause();
        Log.e("niubi", "onPause");
        if (null != mVideoView) {
            mVideoView.stopPlayback();
            mVideoView=null;
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //不可见
            if (null != mVideoView) {
                mVideoView.stopPlayback();
                mVideoView=null;
            }
        } else {
            //可见
        }
    }

    private TextView tvAdd;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dz:
                dzFunction();
                break;
            case R.id.tv_comment:
                getCommentData();
                break;
            case R.id.tv_share:
                // TODO: 2019/8/1 分享
                break;
            case R.id.tv_gz:
                // TODO: 2019/8/1 关注
                break;
            default:
                break;
        }
    }

    /**
     * 点赞视频（更新UI）
     */
    private void dzFunction() {
        tvDz.setText(String.valueOf(Integer.parseInt(tvDz.getText().toString()) + 1));
        tvDz.setTextColor(getResources().getColor(R.color.colorAccent));
        if (mData.size() == 0) return;
        mData.get(positionVo).setDz(true);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(tvAdd, "translationY", 0f, -60f);
        objectAnimator.setDuration(500);
        tvAdd.setVisibility(View.VISIBLE);
        objectAnimator.start();
        toServer("");//点赞视频

        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                tvAdd.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * 点赞视频（数据录入）
     */
    private void toServer(String commentId) {
        Map<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(VideoFragment.this.getContext(), "token"));
        map.put("id", mData.get(positionVo).getId());
        map.put("commentId", commentId);//评论目标ID,为空则默认为评论视频
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(GiftUrl.praseShortVideo, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {

                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }


    //private boolean isLoad = true;

    /**
     * 获取评论列表
     */
    private void getCommentData() {
        commentData.clear();
        if (mData.size() == 0) return;
        Map<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(VideoFragment.this.getContext(), "token"));
        map.put("videoId", mData.get(positionVo).getId());
        map.put("page", "1");
        map.put("limit", "20");
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(GiftUrl.getShortVideoComment, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) return;
                try {
                    JSONObject object = new JSONObject(result);
                    if (0 == object.optInt("errno")) {
                        if (500 == object.optInt("code")) return;
                        String dataStr = object.optString("data");
                        commentData = JsonUtil.string2Obj(dataStr, List.class, CommentMo.class);
                        showCommentView();
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

    private BaseLoadMoreHeaderAdapter commentAdapter;
    private RecyclerView commentRecy;
    private List<CommentMo> commentData = new ArrayList<>();


    /**
     * 弹出评论列表视图
     */
    private void showCommentView() {
        final BottomSheetDialog dialog = new BottomSheetDialog(VideoFragment.this.getContext());
        final View view = LayoutInflater.from(VideoFragment.this.getContext()).inflate(R.layout.dialog_short_comment, null);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        final EditText editText = view.findViewById(R.id.et_content);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setFocusableInTouchMode(true);
                editText.setFocusable(true);
            }
        });


        commentRecy = view.findViewById(R.id.recy);

        commentRecy.setLayoutManager(new LinearLayoutManager(VideoFragment.this.getContext()));
        commentAdapter = new BaseLoadMoreHeaderAdapter<CommentMo>(VideoFragment.this.getContext(),
                commentRecy, commentData, R.layout.short_comment_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, final CommentMo o) {
                SimpleDraweeView draweeView = holder.getView(R.id.drawee_img);
                draweeView.setImageURI(o.getHeadUrl());
                holder.setText(R.id.tv_auth, o.getNickName());
                if (o.getAddTime().equals("刚刚")) {
                    holder.setText(R.id.tv_data, o.getAddTime());
                } else {
                    holder.setText(R.id.tv_data, DateUtil.stampToDate(o.getAddTime()));
                }
                holder.setText(R.id.tv_content, o.getContent());
                holder.setText(R.id.tv_dzsize, o.getPraseCount());

                if (null != o.getChildren() && o.getChildren().size() > 0) {
                    RecyclerView recyc = holder.getView(R.id.recy);
                    recyc.setLayoutManager(new LinearLayoutManager(VideoFragment.this.getContext()));
                    BaseLoadMoreHeaderAdapter adapter = new BaseLoadMoreHeaderAdapter<CommentMo>(VideoFragment.this.getContext(),
                            recyc, o.getChildren(), R.layout.short_comment_item) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, CommentMo o) {

                            holder.setText(R.id.tv_auth, o.getNickName());
                            holder.setText(R.id.tv_data, o.getAddTime());
                            holder.setText(R.id.tv_content, o.getContent());

                            SimpleDraweeView draweeView = holder.getView(R.id.drawee_img);
                            draweeView.setImageURI(o.getHeadUrl());
                            ViewGroup.LayoutParams para1;
                            para1 = draweeView.getLayoutParams();
                            para1.height = 80;
                            para1.width = 80;
                            draweeView.setLayoutParams(para1);
                            holder.getView(R.id.tv_dzsize).setVisibility(View.GONE);
                        }
                    };
                    recyc.setAdapter(adapter);
                }


                final TextView textView = holder.getView(R.id.tv_dzsize);
                BottomImgSize bis = new BottomImgSize<>(VideoFragment.this.getContext());
                bis.setImgSize(59, 59, 1, textView, R.mipmap.short_cm_dz);


                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //是否点赞过该评论
                        if (o.isClick()) {
                            textView.setText(String.valueOf(Integer.parseInt(textView.getText().toString()) - 1));
                            BottomImgSize bis = new BottomImgSize<>(VideoFragment.this.getContext());
                            bis.setImgSize(59, 59, 1, textView, R.mipmap.short_cm_dz);
                            o.setClick(false);
                        } else {
                            textView.setText(String.valueOf(Integer.parseInt(textView.getText().toString()) + 1));
                            BottomImgSize bis = new BottomImgSize<>(VideoFragment.this.getContext());
                            bis.setImgSize(59, 59, 1, textView, R.mipmap.short_cm_dz_h);
                            o.setClick(true);
                            toServer(o.getId());//点赞评论
                        }
                    }
                });
            }
        };

        view.findViewById(R.id.tv_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(editText.getText().toString())) {
                    ToastUtil.showShort(VideoFragment.this.getContext(), "评论内容是空的~~");
                } else {
                    ToastUtil.showShort(VideoFragment.this.getContext(), "评论成功");
                    sendComment(editText.getText().toString(), "");
                    editText.setText("");
                }
            }
        });


        WindowManager wm = (WindowManager) VideoFragment.this.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        //int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）

        float oneHight = height * 0.25f;//屏幕四分之一的高度
        int towHight = height - (int) oneHight;//屏幕高度-屏幕四分之一的高度 = 所展示的弹窗高度（屏幕的四分之三）
        commentRecy.setAdapter(commentAdapter);

        TextView tvSize = view.findViewById(R.id.tv_comment_size);
        tvSize.setText(commentAdapter.getData().size() + "条评论");

        dialog.contentView(view)/*加载视图*/
                .heightParam(towHight)/*,显示的高度*/
                /*动画设置*/
                .inDuration(200)
                .outDuration(200)
                /*.inInterpolator(new BounceInterpolator())
                .outInterpolator(new AnticipateInterpolator())*/
                .cancelable(true)
                .show();
    }

    private void sendComment(final String content, String commentId) {
        Map<String, String> map = new HashMap<>();
        map.put(DyUrl.TOKEN_NAME, getSPKEY(VideoFragment.this.getContext(), "token"));
        map.put("videoId", mData.get(positionVo).getId());
        map.put("content", content);
        if (!StringUtils.isEmpty(commentId)) {
            map.put("commentId", commentId);//评论目标ID,为空则默认为评论视频
        }
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(GiftUrl.commentShortVideo, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String s) {
                if (StringUtils.isEmpty(s)) return;
                CommentMo commentMo = new CommentMo();
                commentMo.setContent(content);
                commentMo.setAddTime("刚刚");
                commentMo.setPraseCount("0");
                commentMo.setHeadUrl(getSPKEY(VideoFragment.this.getContext(), "head"));
                commentMo.setNickName(getSPKEY(VideoFragment.this.getContext(), "name"));
                commentAdapter.addAll2(commentMo);
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });

    }


    private void setAvOption(PLVideoTextureView mVideoView) {
        AVOptions options = new AVOptions();

// 解码方式:
// codec＝AVOptions.MEDIA_CODEC_HW_DECODE，硬解
// codec=AVOptions.MEDIA_CODEC_SW_DECODE, 软解
        int codec = AVOptions.MEDIA_CODEC_AUTO; //硬解优先，失败后自动切换到软解
// 默认值是：MEDIA_CODEC_SW_DECODE
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);


// 快开模式，启用后会加快该播放器实例再次打开相同协议的视频流的速度
        options.setInteger(AVOptions.KEY_FAST_OPEN, 1);

// 默认的缓存大小，单位是 ms
// 默认值是：2000
        options.setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, 2000);

// 最大的缓存大小，单位是 ms
// 默认值是：4000
        options.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 4000);

        mVideoView.setAVOptions(options);
    }
}
