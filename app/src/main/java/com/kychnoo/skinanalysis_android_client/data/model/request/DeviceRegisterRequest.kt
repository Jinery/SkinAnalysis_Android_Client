package com.kychnoo.skinanalysis_android_client.data.model.request

import com.google.gson.annotations.SerializedName

// Model for register device request in api.
data class DeviceRegisterRequest(
    val platform: String,
    @SerializedName("device_uid") val deviceUid: String,
    val name: String,
    val model: String,
    @SerializedName("os_version") val osVersion: String
)
