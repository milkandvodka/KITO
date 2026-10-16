package com.kito.feature.calendar.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kito.core.designsystem.UIColors

@Composable
fun WeekdayHeaderRow(modifier: Modifier = Modifier) {
    val uiColors = UIColors()
    Row(modifier = modifier.fillMaxWidth()) {
        WEEKDAY_INITIALS.forEachIndexed { index, label ->
            Text(
                text = label,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                color = if (index == 0) uiColors.progressAccent else uiColors.textSecondary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
