package com.dabangvr.model.goods;

import java.io.Serializable;
import java.util.List;

public class GoodsDetails implements Serializable {
    private String id;//id
    private String categoryId;
    private String name;//商品名称
    private String title;//商品标题
    private String describe;//商品简介
    private String listUrl;//商品图片
    private String sellingPrice;//普通商品销售价
    private String marketPrice;//市场价
    private String groupPrice;//团购价
    private String proportion;//佣金比例
    private String remainingInventory;//剩余库存
    private String salesVolume;//销量
    private String goodsDesc;//商品详细--HTML
    private String logisticsPrice;
    private List<ImgList> imgList;//商品轮播----图片或视频
    private String goodsId;//商品id
    private String collectTag;//是否已经收藏,1已经收藏 -1未藏

    private List<GoodsSpecVoList> specList;//规格信息
    private List<ProductInfoVoList> productInfoList;//产品信息

    private String deptId;//商家id
    private String deptName;//商家名称
    private String deptLogo;//商家logo
    private String depSalseNum;//商家销量
    private String depAddress;//商家所在的省份


    private String assemblePriec;//团购价
    private String remainingGroupNumber;//团购库存
    private String startTime;//团购开始时间
    private String endTime;//团购结束时间
    private String restrictionVolume;//限购数量

    private String secondsStartTime;//秒杀开始时间
    private String secondsEndTime;//秒杀结束时间
    private String secondsPrice;//秒杀价
    private String remainingSecondsNumber;//秒杀剩余库存


    public static class ImgList{
        private String chartUrl;
        private String videoUrl;

        public ImgList() {
        }

        public ImgList(String chartUrl, String videoUrl) {
            this.chartUrl = chartUrl;
            this.videoUrl = videoUrl;
        }

        public String getChartUrl() {
            return chartUrl;
        }

        public void setChartUrl(String chartUrl) {
            this.chartUrl = chartUrl;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }
    }

    public static class GoodsSpecVoList{
        private String id;//规格id
        private String name;//规格名称
        private List<GoodsSpecList>goodsSpecList;

        public GoodsSpecVoList() {
        }

