package com.ycs.servicetest.list

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ycs.servicetest.R
import com.ycs.servicetest.model.ImageModel


class PubuAdapter(dataList: MutableList<ImageModel>) :
    RecyclerView.Adapter<PubuAdapter.PubuHolder>() {
    private var data: MutableList<ImageModel> = dataList
    private lateinit var mListener: OnItemClickListener
    private lateinit var mLongListener: OnLongItemClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PubuHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.pubu_item, parent, false)

        return PubuHolder(view, mListener, mLongListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PubuHolder, position: Int) {
        val model: ImageModel = data[position]
        val screenWidth = getScreenWidth(holder.itemView.context)
        val lp = holder.showIv.layoutParams
        lp.width = screenWidth / 2
        // 固定设置每个ItemView的高度，防止滑动的复用ItemView的时候重新分配itemView的高度
        // lp.height = ((screenWidth/2)* model.imageHeight).toInt()
        lp.height = (screenWidth * model.imageHeight).toInt()
        holder.showIv.layoutParams = lp
        if (model.desc.isEmpty()) {
            holder.descTv.visibility = View.GONE
        } else {
            holder.descTv.text = model.desc
            holder.descTv.visibility = View.VISIBLE
        }
        holder.time.text = model.time
        holder.videoLen.text = model.len
        holder.videoSize.text = model.size
        Glide.with(holder.itemView.context)
            .setDefaultRequestOptions(
                RequestOptions()
                    .frame(0)
                    .centerCrop()
                    .error(R.drawable.blank)
            )
            .load(model.url)
            .into(holder.showIv)

    }

    fun addData(newData: ImageModel) {
        data.add(newData)
        notifyItemInserted(data.size)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    fun setOnLongItemClickListener(listener: OnLongItemClickListener) {
        this.mLongListener = listener
    }

    fun update(il: MutableList<ImageModel>) {
        this.data = il
        notifyDataSetChanged()
    }

    interface OnLongItemClickListener {
        fun onLongItemClick(view: View?, postion: Int)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, postion: Int)
    }

    class PubuHolder(
        itemView: View,
        itemClickListener: OnItemClickListener,
        itemLongItemClickListener: OnLongItemClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        val showIv: ImageView = itemView.findViewById(R.id.show_iv)
        val descTv: TextView = itemView.findViewById(R.id.desc_tv)
        val videoSize: TextView = itemView.findViewById(R.id.video_len)
        val time: TextView = itemView.findViewById(R.id.time)
        val videoLen: TextView = itemView.findViewById(R.id.len)
        var mListener: OnItemClickListener = itemClickListener
        var mLongListener: OnLongItemClickListener = itemLongItemClickListener

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            mListener.onItemClick(v, position)
        }

        override fun onLongClick(v: View?): Boolean {
            mLongListener.onLongItemClick(v, position)
            return false
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
        val lp: ViewGroup.LayoutParams = holder.itemView.layoutParams
        if (lp is StaggeredGridLayoutManager.LayoutParams) {
            // 瀑布流的布局参数
            lp.isFullSpan = false
            holder.itemView.layoutParams = lp

        }
    }


    private fun getScreenWidth(context: Context): Int {
        val windowManager: WindowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        val windowManager: WindowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}