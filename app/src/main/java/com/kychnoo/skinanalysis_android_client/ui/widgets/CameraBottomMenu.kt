package com.kychnoo.skinanalysis_android_client.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kychnoo.skinanalysis_android_client.R

@Composable
fun CameraBottomMenu(
    onToggleCameraClick: () -> Unit,
    onShotClick: () -> Unit,
    onGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onToggleCameraClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_switch_camera),
                contentDescription = "switch_camera_button",
                modifier = Modifier.fillMaxSize()
            )
        }

        IconButton(
            onClick = onShotClick,
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_shot),
                contentDescription = "take_photo_button",
                modifier = Modifier.fillMaxSize()
            )
        }

        IconButton(
            onClick = onGalleryClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_gallery),
                contentDescription = "open_gallery",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}