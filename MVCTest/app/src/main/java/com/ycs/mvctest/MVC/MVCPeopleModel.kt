package com.ycs.mvctest.MVC

class MVCPeopleModel(mainActivity: MainActivity) {
    private var username:String=""
    private var password:Int=0
    var money:Int=0
    private var mMainActivity: MainActivity = mainActivity
    public fun loadModel(username:String,password:String){
        money=1000000
        mMainActivity.updateUI(this)

    }
}