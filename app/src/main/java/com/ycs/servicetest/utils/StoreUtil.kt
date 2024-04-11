package com.ycs.servicetest.utils

import com.tencent.mmkv.MMKV
import com.ycs.servicetest.MainApplication

/**
 * Created on 2024/04/10.
 * @author carsonyang
 */
class StoreUtil {
    companion object {
        private var mmkv: MMKV? = null
        fun getString(key: String, mmkvId: String? = null, defaultValue: String? = null): String? {
            if (mmkv == null) {
                MMKV.initialize(MainApplication.getAppContext())
            }
            mmkv = if (mmkvId.isNullOrBlank()) {
                MMKV.defaultMMKV()
            } else {
                MMKV.mmkvWithID(mmkvId)
            }

            return if (defaultValue != null)
                mmkv?.decodeString(key, defaultValue)
            else mmkv?.decodeString(key)

        }

        fun getInt(key: String, mmkvId: String? = null, defaultValue: Int? = null): Int? {
            if (mmkv == null) {
                MMKV.initialize(MainApplication.getAppContext())
            }
            mmkv = if (mmkvId.isNullOrBlank()) {
                MMKV.defaultMMKV()
            } else {
                MMKV.mmkvWithID(mmkvId)
            }
            return if (defaultValue != null)
                mmkv?.decodeInt(key, defaultValue)
            else mmkv?.decodeInt(key)

        }

        fun getBool(key: String, mmkvId: String? = null, defaultValue: Boolean? = null): Boolean? {
            if (mmkv == null) {
                MMKV.initialize(MainApplication.getAppContext())
            }
            mmkv = if (mmkvId.isNullOrBlank()) {
                MMKV.defaultMMKV()
            } else {
                MMKV.mmkvWithID(mmkvId)
            }
            return if (defaultValue != null)
                mmkv?.decodeBool(key, defaultValue)
            else mmkv?.decodeBool(key)

        }

        fun <T> setData(key: String, value: T, mmkvId: String? = null) {
            if (mmkv == null) {
                MMKV.initialize(MainApplication.getAppContext())
            }
            mmkv = if (mmkvId.isNullOrBlank()) {
                MMKV.defaultMMKV()
            } else {
                MMKV.mmkvWithID(mmkvId)
            }
            when (value) {
                is String -> {
                    mmkv!!.encode(key, value)
                }

                is Int -> {
                    mmkv!!.encode(key, value)
                }

                is Boolean -> {
                    mmkv!!.encode(key, value)
                }
            }
        }
    }
}