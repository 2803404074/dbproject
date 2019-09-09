package com.dabangvr.model;

import java.io.Serializable;
import java.util.List;

/**
 * 商品类别
 */
public class GoodsCategoryVo implements Serializable {

    //主键
    private String id;
    //分类名称
    private String name;
    //描述
    private String categoryDesc;
    //父节点
    private Integer parentId;
    //排序
    private Integer sort;
    //显示
    private Integer state;
    //图片
    private String categoryImg;
    //icon链接
    private String iconUrl;
    //级别
    private String level;

    private List<GoodsVo> goodsVoList;

    private GoodsCategoryPageVo page;


    public GoodsCategoryPageVo getPage() {
        return page;
    }

    public void setPage(GoodsCategoryPageVo page) {
        this.page = page;
    }

    public List<GoodsVo> getGoodsVoList() {
        return goodsVoList;
    }

    public void setGoodsVoList(List<GoodsVo> goodsVoList) {
        this.goodsVoList = goodsVoList;
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

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getCategoryImg() {
        return categoryImg;
    }

    public void setCategoryImg(String categoryImg) {
        this.categoryImg = categoryImg;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
