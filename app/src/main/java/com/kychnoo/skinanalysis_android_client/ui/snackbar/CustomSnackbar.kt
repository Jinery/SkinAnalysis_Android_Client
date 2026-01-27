package com.kychnoo.skinanalysis_android_client.ui.snackbar

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kychnoo.skinanalysis_android_client.data.model.SnackbarType

@Composable
fun CustomSnackbar(data: SnackbarData, type: SnackbarType) {
    val backgroundColor = when (type) {
        SnackbarType.ERROR -> Color(0xFFD32F2F)
        SnackbarType.SUCCESS -> Color(0xFF388E3C)
        SnackbarType.INFO -> Color(0xFF1976D2)
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Text(
            text = data.visuals.message,
            color = Color.White,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}