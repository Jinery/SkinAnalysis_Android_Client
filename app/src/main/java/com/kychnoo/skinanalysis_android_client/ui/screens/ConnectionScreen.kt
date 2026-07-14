package com.kychnoo.skinanalysis_android_client.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kychnoo.skinanalysis_android_client.R
import com.kychnoo.skinanalysis_android_client.data.model.events.NavigationEvent
import com.kychnoo.skinanalysis_android_client.ui.viewmodel.ConnectionViewModel
import kotlinx.serialization.Serializable

@Serializable
object ConnectionScreenRoute

@Composable
fun ConnectionScreen(
    innerPadding: PaddingValues,
    onSuccess: () -> Unit, // onSuccess callback.
    modifier: Modifier = Modifier,
    viewModel: ConnectionViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState() // get ui state from viewModel.

    LaunchedEffect(viewModel.navigationEvent) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                NavigationEvent.NavigateToAnalysisScreen -> onSuccess()  // If state is success then send callback.
                else -> {}
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
    ) {
        val isLandscape = maxWidth > maxHeight
        val isTablet = maxWidth > 600.dp

        val fillFraction by remember {
            derivedStateOf {
                when {
                    isLandscape -> 0.7f
                    isTablet -> 0.6f
                    else -> 1f
                }
            }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    if (!isLandscape) PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding(),
                        start = 0.dp,
                        end = 0.dp
                    ) else innerPadding
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.enter_connection_id),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                stringResource(R.string.get_key_in_tg_bot),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = state.connectionInputValue,
                onValueChange = { newValue -> viewModel.updateInputValue(newValue) },
                label = { Text(stringResource(R.string.id_from_bot)) },
                modifier = Modifier.fillMaxWidth(fillFraction),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (state.connectionInputValue.isNotBlank()) {
                        viewModel.registerDevice(state.connectionInputValue)
                    }
                },
                modifier = Modifier.fillMaxWidth(fillFraction / 2),
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
        }
    }
}