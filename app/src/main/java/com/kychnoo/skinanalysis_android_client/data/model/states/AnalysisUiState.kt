package com.kychnoo.skinanalysis_android_client.data.model.states

import android.net.Uri
import com.kychnoo.skinanalysis_android_client.data.model.states.analyse.ScreenAnalysisState
import com.kychnoo.skinanalysis_android_client.data.model.states.camera.CameraState

data class AnalysisUiState(
    val screenState: ScreenAnalysisState = ScreenAnalysisState(),
    val cameraState: CameraState = CameraState()
)