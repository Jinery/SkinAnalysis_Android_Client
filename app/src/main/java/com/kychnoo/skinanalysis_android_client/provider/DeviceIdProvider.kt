package com.kychnoo.skinanalysis_android_client.provider

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

@SuppressLint("StaticFieldLeak")
object DeviceIdProvider {
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown"
    }
}