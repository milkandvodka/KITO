package com.kito.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OverallAttendanceCard(
    colors: UIColors
) {
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
        targetProgress = 0.9f
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(
                ratio = 1f
            )
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xfffffff),
                        Color(0xFFB45104)
                    ),
                    tileMode = TileMode.Mirror
                ),
                shape = RoundedCornerShape(26.dp)
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularWavyProgressIndicator(
                    progress = {
                        progress
                    },
                    modifier = Modifier
                        .size(200.dp),
                    waveSpeed = 20.dp,
                    wavelength = 80.dp,
                    color = colors.accentOrangeStart,
                    trackColor = colors.progressAccent
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.displaySmallEmphasized
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(color = colors.accentOrangeStart, shape = CircleShape)
                        .size(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Attended",
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.labelLargeEmphasized
                )
                Spacer(modifier = Modifier.width(24.dp))
                Box(
                    modifier = Modifier
                        .background(color = colors.progressAccent, shape = CircleShape)
                        .size(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Not Attended",
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.labelLargeEmphasized
                )
            }
        }
    }
}
