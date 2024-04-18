package com.ycs.servicetest.utils

import android.widget.Toast
import com.ycs.servicetest.MainApplication

/**
 * Created on 2024/04/17.
 * @author carsonyang
 */
fun showToast(
    message: String,
) {
    val applicationContext = MainApplication.getAppContext() ?: return
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
}