package com.ycs.smartcanteen.ui

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.ycs.smartcanteen.R


class SubmitPage(context: Context,pageNum:Int) : BottomPopupView(context) {
    var pageNum=pageNum
    override fun onCreate() {
        super.onCreate()

        val activity = context as FragmentActivity
//        pager.adapter = object : FragmentPagerAdapter(activity.supportFragmentManager) {
//            override fun getItem(position: Int): Fragment {
//                return CookbookFragment()
//            }
//
//            override fun getCount(): Int {
//                return pageNum
//            }
//        }

    }

    override fun getImplLayoutId(): Int {

        return R.layout.page_submit
    }
    override fun onShow() {
        super.onShow()
    }

    override fun onDismiss() {
        super.onDismiss()
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context) * .7f).toInt()
    }

}