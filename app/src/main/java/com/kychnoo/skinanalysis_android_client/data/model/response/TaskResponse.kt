package com.kychnoo.skinanalysis_android_client.data.model.response

import com.google.gson.annotations.SerializedName
import com.kychnoo.skinanalysis_android_client.data.model.TaskStatus
import java.time.LocalDateTime

data class TaskResponse(
    @SerializedName("task_id") val taskId: String,
    val status: TaskStatus,
    val message: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("result_url")val resultUrl: String? = null,
    val process: Int? = null,
)
