package com.kito.feature.calendar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.UIColors

@Composable
fun OfflinePill(modifier: Modifier = Modifier) {
    val uiColors = UIColors()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(Icons.Rounded.CloudOff, contentDescription = null, tint = uiColors.progressAccent, modifier = Modifier.size(16.dp))
        Text(
            text = "Offline · some items may be missing",
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.labelSmall,
            color = uiColors.textSecondary,
        )
    }
}
