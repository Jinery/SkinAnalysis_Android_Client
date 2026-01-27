package com.kychnoo.skinanalysis_android_client.data.model

// Data Class for connection UI State.
data class ConnectionUiState(
    val isSuccess: Boolean = false,
    val connectionId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isConnected: Boolean get() = connectionId != null
    val shouldShowError: Boolean get() = error != null
}
