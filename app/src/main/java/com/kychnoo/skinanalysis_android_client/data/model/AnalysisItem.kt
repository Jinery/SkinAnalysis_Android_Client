package com.kychnoo.skinanalysis_android_client.data.model

data class AnalysisItem(
    val label: SkinDetectType,
    val confidence: Double,
    val box: CropBox
)
