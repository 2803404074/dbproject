package com.dabangvr.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.base.BaseNewActivity;
import com.dabangvr.my.activity.OrderDetailedActivity;
import com.dabangvr.util.SPUtils2;
import com.dabangvr.util.StatusBarUtil;
import com.example.mina.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * 只有支付成功才会来此页面
 */
public class OrtherOKActivity extends BaseNewActivity implements View.OnClickListener {

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvPhone)
    TextView tvPhone;

    @BindView(R.id.tv_addressStr)
    TextView tvAddressStr;

    @BindView(R.id.tv_price)
    TextView tvPrice;

    @BindView(R.id.tv_seeOrder)
    TextView tvSeeOrder;

    @BindView(R.id.tv_break)
    TextView tvBreak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_orther_ok;
    }

    @Override
    public void finish() {
        super.finish();
        SPUtils2.instance(this).remove("addName");
        SPUtils2.instance(this).remove("addPhone");
        SPUtils2.instance(this).remove("addStr");
        SPUtils2.instance(this).remove("orderPriceStr");
    }

    @Override
    public void initView() {
        tvName.setText((String) SPUtils2.instance(this).getkey("addName",""));
        tvPhone.setText((String) SPUtils2.instance(this).getkey("addPhone",""));
        tvAddressStr.setText((String) SPUtils2.instance(this).getkey("addStr",""));
        tvPrice.setText((String)SPUtils2.instance(this).getkey("orderPriceStr",""));



        tvSeeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OrderDetailedActivity.class);
                intent.putExtra("orderId",(String)SPUtils2.instance(getContext()).getkey("payOrderId",""));
                startActivity(intent);
                finish();
            }
        });

        tvBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void send(String tag) {
        JSONObject o = new JSONObject();
        try {
            o.put("sand", "sand");
            o.put("name", getSPKEY(this,"name"));
            o.put("head", getSPKEY(this,"head"));
            o.put("address", tag);
            o.put("type", "100");
            o.put("mess", "下了一单啦~~");
            SessionManager.getInstance().writeToServer(o);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }
}
