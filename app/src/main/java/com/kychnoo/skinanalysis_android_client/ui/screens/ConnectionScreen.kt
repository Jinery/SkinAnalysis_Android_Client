package com.kychnoo.skinanalysis_android_client.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kychnoo.skinanalysis_android_client.R
import com.kychnoo.skinanalysis_android_client.data.model.SnackbarType
import com.kychnoo.skinanalysis_android_client.ui.snackbar.CustomSnackbar
import com.kychnoo.skinanalysis_android_client.ui.viewmodel.ConnectionViewModel

@Composable
fun ConnectionScreen(
    viewModel: ConnectionViewModel,
    onSuccess: () -> Unit // onSuccess callback.
) {
    val state by viewModel.uiState.collectAsState() // get ui state from viewModel.
    var inputId by remember { mutableStateOf("") } // Input id with mutable state.
    val snackbarHostState = remember { SnackbarHostState() } // Snackbar host state.

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                CustomSnackbar(data, if (state.error != null) SnackbarType.ERROR else SnackbarType.SUCCESS) // Add custom snackbar.
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.enter_connection_id), style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.get_key_in_tg_bot), style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = inputId,
                onValueChange = { inputId = it },
                label = { Text(stringResource(R.string.id_from_bot)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (inputId.isNotBlank()) {
                        viewModel.registerDevice(inputId)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.get_started))
                }
            }

            LaunchedEffect(state.error) {
                state.error?.let {
                    snackbarHostState.showSnackbar(it) // Show snackbar on error.
                    viewModel.dismissError() // dismiss error in view model.
                }
            }

            LaunchedEffect(state.isSuccess) {
                if (state.isSuccess) {
                    onSuccess() // If state if success send callback.
                }
            }
        }
    }
}