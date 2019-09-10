package com.dabangvr.home.activity.two;

/**
 * @author parting_soul
 * @date 2019/1/11
 */
public class LeftSortBean {
    private String typeName;
    private int id;
    private boolean isSelected;

    public LeftSortBean(String typeName,int id) {
        this.typeName = typeName;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
