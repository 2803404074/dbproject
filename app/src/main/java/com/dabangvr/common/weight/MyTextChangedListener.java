package com.dabangvr.common.weight;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.Map;

/**
 * 获取列表里面的editText的值
 */
public class MyTextChangedListener implements TextWatcher {

    public BaseRecyclerHolder holder;
    public Map<String, String> contents;
    private String depId;

    public MyTextChangedListener(BaseRecyclerHolder holder,String depId,Map<String, String> contents){
        this.holder = holder;
        this.contents = contents;
        this.depId = depId;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(holder != null && contents != null){
            int adapterPosition = holder.getAdapterPosition();
            //ToastUtil.showShort(OrderActivity.this,"牛逼:"+adapterPosition);
            contents.put(depId,editable.toString());
        }
    }
}