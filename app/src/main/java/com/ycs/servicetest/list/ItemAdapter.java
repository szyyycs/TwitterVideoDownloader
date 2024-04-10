package com.ycs.servicetest.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ycs.servicetest.R;

import java.util.ArrayList;
import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Items> itemslist;
    private OnItemLongClickListener itemLongClickListener;
    private OnItemClickListener mListener;

    public ItemAdapter(ArrayList<Items> itemsList) {
        this.itemslist = itemsList;
    }

    private Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item, parent, false);
        ViewHolder holder = new ViewHolder(view, mListener, itemLongClickListener);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        final Items items = itemslist.get(position);
        holder.items_tv.setText(items.getText());
        if (context != null) {
            Glide.with(context)
                    .setDefaultRequestOptions(
                            new RequestOptions()
                                    .frame(0)
                                    .centerCrop()
                                    .error(R.mipmap.blank)
                    )
                    .load(items.getUrl())
                    .into(holder.items_img);
        }
        holder.size_tv.setText(items.getSize());
        holder.time_tv.setText(items.getTime());

        holder.text_tv.setText(items.getTwitterText());
        if (holder.text_tv.getLineCount() > 3) {//判断行数大于多少时改变
            int lineEndIndex = holder.text_tv.getLayout().getLineEnd(2); //设置第4行打省略号
            String text = holder.text_tv.getText().subSequence(0, lineEndIndex - 2) + "...";
            holder.text_tv.setText(text);
        }

        holder.videolen_tv.setText(items.getVideo_len());
    }

    @Override
    public int getItemCount() {
        return itemslist.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView items_tv;
        ImageView items_img;
        TextView time_tv;
        TextView size_tv;
        TextView videolen_tv;
        TextView text_tv;
        private final OnItemClickListener mListener;
        private final OnItemLongClickListener itemLongClickListener;

        public ViewHolder(View view, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
            super(view);
            mListener = listener;
            itemLongClickListener = longClickListener;
            //给item设置点击事件
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            text_tv = view.findViewById(R.id.text);
            items_tv = view.findViewById(R.id.item_tv);
            items_img = view.findViewById(R.id.item_icon);
            time_tv = view.findViewById(R.id.time);
            videolen_tv = view.findViewById(R.id.video_len);
            size_tv = view.findViewById(R.id.size);

        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(v, getPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            itemLongClickListener.onItemLongClick(v, getPosition());
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
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    public void update(List<Items> il) {
        this.itemslist = il;
        //notifyItemInserted(0);
        notifyDataSetChanged();
    }

    public void updateOne(List<Items> il) {
        this.itemslist = il;
        notifyItemInserted(il.size() - 1);
        notifyDataSetChanged();
    }

    public void updateOnepic(int position) {
        notifyItemChanged(position);
    }

    public void clearAll() {
        itemslist.clear();
        //添加动画
        //notifyItemInserted(0);
        notifyDataSetChanged();
    }

    public Items getItem(int position) {
        return itemslist.get(position);

    }
}
