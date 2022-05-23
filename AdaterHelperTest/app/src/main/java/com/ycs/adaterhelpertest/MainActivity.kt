package com.ycs.adaterhelpertest

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var listView=findViewById<RecyclerView>(R.id.list)
        var list= mutableListOf<Item>()
        list.add(Item("111"))
        list.add(Item("222"))
        list.add(Item("333"))
        var adapter=MyAdapter(R.layout.item_layout, list)
        listView.layoutManager=MyLinearLayoutManage(this)
        listView.adapter=adapter
        adapter.setOnItemClickListener { _, _, position ->
            Toast.makeText(this, "$position", Toast.LENGTH_SHORT).show()
        }

        Thread{
            Thread.sleep(2000)
            runOnUiThread {
                Toast.makeText(this, "开始变", Toast.LENGTH_SHORT).show()
                var newList= mutableListOf<Item>()
                newList.add(Item("111"))
                newList.add(Item("222"))
                newList.add(Item("444"))
                newList.add(Item("333"))

                DiffUtil.calculateDiff(DiffCallBack(newList,list),false)
            }

        }.start()


    }
    class MyLinearLayoutManage(context: Context?) : LinearLayoutManager(context) {
        override fun onLayoutChildren(
            recycler: RecyclerView.Recycler?,
            state: RecyclerView.State?
        ) {
            try{
                super.onLayoutChildren(recycler, state)
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }
}