package com.kito.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun ScheduleCard(colors: UIColors) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                colors.cardBackground,
                RoundedCornerShape(22.dp)
            )
            .padding( horizontal = 16.dp, vertical = 8.dp)
    ) {
        ScheduleItem("AI A-201", "8:00 - 8:30", colors)
        ScheduleItem("ML B-102", "8:30 - 9:00", colors)
        ScheduleItem("UHV C-101", "10:00 - 10:30", colors)
        ScheduleItem("Extra", "12:00 - 12:30", colors)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScheduleItem(
    title: String,
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
            Text(
                text = title,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.labelLargeEmphasized
            )
            Text(
                text = time,
                color = colors.textPrimary.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelSmallEmphasized,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
