package com.ycs.servicetest.common

import com.ycs.servicetest.utils.StoreUtil

/**
 * Created on 2024/04/10.
 * @author carsonyang
 */
object Config {
    val isFirstIntoAPP: Boolean = StoreUtil.getInt(KVKey.ENTER_APP_NUMBER) == 1


}