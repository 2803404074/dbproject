package com.example.widget;

/**
 * 选择成功回调
 */
public interface OnSelectedListener {
    //void onSelected(String title, String smallTitle, int id);

    /**
     *
     * @param tagId 所属的父及id
     * @param id 规格id,335
     */
    void onSelected(int tagId, String id);
}
