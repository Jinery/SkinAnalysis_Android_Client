package com.kychnoo.skinanalysis_android_client.ui.viewmodel

import android.net.Uri
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kychnoo.skinanalysis_android_client.R
import com.kychnoo.skinanalysis_android_client.data.DataStoreManager
import com.kychnoo.skinanalysis_android_client.data.manager.snackbar.SnackbarManager
import com.kychnoo.skinanalysis_android_client.data.model.TaskStatus
import com.kychnoo.skinanalysis_android_client.data.model.states.AnalysisUiState
import com.kychnoo.skinanalysis_android_client.data.model.states.analyse.ScreenAnalysisState
import com.kychnoo.skinanalysis_android_client.data.model.types.SnackbarType
import com.kychnoo.skinanalysis_android_client.data.repository.CameraRepository
import com.kychnoo.skinanalysis_android_client.data.repository.ConnectionRepository
import com.kychnoo.skinanalysis_android_client.data.repository.SkinAnalysisRepository
import com.kychnoo.skinanalysis_android_client.provider.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val analysisRepository: SkinAnalysisRepository,
    private val cameraRepository: CameraRepository,
    connectionRepository: ConnectionRepository,
    private val resourceProvider: ResourceProvider,
    snackbarManager: SnackbarManager
) : ViewModel(), SnackbarManager by snackbarManager {
    private val _screenState: MutableStateFlow<ScreenAnalysisState> = MutableStateFlow(ScreenAnalysisState())

    val uiState: StateFlow<AnalysisUiState> = combine(
        _screenState,
        cameraRepository.cameraState,
        connectionRepository.getConnectionIdAsFlow(),
    ) { screenState, cameraState, connectionId ->
        AnalysisUiState(
            connectionId = connectionId,
            screenState = screenState,
            cameraState = cameraState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalysisUiState()
    )

    fun uploadAndAnalyse(uri: Uri) {
        _screenState.update { it.copy(imageUri = uri, isAnalysing = true) }

        viewModelScope.launch {
            val uploadResult = analysisRepository.analyzeImage(uri)
            uploadResult.onSuccess { taskId ->
                pollTaskStatus(taskId)
            }.onFailure { th ->
                showSnackbar(th.message ?: resourceProvider.getString(R.string.missing_message), SnackbarType.ERROR)
                _screenState.update { it.copy(isAnalysing = false) }
            }
        }
    }

    fun initializeCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        cameraRepository.initializeCamera(lifecycleOwner, previewView)
    }

    fun switchCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        cameraRepository.switchCamera(lifecycleOwner, previewView)
    }

    fun takePhoto() {
        cameraRepository.takePhoto(
            onPhotoTaken = { _, uri ->
                uploadAndAnalyse(uri)
            },
            onError = { th ->
                viewModelScope.launch {
                    showSnackbar(
                        th.message ?: resourceProvider.getString(R.string.camera_error),
                        SnackbarType.ERROR
                    )
                }
            }
        )
    }

    fun clearCamera() {
        cameraRepository.shutdown()
    }

    override fun onCleared() {
        clearCamera()
    }

    fun handleCameraPermissionDenied() {
        viewModelScope.launch {
            showSnackbar(
                "To take a photo, we need access to the camera. Please grant access through the settings.",
                SnackbarType.INFO
            )
        }
    }

    private suspend fun pollTaskStatus(taskId: String) {
        var attempts = 0
        val maxAttempts = 30

        while (attempts < maxAttempts) {
            val status = analysisRepository.getTaskStatus(taskId)
            status.onSuccess { taskResponse ->
                when (taskResponse.status) {
                    TaskStatus.COMPLETED -> {
                        val result = analysisRepository.getTaskResult(taskId)
                        result.onSuccess { analysisRes ->
                            if (analysisRes.imageUrl == null) {
                                showSnackbar(analysisRes.message, SnackbarType.INFO)
                            }
                            _screenState.update { it.copy(analysisResultUrl = analysisRes.imageUrl, isAnalysing = false) }
                        }.onFailure { th ->
                            showSnackbar(th.message ?: resourceProvider.getString(R.string.missing_message), SnackbarType.ERROR)
                        }
                        return
                    }
                    TaskStatus.FAILED -> {
                        showSnackbar(taskResponse.message, SnackbarType.ERROR)
                        _screenState.update { it.copy(isAnalysing = false) }
                        return
                    }
                    else -> { /*  Wait...  */ }
                }
            }.onFailure { th ->
                showSnackbar(th.message ?: resourceProvider.getString(R.string.missing_message), SnackbarType.ERROR)
                _screenState.update { it.copy(isAnalysing = false) }
                return
            }

            attempts++
            delay(2.seconds)
        }
        showSnackbar(resourceProvider.getString(R.string.error_timeout), SnackbarType.ERROR)
        _screenState.update { it.copy(isAnalysing = false) }
    }

    fun dropState() {
        _screenState.update { it.copy(isAnalysing = false, analysisResultUrl = null) }
    }
}