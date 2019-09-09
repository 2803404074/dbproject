package com.dabangvr.model;

import com.dabangvr.R;

import java.util.ArrayList;
import java.util.List;

public class QCountry {


    public List<MyContry> getContryData(){
        List<MyContry> list = new ArrayList<>();
        list.add(new MyContry("1036112","中国", R.mipmap.g_china));
        list.add(new MyContry("1036115","英国", R.mipmap.g_britain));
        list.add(new MyContry("1036113","美国", R.mipmap.g_usa));
        list.add(new MyContry("1036114","韩国", R.mipmap.g_korea));
        return list;
    }

    public class MyContry{
        private String id;
        private String name;
        private int categoryImg;

        public MyContry(String id, String name, int categoryImg) {
            this.id = id;
            this.name = name;
            this.categoryImg = categoryImg;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCategoryImg() {
            return categoryImg;
        }

        public void setCategoryImg(int categoryImg) {
            this.categoryImg = categoryImg;
        }
    }

}
