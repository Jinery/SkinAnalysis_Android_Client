package com.kychnoo.skinanalysis_android_client.data.repository

import android.content.ContentResolver
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.kychnoo.skinanalysis_android_client.data.camera.CameraManager
import com.kychnoo.skinanalysis_android_client.data.model.states.camera.CameraState
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraRepository @Inject constructor(
    private val cameraManager: CameraManager
) {
    val cameraState: StateFlow<CameraState> = cameraManager.cameraState

    fun initializeCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        isFrontCamera: Boolean = false
    ) {
        val selector = if (isFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        cameraManager.initializeCamera(lifecycleOwner, previewView, selector)
    }

    fun switchCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        cameraManager.switchCamera(lifecycleOwner, previewView)
    }

    fun takePhoto(onPhotoTaken: (Uri) -> Unit, onError: (Exception) -> Unit) {
        cameraManager.takePhoto(onPhotoTaken, onError)
    }

    fun deletePhoto(uri: Uri) {
        cameraManager.deletePhoto(uri)
    }

    fun shutdown() {
        cameraManager.shutdown()
    }
}
