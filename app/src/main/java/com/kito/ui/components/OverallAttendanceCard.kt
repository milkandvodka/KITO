package com.kito.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalHazeMaterialsApi::class,
    ExperimentalHazeApi::class
)
@Composable
fun OverallAttendanceCard(
    colors: UIColors,
    sapLoggedIn: Boolean,
    percentageOverall: Double,
    percentageHighest: Double,
    percentageLowest: Double,
    onClick:() -> Unit,
    onNavigate:()-> Unit,
) {
    var targetProgressOverall by remember { mutableFloatStateOf(0f) }
    var targetProgressHighest by remember { mutableFloatStateOf(0f) }
    var targetProgressLowest by remember { mutableFloatStateOf(0f) }

    val progressOverall by animateFloatAsState(
        targetValue = targetProgressOverall,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "attendance"
    )
    val progressHighest by animateFloatAsState(
        targetValue = targetProgressHighest,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "attendance"
    )
    val progressLowest by animateFloatAsState(
        targetValue = targetProgressLowest,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "attendance"
    )

    val hazeEffect = rememberHazeState()

    LaunchedEffect(percentageOverall, sapLoggedIn) {
        targetProgressOverall =
            if (sapLoggedIn) {
                (percentageOverall / 100.0)
                    .toFloat()
                    .coerceIn(0f, 1f)
            }else{
                0.8f
            }
    }
    LaunchedEffect(percentageHighest, sapLoggedIn) {
        targetProgressHighest =
            if (sapLoggedIn) {
                (percentageHighest / 100.0)
                    .toFloat()
                    .coerceIn(0f, 1f)
            }else{
                0.8f
            }
    }
    LaunchedEffect(percentageLowest, sapLoggedIn) {
        targetProgressLowest =
            if (sapLoggedIn) {
                (percentageLowest / 100.0)
                    .toFloat()
                    .coerceIn(0f, 1f)
            }else{
                0.8f
            }
    }
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(22.dp))
            .clickable(
                onClick = {
                    onNavigate()
                }
            )
    ) {
        Box(
            modifier = Modifier.hazeSource(hazeEffect)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .aspectRatio(
                        ratio = 1.7f
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
                    Row (
                        modifier = Modifier
                            .weight(1f)
                    ){
                        Spacer(
                            modifier = Modifier.width(4.dp)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.aspectRatio(1f)
                            ) {
                                CircularWavyProgressIndicator(
                                    progress = {
                                        progressHighest
                                    },
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    waveSpeed = 30.dp,
                                    wavelength = 45.dp,
                                    color = colors.accentOrangeStart,
                                    trackColor = colors.progressAccent,
                                    amplitude = {
                                        1f
                                    }
                                )
                                Text(
                                    text = "${"%.1f".format(progressHighest * 100)}%",
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.titleLargeEmphasized
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Highest",
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.labelLargeEmphasized
                            )
                        }
                        Spacer(
                            modifier = Modifier.width(4.dp)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.aspectRatio(1f)
                            ) {
                                CircularWavyProgressIndicator(
                                    progress = {
                                        progressOverall
                                    },
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    waveSpeed = 30.dp,
                                    wavelength = 45.dp,
                                    color = colors.accentOrangeStart,
                                    trackColor = colors.progressAccent,
                                    amplitude = {
                                        1f
                                    }
                                )
                                Text(
                                    text = "${"%.1f".format(progressOverall * 100)}%",
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.titleLargeEmphasized
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Overall",
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.labelLargeEmphasized
                            )
                        }
                        Spacer(
                            modifier = Modifier.width(4.dp)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.aspectRatio(1f)
                            ) {
                                CircularWavyProgressIndicator(
                                    progress = {
                                        progressLowest
                                    },
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    waveSpeed = 30.dp,
                                    wavelength = 45.dp,
                                    color = colors.accentOrangeStart,
                                    trackColor = colors.progressAccent,
                                    amplitude = {
                                        1f
                                    }
                                )
                                Text(
                                    text = "${"%.1f".format(progressLowest * 100)}%",
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.titleLargeEmphasized
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Lowest",
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.labelLargeEmphasized
                            )
                        }
                        Spacer(
                            modifier = Modifier.width(4.dp)
                        )
                    }
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(bottom = 16.dp),
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .background(color = colors.accentOrangeStart, shape = CircleShape)
//                                .size(12.dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(
//                            text = "Attended",
//                            fontFamily = FontFamily.Monospace,
//                            style = MaterialTheme.typography.labelLargeEmphasized
//                        )
//                        Spacer(modifier = Modifier.width(24.dp))
//                        Box(
//                            modifier = Modifier
//                                .background(color = colors.progressAccent, shape = CircleShape)
//                                .size(12.dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(
//                            text = "Not Attended",
//                            fontFamily = FontFamily.Monospace,
//                            style = MaterialTheme.typography.labelLargeEmphasized
//                        )
//                    }
                }
            }
        }
        if (!sapLoggedIn) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .aspectRatio(
                        ratio = 1.7f
                    )
                    .fillMaxSize()
                    .clip(
                        shape = RoundedCornerShape(26.dp)
                    )
                    .hazeEffect(state = hazeEffect, style = HazeMaterials.ultraThin()) {
                        blurRadius = 15.dp
                        noiseFactor = 0.05f
                        inputScale = HazeInputScale.Auto
                    }
            ) {
                Button(
                    onClick = {
                        onClick()
                    },
                    modifier = Modifier.align(Alignment.Center),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.progressAccent,
                        contentColor = colors.textPrimary
                    )
                ) {
                    Text(
                        text = "Connect to sap",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelMediumEmphasized
                    )
                }
            }
        }
    }
}
