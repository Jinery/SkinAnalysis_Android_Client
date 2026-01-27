package com.kychnoo.skinanalysis_android_client.data.model.response

import com.google.gson.annotations.SerializedName
import com.kychnoo.skinanalysis_android_client.data.model.AnalysisItem

data class AnalysisResponse(
    val status: String,
    val message: String,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("analysis_result") val analysisResult: List<AnalysisItem>
)
