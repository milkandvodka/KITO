package com.kito.ui.newUi.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kito.data.remote.model.TeacherScheduleByIDModel
import com.kito.ui.components.UIColors
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState

private val dayPriority = mapOf(
    "Mon" to 1,
    "Tue" to 2,
    "Wed" to 3,
    "Thu" to 4,
    "Fri" to 5,
    "Sat" to 6,
    "Sun" to 7
)


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalHazeApi::class,
    ExperimentalHazeMaterialsApi::class
)
@Composable
fun FacultyDetailScreen(
    facultyName: String,
    facultyRoom: String,
    facultyEmail: String,
    schedule: List<TeacherScheduleByIDModel>
) {
    val uiColors = UIColors()
    val hazeState = rememberHazeState()
    val cardHaze = rememberHazeState()

    val groupedSchedule = schedule
        .sortedWith(
            compareBy<TeacherScheduleByIDModel>(
                { dayPriority[it.day] ?: Int.MAX_VALUE },
                { timeToSortableMinutes(it.start_time) }
            )
        )
        .groupBy { it.day }



    Box(modifier = Modifier.hazeSource(cardHaze)) {

        LazyColumn(
            contentPadding = PaddingValues(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 70.dp,
                bottom = 86.dp + WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(2.5.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .hazeSource(hazeState)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(6.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        uiColors.cardBackground,
                                        Color(0xFF2F222F),
                                        Color(0xFF2F222F),
                                        uiColors.cardBackgroundHigh
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = facultyName,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = uiColors.textPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Faculty Room: $facultyRoom",
                                fontFamily = FontFamily.Monospace,
                                color = uiColors.textSecondary
                            )
                            Text(
                                text = "Email: $facultyEmail",
                                fontFamily = FontFamily.Monospace,
                                color = uiColors.textSecondary
                            )
                        }
                    }
                }
            }
            groupedSchedule.forEach { (day, classes) ->

                item {
                    Text(
                        text = day,
                        modifier = Modifier.padding(start = 8.dp, top = 16.dp),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = uiColors.textPrimary,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                itemsIndexed(classes) { index, item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(
                            topStart = if (index == 0) 24.dp else 4.dp,
                            topEnd = if (index == 0) 24.dp else 4.dp,
                            bottomStart = if (index == classes.size - 1) 24.dp else 4.dp,
                            bottomEnd = if (index == classes.size - 1) 24.dp else 4.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            uiColors.cardBackground,
                                            Color(0xFF2F222F),
                                            Color(0xFF2F222F),
                                            uiColors.cardBackgroundHigh
                                        )
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "${formatTime(item.start_time)} - ${formatTime(item.end_time)}",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = uiColors.textPrimary
                                )
                                Text(
                                    text = item.subject,
                                    fontFamily = FontFamily.Monospace,
                                    color = uiColors.textSecondary
                                )
                                Text(
                                    text = "Room: ${item.room}",
                                    fontFamily = FontFamily.Monospace,
                                    color = uiColors.textSecondary
                                )
                                Text(
                                    text = "Batch: ${item.batch}",
                                    fontFamily = FontFamily.Monospace,
                                    color = uiColors.textSecondary
                                )
                            }
                        }
                    }
                }

            }
        }
        Column(
            modifier = Modifier
                .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                    blurRadius = 15.dp
                    noiseFactor = 0.05f
                    alpha = 0.98f
                }
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(
                modifier = Modifier.height(
                    16.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                )
            )
            Text(
                text = "Faculty Detail",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = uiColors.textPrimary,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
fun formatTime(time: String): String {
    val parts = time.split(":")
    val hour = parts[0].toIntOrNull() ?: return time
    val minute = parts.getOrNull(1) ?: "00"

    return when {
        hour in 1..7 -> "${hour}:${minute} PM"
        hour in 8..12 -> "${hour}:${minute} AM"
        hour >= 13 -> "${hour - 12}:${minute} PM"
        else -> time
    }
}
fun timeToSortableMinutes(time: String): Int {
    val parts = time.split(":")
    val hour = parts[0].toIntOrNull() ?: return Int.MAX_VALUE
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

    val hour24 = when {
        hour in 1..7 -> hour + 12
        hour in 8..12 -> hour
        hour >= 13 -> hour
        else -> hour
    }

    return hour24 * 60 + minute
}

