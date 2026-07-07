package com.kychnoo.skinanalysis_android_client.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kychnoo.skinanalysis_android_client.R
import com.kychnoo.skinanalysis_android_client.data.DataStoreManager
import com.kychnoo.skinanalysis_android_client.data.manager.snackbar.SnackbarManager
import com.kychnoo.skinanalysis_android_client.data.model.TaskStatus
import com.kychnoo.skinanalysis_android_client.data.model.states.AnalysisUiState
import com.kychnoo.skinanalysis_android_client.data.model.types.SnackbarType
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
    dataStoreManager: DataStoreManager,
    private val repository: SkinAnalysisRepository,
    private val resources: ResourceProvider,
    snackbarManager: SnackbarManager
) : ViewModel(), SnackbarManager by snackbarManager {
    private val _uiState: MutableStateFlow<AnalysisUiState> = MutableStateFlow(AnalysisUiState())

    val uiState: StateFlow<AnalysisUiState> = combine(
        _uiState,
        dataStoreManager.getConnectionIdFlow
    ) { state, connectionId -> state.copy(connectionId = connectionId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalysisUiState()
    )

    fun uploadAndAnalyse(uri: Uri) {
        _uiState.update { it.copy(imageUri = uri, isAnalysing = true) }

        viewModelScope.launch {
            val uploadResult = repository.analyzeImage(uri)
            uploadResult.onSuccess { taskId ->
                pollTaskStatus(taskId)
            }.onFailure { th ->
                showSnackbar(th.message ?: resources.getString(R.string.missing_message), SnackbarType.ERROR)
                _uiState.update { it.copy(isAnalysing = false) }
            }
        }
    }

    private suspend fun pollTaskStatus(taskId: String) {
        var attempts = 0
        val maxAttempts = 30

        while (attempts < maxAttempts) {
            val status = repository.getTaskStatus(taskId)
            status.onSuccess { taskResponse ->
                when (taskResponse.status) {
                    TaskStatus.COMPLETED -> {
                        val result = repository.getTaskResult(taskId)
                        result.onSuccess { analysisRes ->
                            _uiState.update { it.copy(analysisResultUrl = analysisRes.imageUrl, isAnalysing = false) }
                        }.onFailure { th ->
                            showSnackbar(th.message ?: resources.getString(R.string.missing_message), SnackbarType.ERROR)
                        }
                        return
                    }
                    TaskStatus.FAILED -> {
                        showSnackbar(taskResponse.message, SnackbarType.ERROR)
                        _uiState.update { it.copy(isAnalysing = false) }
                        return
                    }
                    else -> { /*  Wait...  */ }
                }
            }.onFailure { th ->
                showSnackbar(th.message ?: resources.getString(R.string.missing_message), SnackbarType.ERROR)
                _uiState.update { it.copy(isAnalysing = false) }
                return
            }

            attempts++
            delay(2.seconds)
        }
        showSnackbar(resources.getString(R.string.error_timeout), SnackbarType.ERROR)
        _uiState.update { it.copy(isAnalysing = false) }
    }
}