package com.dabangvr.common.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.model.Goods;
import com.dabangvr.model.ZBGoods;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.CheckViewHolder>{
    private Context mContext;
    private List<ZBGoods> lists;
    private HashMap<Integer,Boolean> Maps=new HashMap<Integer,Boolean>();
    private HashMap<Integer,Boolean>AllMaps=new HashMap<Integer,Boolean>();
    private HashMap<Integer,Boolean> Maps2=new HashMap<Integer,Boolean>();
    public RecyclerViewOnItemClickListener onItemClickListener;

    //成员方法，初始化checkBox的状态，默认全部不选中
    public CheckAdapter(Context context, List<ZBGoods> lists){
        this.mContext=context;
        this.lists=lists;
        initMap();
    }

    //初始化map内的数据状态，全部重置为false，即为选取状态
    private void initMap() {
        for (int i = 0; i < lists.size(); i++) {
            Maps.put(i, false);
        }
    }

    public void updateData(List<ZBGoods> data) {
        lists.clear();
        lists.addAll(data);
        notifyDataSetChanged();
    }

    public List<ZBGoods> getList(){
        return lists;
    }

    public void addAll(List<ZBGoods> data) {
        lists.addAll(data);
        notifyDataSetChanged();
    }

    //获取最终的map存储数据
    public Map<Integer,Boolean> getMap(){
        return Maps;
    }

    //获取最终的map存储数据
    public Map<Integer,Boolean> getMap2(){
        return Maps2;
    }

    //后续扩展 - 获取最终的map存储数据
    public Map<Integer,Boolean>getAllMap(){
        return AllMaps;
    }

    //点击item选中CheckBox
    public void setSelectItem(int position) {
        //对当前状态取反
        if (Maps.get(position)) {
            Maps.put(position, false);
            Maps2.put(position, false);
        } else {
            Maps.put(position, true);
            Maps2.put(position, false);
        }
        notifyItemChanged(position);
    }
    @Override
    public CheckAdapter.CheckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CheckViewHolder checkViewHolder = new CheckViewHolder(LayoutInflater.from(mContext).inflate(R.layout.play_lv_item, parent,false),onItemClickListener);
        return checkViewHolder;
    }

    @Override
    public void onBindViewHolder(CheckAdapter.CheckViewHolder holder, final int position) {
        String name = lists.get(position).getName();
        holder.mName.setText(name);
        if(!StringUtils.isEmpty(lists.get(position).getListUrl())){
            Glide.with(mContext).load(lists.get(position).getListUrl()).into(holder.img);
        }
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Maps2.put(position,isChecked);
                }
                Maps.put(position,isChecked);
            }
        });

        if(Maps.get(position)==null){
            Maps.put(position,false);
        }
        //没有设置tag之前会有item重复选框出现，设置tag之后，此问题解决
        holder.mCheckBox.setChecked(Maps.get(position));


        //之后扩展使用
        AllMaps.put(position,true);
    }


    @Override
    public int getItemCount() {
        return lists ==null?0:lists.size();
    }

    public void setItemClickListener(RecyclerViewOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class CheckViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RecyclerViewOnItemClickListener mListener;
        private TextView mName;
        private CheckBox mCheckBox;
        private ImageView img;
        public CheckViewHolder(View itemView, RecyclerViewOnItemClickListener onItemClickListener) {
            super(itemView);
            this.mListener = onItemClickListener;
            itemView.setOnClickListener(this);
            mName = (TextView) itemView.findViewById(R.id.item_name);
            img = itemView.findViewById(R.id.play_item_img);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.play_cb);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClickListener(v, getPosition());
            }
        }
    }
    //接口回调设置点击事件
    public interface RecyclerViewOnItemClickListener {
        //点击事件
        void onItemClickListener(View view, int position);
    }
}


