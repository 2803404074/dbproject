package com.example.gangedrecyclerview;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class DepSortBean implements Parcelable {
    private String name;
    private String tag;
    private boolean isTitle;
    private String imgsrc;

    private String titleName;


    public static final Creator<DepSortBean> CREATOR = new Creator<DepSortBean>() {
        @Override
        public DepSortBean createFromParcel(Parcel in) {
            return new DepSortBean(in);
        }

        @Override
        public DepSortBean[] newArray(int size) {
            return new DepSortBean[size];
        }
    };

    public String getTitle() {
        return title;
    }
    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    private String title;



    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }


    public DepSortBean(String name) {
        this.name = name;
    }

    //商品分类
    private ArrayList<CategoryOneArrayBean> deliveryGoodsTypeVos;

    protected DepSortBean(Parcel in) {
        name = in.readString();
        tag = in.readString();
        isTitle = in.readByte() != 0;
    }


    public ArrayList<CategoryOneArrayBean> getCategoryOneArray() {
        return deliveryGoodsTypeVos;
    }

    public void setCategoryOneArray(ArrayList<CategoryOneArrayBean> deliveryGoodsTypeVos) {
        this.deliveryGoodsTypeVos = deliveryGoodsTypeVos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(tag);
        dest.writeByte((byte) (isTitle ? 1 : 0));
        dest.writeString(imgsrc);
        dest.writeString(titleName);
        dest.writeString(title);
        dest.writeTypedList(deliveryGoodsTypeVos);
    }


    /**
     * 商品分类
     */
    public static class CategoryOneArrayBean implements Parcelable {
        /**
         * categoryTwoArray : [{"name":"处方药(RX)","imgsrc":"https://121.10.217.171:9002/_ui/desktop/common/cmyy/image/app_tongsufenlei_chufangyao.png","cacode":"chufangyao"},{"name":"非处方(OTC)","imgsrc":"https://121.10.217.171:9002/_ui/desktop/common/cmyy/image/app_tongsufenlei_feichufang.png","cacode":"feichufang"},{"name":"抗生素","imgsrc":"https://121.10.217.171:9002/_ui/desktop/common/cmyy/image/app_tongsufenlei_kangshengsu.png","cacode":"kangshengsu"}]
         * name : 通俗分类
         * imgsrc : https://121.10.217.171:9002/_ui/desktop/common/cmyy/image/app_0.png
         * cacode : tongsufenlei
         */

        private String id;
        private String name;
        private List<CategoryTwoArrayBean> deliveryGoodsVoList;

        protected CategoryOneArrayBean(Parcel in) {
            id = in.readString();
            name = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<CategoryOneArrayBean> CREATOR = new Creator<CategoryOneArrayBean>() {
            @Override
            public CategoryOneArrayBean createFromParcel(Parcel in) {
                return new CategoryOneArrayBean(in);
            }

            @Override
            public CategoryOneArrayBean[] newArray(int size) {
                return new CategoryOneArrayBean[size];
            }
        };

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() { return id; }

        public void setId(String id) { this.id = id; }

        public List<CategoryTwoArrayBean> getCategoryTwoArray() {
            return deliveryGoodsVoList;
        }

        public void setCategoryTwoArray(List<CategoryTwoArrayBean> deliveryGoodsVoList) {
            this.deliveryGoodsVoList = deliveryGoodsVoList;
        }


        /**
         * 商品
         */
        public static class CategoryTwoArrayBean implements Parcelable {
            /**
             * name :
             * imgsrc : https://121.10.217.171:9002/_ui/desktop/common/cmyy/image/app_tongsufenlei_chufangyao.png
             * cacode : chufangyao
             */

            private String id;
            private String name;
            private String listUrl;
            private List<GoodsDetails.GoodsSpecVoList> specList;
            private List<GoodsDetails.ProductInfoVoList> deliveryProductInfoList;
            private String deliverPrice;//配送价
            private String packagingPrice;//打包费
            private String startingPrice;//起送价
            private String salesVolume;//销量
            private String sellingPrice;//销售价
            private String remainingInventory;//库存
            private String marketPrice;//市场价

            protected CategoryTwoArrayBean(Parcel in) {
                id = in.readString();
                name = in.readString();
                listUrl = in.readString();
                deliverPrice = in.readString();
                packagingPrice = in.readString();
                startingPrice = in.readString();
                salesVolume = in.readString();
                sellingPrice = in.readString();
                remainingInventory = in.readString();
                marketPrice = in.readString();
            }

            public static final Creator<CategoryTwoArrayBean> CREATOR = new Creator<CategoryTwoArrayBean>() {
                @Override
                public CategoryTwoArrayBean createFromParcel(Parcel in) {
                    return new CategoryTwoArrayBean(in);
                }

                @Override
                public CategoryTwoArrayBean[] newArray(int size) {
                    return new CategoryTwoArrayBean[size];
                }
            };

            public String getSellingPrice() {
                return sellingPrice;
            }

            public void setSellingPrice(String sellingPrice) {
                this.sellingPrice = sellingPrice;
            }

            public String getId() { return id; }

            public void setId(String id) { this.id = id; }

            public String getName() { return name; }

            public void setName(String name) { this.name = name; }

            public String getListUrl() { return listUrl; }

            public void setListUrl(String listUrl) { this.listUrl = listUrl; }

            public List<GoodsDetails.GoodsSpecVoList> getDeliveryGoodsSpecVoList() {
                return specList;
            }

            public void setDeliveryGoodsSpecVoList(List<GoodsDetails.GoodsSpecVoList> deliveryGoodsSpecVoList) {
                this.specList = deliveryGoodsSpecVoList;
            }

            public List<GoodsDetails.ProductInfoVoList> getDeliveryProductInfoVoList() {
                return deliveryProductInfoList;
            }

            public void setDeliveryProductInfoVoList(List<GoodsDetails.ProductInfoVoList> deliveryProductInfoVoList) {
                this.deliveryProductInfoList = deliveryProductInfoVoList;
            }

            public String getDeliverPrice() {
                return deliverPrice;
            }

            public void setDeliverPrice(String deliverPrice) {
                this.deliverPrice = deliverPrice;
            }

            public String getPackagingPrice() {
                return packagingPrice;
            }

            public void setPackagingPrice(String packagingPrice) {
                this.packagingPrice = packagingPrice;
            }

            public String getStartingPrice() {
                return startingPrice;
            }

            public void setStartingPrice(String startingPrice) {
                this.startingPrice = startingPrice;
            }

            public String getSalesVolume() {
                return salesVolume;
            }

            public void setSalesVolume(String salesVolume) {
                this.salesVolume = salesVolume;
            }

            public String getRemainingInventory() {
                return remainingInventory;
            }

            public void setRemainingInventory(String remainingInventory) {
                this.remainingInventory = remainingInventory;
            }

            public String getMarketPrice() {
                return marketPrice;
            }

            public void setMarketPrice(String marketPrice) {
                this.marketPrice = marketPrice;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(id);
                dest.writeString(name);
                dest.writeString(listUrl);
                dest.writeString(deliverPrice);
                dest.writeString(packagingPrice);
                dest.writeString(startingPrice);
                dest.writeString(salesVolume);
                dest.writeString(sellingPrice);
                dest.writeString(remainingInventory);
                dest.writeString(marketPrice);
            }
        }
    }


}
