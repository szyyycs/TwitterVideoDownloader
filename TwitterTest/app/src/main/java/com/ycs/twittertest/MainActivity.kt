package com.ycs.twittertest


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.twitter.sdk.android.core.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object{
        var TAG="yyy"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setContentView(R.layout.activity_main)
        twitterButton()
    }
    private fun init(){
        var key=getString(R.string.twitter_consumer_key)
        var secret=getString(R.string.twitter_consumer_secret)
        val config = TwitterConfig.Builder(this)
                .logger(DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(TwitterAuthConfig(key, secret))
                .debug(true)
                .build()
        Twitter.initialize(config)
    }
    private fun twitterButton() {

        loginButton.callback = object : Callback<TwitterSession?>() {
            override fun success(result: Result<TwitterSession?>) {
                // Do something with result, which provides a TwitterSession for making API calls
                // result里面包含了用户的信息，我们可以从中取出token，tokenSecret
                // 如果我们有自己的后台服务器，发送这两个到我们自己的后台，后台再去验证）
                Log.d(TAG, "success: ")

                val authToken: TwitterAuthToken = result.data!!.authToken
                val token = authToken.token
                val tokenSecret = authToken.secret
                val userName: String = result.data!!.userName
                val userId: String = result.data!!.userId.toString() + ""
                Log.i(TAG, token)
                Log.i(TAG, tokenSecret)
                Log.i(TAG, userName)
                Log.i(TAG, userId)

            }

            override  fun failure(exception: TwitterException) { // Do something on failure
                exception.printStackTrace()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result to the login button.
        if (loginButton != null) {
            loginButton.onActivityResult(requestCode, resultCode, data)
        }
    }

}