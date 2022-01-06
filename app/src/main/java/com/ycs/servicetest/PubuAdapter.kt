package com.ycs.servicetest

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class PubuAdapter(dataList: MutableList<ImageModel>) : RecyclerView.Adapter<PubuAdapter.PubuHolder>() {
    private var data:MutableList<ImageModel> = dataList;
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PubuHolder {
        var view:View=LayoutInflater.from(parent.context).inflate(R.layout.pubu_item, parent, false);
        Log.d("yyy", "onBindViewHolder: 2")
        return PubuHolder(view);
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PubuHolder, position: Int) {
        Log.d("yyy", "onBindViewHolder: 1")
        var model:ImageModel=data[position]
        val screenWidth = getScreenWidth(holder.itemView.context)
        val lp = holder.showIv.layoutParams
        lp.width = screenWidth / 2
        // 固定设置每个ItemView的高度，防止滑动的复用ItemView的时候重新分配itemView的高度
        lp.height = (screenWidth * model.imageHeight).toInt()
        holder.showIv.layoutParams = lp
        holder.descTv.text=model.desc

        Glide.with(holder.itemView.context)
                .setDefaultRequestOptions(
                        RequestOptions()
                                .frame(0)
                                .centerCrop()
                                .error(R.mipmap.blank)
                )
                .load(model.url)
                .into(holder.showIv)

    }
    fun addData(newData:ImageModel) {
            data.add(newData)
            notifyItemInserted(data.size)
    }
    class PubuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showIv: ImageView = itemView.findViewById(R.id.show_iv)
        val descTv: TextView = itemView.findViewById(R.id.desc_tv)
        init {
            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "position:$layoutPosition",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var mRecyclerView: RecyclerView? = null
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.mRecyclerView = recyclerView
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            layoutManager.spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    // 指定每个item占用几个网格坑位
                    return 1
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: PubuHolder) {
        super.onViewAttachedToWindow(holder)
        val lp: ViewGroup.LayoutParams = holder.itemView.getLayoutParams()
        if (lp is StaggeredGridLayoutManager.LayoutParams) {
            // 瀑布流的布局参数
            val layoutParams = lp
            layoutParams.isFullSpan = false
            holder.itemView.layoutParams = layoutParams

        }
    }
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView!!)
    }

    override fun onViewDetachedFromWindow(holder: PubuHolder) {
        super.onViewDetachedFromWindow(holder)
    }
    fun getScreenWidth(context: Context): Int {
        val windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.getDefaultDisplay().getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        val windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}