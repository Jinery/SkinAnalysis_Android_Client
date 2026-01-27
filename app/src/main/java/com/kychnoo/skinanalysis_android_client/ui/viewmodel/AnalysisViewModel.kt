package com.kychnoo.skinanalysis_android_client.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kychnoo.skinanalysis_android_client.data.DataStoreManager
import com.kychnoo.skinanalysis_android_client.data.model.TaskStatus
import com.kychnoo.skinanalysis_android_client.data.repository.SkinAnalysisRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnalysisViewModel(
    private val dataStoreManager: DataStoreManager,
    private val repository: SkinAnalysisRepository
) : ViewModel() {
    var imageUri by mutableStateOf<Uri?>(value = null)
    var analysisResultUrl by mutableStateOf<String?>(null)
    var isAnalysing by mutableStateOf(false)


    val connectionIdState: StateFlow<String?> = dataStoreManager.getConnectionIdFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun uploadAndAnalyse(context: Context, uri: Uri) {
        imageUri = uri
        isAnalysing = true

        viewModelScope.launch {
            val uploadResult = repository.analyzeImage(context, uri)
            uploadResult.onSuccess { taskId ->
                pollTaskStatus(taskId)
            }.onFailure {
                isAnalysing = false
            }
        }
    }

    private suspend fun pollTaskStatus(taskId: String) {
        var completed = false
        while (!completed) {
            val status = repository.getTaskStatus(taskId)
            if (status.getOrNull()?.status == TaskStatus.COMPLETED) {
                val result = repository.getTaskResult(taskId)
                analysisResultUrl = result.getOrNull()?.imageUrl
                completed = true
                isAnalysing = false
            }
            delay(2000)
        }
    }
}