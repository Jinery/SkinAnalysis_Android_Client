package com.kychnoo.skinanalysis_android_client.data.model.results.permissions

data class PermissionsResult(
    val allGranted: Boolean,
    val grantedPermissions: List<String>,
    val deniedPermissions: List<String>
)
