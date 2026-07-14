package com.kychnoo.skinanalysis_android_client.data.camera

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.hardware.display.DisplayManager
import android.net.Uri
import android.provider.MediaStore
import android.view.Surface
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.kychnoo.skinanalysis_android_client.data.camera.analyzer.LuminosityAnalyzer
import com.kychnoo.skinanalysis_android_client.data.model.states.camera.CameraState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * A manager for working with CameraX.
 * Encapsulates the logic for initialization, binding to the Lifecycle, and taking photos.
 */
class CameraManager(
    private val context: Context
) {
    private val _cameraState = MutableStateFlow(CameraState())
    val cameraState: StateFlow<CameraState> = _cameraState.asStateFlow()

    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var imageAnalysis: ImageAnalysis? = null

    private val cameraExecutor: Executor = Executors.newSingleThreadExecutor()
    private var luminosityAnalyzer: LuminosityAnalyzer? = null
    private var imageCapture: ImageCapture? = null

    private var displayListener: DisplayManager.DisplayListener? = null

    /**
     * Initialize camera with selected camera selector.
     */
    fun initializeCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()

                _cameraState.value = _cameraState.value.copy(
                    cameraSelector = cameraSelector,
                    isCameraInitialized = true
                )

                bindCameraUseCases(
                    lifecycleOwner = lifecycleOwner,
                    previewView = previewView,
                    cameraSelector = cameraSelector
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Toggle camera between front and back.
     */
    fun switchCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        val newCameraSelector = if (_cameraState.value.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        cameraProvider?.unbindAll()

        bindCameraUseCases(
            lifecycleOwner = lifecycleOwner,
            previewView = previewView,
            cameraSelector = newCameraSelector
        )

        _cameraState.value = _cameraState.value.copy(cameraSelector = newCameraSelector)
    }

    /**
     * Bind use cases to lifecycle.
     */
    private fun bindCameraUseCases(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        cameraSelector: CameraSelector
    ) {
        val cameraProvider = cameraProvider ?: return

        preview = Preview.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()
            .also {
                it.surfaceProvider = previewView.surfaceProvider
            }

        imageCapture = ImageCapture.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()

        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

        displayListener?.let { displayManager.unregisterDisplayListener(it) }

        displayListener = object : DisplayManager.DisplayListener {
            override fun onDisplayAdded(displayId: Int) {}
            override fun onDisplayRemoved(displayId: Int) {}
            override fun onDisplayChanged(displayId: Int) {
                val display = previewView.display ?: return
                if (displayId == display.displayId) {
                    val rotation = display.rotation
                    imageCapture?.targetRotation = rotation
                    preview?.targetRotation = rotation
                }
            }
        }

        displayManager.registerDisplayListener(displayListener, null)

        luminosityAnalyzer = LuminosityAnalyzer { luminosity ->
            _cameraState.value = _cameraState.value.copy(
                luminosity = luminosity,
                isDarkCondition = luminosity < DARK_THRESHOLD
            )
        }

        imageAnalysis = ImageAnalysis.Builder()
            .setTargetRotation(previewView.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, luminosityAnalyzer!!)
            }

        try {
            cameraProvider.unbindAll()

            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis,
                imageCapture
            )

            _cameraState.value = _cameraState.value.copy(isPreviewActive = true)
        } catch (e: Exception) {
            e.printStackTrace()
            _cameraState.value = _cameraState.value.copy(isPreviewActive = false)
        }
    }

    /**
     * Take photo from current camera.
     */
    fun takePhoto(
        onPhotoTaken: (Uri) -> Unit,
        onError: (Exception) -> Unit,
    ) {
         camera ?: run {
            onError(Exception("Camera not initialized"))
            return
        }

        val imageCapture = imageCapture ?: run {
            onError(Exception("Camera use cases not bound yet"))
            return
        }

        val imageFile = File(context.cacheDir, "SkA_IMG_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()

        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val uri = outputFileResults.savedUri
                    uri?.let {
                        onPhotoTaken(it)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    onError(exception)
                }
            }
        )
    }

    fun deletePhoto(uri: Uri) {
        try {
            if (uri.scheme == ContentResolver.SCHEME_FILE) {
                uri.path?.let { File(it).delete() }
            } else {
                context.contentResolver.delete(uri, null, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Shutdown camera.
     */
    fun shutdown() {
        cameraProvider?.unbindAll()
        displayListener?.let { listener ->
            val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            displayManager.unregisterDisplayListener(listener)
            displayListener = null
        }
        _cameraState.value = _cameraState.value.copy(
            isPreviewActive = false,
            isCameraInitialized = false
        )
    }

    companion object {
        private const val DARK_THRESHOLD = 50.0
    }
}
