package com.dabangvr.model;

import java.util.List;

/**
 * 购物车数据的bean类
 */

public class ShoppingCarDataBean {

    private int errno;
    private NewBean data;
//    private int errno;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public NewBean getData() {
        return data;
    }

    public void setData(NewBean data) {
        this.data = data;
    }

    public static class NewBean{
        private List<DatasBean> goods2CartList;

        public NewBean() {
        }

        public List<DatasBean> getGoods2CartList() {
            return goods2CartList;
        }

        public void setGoods2CartList(List<DatasBean> goods2CartList) {
            this.goods2CartList = goods2CartList;
        }
    }


    public static class DatasBean {

        private String deptId;
        private String deptName;
        private boolean isSelect_shop;      //店铺是否在购物车中被选中
        private List<GoodsBean> gcvList;

        public boolean getIsSelect_shop() {
            return isSelect_shop;
        }

        public void setIsSelect_shop(boolean select_shop) {
            isSelect_shop = select_shop;
        }

        public String getDeptId() {
            return deptId;
        }

        public void setDeptId(String deptId) {
            this.deptId = deptId;
        }

        public String getDeptName() {
            return deptName;
        }

        public void setDeptName(String deptName) {
            this.deptName = deptName;
        }

        public List<GoodsBean> getGcvList() {
            return gcvList;
        }

        public void setGcvList(List<GoodsBean> gcvList) {
            this.gcvList = gcvList;
        }

        public static class GoodsBean {

            private int id;
            private int goodsId;
            private int userId;
            private int productId;
            private int deptId;
            private String goodsName;
            private String productName;
            private String marketPrice;
            private String retailPrice;
            private int number;
            private String listUrl;

            private boolean isSelect;        //商品是否在购物车中被选中

            public boolean getIsSelect() {
                return isSelect;
            }

            public void setIsSelect(boolean isSelect) {
                this.isSelect = isSelect;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getGoodsId() {
                return goodsId;
            }

            public void setGoodsId(int goodsId) {
                this.goodsId = goodsId;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public int getProductId() {
                return productId;
            }

            public void setProductId(int productId) {
                this.productId = productId;
            }

            public int getDeptId() {
                return deptId;
            }

            public void setDeptId(int deptId) {
                this.deptId = deptId;
            }

            public String getGoodsName() {
                return goodsName;
            }

            public void setGoodsName(String goodsName) {
                this.goodsName = goodsName;
            }

            public String getProductName() {
                return productName;
            }

            public void setProductName(String productName) {
                this.productName = productName;
            }

            public String getMarketPrice() {
                return marketPrice;
            }

            public void setMarketPrice(String marketPrice) {
                this.marketPrice = marketPrice;
            }

            public String getRetailPrice() {
                return retailPrice;
            }

            public void setRetailPrice(String retailPrice) {
                this.retailPrice = retailPrice;
            }

            public int getNumber() {
                return number;
            }

            public void setNumber(int number) {
                this.number = number;
            }

            public String getListUrl() {
                return listUrl;
            }

            public void setListUrl(String listUrl) {
                this.listUrl = listUrl;
            }

            public boolean isSelect() {
                return isSelect;
            }

            public void setSelect(boolean select) {
                isSelect = select;
            }
        }
    }
}
