package com.ycs.servicetest.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ycs.servicetest.R;
import com.ycs.servicetest.model.VideoModel;

import java.lang.ref.WeakReference;
import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<VideoModel> itemDataList;
    private final WeakReference<Context> context ;


    public ViewPagerAdapter(Context context, List<VideoModel> itemDataList) {
        this.itemDataList = itemDataList;
        this.context = new WeakReference<>(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(context.get()).inflate(R.layout.layout_viewpager2_item, parent, false);
        RecyclerView.ViewHolder holder = new RecyclerItemNormalHolder(context.get(), v);
        holder.setIsRecyclable(true);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        RecyclerItemNormalHolder recyclerItemViewHolder = (RecyclerItemNormalHolder) holder;
        recyclerItemViewHolder.setRecyclerBaseAdapter(this);
        recyclerItemViewHolder.onBind(position, itemDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public void update(List<VideoModel> data) {
        itemDataList = data;
        notifyDataSetChanged();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

    }


    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        RecyclerItemNormalHolder recyclerItemNormalHolder = (RecyclerItemNormalHolder) holder;
        if (recyclerItemNormalHolder.getPlayer() != null) {
            recyclerItemNormalHolder.getPlayer().release();
        }
    }

}
