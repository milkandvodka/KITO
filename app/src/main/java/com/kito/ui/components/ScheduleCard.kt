package com.kito.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScheduleCard(colors: UIColors) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                colors.cardBackground,
                RoundedCornerShape(22.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "My Schedule",
            color = colors.textPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(Modifier.height(12.dp))

        ScheduleItem("AI A-201", "8:00 - 8:30", colors)
        ScheduleItem("ML B-102", "8:30 - 9:00", colors)
        ScheduleItem("Universal Human Values C-101", "10:00 - 10:30", colors)
        ScheduleItem("Extra", "12:00 - 12:30", colors)
    }
}

@Composable
private fun ScheduleItem(
    title: String,
    time: String,
    colors: UIColors
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = title,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            fontSize = 18.sp,
        )
        Text(
            text = time,
            color = colors.textPrimary.copy(alpha = 0.85f),
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
