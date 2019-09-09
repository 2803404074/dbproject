package com.dabangvr.model;



import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class OrtherStatic {
    public static List<TabAndViewPagerMo> setData(){
        List<TabAndViewPagerMo>list = new ArrayList<>();
        for (int i=0;i<6;i++){
            TabAndViewPagerMo title = new TabAndViewPagerMo();
            switch (i){
                case 0:{
                    title.setId("");
                    title.setTitle("全部");
                    break;
                }
                case 1:{
                    title.setId("0");
                    title.setTitle("待付款");
                    break;
                }
                case 2:{
                    title.setId("201");//已发货
                    title.setTitle("待发货");
                    break;
                }
                case 3:{
                    title.setId("300");
                    title.setTitle("待收货");
                    break;
                }
                case 4:{
                    title.setId("301");
                    title.setTitle("待评价");
                    break;
                }
            }
            list.add(title);
        }
        return list;
    }
}
