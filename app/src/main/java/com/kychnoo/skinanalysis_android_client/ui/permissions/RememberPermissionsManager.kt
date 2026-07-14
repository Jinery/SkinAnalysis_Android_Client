package com.kychnoo.skinanalysis_android_client.ui.permissions

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.kychnoo.skinanalysis_android_client.data.model.results.permissions.PermissionsResult

@Composable
fun rememberPermissionsManager(
    permissions: List<String>,
    onPermissionsResult: (PermissionsResult) -> Unit = {  },
): PermissionsManager {
    val context = LocalContext.current

    val manager = remember {
        PermissionsManager(permissions, onPermissionsResult)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        manager.handlePermissionsResult(results)
    }

    LaunchedEffect(permissions) {
        val currentResults = permissions.associateWith { permissions ->
            context.checkSelfPermission(permissions) == PackageManager.PERMISSION_GRANTED
        }
        manager.handlePermissionsResult(currentResults)
    }

    manager.setLauncher(launcher)

    return manager
}