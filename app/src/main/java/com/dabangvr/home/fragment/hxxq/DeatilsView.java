package com.dabangvr.home.fragment.hxxq;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.dep.activity.DepMessActivity;
import com.dabangvr.main.MyApplication;
import com.dabangvr.util.MyWebViewClient;
import com.dabangvr.util.TextUtil;

public class DeatilsView extends LinearLayout {


    //商家头像
    private ImageView ivDepHead;

    //商家名称
    private TextView tvDepName;

    //店铺销量
   // private TextView tvDepSale;

    //商品详细的WebView
    private WebView webView;

    private String depId;


    private Context context;
    public DeatilsView(Context context) {
        this(context, null);
        this.context = context;
    }

    public DeatilsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeatilsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.hxxq_view_detalis, this, true);

        //商家头像
        ivDepHead = view.findViewById(R.id.dep_head);

        //商家名称
        tvDepName = view.findViewById(R.id.dep_name);

        //进店点击跳转
        view.findViewById(R.id.go_dep).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DepMessActivity.class);
                intent.putExtra("depId",depId);
                context.startActivity(intent);
            }
        });

        //店铺销量
        //tvDepSale = view.findViewById(R.id.dep_sale);

        //商品详细的WebView
        webView = view.findViewById(R.id.hx_bom_web_view);
    }

    public void setIvDepHead(String url) {
        Glide.with(MyApplication.getInstance()).load(url).into(ivDepHead);
    }

    public void setTvDepName(String depName) {
        tvDepName.setText(depName);
    }

//    public void setTvDepSale(String depSale){
//        tvDepSale.setText(TextUtil.isNull(depSale));
//    }

    public void setWebView(String html) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存
        webView.getSettings().setUserAgentString(System.getProperty("http.agent"));
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把html中的内容放大webview等宽的一列中
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new MyWebViewClient(webView));
        webView.loadData(html, "text/html", "UTF-8");
    }

    public void setDepId(String depId) {
        this.depId = depId;
    }
}
