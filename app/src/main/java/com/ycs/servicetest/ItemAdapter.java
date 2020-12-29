package com.ycs.servicetest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Items> itemslist;
    private OnItemLongClickListener itemLongClickListener;
    private OnItemClickListener mListener;
    public ItemAdapter(ArrayList<Items> itemsList) {
        this.itemslist=itemsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item, parent, false);
        ViewHolder holder = new ViewHolder(view,mListener,itemLongClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        final Items items = itemslist.get(position);
        holder.items_tv.setText(items.getText());
        holder.items_img.setImageBitmap(items.getSrc());
        holder.size_tv.setText(items.getSize());
        holder.time_tv.setText(items.getTime());
        holder.videolen_tv.setText(items.getVideo_len());
    }

    @Override
    public int getItemCount() {
        return itemslist.size();
    }



    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        TextView items_tv;
        ImageView items_img;
        TextView time_tv;
        TextView size_tv;
        TextView videolen_tv;
        private OnItemClickListener mListener;
        private OnItemLongClickListener itemLongClickListener;
        public ViewHolder(View view, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
            super(view);
            mListener=listener;
            itemLongClickListener=longClickListener;
            //给item设置点击事件
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            items_tv = (TextView) view.findViewById(R.id.item_tv);
            items_img=(ImageView) view.findViewById(R.id.item_icon);
            time_tv=view.findViewById(R.id.time);
            videolen_tv=view.findViewById(R.id.video_len);
            size_tv=view.findViewById(R.id.size);

        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(v,getPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            itemLongClickListener.onItemLongClick(v,getPosition());
            return false;
        }
    }
    public interface OnItemClickListener {
         void onItemClick(View view, int postion);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int postion);
    }
    //自定义的方法
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener=listener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.itemLongClickListener=listener;
    }
    public void update(List<Items> il){
        this.itemslist=il;
        //notifyItemInserted(0);
        notifyDataSetChanged();
    }
    public  void clearAll(){
        itemslist.clear();
        //添加动画
        //notifyItemInserted(0);
        notifyDataSetChanged();
    }
    public Items getItem(int position){
        return itemslist.get(position);

    };
}
