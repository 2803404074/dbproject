package com.dabangvr.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dabangvr.R;
import com.dabangvr.home.activity.HxxqLastActivity;
import com.dabangvr.model.RotaMo;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.DyUrl;
import okhttp3.Call;

/**
 * 轮播图工具类
 */
public class BannerStart {

    /**
     * 开始轮播方法
     * <p>
     * 1首页
     * 7新品
     * 4秒杀
     * 5全球购
     * 6分类搜索
     */
    public static void starBanner(final Context context, final Banner banner, String parentId) {

        final List<String> title = new ArrayList<>();
        final List<String> img = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("parentId", parentId);
        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.ROTA, map,
                new GsonObjectCallback<String>(DyUrl.BASE) {
                    //主线程处理
                    @Override
                    public void onUi(String msg) {
                        try {
                            JSONObject object = new JSONObject(msg);
                            int code = object.optInt("errno");
                            if (code == 0) {//成功
                                JSONObject data = object.optJSONObject("data");

                                String array = data.optString("goodsRotationList");

                                final List<RotaMo> list = JsonUtil.string2Obj(array, List.class, RotaMo.class);
                                for (int i = 0; i < list.size(); i++) {
                                    title.add(list.get(i).getTitle());
                                    img.add(list.get(i).getChartUrl());
                                }

                                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
                                banner.setIndicatorGravity(BannerConfig.CENTER);
                                banner.setBannerStyle(BannerConfig.NOT_INDICATOR);
                                banner.setImageLoader(new ImageLoader() {
                                    @Override
                                    public void displayImage(Context context, Object path, ImageView imageView) {
                                        //Glide.with(context).load((String) path).into(imageView);
//                                        GlideLoadUtils.getInstance().glideLoad(context,(String) path,imageView);

                                        RequestOptions myOptions = new RequestOptions().optionalTransform
                                                (new GlideRoundedCornersTransform(15
                                                        , GlideRoundedCornersTransform.CornerType.ALL));
                                        Glide.with(context).load(path).apply(myOptions).into(imageView);
                                    }
                                });
                                banner.isAutoPlay(true);
                                banner.setDelayTime(3000);
                                banner.setImages(img);
                                banner.setBannerTitles(title);
                                banner.setBannerAnimation(Transformer.Default);
                                banner.start();
                                banner.setOnBannerListener(new OnBannerListener() {
                                    @Override
                                    public void OnBannerClick(int position) {
                                        Intent intent = new Intent(context, HxxqLastActivity.class);
                                        intent.putExtra("id", list.get(position).getJumpUrl());
                                        intent.putExtra("type", 0);
                                        context.startActivity(intent);
                                    }
                                });
                            }//失败
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //请求失败
                    @Override
                    public void onFailed(Call call, IOException e) {

                    }
                });
    }


}
