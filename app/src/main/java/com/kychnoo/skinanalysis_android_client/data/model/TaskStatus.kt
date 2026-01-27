package com.kychnoo.skinanalysis_android_client.data.model

import com.google.gson.annotations.SerializedName

enum class TaskStatus {
    @SerializedName("pending") PENDING,
    @SerializedName("processing") PROCESSING,
    @SerializedName("completed") COMPLETED,
    @SerializedName("failed") FAILED
}