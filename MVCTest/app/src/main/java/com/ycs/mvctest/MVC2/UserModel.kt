package com.ycs.mvctest.MVC2

import android.text.TextUtils

class UserModel :IUserModel {
    override fun login(user: String, pwd: String, loginListener: OnLoginListener) {
        if(TextUtils.isEmpty(user)||TextUtils.isEmpty(pwd)){
            return
        }
        if("wanghao" == user &&"123"==pwd){
            loginListener.onSuccess(User(user,pwd))
        }else{
            loginListener.onFailed()
        }
    }
}