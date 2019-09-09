package com.dabangvr.home.activity.two;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author parting_soul
 * @date 2019/1/11
 */
public class RightBean implements MultiItemEntity {
    private String name;
    private String id;
    private String imgRes;
    private int type;
    private String groupName;
    private String jumpUrl;


    public static final int TYPE_ITEM = 0;
    public static final int TYPE_TITLE = 1;

    public RightBean(String name, String id, String categoryImg,String  jumpUrl, String groupName) {
        this.name = name;
        this.id = id;
        this.imgRes = categoryImg;
        this.type = TYPE_ITEM;
        this.groupName = groupName;
        this.jumpUrl=jumpUrl;
    }

    public RightBean(String name) {
        this.name = name;
        this.groupName = name;
        this.type = TYPE_TITLE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgRes() {
        return imgRes;
    }

    public void setImgRes(String imgRes) {
        this.imgRes = imgRes;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public int getItemType() {
        return type;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }
}
