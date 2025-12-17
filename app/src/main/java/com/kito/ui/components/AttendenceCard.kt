package com.kito.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AttendanceCard(item: AttendanceItem) {
    val uiColors = UIColors()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = uiColors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title
            Text(
                text = item.title,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = uiColors.textPrimary,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Attendance row
            Text(
                text = "Attendance: ${item.present}/${item.total}",
                fontFamily = FontFamily.Monospace,
                color = uiColors.textSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar visual (thin purple line like screenshot)
            val progress = remember(item) {
                (item.percentage / 100f).coerceIn(0f, 1f)
            }
            LinearWavyProgressIndicator(
                progress = {
                    progress
                },
                color = uiColors.accentOrangeStart,
                trackColor = uiColors.progressAccent,
                modifier = Modifier.fillMaxWidth(),
                amplitude = {
                    0.8f
                },
                waveSpeed = 20.dp,
                wavelength = 50.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Percentage and faculty
            Text(
                text = "Percentage: ${"%.2f".format(item.percentage)}%",
                fontFamily = FontFamily.Monospace,
                color = uiColors.textSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Faculty: ${item.faculty}",
                fontFamily = FontFamily.Monospace,
                color = uiColors.textSecondary,
                fontSize = 14.sp
            )
        }
    }
}