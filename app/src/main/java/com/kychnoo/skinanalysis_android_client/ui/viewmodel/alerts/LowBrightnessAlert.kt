package com.kychnoo.skinanalysis_android_client.ui.viewmodel.alerts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kychnoo.skinanalysis_android_client.R
import com.kychnoo.skinanalysis_android_client.ui.theme.Snow
import com.kychnoo.skinanalysis_android_client.ui.theme.SoftBlack

@Composable
fun LowBrightnessAlert(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CircleShape,
        border = BorderStroke(
            width = 1.dp,
            color = Color.Gray
        ),
        colors = CardColors(
            containerColor = SoftBlack,
            contentColor = Snow,
            disabledContainerColor = SoftBlack,
            disabledContentColor = Snow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.low_brightness),
                contentDescription = "Low brightness icon",
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = stringResource(R.string.low_brightness),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
private fun LowBrightnessAlertPreview() {
    LowBrightnessAlert()
}