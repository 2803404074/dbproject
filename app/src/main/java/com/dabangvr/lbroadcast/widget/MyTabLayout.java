package com.dabangvr.lbroadcast.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.dabangvr.R;

import java.util.ArrayList;
import java.util.List;

public class MyTabLayout extends TabLayout {
    private List<String> titles;

    public MyTabLayout(Context context) {
        super(context);
        init();
    }

    public MyTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        titles = new ArrayList<>();

        this.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(Tab tab) {
                /**
                 * 设置当前选中的Tab为特殊高亮样式。
                 */
                if (tab != null && tab.getCustomView() != null) {
                    TextView tab_text = tab.getCustomView().findViewById(R.id.tab_text);

                    TextPaint paint = tab_text.getPaint();
                    paint.setFakeBoldText(true);

                    tab_text.setTextColor(Color.WHITE);
                    tab_text.setBackgroundResource(R.drawable.tablayout_item_pressed);

                    ImageView tab_layout_indicator = tab.getCustomView().findViewById(R.id.tab_indicator);
                    tab_layout_indicator.setBackgroundResource(R.drawable.tablayout_item_indicator);
                }
            }

            @Override
            public void onTabUnselected(Tab tab) {
                /**
                 * 重置所有未选中的Tab颜色、字体、背景恢复常态(未选中状态)。
                 */
                if (tab != null && tab.getCustomView() != null) {
                    TextView tab_text = tab.getCustomView().findViewById(R.id.tab_text);

                    tab_text.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    TextPaint paint = tab_text.getPaint();
                    paint.setFakeBoldText(false);
                    tab_text.setBackgroundResource(R.drawable.tablayout_item_normal);

                    ImageView tab_indicator = tab.getCustomView().findViewById(R.id.tab_indicator);
                    tab_indicator.setBackgroundResource(0);
                }
            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });
    }

    public void setTitle(List<String> titles) {
        this.titles = titles;

        /**
         * 开始添加切换的Tab。
         */
        for (String title : this.titles) {
            Tab tab = newTab();
            tab.setCustomView(R.layout.tablayout_item);

            if (tab.getCustomView() != null) {
                TextView tab_text = tab.getCustomView().findViewById(R.id.tab_text);
                tab_text.setText(title);
            }

            this.addTab(tab);
        }
    }
}
