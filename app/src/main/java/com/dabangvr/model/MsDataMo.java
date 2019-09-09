package com.dabangvr.model;

import java.util.ArrayList;
import java.util.List;

public class MsDataMo {
    private String id;
    private String name;

    public MsDataMo(String id, String name) {
        this.id = id;
        this.name = name;
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

    public static List<MsDataMo>getMs(){
        List<MsDataMo>list = new ArrayList<>();
        for (int i=0;i<24;i++){
            if(i<10){
                list.add(new MsDataMo(""+i,"0"+i+":00"));
            }else {
                list.add(new MsDataMo(""+i,i+":00"));
            }
        }
        return list;
    }
}
