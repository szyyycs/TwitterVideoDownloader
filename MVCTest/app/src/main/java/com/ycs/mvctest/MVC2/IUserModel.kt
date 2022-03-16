package com.ycs.mvctest.MVC2

interface IUserModel {
    fun login(user:String,pwd:String,loginListener:OnLoginListener)
}
interface OnLoginListener{
    fun onSuccess(user:User)
    fun onFailed()
}