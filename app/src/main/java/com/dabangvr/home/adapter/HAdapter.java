package com.dabangvr.home.adapter;


public class HAdapter{

//    private Context context;
//    private SPUtils spUtils;
//    private boolean isLoad1 = false;
//    private boolean isLoad2 = false;
//    private boolean isLoad3 = false;
//    private boolean isLoad4 = false;
//    private boolean isLoad5 = false;
//    private boolean isLoad6 = false;
//
//    private int COMMENT_FIRST = 0;
//    private int COMMENT_SECOND = 1;
//    private int COMMENT_LAST = 2;
//    private int COMMENT_FOUR = 3;
//    private int COMMENT_FIVE = 4;
//
//    private int page = 1;
//    private List<Goods> list = new ArrayList<>();
//    private BaseLoadMoreHeaderAdapter adapter;
////    private int COMMENT_SIX = 5;
////    private int COMMENT_SEVEN = 6;
//
//    private ClickLinstener clickLinstener;
//    private LoadingDialog loadingDialog;
//
//    public HAdapter(Context context, ClickLinstener clickLinstener, LoadingDialog loadingDialog) {
//        this.context = context;
//        this.clickLinstener = clickLinstener;
//        this.loadingDialog = loadingDialog;
//        loadingDialog.show();
//        spUtils = new SPUtils(context, "db_user");
//    }
//
//    public interface ClickLinstener {
//        void clike(View view, String jumpUrl);
//    }
//
//    @Override
//    public BaseRecyclerHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        if (viewType == COMMENT_FIRST) {
//            View view = LayoutInflater.from(viewGroup.getContext())
//                    .inflate(R.layout.home_viewholde02, viewGroup, false);
//            return BaseRecyclerHolder.getRecyclerHolder(context, view);
//        } else if (viewType == COMMENT_SECOND) {
//            View view = LayoutInflater.from(viewGroup.getContext())
//                    .inflate(R.layout.home_viewholde03, viewGroup, false);
//            return BaseRecyclerHolder.getRecyclerHolder(context, view);
//        } else if (viewType == COMMENT_LAST) {
//            View view = LayoutInflater.from(viewGroup.getContext())
//                    .inflate(R.layout.home_viewholde04, viewGroup, false);
//            return BaseRecyclerHolder.getRecyclerHolder(context, view);
//        } else if (viewType == COMMENT_FOUR) {
//            View view = LayoutInflater.from(viewGroup.getContext())
//                    .inflate(R.layout.home_viewholde06, viewGroup, false);
//            return BaseRecyclerHolder.getRecyclerHolder(context, view);
//        } else if (viewType == COMMENT_FIVE) {
//            View view = LayoutInflater.from(viewGroup.getContext())
//                    .inflate(R.layout.recy_demo_load, viewGroup, false);
//            return BaseRecyclerHolder.getRecyclerHolder(context, view);
//        }
////        else if (viewType == COMMENT_FIVE) {
////            View view = LayoutInflater.from(viewGroup.getContext())
////                    .inflate(R.layout.recy_demo, viewGroup, false);
////            return BaseRecyclerHolder.getRecyclerHolder(context, view);
////        }
////        else if (viewType == COMMENT_SIX) {
////            View view = LayoutInflater.from(viewGroup.getContext())
////                    .inflate(R.layout.recy_demo, viewGroup, false);
////            return BaseRecyclerHolder.getRecyclerHolder(context, view);
////        }
////        else if(viewType == COMMENT_SEVEN){
////            View view = LayoutInflater.from(viewGroup.getContext())
////                    .inflate(R.layout.recy_demo, viewGroup, false);
////            return BaseRecyclerHolder.getRecyclerHolder(context, view);
////        }
//        return null;
//    }
//
//
//    //将数据与界面进行绑定的操作
//    @Override
//    public void onBindViewHolder(final BaseRecyclerHolder viewHolder, int position) {
//        switch (position) {
//            case 0: {//顶部主播列表
//                if (!isLoad2) {
//                    ImageView img01 = viewHolder.getView(R.id.view_holder_img_02);
//                    ImageView img02 = viewHolder.getView(R.id.view_holder_img_03);
//                    ImageView img03 = viewHolder.getView(R.id.view_holder_img_04);
//                    ImageView img04 = viewHolder.getView(R.id.view_holder_img_05);
//                    ImageView img05 = viewHolder.getView(R.id.view_holder_img_06);
//                    ImageView img06 = viewHolder.getView(R.id.view_holder_img_07);
//                    ImageView img07 = viewHolder.getView(R.id.view_holder_img_09);
//                    ImageView img08 = viewHolder.getView(R.id.view_holder_img_10);
//
//                    TextView tv01 = viewHolder.getView(R.id.view_holder_txt_02);
//                    TextView tv02 = viewHolder.getView(R.id.view_holder_txt_03);
//                    TextView tv03 = viewHolder.getView(R.id.view_holder_txt_04);
//                    TextView tv04 = viewHolder.getView(R.id.view_holder_txt_05);
//                    TextView tv05 = viewHolder.getView(R.id.view_holder_txt_06);
//                    TextView tv06 = viewHolder.getView(R.id.view_holder_txt_07);
//                    TextView tv07 = viewHolder.getView(R.id.view_holder_txt_09);
//                    TextView tv08 = viewHolder.getView(R.id.view_holder_txt_10);
//                    final ImageView arr[] = {img01, img02, img03, img04, img05, img06, img07, img08};
//                    final TextView arr2[] = {tv01, tv02, tv03, tv04, tv05, tv06, tv07, tv08};
//
//                    //判断是否有缓存
//                    String str = (String) spUtils.getkey("home_menu", "");
//                    if (!StringUtils.isEmpty(str)) {
//                        final List<MenuMo> list = JsonUtil.string2Obj(str, List.class, MenuMo.class);
//                        for (int i = 0; i < list.size(); i++) {
//                            Glide.with(context).load(list.get(i).getIconUrl()).into(arr[i]);
//                            arr2[i].setText(list.get(i).getTitle());
//                            final int finalI = i;
//                            arr[i].setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    clickLinstener.clike(v, list.get(finalI).getJumpUrl());
//                                }
//                            });
//                        }
//                        loadingDialog.dismiss();
//                    } else {
//                        Map<String, String> map = new HashMap<>();
//                        map.put("mallSpeciesId", "1");
//                        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getChannelMenuList, map,
//                                new GsonObjectCallback<String>(DyUrl.BASE) {
//                                    //主线程处理
//                                    @Override
//                                    public void onUi(String msg) {
//                                        try {
//                                            JSONObject object = new JSONObject(msg);
//                                            int code = object.optInt("errno");
//                                            if (code == 0) {//成功
//                                                JSONObject data = object.optJSONObject("data");
//                                                String array = data.optString("channelMenuList");
//                                                final List<MenuMo> list = JsonUtil.string2Obj(array, List.class, MenuMo.class);
//                                                for (int i = 0; i < list.size(); i++) {
//                                                    Glide.with(context).load(list.get(i).getIconUrl()).into(arr[i]);
//                                                    arr2[i].setText(list.get(i).getTitle());
//                                                    final int finalI = i;
//                                                    arr[i].setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View v) {
//                                                            clickLinstener.clike(v, list.get(finalI).getJumpUrl());
//                                                        }
//                                                    });
//                                                }
//                                                spUtils.put("home_menu", array);
//                                            }//失败
//                                            isLoad2 = true;
//                                            loadingDialog.dismiss();
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//
//                                    //请求失败
//                                    @Override
//                                    public void onFailed(Call call, IOException e) {
//                                        //ToastUtil.showShort(context,"请求超时");
//                                        loadingDialog.dismiss();
//                                    }
//
//                                    @Override
//                                    public void onFailure(Call call, IOException e) {
//                                        super.onFailure(call, e);
//                                        //ToastUtil.showShort(context,"请求超时,网络太差啦");
//                                        loadingDialog.dismiss();
//                                    }
//                                });
//                    }
//                }
//                break;
//            }
//            case 1: {
//                if (!isLoad3) {
//                    Banner banner = viewHolder.getView(R.id.home_banner);
//                    BannerStart bannerStart = new BannerStart(context, banner);
//                    bannerStart.starBanner("1");
//                    isLoad3 = true;
//                }
//                break;
//            }
//            case 2: {
//                if (!isLoad4) {
//                    Map<String, String> map = new HashMap<>();
//                    map.put("hoursTime", "0");
//                    map.put("page", "1");
//                    map.put("limit", "2");
//                    if(!loadingDialog.isShowing()){
//                        loadingDialog.show();
//                    }
//                    OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getSecondsKillGoodsList, map,
//                            new GsonObjectCallback<String>(DyUrl.BASE) {
//                                @Override
//                                public void onUi(String result) {
//                                    try {
//                                        JSONObject object = new JSONObject(result);
//                                        int errno = object.optInt("errno");
//                                        if (errno == 0) {
//                                            if(500 == object.optInt("code")){
//                                                loadingDialog.dismiss();
//                                                return;
//                                            }
//                                            JSONObject dataObj = object.optJSONObject("data");
//                                            String str = dataObj.optString("goodsList");
//                                            final List<Goods> list = JsonUtil.string2Obj(str, List.class, Goods.class);
//                                            if (null != list && list.size() > 0) {
//                                                //第一个商品
//                                                viewHolder.setImageByUrl(R.id.h_ms_img01, list.get(0).getListUrl());
//                                                viewHolder.setText(R.id.price01, list.get(0).getSellingPrice());
//                                                TextView buyNow = viewHolder.getView(R.id.ms_buy);
//                                                buyNow.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        Intent intent = new Intent(context, HxxqActivity.class);
//                                                        intent.putExtra("id", list.get(0).getId());
//                                                        intent.putExtra("type", 2);
//                                                        intent.putExtra("endTime", list.get(0).getSecondsEndTime());
//                                                        context.startActivity(intent);
//                                                    }
//                                                });
//
//                                                //第二个商品
//                                                if (list.size() > 1) {
//                                                    viewHolder.setImageByUrl(R.id.h_ms_img02, list.get(1).getListUrl());
//                                                    viewHolder.setText(R.id.price02, list.get(1).getSellingPrice());
//                                                    Intent intent = new Intent(context, HxxqActivity.class);
//                                                    intent.putExtra("id", list.get(1).getId());
//                                                    intent.putExtra("type", 2);
//                                                    intent.putExtra("endTime", list.get(1).getSecondsEndTime());
//                                                    context.startActivity(intent);
//                                                }else {
//                                                    LinearLayout layout = viewHolder.getView(R.id.h_ms_tag2);
//                                                    layout.setVisibility(View.GONE);
//                                                }
//
//                                                //秒杀跳转
//                                                LinearLayout layout = viewHolder.getView(R.id.ms_layout);
//                                                layout.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        Intent intent = new Intent(context, XsMsActivity.class);
//                                                        context.startActivity(intent);
//                                                    }
//                                                });
//                                                isLoad4 = true;
//                                            } else {
//                                                LinearLayout layout = viewHolder.getView(R.id.ms_view);
//                                                layout.setVisibility(View.GONE);
//                                            }
//                                        }
//                                        loadingDialog.dismiss();
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                @Override
//                                public void onFailed(Call call, IOException e) {
//
//                                }
//                            });
//
//
//                    //新品推荐//显示两个
//                    Map<String, String> map2 = new HashMap<>();
//                    map2.put("page", "1");//1036108
//                    map2.put("limit", "2");
//                    OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.NEWS, map2, new GsonObjectCallback<String>(DyUrl.BASE) {
//                        @Override
//                        public void onUi(String result) {
//                            if (StringUtils.isEmpty(result)) {
//                                return;
//                            }
//                            try {
//                                JSONObject object = new JSONObject(result);
//                                if (0 == object.optInt("errno")) {
//                                    if (500 == object.optInt("code")) {
//                                        return;
//                                    }
//                                    JSONObject dataObj = object.optJSONObject("data");
//                                    String data = dataObj.optString("GoodsCategoryList");//获取秒杀的商家数组
//                                    List<GoodsCategoryVo> list = JsonUtil.string2Obj(data, List.class, GoodsCategoryVo.class);
//
//
//                                    if (list != null && list.size() > 0) {
//                                        //获取第一个商家的第一个商品
//                                        final GoodsVo goods01 = list.get(0).getGoodsVoList().get(0);
//                                        viewHolder.setImageByUrl(R.id.new_01, goods01.getListUrl());
//                                        viewHolder.getView(R.id.new_01).setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                Intent intent = new Intent(context, HxxqActivity.class);
//                                                intent.putExtra("id", goods01.getId());
//                                                intent.putExtra("type", 0);
//                                                context.startActivity(intent);
//                                            }
//                                        });
//                                        //获取第二个商家的第一个商品
//                                        if (list.size() > 1) {
//                                            final GoodsVo goods02 = list.get(1).getGoodsVoList().get(0);
//                                            viewHolder.setImageByUrl(R.id.new_02, goods02.getListUrl());
//                                            viewHolder.getView(R.id.new_02).setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    Intent intent = new Intent(context, HxxqActivity.class);
//                                                    intent.putExtra("id", goods02.getId());
//                                                    intent.putExtra("type", 0);
//                                                    context.startActivity(intent);
//                                                }
//                                            });
//                                        }
//
//                                        LinearLayout layout = viewHolder.getView(R.id.h_new_tag);
//                                        layout.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                Intent intent = new Intent(context,NewReleaseActivity.class);
//                                                context.startActivity(intent);
//                                            }
//                                        });
//                                    } else {
//                                        LinearLayout layout = viewHolder.getView(R.id.new_view);
//                                        layout.setVisibility(View.GONE);
//                                    }
//
//                                }
//                                loadingDialog.dismiss();
//                            } catch (JSONException e) {
//
//                            }
//                        }
//
//                        @Override
//                        public void onFailed(Call call, IOException e) {
//                            call.cancel();
//                        }
//                    });
//                }
//
//                break;
//            }
//            case 3: {//
//                if (!isLoad6) {
//                    RecyclerView recy01 = viewHolder.getView(R.id.recy_01);//横向滑动
//
//                    final RecyclerView recy02 = viewHolder.getView(R.id.recy_02);//纵向滑动
//                    LinearLayoutManager layoutmanager1 = new LinearLayoutManager(context);
//                    layoutmanager1.setOrientation(LinearLayoutManager.HORIZONTAL);
//                    recy01.setLayoutManager(layoutmanager1);
//                    LinearLayoutManager layoutmanager2 = new LinearLayoutManager(context);
//                    layoutmanager2.setOrientation(LinearLayoutManager.VERTICAL);
//                    recy02.setLayoutManager(layoutmanager2);
//
//                    final ImageView imageView = viewHolder.getView(R.id.h_recycler_view_img);
//                    //横向适配-------暂无数据
//                    BaseRecyclerAdapter adapter01 = new BaseRecyclerAdapter<Goods>(context, null, 0) {
//                        @Override
//                        public void convert(BaseRecyclerHolder holder, Goods item, int position, boolean isScrolling) {
//
//                        }
//                    };
//
//                    //纵向适配
//                    Map<String, String> map = new HashMap<>();
//                    map.put("page", "1");
//                    map.put("limit", "5");
//                    map.put("categoryId", "-1");//默认中国
//                    OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGlobalList, map, new GsonObjectCallback<String>(DyUrl.BASE) {
//                        @Override
//                        public void onUi(String result) {
//                            try {
//                                JSONObject object = new JSONObject(result);
//                                if (500 == object.optInt("code")) {
//                                    return;
//                                }
//                                if (0 == object.optInt("errno")) {
//                                    JSONObject data = object.optJSONObject("data");
//                                    String allGoods = data.optString("goodsLists");
//                                    final List<Goods> allGoodsList = JsonUtil.string2Obj(allGoods, List.class, Goods.class);
//                                    if (allGoodsList != null && allGoodsList.size() > 0) {
//                                        Glide.with(context).load(allGoodsList.get(0).getListUrl()).into(imageView);
//                                        imageView.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                Intent intent = new Intent(context, HxxqActivity.class);
//                                                intent.putExtra("id", allGoodsList.get(0).getId());
//                                                intent.putExtra("type", "0");
//                                                context.startActivity(intent);
//                                            }
//                                        });
//                                        BaseRecyclerAdapter adapter02 = new BaseRecyclerAdapter<Goods>(context, allGoodsList, R.layout.sc_recy_item) {
//                                            @Override
//                                            public void convert(BaseRecyclerHolder holder, Goods item, int position, boolean isScrolling) {
//                                                holder.setImageByUrl(R.id.sc_img, item.getListUrl());
//                                                holder.setText(R.id.sc_title, item.getName());
//                                                holder.setText(R.id.sc_markp, item.getMarketPrice());
//                                                holder.setText(R.id.sc_price, item.getSellingPrice());
//                                                isLoad6 = true;
//                                            }
//                                        };
//                                        recy02.setAdapter(adapter02);
//                                        adapter02.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
//                                            @Override
//                                            public void onItemClick(RecyclerView parent, View view, int position) {
//                                                Intent intent = new Intent(context, HxxqActivity.class);
//                                                intent.putExtra("id", allGoodsList.get(position).getId());
//                                                intent.putExtra("type", "0");
//                                                context.startActivity(intent);
//                                            }
//                                        });
//                                    } else {
//                                        LinearLayout layout = viewHolder.getView(R.id.qqg_view);
//                                        layout.setVisibility(View.GONE);
//                                    }
//                                }
//                                loadingDialog.dismiss();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailed(Call call, IOException e) {
//
//                        }
//                    });
//                }
//                break;
//            }
//
//            case 4: {//所有商品列表
//                if (!isLoad5) {
//                    RecyclerView recy = viewHolder.getView(R.id.recycler_view);
//                    GridLayoutManager manager = new GridLayoutManager(context, 2);
//                    recy.setLayoutManager(manager);
//                    adapter = new BaseLoadMoreHeaderAdapter<Goods>(context, recy, list, R.layout.new_release_item) {
//                        @Override
//                        public void convert(Context mContext, BaseRecyclerHolder holder, final Goods goods) {
//                            holder.setImageByUrl(R.id.new_item_img, goods.getListUrl());
//                            holder.setText(R.id.new_item_msg, goods.getName());
//                            holder.setText(R.id.new_item_salse, goods.getSellingPrice());
//                        }
//
//                    };
//                    recy.setAdapter(adapter);
//                    adapter.setOnItemClickListener(new BaseLoadMoreHeaderAdapter.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(View view, int position) {
//                            Intent intent = new Intent(context, HxxqActivity.class);
//                            intent.putExtra("id", list.get(position).getId());
//                            intent.putExtra("type", 0);
//                            context.startActivity(intent);
//                        }
//                    });
//                    setData(true);
//
//
//                    //加载更多
//                    recy.addOnScrollListener(new onLoadMoreListener() {
//                        @Override
//                        protected void onLoading(int countItem, int lastItem) {
//                            page += 1;
//                            setData(false);
//                        }
//                    });
//
//                isLoad5 = true;
//                    loadingDialog.dismiss();
//            }
//            break;
//        }
//
//        default:
//        break;
//    }
//
//}
//
//    /**
//     * 根据position的不同选择不同的布局类型
//     */
//    @Override
//    public int getItemViewType(int position) {
//        if (position == 0) return COMMENT_FIRST;
//        if (position == 1) return COMMENT_SECOND;
//        if (position == 2) return COMMENT_LAST;
//        if (position == 3) return COMMENT_FOUR;
//        if (position == 4) return COMMENT_FIVE;
//        return position;
//    }
//
//    //获取数据的数量
//    @Override
//    public int getItemCount() {
//        return 5;
//    }
//
//    private void setData(final boolean isFlush){
//        Map<String, String> map = new HashMap<>();
//        map.put("page", String.valueOf(page));
//        map.put("limit", "10");
//        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.getGoodsLists, map, new GsonObjectCallback<String>(DyUrl.BASE) {
//            @Override
//            public void onUi(String result) {
//                if(StringUtils.isEmpty(result)){return;}
//                try {
//                    JSONObject object = new JSONObject(result);
//                    if(0 == object.optInt("errno")){
//                        JSONObject dataObj = object.optJSONObject("data");
//                        String goodsStr = dataObj.optString("goodsList");
//                        list = JsonUtil.string2Obj(goodsStr,List.class,Goods.class);
//                        if(isFlush){
//                            adapter.updateData(list);
//                        }else {
//                            adapter.addAll(list);
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailed(Call call, IOException e) {
//
//            }
//        });
//    }

}