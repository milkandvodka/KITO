package com.kito.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScheduleItem(
    title: String,
    room: String,
    time: String,
    colors: UIColors
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(32.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(colors.accentOrangeStart, colors.accentOrangeEnd)
                    ),
                    RoundedCornerShape(2.dp)
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.padding(vertical = 6.dp)) {
            Row {
                Text(
                    text = title,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.labelLargeEmphasized,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = room,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.labelMediumEmphasized
                )
            }
            Text(
                text = time,
                color = colors.textPrimary.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelSmallEmphasized,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}