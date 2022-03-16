package com.ycs.mvctest.MVC

class MVCController(activity: MainActivity) {
    //用于传递交互、更新数据
    var mvcActivity:MainActivity=activity
    lateinit var mvcPeopleModel: MVCPeopleModel
    public fun loadData(username:String, password:String){
        mvcPeopleModel=MVCPeopleModel(mvcActivity)
        mvcPeopleModel.loadModel(username, password)
        //how can i find a friend,kuku.
        //i want to managea happyBird.

    }
}