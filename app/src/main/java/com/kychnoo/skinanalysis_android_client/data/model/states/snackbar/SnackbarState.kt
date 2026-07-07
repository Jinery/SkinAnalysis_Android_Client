package com.kychnoo.skinanalysis_android_client.data.model.states.snackbar

import com.kychnoo.skinanalysis_android_client.data.model.types.SnackbarType

data class SnackbarState(
    val message: String,
    val type: SnackbarType = SnackbarType.INFO
)