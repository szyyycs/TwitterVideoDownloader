package com.ycs.smartcanteen.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ycs.smartcanteen.R;
import com.ycs.smartcanteen.ui.Bean;
import com.zhxh.ximageviewlib.ShapeImageView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CookAdapter extends RecyclerView.Adapter<CookAdapter.ViewHolder> {
    private List<Bean.DishItem> itemslist;
    private OnItemLongClickListener itemLongClickListener;
    private OnItemClickListener mListener;
    public CookAdapter(ArrayList<Bean.DishItem> itemsList) {
        this.itemslist=itemsList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        ViewHolder holder = new ViewHolder(view,mListener,itemLongClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bean.DishItem item=itemslist.get(position);
        holder.tv_name.setText(item.getName());
        holder.tv_type.setText(item.getType());
        holder.iv_icon.setImageResource(item.getSrc());

    }

    @Override
    public int getItemCount() {
        return itemslist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        private OnItemClickListener mListener;
        private OnItemLongClickListener itemLongClickListener;
        private TextView tv_name;
        private TextView tv_type;
        private ShapeImageView iv_icon;
        public ViewHolder(View view, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
            super(view);
            mListener=listener;
            itemLongClickListener=longClickListener;
            //给item设置点击事件
            itemView.setOnClickListener(this);
            view.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
           // Log.d("yyy", "oncreate");
            tv_name=view.findViewById(R.id.name);
            tv_type = view.findViewById(R.id.type);
            iv_icon=view.findViewById(R.id.icon);
        }


        @Override
        public void onClick(View view) {
            Log.d("yyy", "onClick: ");
            if(mListener!=null){
                mListener.onItemClick(view,getPosition());
            }

        }

        @Override
        public boolean onLongClick(View view) {
            Log.d("yyy", "longonClick: ");
            if(itemLongClickListener!=null){
                itemLongClickListener.onItemLongClick(view,getPosition());
            }

            return false;
        }
    }
        public interface OnItemClickListener {
            void onItemClick(View view, int postion);
        }
        public interface OnItemLongClickListener {
            void onItemLongClick(View view, int postion);
        }
        public void setOnItemClickListener(OnItemClickListener listener){
            this.mListener=listener;
        }
        public void setOnItemLongClickListener(OnItemLongClickListener listener){
            this.itemLongClickListener=listener;
        }
        public void update(List<Bean.DishItem> il){
            this.itemslist=il;
            notifyDataSetChanged();
   }
}
