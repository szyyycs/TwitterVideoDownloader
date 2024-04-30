package com.ycs.servicetest.common

import android.os.Environment
import com.ycs.servicetest.utils.KVUtil

/**
 * Created on 2024/04/10.
 * @author carsonyang
 */
object Config {
    val isFirstIntoAPP: Boolean = KVUtil.getInt(KVKey.ENTER_APP_NUMBER) == 1
    const val defaultDownloadPath = ".savedPic"
    val downloadPathUrl: String
        get() = Environment.getExternalStorageDirectory().toString() +
                "/${
                    KVUtil.getString(
                        KVKey.SAVE_URL,
                        defaultDownloadPath
                    ) ?: defaultDownloadPath
                }/"
    const val DEFAULT_OPEN_FINGER = false
    const val DEFAULT_SHUFFLE_VIDEOS = true
    const val DEFAULT_IS_XHS_PLAY_INTO_TIKTOK = true
}