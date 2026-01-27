package com.kychnoo.skinanalysis_android_client.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kychnoo.skinanalysis_android_client.R
import com.kychnoo.skinanalysis_android_client.data.remote.ApiService.Companion.BASE_URL
import com.kychnoo.skinanalysis_android_client.provider.DeviceIdProvider
import com.kychnoo.skinanalysis_android_client.ui.viewmodel.AnalysisViewModel

@Composable
fun MainScreen(viewModel: AnalysisViewModel) {
    val context = LocalContext.current // get current context.
    val currentConnectionId by viewModel.connectionIdState.collectAsState() // get state from view model/

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        uri: Uri? -> uri?.let { viewModel.uploadAndAnalyse(context, it) }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { launcher.launch("image/*") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Photo",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) {
        padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            if (viewModel.isAnalysing) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(stringResource(R.string.wait_analysis), modifier = Modifier.offset(y = 16.dp))
                }
            }

            viewModel.analysisResultUrl?.let { url -> // On get image url.
                val idSnapshot = currentConnectionId

                if (idSnapshot != null) { // if id snapshot is not null.
                    val fullUrl = BASE_URL + url // create fill url.

                    val headers = NetworkHeaders.Builder() // Create headers.
                        .add("connection-id", idSnapshot)
                        .add("X-Device-ID", DeviceIdProvider.getDeviceId())
                        .build()

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(fullUrl)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .diskCachePolicy(CachePolicy.DISABLED) // Disable cache policy.
                            .httpHeaders(headers) // Add headers.
                            .crossfade(true) // Enable crossfade.
                            .build(),
                        contentDescription = "Result",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Text(stringResource(R.string.authorization_error))
                }
            }
        }
    }
}