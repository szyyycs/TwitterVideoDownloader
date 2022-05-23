package com.ycs.adaterhelpertest

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * <pre>
 *     author : yangchaosheng
 *     e-mail : yangchaosheng@hisense.com
 *     time   : 2022/05/20
 *     desc   :
 * </pre>
 */
 class MyAdapter(layoutResId: Int, data: MutableList<Item>?) :
    BaseQuickAdapter<Item, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: Item) {
        holder.setText(R.id.tv_name,"第"+item.str+"个选项")
    }

}