package com.example.mylibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MessDetailsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_details);
        initView();
    }

    private void initView() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String logo = getIntent().getStringExtra("logo");
        String mes = getIntent().getStringExtra("mes");
        String name = getIntent().getStringExtra("name");
        String data = getIntent().getStringExtra("data");

        TextView tvName = findViewById(R.id.name);
        ImageView ivLogo = findViewById(R.id.m_logo);
        TextView tvMes = findViewById(R.id.m_mes);
        TextView tvData = findViewById(R.id.m_data);

        Glide.with(this).load(logo).into(ivLogo);
        tvName.setText(name);
        tvData.setText(data);
        tvMes.setText(mes);

    }

}
