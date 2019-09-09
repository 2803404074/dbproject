package com.dabangvr.common.weight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import com.dabangvr.R;
import com.dabangvr.model.ChildrenCity;

import java.util.List;

public class ShowButtonLayoutData<T> {
    private Context context;
    private ShowButtonLayout layout;
    private List<T> data;
    private MyClickListener mListener;

    public ShowButtonLayoutData(Context context, ShowButtonLayout layout, List<T> data, MyClickListener mListener) {
        this.context = context;
        this.layout = layout;
        this.data = data;
        this.mListener = mListener;
    }

    //自定义接口，用于回调按钮点击事件到Activity
    public interface MyClickListener{
        public void clickListener(View v,double lot,double lat, boolean isCheck);
    }



    public void setData() {
        CheckBox views[] = new CheckBox[data.size()];
        //热门数据源
        for (int i = 0; i < data.size(); i++) {
            final CheckBox view = (CheckBox) LayoutInflater.from(context).inflate(R.layout.hot_search_tv, layout, false);
            if (data.get(i) instanceof String){
                view.setText((String)data.get(i));
                view.setTag(data.get(i));
            }
            if(data.get(i) instanceof ChildrenCity){
                view.setText((String)((ChildrenCity) data.get(i)).getName());
                view.setTag((String)((ChildrenCity) data.get(i)).getName());
            }


            views[i] = view;
            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //String tag = (String) v.getTag();

                    if(data.get(finalI) instanceof String){
                        mListener.clickListener(v,0,0,view.isChecked());
                    }
                    if(data.get(finalI) instanceof ChildrenCity){
                        ChildrenCity city = (ChildrenCity) data.get(finalI);
                        double lot = city.getLog();
                        double lat = city.getLat();
                        mListener.clickListener(v,lot,lat,view.isChecked());
                        view.setText((String)((ChildrenCity) data.get(finalI)).getName());
                    }
                    if(view.isChecked()){
                        view.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    }else {
                        view.setTextColor(context.getResources().getColor(R.color.black));
                    }
                    //getHttp(tag);
                }
            });
            layout.addView(view);
        }
    }


    public void setData2() {
        CheckBox views[] = new CheckBox[data.size()];
        //热门数据源
        for (int i = 0; i < data.size(); i++) {
            final CheckBox view = (CheckBox) LayoutInflater.from(context).inflate(R.layout.hot_search_tv, layout, false);
            if (data.get(i) instanceof String){
                view.setText((String)data.get(i));
                view.setTag(i);
            }
            if(data.get(i) instanceof ChildrenCity){
                view.setText((String)((ChildrenCity) data.get(i)).getName());
                view.setTag(i);
            }


            views[i] = view;
            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //String tag = (String) v.getTag();

                    if(data.get(finalI) instanceof String){
                        mListener.clickListener(v,0,0,view.isChecked());
                    }
                    if(data.get(finalI) instanceof ChildrenCity){
                        ChildrenCity city = (ChildrenCity) data.get(finalI);
                        double lot = city.getLog();
                        double lat = city.getLat();
                        mListener.clickListener(v,lot,lat,view.isChecked());
                        view.setText((String)((ChildrenCity) data.get(finalI)).getName());
                    }
                    if(view.isChecked()){
                        view.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    }else {
                        view.setTextColor(context.getResources().getColor(R.color.black));
                    }
                    //getHttp(tag);
                }
            });
            layout.addView(view);
        }
    }
}
