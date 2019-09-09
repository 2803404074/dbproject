package com.dabangvr.model;

import java.util.List;

public class GoodsCategoryJson {

    /**
     * level : 1
     * children : [{"level":"1,1036095","children":[],"name":"鱿鱼干","id":1036378,"sort":1,"state":1,"label":"鱿鱼干","categoryImg":"http://image.vrzbgw.com/upload/20190730/10385916073097.jpg","value":"1036378","parentId":1036095,"categoryDesc":"1 "},{"level":"1,1036095","children":[],"name":"鳗鱼干","id":1036379,"sort":1,"state":1,"label":"鳗鱼干","categoryImg":"http://image.vrzbgw.com/upload/20190730/10395633295a5e.jpg","value":"1036379","parentId":1036095,"categoryDesc":"1"},{"level":"1,1036095","children":[],"name":"干虾皮","id":1036380,"sort":1,"state":1,"label":"干虾皮","categoryImg":"http://image.vrzbgw.com/upload/20190730/1040522752ecf3.jpg","value":"1036380","parentId":1036095,"categoryDesc":"1"},{"level":"1,1036095","children":[],"name":"咸鱼干","id":1036381,"sort":1,"state":1,"label":"咸鱼干","categoryImg":"http://image.vrzbgw.com/upload/20190730/104133721af6b7.jpg","value":"1036381","parentId":1036095,"categoryDesc":"1"},{"level":"1,1036095","children":[],"name":"沙丁鱼干","id":1036382,"sort":1,"state":1,"label":"沙丁鱼干","categoryImg":"http://image.vrzbgw.com/upload/20190730/1042128514a295.jpg","value":"1036382","parentId":1036095,"categoryDesc":"1"},{"level":"1,1036095","children":[],"name":"鲍鱼干","id":1036383,"sort":1,"state":1,"label":"鲍鱼干","categoryImg":"http://image.vrzbgw.com/upload/20190730/104242451dfc12.jpg","value":"1036383","parentId":1036095,"categoryDesc":"1"},{"level":"1,1036095","children":[],"name":"小黄鱼干","id":1036384,"sort":1,"state":1,"label":"小黄鱼干","categoryImg":"http://image.vrzbgw.com/upload/20190730/104353416d56a1.jpg","value":"1036384","parentId":1036095,"categoryDesc":"1"}]
     * name : 海鲜批发
     * id : 1036095
     * sort : 1
     * state : 1
     * label : 海鲜批发
     * categoryImg : http://image.vrzbgw.com/upload/20190808/04275484956a42.png
     * value : 1036095
     * parentId : 1
     * categoryDesc : 海鲜批发
     */

    private String level;
    private String name;
    private int id;
    private int sort;
    private int state;
    private String label;
    private String categoryImg;
    private String value;
    private int parentId;
    private String categoryDesc;
    private List<ChildrenBean> children;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCategoryImg() {
        return categoryImg;
    }

    public void setCategoryImg(String categoryImg) {
        this.categoryImg = categoryImg;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }

    public List<ChildrenBean> getChildren() {
        return children;
    }

    public void setChildren(List<ChildrenBean> children) {
        this.children = children;
    }

    public static class ChildrenBean {
        /**
         * level : 1,1036095
         * children : []
         * name : 鱿鱼干
         * id : 1036378
         * sort : 1
         * state : 1
         * label : 鱿鱼干
         * categoryImg : http://image.vrzbgw.com/upload/20190730/10385916073097.jpg
         * value : 1036378
         * parentId : 1036095
         * categoryDesc : 1
         */

        private String level;
        private String name;
        private String id;
        private int sort;
        private int state;
        private String label;
        private String categoryImg;
        private String value;
        private int parentId;
        private String categoryDesc;
        private List<?> children;

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
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

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getCategoryImg() {
            return categoryImg;
        }

        public void setCategoryImg(String categoryImg) {
            this.categoryImg = categoryImg;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getParentId() {
            return parentId;
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }

        public String getCategoryDesc() {
            return categoryDesc;
        }

        public void setCategoryDesc(String categoryDesc) {
            this.categoryDesc = categoryDesc;
        }

        public List<?> getChildren() {
            return children;
        }

        public void setChildren(List<?> children) {
            this.children = children;
        }
    }
}
