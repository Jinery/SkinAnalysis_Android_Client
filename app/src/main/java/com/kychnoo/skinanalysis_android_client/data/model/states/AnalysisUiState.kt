package com.kychnoo.skinanalysis_android_client.data.model.states

import android.net.Uri

data class AnalysisUiState(
    val imageUri: Uri? = null,
    val analysisResultUrl: String? = null,
    val isAnalysing: Boolean = false,
    val connectionId: String? = null
)