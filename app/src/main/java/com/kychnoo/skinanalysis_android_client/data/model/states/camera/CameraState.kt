package com.kychnoo.skinanalysis_android_client.data.model.states.camera

import androidx.camera.core.CameraSelector

data class CameraState(
    val isPreviewActive: Boolean = false,
    val luminosity: Double = 0.0,
    val isDarkCondition: Boolean = false,
    val isCameraInitialized: Boolean = false,
    val cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
)
