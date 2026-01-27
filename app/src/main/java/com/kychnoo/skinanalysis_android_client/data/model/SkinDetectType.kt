package com.kychnoo.skinanalysis_android_client.data.model

import com.google.gson.annotations.SerializedName

enum class SkinDetectType {
    @SerializedName("problem") PROBLEM,
    @SerializedName("nevus") NEVUS,
    @SerializedName("healthy") HEALTHY
}