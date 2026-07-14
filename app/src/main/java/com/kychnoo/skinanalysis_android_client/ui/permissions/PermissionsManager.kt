package com.kychnoo.skinanalysis_android_client.ui.permissions

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.kychnoo.skinanalysis_android_client.data.model.results.permissions.PermissionsResult
import com.kychnoo.skinanalysis_android_client.data.model.states.permissions.PermissionState

class PermissionsManager(
    private val permissions: List<String>,
    private val onPermissionsResult: (PermissionsResult) -> Unit,
) {
    private val _permissionsStates = mutableStateOf<Map<String, PermissionState>>(emptyMap())
    val permissionsStates: State<Map<String, PermissionState>> = _permissionsStates

    private var launcher: ActivityResultLauncher<Array<String>>? = null

    fun setLauncher(newLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>) {
        this.launcher = newLauncher
    }

    fun requestPermissions(newLauncher: ActivityResultLauncher<Array<String>>? = null) {
        if (launcher == null && newLauncher != null)
            launcher = newLauncher
        launcher?.launch(permissions.toTypedArray())
    }

    fun handlePermissionsResult(result: Map<String, Boolean>) {
        val newStates = mutableMapOf<String, PermissionState>()
        val grantedPermissions = mutableListOf<String>()
        val deniedPermissions = mutableListOf<String>()

        result.forEach { (permission, granted) ->
            newStates[permission] = PermissionState(
                permission = permission,
                isGranted = granted,
            )
            if (granted) grantedPermissions.add(permission)
            else deniedPermissions.add(permission)
        }

        _permissionsStates.value = newStates
        onPermissionsResult(
            PermissionsResult(
                allGranted = grantedPermissions.size == permissions.size,
                grantedPermissions = grantedPermissions,
                deniedPermissions = deniedPermissions
            )
        )
    }

    fun isPermissionGranted(permission: String): Boolean
        = permissionsStates.value[permission]?.isGranted ?: false

    fun areAllPermissionsGranted(): Boolean
        = permissionsStates.value.all { it.value.isGranted }
}