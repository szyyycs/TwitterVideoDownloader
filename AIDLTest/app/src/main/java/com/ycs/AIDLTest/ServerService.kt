package com.ycs.AIDLTest

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import org.qiyi.video.svg.Andromeda
import org.qiyi.video.svg.IPCCallback

class ServerService : Service() {
    companion object{
        var TAG="ServerService"
    }
    //实现了Messenger和AIDL//但是没实现Andromeda
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    private var mMessenger=Messenger(MessengerHandler())
    var recMessenger:Messenger?=null
    override fun onBind(intent: Intent): IBinder {
        Andromeda.registerRemoteService(IBuyBlueBerry::class.java, TestAndromeda.getInstance())
        return mMessenger.binder
       // return TestImpl()
    }
    class TestImpl :ITestInterface.Stub(){
        override fun getNum(name: String?): String {
            return "返回的名字是$name"
        }

    }
    class TestAndromeda: IBuyBlueBerry.Stub() {
        companion object{
            //final表示不能改变的常量
            //static表示是一个静态变量
            private lateinit var instance:TestAndromeda
            open fun getInstance(): TestAndromeda? {
                if(instance==null){
                    synchronized(TestAndromeda::class){
                        if(instance==null){
                            instance=TestAndromeda()
                        }
                    }
                }
                return instance
            }
        }

//        override fun asBinder(): IBinder {
//            if(instance==null){
//                synchronized(TestAndromeda::class){
//                    if(instance==null){
//                        instance=TestAndromeda()
//                    }
//                }
//            }
//            return instance
//        }

        @Throws(RemoteException::class)
        override fun buyNum(num: Int, callback: IPCCallback?) {

        }
        @Throws(RemoteException::class)
        override fun getPrice() :Int{
            return 3
        }

    }
    inner class MessengerHandler :Handler(){
        override fun handleMessage(msg: Message) {
            recMessenger=msg.replyTo
            when(msg.what){
                1 -> {
                    Log.d(TAG, "getMessage:1 ")
                    var msg = Message.obtain(null, 1)
                    recMessenger?.send(msg)
                }

                2 -> {
                    Log.d(TAG, "getMessage:2 ")
                    var msg = Message.obtain(null, 2)

                    recMessenger?.send(msg)
                }
                else->{
                    super.handleMessage(msg)
                }
            }

        }
    }

}