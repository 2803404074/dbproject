package com.dabangvr.model.order;

import java.util.List;

public class DepGoods {
    private String deptLogo;
    private String deptName;
    private String deptTotalPrice;
    private String deptLogisticsTotalPrice;
    private String deptId;
    private List<GoodsList> goodsList;
    private String deptGoodsTotalPrice;

    public DepGoods() {
    }

    public String getDeptGoodsTotalPrice() {
        return deptGoodsTotalPrice;
    }

    public void setDeptGoodsTotalPrice(String deptGoodsTotalPrice) {
        this.deptGoodsTotalPrice = deptGoodsTotalPrice;
    }

    public String getDeptLogo() {
        return deptLogo;
    }

    public void setDeptLogo(String deptLogo) {
        this.deptLogo = deptLogo;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptTotalPrice() {
        return deptTotalPrice;
    }

    public void setDeptTotalPrice(String deptTotalPrice) {
        this.deptTotalPrice = deptTotalPrice;
    }

    public String getDeptLogisticsTotalPrice() {
        return deptLogisticsTotalPrice;
    }

    public void setDeptLogisticsTotalPrice(String deptLogisticsTotalPrice) {
        this.deptLogisticsTotalPrice = deptLogisticsTotalPrice;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public List<GoodsList> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<GoodsList> goodsList) {
        this.goodsList = goodsList;
    }

    public static class GoodsList{
        private String goodsName;
        private String retailPrice;
        private String marketPrice;
        private String logisticsPrice;
        private String number;
        private String listUrl;
        private String goodsListUrl;
        private String productName;

        public GoodsList() {
        }

        public String getGoodsListUrl() {
            return goodsListUrl;
        }

        public void setGoodsListUrl(String goodsListUrl) {
            this.goodsListUrl = goodsListUrl;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public String getRetailPrice() {
            return retailPrice;
        }

        public void setRetailPrice(String retailPrice) {
            this.retailPrice = retailPrice;
        }

        public String getMarketPrice() {
            return marketPrice;
        }

        public void setMarketPrice(String marketPrice) {
            this.marketPrice = marketPrice;
        }

        public String getLogisticsPrice() {
            return logisticsPrice;
        }

        public void setLogisticsPrice(String logisticsPrice) {
            this.logisticsPrice = logisticsPrice;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getListUrl() {
            return listUrl;
        }

        public void setListUrl(String listUrl) {
            this.listUrl = listUrl;
        }
    }
}
