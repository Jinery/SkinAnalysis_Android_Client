package com.kychnoo.skinanalysis_android_client.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import com.kychnoo.skinanalysis_android_client.R
import com.kychnoo.skinanalysis_android_client.data.model.results.permissions.PermissionsResult
import com.kychnoo.skinanalysis_android_client.data.model.types.SnackbarType
import com.kychnoo.skinanalysis_android_client.data.remote.ApiService.Companion.BASE_URL
import com.kychnoo.skinanalysis_android_client.ui.permissions.rememberPermissionsManager
import com.kychnoo.skinanalysis_android_client.ui.theme.Snow
import com.kychnoo.skinanalysis_android_client.ui.viewmodel.AnalysisViewModel
import com.kychnoo.skinanalysis_android_client.ui.widgets.CameraBottomMenu
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object MainScreenRoute

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: AnalysisViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissions = listOf(
        android.Manifest.permission.CAMERA,
    )

    var permissionsResult by remember { mutableStateOf<PermissionsResult?>(null) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val scaleX by animateFloatAsState(
        targetValue = if (uiState.cameraState.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) 1F else -1F,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        uri: Uri? -> uri?.let { viewModel.uploadAndAnalyse(it) }
    }

    val previewView = remember {
        PreviewView(context)
    }

    val permissionsManager = rememberPermissionsManager(
        permissions = permissions,
        onPermissionsResult = { result ->
            permissionsResult = result
            if (result.grantedPermissions.contains(permissions.first())) {
                viewModel.initializeCamera(lifecycleOwner, previewView)
            }
            if (!result.allGranted) {
                println("Denied: ${result.deniedPermissions}")
            }
        }
    )

    BackHandler {
        if (uiState.screenState.analysisResultUrl != null) {
            viewModel.dropState()
        }
    }

    LaunchedEffect(permissionsManager) {
        permissionsManager.requestPermissions()
    }

    DisposableEffect(lifecycleOwner) {
        viewModel.initializeCamera(lifecycleOwner, previewView)
        onDispose {
            viewModel.clearCamera()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.65F)
                .align(Alignment.Center)
                .offset(y = (-64).dp)
        ) {
            val currentScreenState = uiState.screenState
            if ((currentScreenState.isAnalysing && currentScreenState.imageUri != null) || currentScreenState.analysisResultUrl != null) {
                AnalysisImage(
                    imageUri = currentScreenState.analysisResultUrl ?: currentScreenState.imageUri!!,
                    statusMessage = currentScreenState.statusMessage,
                    isAnalysing = currentScreenState.isAnalysing,
                )
            } else {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .graphicsLayer(scaleX = scaleX)
                )
            }
        }

        AnimatedVisibility(
            visible = !uiState.screenState.isAnalysing && uiState.screenState.analysisResultUrl.isNullOrBlank(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 52.dp)
        ) {
            CameraBottomMenu(
                onToggleCameraClick = {
                    viewModel.switchCamera(lifecycleOwner, previewView)
                },
                onShotClick = {
                    if (permissionsManager.isPermissionGranted(permissions.first())) {
                        viewModel.takePhoto()
                    } else {
                        viewModel.handleCameraPermissionDenied()
                        permissionsManager.requestPermissions()
                    }
                },
                onGalleryClick = { launcher.launch("image/*") }
            )
        }
    }
}

@Composable
private fun BoxScope.AnalysisImage(
    imageUri: Any,
    statusMessage: String,
    isAnalysing: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isAnalysing) Color.Black.copy(alpha = .7F) else Color.Transparent,
        animationSpec = tween(150)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = "Analysing Image",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
        )
    }

    AnimatedVisibility(
            visible = isAnalysing && statusMessage.isNotBlank(),
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 24.dp)
        ) {
        Text(
            text = statusMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = Snow,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}