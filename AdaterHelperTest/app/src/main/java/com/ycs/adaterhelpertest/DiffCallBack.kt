package com.ycs.adaterhelpertest

import android.util.Log
import androidx.recyclerview.widget.DiffUtil

/**
 * <pre>
 *     author : yangchaosheng
 *     e-mail : yangchaosheng@hisense.com
 *     time   : 2022/05/20
 *     desc   :
 * </pre>
 */
class DiffCallBack(var newList: MutableList<Item>, var oldList: MutableList<Item>): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
       return oldList.size
    }

    override fun getNewListSize(): Int {
       return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        var ii=newList[newItemPosition] == oldList[oldItemPosition]
        Log.d("yyy", "areItemsTheSame: $ii 和oldItemPosition${oldItemPosition}和newItemPosition$newItemPosition")
        return ii
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        var bb=newList[newItemPosition].str == oldList[oldItemPosition].str
        Log.d("yyy", "areContentsTheSame:$bb 和oldItemPosition${oldItemPosition}和newItemPosition$newItemPosition")
        return bb
    }
}