        public GoodsSpecVoList(String id, String name, List<GoodsSpecList> goodsSpecList) {
            this.id = id;
            this.name = name;
            this.goodsSpecList = goodsSpecList;
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

        public List<GoodsSpecList> getGoodsSpecList() {
            return goodsSpecList;
        }

        public void setGoodsSpecList(List<GoodsSpecList> goodsSpecList) {
            this.goodsSpecList = goodsSpecList;
        }

        public static class GoodsSpecList{
            private String id;
            private String value;

            public GoodsSpecList() {
            }

            public GoodsSpecList(String id, String value) {
                this.id = id;
                this.value = value;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }

    public static class ProductInfoVoList{
        private String id;//产品id
        private String pictureUrl;//产品图片
        private String name;//产品规格
        private String number;//库存
        private String retailPrice;//普通价
        private String marketPrice;//市场价
        private String groupPrice;//拼团价
        private String goodsSpecIds;//产品552_662
        private String secondsPrice;//秒杀价

        public ProductInfoVoList() {
        }

        public String getPictureUrl() {
            return pictureUrl;
        }

        public ProductInfoVoList(String id, String name, String number, String retailPrice, String marketPrice, String groupPrice, String goodsSpecIds, String secondsPrice) {
            this.id = id;
            this.name = name;
            this.number = number;
            this.retailPrice = retailPrice;
            this.marketPrice = marketPrice;
            this.groupPrice = groupPrice;
            this.goodsSpecIds = goodsSpecIds;
            this.secondsPrice = secondsPrice;
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

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
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

        public String getGroupPrice() {
            return groupPrice;
        }

        public void setGroupPrice(String groupPrice) {
            this.groupPrice = groupPrice;
        }

        public String getGoodsSpecIds() {
            return goodsSpecIds;
        }

        public void setGoodsSpecIds(String goodsSpecIds) {
            this.goodsSpecIds = goodsSpecIds;
        }

        public String getSecondsPrice() {
            return secondsPrice;
        }

        public void setSecondsPrice(String secondsPrice) {
            this.secondsPrice = secondsPrice;
        }
    }



    public GoodsDetails() {
    }

    public String getDepSalseNum() {
        return depSalseNum;
    }

    public void setDepSalseNum(String depSalseNum) {
        this.depSalseNum = depSalseNum;
    }

    public List<ImgList> getImgList() {
        return imgList;
    }

    public void setImgList(List<ImgList> imgList) {
        this.imgList = imgList;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getListUrl() {
        return listUrl;
    }

    public void setListUrl(String listUrl) {
        this.listUrl = listUrl;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getGroupPrice() {
        return groupPrice;
    }

    public void setGroupPrice(String groupPrice) {
        this.groupPrice = groupPrice;
    }

    public String getProportion() {
        return proportion;
    }

    public void setProportion(String proportion) {
        this.proportion = proportion;
    }

    public String getRemainingInventory() {
        return remainingInventory;
    }

    public void setRemainingInventory(String remainingInventory) {
        this.remainingInventory = remainingInventory;
    }

    public String getSalesVolume() {
        return salesVolume;
    }

    public void setSalesVolume(String salesVolume) {
        this.salesVolume = salesVolume;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

//    public String getImgList() {
//        return imgList;
//    }
//
//    public void setImgList(String imgList) {
//        this.imgList = imgList;
//    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public List<GoodsSpecVoList> getSpecList() {
        return specList;
    }

    public void setSpecList(List<GoodsSpecVoList> specList) {
        this.specList = specList;
    }

    public List<ProductInfoVoList> getProductInfoList() {
        return productInfoList;
    }

    public void setProductInfoList(List<ProductInfoVoList> productInfoList) {
        this.productInfoList = productInfoList;
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

    public String getDeptLogo() {
        return deptLogo;
    }

    public void setDeptLogo(String deptLogo) {
        this.deptLogo = deptLogo;
    }

    public String getAssemblePriec() {
        return assemblePriec;
    }

    public void setAssemblePriec(String assemblePriec) {
        this.assemblePriec = assemblePriec;
    }

    public String getRemainingGroupNumber() {
        return remainingGroupNumber;
    }

    public void setRemainingGroupNumber(String remainingGroupNumber) {
        this.remainingGroupNumber = remainingGroupNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRestrictionVolume() {
        return restrictionVolume;
    }

    public void setRestrictionVolume(String restrictionVolume) {
        this.restrictionVolume = restrictionVolume;
    }

    public String getSecondsStartTime() {
        return secondsStartTime;
    }

    public void setSecondsStartTime(String secondsStartTime) {
        this.secondsStartTime = secondsStartTime;
    }

    public String getSecondsEndTime() {
        return secondsEndTime;
    }

    public void setSecondsEndTime(String secondsEndTime) {
        this.secondsEndTime = secondsEndTime;
    }

    public String getSecondsPrice() {
        return secondsPrice;
    }

    public void setSecondsPrice(String secondsPrice) {
        this.secondsPrice = secondsPrice;
    }

    public String getRemainingSecondsNumber() {
        return remainingSecondsNumber;
    }

    public void setRemainingSecondsNumber(String remainingSecondsNumber) {
        this.remainingSecondsNumber = remainingSecondsNumber;
    }

    public String getLogisticsPrice() {
        return logisticsPrice;
    }

    public void setLogisticsPrice(String logisticsPrice) {
        this.logisticsPrice = logisticsPrice;
    }

    public String getDepAddress() {
        return depAddress;
    }

    public void setDepAddress(String depAddress) {
        this.depAddress = depAddress;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCollectTag() {
        return collectTag;
    }

    public void setCollectTag(String collectTag) {
        this.collectTag = collectTag;
    }
}
