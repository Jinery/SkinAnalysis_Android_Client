package com.kychnoo.skinanalysis_android_client.data.manager.snackbar

import com.kychnoo.skinanalysis_android_client.data.model.states.snackbar.SnackbarState
import com.kychnoo.skinanalysis_android_client.data.model.types.SnackbarType
import kotlinx.coroutines.flow.Flow

interface SnackbarManager {
    val snackbarEvents: Flow<SnackbarState>
    suspend fun showSnackbar(message: String, type: SnackbarType = SnackbarType.INFO)
}