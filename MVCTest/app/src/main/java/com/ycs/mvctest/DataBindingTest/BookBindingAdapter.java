package com.ycs.mvctest.DataBindingTest;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import com.ycs.mvctest.BookMVC.Book;
import com.ycs.mvctest.R;

import java.util.List;

public class BookBindingAdapter extends RecyclerView.Adapter<BookBindingViewHolder> {
    private List<Book> data;

    public BookBindingAdapter(List<Book> data) {
        this.data=data;
    }

    @NonNull
    @Override
    public BookBindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookBindingViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.activity_data_binding,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookBindingViewHolder holder, int position) {
        holder.getBinding().setBook(data.get(position));
        holder.getBinding().setVariable(BR.book,data.get(position));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    public void setData(List<Book> data) {
        this.data = data;
    }
}
