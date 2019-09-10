package com.dabangvr.model.goods;

import java.util.List;

/**
 * 商品基本信息
 */
public class BaseGoods {
    private String id;
    private String name;
    private String listUrl;
    private String sellingPrice;
    private String marketPrice;
    private String groupPrice;
    private String remainingInventory;
    private String salesVolume;
    private String goodsId;
    private String title;
    private List<ActivityMenu> activitys;

    public BaseGoods() {
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getListUrl() {
        return listUrl;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public String getMarketPrice() {
        return marketPrice;
    }

    public String getGroupPrice() {
        return groupPrice;
    }

    public String getRemainingInventory() {
        return remainingInventory;
    }

    public String getSalesVolume() {
        return salesVolume;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public List<ActivityMenu> getActivitys() {
        return activitys;
    }

    public static class ActivityMenu{
        private String name;

        public ActivityMenu() {
        }

        public String getName() {
            return name;
        }
    }
}
