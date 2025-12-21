package com.kito.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OverallAttendanceCard(item: AttendanceItem) {
    val uiColors = UIColors()
    var targetProgress by remember { mutableFloatStateOf(0f) }

    val progress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "attendance"
    )

    // 👇 This triggers the animation ONCE
    LaunchedEffect(Unit) {
        targetProgress = (item.percentage / 100f).coerceIn(0f, 1f)
    }
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