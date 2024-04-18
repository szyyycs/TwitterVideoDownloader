package com.ycs.servicetest.utils

import android.content.Context
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.ycs.servicetest.MainApplication
import com.ycs.servicetest.common.KVKey

/**
 * Created on 2024/04/17.
 * @author carsonyang
 */
object BiometricUtil {
    var isSupportBiometricPrompt: Boolean? = null
    val biometricManager: BiometricManager? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MainApplication.getAppContext()!!
                .getSystemService(Context.BIOMETRIC_SERVICE) as BiometricManager
        } else {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun fingerVerify(onSuccess: () -> Unit) {
        val cancellationSignal = CancellationSignal()
        cancellationSignal.setOnCancelListener {
            Toast.makeText(MainApplication.getAppContext(), "您取消了扫描", Toast.LENGTH_SHORT)
                .show()
        }
        val biometricPrompt = BiometricPrompt.Builder(MainApplication.getAppContext())
            .setTitle("指纹验证")
            .setSubtitle("请扫描你的指纹")
            .setDescription("扫描指纹后进入该功能")
            .setNegativeButton(
                "取消",
                MainApplication.getAppContext()!!.mainExecutor
            ) { _, _ ->
                showToast("取消验证")
            }
            .build()
        biometricPrompt.authenticate(
            cancellationSignal,
            MainApplication.getAppContext()!!.mainExecutor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    if (!biometricEnable()) {
                        KVUtil.setData(KVKey.OPEN_FINGER, false, KVKey.SETTING)
                        showToast("设备未开启指纹验证")
                        onSuccess()
                    } else {
                        showToast("指纹验证错误")
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showToast("指纹验证失败")
                }

            })
    }

    fun biometricEnable(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (biometricManager == null) {
                isSupportBiometricPrompt = false
                return isSupportBiometricPrompt!!
            }
            when (biometricManager!!.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    isSupportBiometricPrompt = true
                }

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    isSupportBiometricPrompt = false
                }

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    isSupportBiometricPrompt = false
                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    isSupportBiometricPrompt = false
                }

                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                    isSupportBiometricPrompt = false
                }

                else -> {
                    isSupportBiometricPrompt = false
                }
            }
            return isSupportBiometricPrompt!!
        } else {
            return false
        }

    }
}