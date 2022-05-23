package com.ycs.AIDLTest

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.multidex.MultiDex
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import kotlinx.android.synthetic.main.activity_main.*
import org.qiyi.video.svg.Andromeda
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {
    private var itInterface: ITestInterface? = null
    private var mMessenger: Messenger? = null
    private var binder:IBinder?=null

    private var clientMessenger: Messenger = Messenger(MessengerHandler(this))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //bindAIDL()
        //bindMessenger()
        useAndromeda()
        
        btn.setOnClickListener {
           Toast.makeText(this, "这是更新的版本", Toast.LENGTH_SHORT).show()
//            itInterface?.let {
//                Toast.makeText(this@MainActivity, "连接了！" + it.getNum("Yang"), Toast.LENGTH_SHORT).show()
//            }
//            mMessenger?.let {
//                try {
//                    var msg = Message.obtain(null, 1)
//                    msg.replyTo = clientMessenger
//                    it.send(msg)
//                } catch (e: RemoteException) {
//                    e.printStackTrace()
//                }
//            }
//            binder?.let {
//                Log.d("yyy", "onCreate: ")
//                var buyBlueBerry=IBuyBlueBerry.Stub.asInterface(binder)
//                buyBlueBerry?.let {
//                    toast(buyBlueBerry.price)
//                }
//            }
        }
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base)


        // 安装tinker
        Beta.installTinker()
    }
    private fun useAndromeda(){
        Andromeda.init(this)
        binder=Andromeda.with(this)
            .getRemoteService(IBuyBlueBerry::class.java)
    }
    private fun bindMessenger() {

        bindService(Intent(this, ServerService::class.java), object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                mMessenger = Messenger(p1)
            }

            override fun onServiceDisconnected(p0: ComponentName?) {

            }

        }, Context.BIND_AUTO_CREATE)
    }
    private fun toast(num: Int){
        Toast.makeText(this@MainActivity, "$num", Toast.LENGTH_SHORT).show()
    }

    //kotlin默认就是public
    private fun bindAIDL() {
        bindService(Intent(this, ServerService::class.java), object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                itInterface = ITestInterface.Stub.asInterface(p1)
                Toast.makeText(this@MainActivity, "连接了！", Toast.LENGTH_SHORT).show()
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                Toast.makeText(this@MainActivity, "断开连接了！", Toast.LENGTH_SHORT).show()
            }

        }, Context.BIND_AUTO_CREATE)
    }

    class MessengerHandler(activity: MainActivity) : Handler(Looper.getMainLooper()) {
        private val act=WeakReference(activity)
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> {
                    Toast.makeText(act.get(), "Messenger方式收到消息1啦", Toast.LENGTH_SHORT).show()
                }
                2 -> {
                    Toast.makeText(act.get(), "Messenger方式收到消息2啦", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    super.handleMessage(msg)
                }
            }

        }
    }

}