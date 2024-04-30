package com.ycs.servicetest.common

import android.Manifest

object Constant {
    //Create an developper account on twitter and get your api key
    const val TWITTER_KEY = "wHa3s8vF5cmke6HEaiCiq9aa9"
    const val TWITTER_SECRET = "ZdU9kcF6XjUudLQ8qoW8XzxwMx5HPscrI6gUIrrmu5fnfkyBv1"

    //Permissions
    val PERMISSION_STRORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    const val REQUEST_CODE = 20
    const val TAG = "yyy"
    const val INTENT_POSITION = "POSITION"
    const val INTENT_LIST = "LIST"

}
const val TAG = "yyy"