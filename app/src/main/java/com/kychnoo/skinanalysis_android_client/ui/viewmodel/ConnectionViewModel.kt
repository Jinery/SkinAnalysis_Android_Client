package com.kychnoo.skinanalysis_android_client.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kychnoo.skinanalysis_android_client.R
import com.kychnoo.skinanalysis_android_client.data.manager.snackbar.SnackbarManager
import com.kychnoo.skinanalysis_android_client.data.model.events.NavigationEvent
import com.kychnoo.skinanalysis_android_client.data.model.states.ConnectionUiState
import com.kychnoo.skinanalysis_android_client.data.model.types.SnackbarType
import com.kychnoo.skinanalysis_android_client.data.repository.ConnectionRepository
import com.kychnoo.skinanalysis_android_client.data.repository.SkinAnalysisRepository
import com.kychnoo.skinanalysis_android_client.provider.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val connectionRepository: ConnectionRepository,
    private val skinRepository: SkinAnalysisRepository,
    private val resources: ResourceProvider,
    snackbarManager: SnackbarManager
) : ViewModel(), SnackbarManager by snackbarManager {
    private val _uiState = MutableStateFlow(ConnectionUiState())
    val uiState: StateFlow<ConnectionUiState> = _uiState.asStateFlow()

    private val _navigationEvent: Channel<NavigationEvent> = Channel<NavigationEvent>(Channel.BUFFERED)
    val navigationEvent: Flow<NavigationEvent> = _navigationEvent.receiveAsFlow()

    init {
        observeConnectionId()
    }

    private fun observeConnectionId() {
        viewModelScope.launch {
            connectionRepository.getConnectionIdAsFlow()
                .onStart {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .collect { id ->
                    _uiState.update { it.copy(connectionId = id) }
                    if (id == null) {
                        _uiState.update { it.copy(isLoading = false) }
                    } else {
                        checkEndpoint()
                    }
                }
        }
    }

    private suspend fun checkEndpoint() { // Check secure endpoint on starting application.
        _uiState.update { it.copy(isLoading = true) }

        val result = skinRepository.checkSecureEndpoint() // Send request for check secure endpoint to api.

        result.onSuccess { _ ->
            _uiState.update { it.copy(isLoading = false) }
            _navigationEvent.send(NavigationEvent.NavigateToAnalysisScreen)
        }.onFailure { e ->
            showSnackbar(
                e.message ?: resources.getString(R.string.missing_message),
                SnackbarType.ERROR
            )
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun updateInputValue(inputValue: String) {
        _uiState.update { it.copy(connectionInputValue = inputValue) }
    }

    fun registerDevice(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } // Set loading mode and update state.
            connectionRepository.saveConnectionId(id) // Save connection id(Tempolary).

            val result = skinRepository.registerDevice(id) // Send request for register device to api.

            result.onSuccess { newId ->
                saveConnectionId(newId) // If result returns success, save connection id.
            }.onFailure { e ->
                showSnackbar(e.message ?: resources.getString(R.string.registration_failed), SnackbarType.ERROR)
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    private suspend fun saveConnectionId(id: String) { // Function to save connection id with update state.
        _uiState.update { it.copy(isLoading = true) }
        try {
            connectionRepository.saveConnectionId(id)
        } catch (e: Exception) {
            showSnackbar(
                e.message ?: resources.getString(R.string.failed_to_save),
                SnackbarType.ERROR
            )
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }
}