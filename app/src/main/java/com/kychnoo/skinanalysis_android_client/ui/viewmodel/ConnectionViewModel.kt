package com.kychnoo.skinanalysis_android_client.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kychnoo.skinanalysis_android_client.R
import com.kychnoo.skinanalysis_android_client.data.model.ConnectionUiState
import com.kychnoo.skinanalysis_android_client.data.repository.ConnectionRepository
import com.kychnoo.skinanalysis_android_client.data.repository.SkinAnalysisRepository
import com.kychnoo.skinanalysis_android_client.provider.ResourceProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConnectionViewModel(
    private val connectionRepository: ConnectionRepository,
    private val skinRepository: SkinAnalysisRepository,
    private val resources: ResourceProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConnectionUiState())
    val uiState: StateFlow<ConnectionUiState> = _uiState.asStateFlow()

    init {
        loadInitialId()
    }

    private fun loadInitialId() { // Load id and check secure endpoint on starting application.
        viewModelScope.launch {
            val id = connectionRepository.getConnectionId().firstOrNull() // Get connection id from connect repository.
            if (id == null) {
                _uiState.update { it.copy(isLoading = false) } // If connection id is null update the state.
                return@launch // Exit from the function.
            }

            _uiState.update { it.copy(isLoading = true) } // Update state and set isLoading variable.
            val result = skinRepository.checkSecureEndpoint() // Send request for check secure endpoint to api.

            result.onSuccess { message ->
                _uiState.update { it.copy(connectionId = id, isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                connectionRepository.clearConnectionId() // If result returns failure status, clear connection id.
                _uiState.update { it.copy(
                    connectionId = null,
                    isLoading = false,
                    error = e.message ?: resources.getString(R.string.missing_message)
                ) }
            }
        }
    }

    fun registerDevice(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) } // Set loading mode and update state.
            connectionRepository.saveConnectionId(id) // Save connection id(Tempolary).

            val result = skinRepository.registerDevice(id) // Send request for register device to api.

            result.onSuccess { newId ->
                saveConnectionId(newId) // If result returns success, save connection id.
                _uiState.update { it.copy(isLoading = false, connectionId = newId, error = null, isSuccess = true) }
            }.onFailure { e ->
                clearConnectionId() // Else clear connection id, return error result and update state.
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: resources.getString(R.string.registration_failed),
                        connectionId = null,
                        isSuccess = false
                    )
                }
            }
        }
    }

    fun saveConnectionId(id: String) { // Function to save connection id with update state.
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                connectionRepository.saveConnectionId(id)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: resources.getString(R.string.failed_to_save),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearConnectionId() {
        viewModelScope.launch {
            connectionRepository.clearConnectionId()
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}