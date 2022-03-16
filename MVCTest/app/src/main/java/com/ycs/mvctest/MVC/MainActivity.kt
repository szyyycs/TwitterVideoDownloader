package com.ycs.mvctest.MVC

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.ycs.mvctest.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var mvcController:MVCController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mvcController= MVCController(this)
        login.setOnClickListener {
            mvcController.loadData("张三","123456***")
        }

    }
    public fun updateUI(mvcPeopleModel: MVCPeopleModel){
        textView.setText(mvcPeopleModel.money)
    }
}