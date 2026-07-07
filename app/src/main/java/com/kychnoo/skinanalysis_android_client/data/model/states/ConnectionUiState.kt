package com.kychnoo.skinanalysis_android_client.data.model.states

// Data Class for connection UI State.
data class ConnectionUiState(
    val isSuccess: Boolean = false,
    val connectionId: String? = null,
    val isLoading: Boolean = false,
) {
    val isConnected: Boolean get() = connectionId != null
}