package com.kychnoo.skinanalysis_android_client.data.model.states.analyse

import android.net.Uri

data class ScreenAnalysisState(
    val imageUri: Uri? = null,
    val analysisResultUrl: String? = null,
    val isAnalysing: Boolean = false,
    val statusMessage: String = ""
)