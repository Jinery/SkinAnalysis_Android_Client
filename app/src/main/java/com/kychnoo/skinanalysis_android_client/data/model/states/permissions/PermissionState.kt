package com.kychnoo.skinanalysis_android_client.data.model.states.permissions

data class PermissionState(
    val permission: String,
    val isGranted: Boolean,
    val shouldShowRationale: Boolean = false,
)