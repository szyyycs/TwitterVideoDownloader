package com.ycs.mvctest.DataBindingTest;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ycs.mvctest.databinding.ItemListBinding;

public class BookBindingViewHolder extends RecyclerView.ViewHolder {
    private ItemListBinding  t;
    public BookBindingViewHolder(@NonNull ItemListBinding t) {
        super(t.getRoot());
    }

    public ItemListBinding getBinding() {
        return t;
    }

    public void setT(ItemListBinding t) {
        this.t = t;
    }
}
