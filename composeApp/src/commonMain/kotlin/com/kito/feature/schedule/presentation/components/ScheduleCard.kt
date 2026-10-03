package com.kito.feature.schedule.presentation.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kito.core.common.util.currentLocalDateTime
import com.kito.core.common.util.formatTo12Hour
import com.kito.core.designsystem.UIColors
import com.kito.core.designsystem.meshGradient
import com.kito.core.presentation.components.animation.RelaxAnimation
import com.kito.feature.schedule.domain.model.ScheduleItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScheduleCard(
    colors: UIColors,
    isScheduleEmpty: Boolean,
    schedule: List<ScheduleItem>,
    nextSchedule: List<ScheduleItem>,
    onCLick: () -> Unit,
    enableAnimations: Boolean = true
) {
    val now = rememberCurrentTime(enableAnimations)
    val (ongoing, upcomingList) = remember(schedule, now) {
        findOngoingAndAllUpcoming(schedule, now)
    }
    val meshColors = listOf(
        Color(0xFF77280F).copy(alpha = 0.82f),
        Color(0xFF753107).copy(alpha = 0.82f),
        Color(0xFF62290A).copy(alpha = 0.82f),
        Color(0xFF46180C).copy(alpha = 0.82f),
        Color(0xFFA14B09).copy(alpha = 0.70f),
        Color(0xFF6B1414).copy(alpha = 0.75f),
    )
    val animatedPointMid = remember { Animatable(.8f) }
    val animatedPointTop = remember { Animatable(.8f) }
    val meshColorAnimators = remember {
        List(15) { index ->
            Animatable(meshColors[index % meshColors.size])
        }
    }
    LaunchedEffect(Unit) {
        if (!enableAnimations) return@LaunchedEffect
        meshColorAnimators.forEachIndexed { i, anim ->
            launch {
                val random = Random(i * 97)
                while (true) {
                    val nextColor = meshColors[random.nextInt(meshColors.size)]
                    anim.animateTo(
                        targetValue = nextColor,
                        animationSpec = tween(
                            durationMillis = random.nextInt(1800, 4200),
                            easing = LinearOutSlowInEasing
                        )
                    )
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable { onCLick() }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(165.dp)
                .background(colors.cardBackground, RoundedCornerShape(22.dp))
                .padding(horizontal = 8.dp)
        ) {
            if(!isScheduleEmpty) {
                if (ongoing != null || upcomingList.isNotEmpty() || nextSchedule.isNotEmpty()){
                    item{
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                if (ongoing != null) {
                    item {
                        Text(
                            text = "Ongoing",
                            color = colors.textSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(12.dp)
                                    )
                                    .meshGradient(
                                        points = listOf(
                                            listOf(
                                                Offset(0f, 0f) to meshColorAnimators[0].value,
                                                Offset(0.25f, 0f) to meshColorAnimators[1].value,
                                                Offset(0.5f, 0f) to meshColorAnimators[2].value,
                                                Offset(0.75f, 0f) to meshColorAnimators[3].value,
                                                Offset(1f, 0f) to meshColorAnimators[4].value,
                                            ),
                                            listOf(
                                                Offset(
                                                    -0.05f,
                                                    0.55f
                                                ) to meshColorAnimators[5].value,
                                                Offset(
                                                    0.2f,
                                                    animatedPointTop.value
                                                ) to meshColorAnimators[6].value,
                                                Offset(0.5f, 0.6f) to meshColorAnimators[7].value,
                                                Offset(
                                                    0.8f,
                                                    animatedPointMid.value
                                                ) to meshColorAnimators[8].value,
                                                Offset(1.05f, 0.55f) to meshColorAnimators[9].value,
                                            ),
                                            listOf(
                                                Offset(0f, 1f) to meshColorAnimators[10].value,
                                                Offset(0.25f, 1f) to meshColorAnimators[11].value,
                                                Offset(0.5f, 1f) to meshColorAnimators[12].value,
                                                Offset(0.75f, 1f) to meshColorAnimators[13].value,
                                                Offset(1f, 1f) to meshColorAnimators[14].value,
                                            ),
                                        ),
                                        resolutionX = 30
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    ScheduleItem(
                                        title = ongoing.subject,
                                        time = "${formatTo12Hour(ongoing.startTime)} - ${
                                            formatTo12Hour(ongoing.endTime)
                                        }",
                                        room = ongoing.room ?: "No Room",
                                        colors = colors
                                    )
                                }
                            }
                        }
                    }
                }
                if (upcomingList.isNotEmpty()) {
                    item {
                        Text(
                            text = "Upcoming",
                            color = colors.textSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    itemsIndexed(upcomingList) { index, upcoming ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = colors.cardBackgroundHigh
                            ),
                            shape = RoundedCornerShape(
                                topStart = if (index == 0) 12.dp else 4.dp,
                                topEnd = if (index == 0) 12.dp else 4.dp,
                                bottomStart = if (index == upcomingList.lastIndex) 12.dp else 4.dp,
                                bottomEnd = if (index == upcomingList.lastIndex) 12.dp else 4.dp
                            )
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                                ScheduleItem(
                                    title = upcoming.subject,
                                    time = "${formatTo12Hour(upcoming.startTime)} - ${
                                        formatTo12Hour(upcoming.endTime)
                                    }",
                                    room = upcoming.room ?: "No Room",
                                    colors = colors
                                )
                            }
                        }
                    }
                }
                if (ongoing == null && upcomingList.isEmpty() && nextSchedule.isNotEmpty()) {
                    item {
                        Text(
                            text = "Tomorrow's Schedule",
                            color = colors.textSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    itemsIndexed(nextSchedule) { index, upcoming ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = colors.cardBackgroundHigh
                            ),
                            shape = RoundedCornerShape(
                                topStart = if (index == 0) 12.dp else 4.dp,
                                topEnd = if (index == 0) 12.dp else 4.dp,
                                bottomStart = if (index == nextSchedule.lastIndex) 12.dp else 4.dp,
                                bottomEnd = if (index == nextSchedule.lastIndex) 12.dp else 4.dp
                            )
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                                ScheduleItem(
                                    title = upcoming.subject,
                                    time = "${formatTo12Hour(upcoming.startTime)} - ${
                                        formatTo12Hour(upcoming.endTime)
                                    }",
                                    room = upcoming.room ?: "No Room",
                                    colors = colors
                                )
                            }
                        }
                    }
                }else if (ongoing == null && upcomingList.isEmpty() && nextSchedule.isEmpty()){
                    item{
                        Box(
                            modifier = Modifier
                                .fillParentMaxHeight(),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.Center),
                                contentAlignment = Alignment.Center
                            ) {
                                RelaxAnimation()
                            }
                            Text(
                                text = "Weekend, Enjoy!",
                                color = colors.textSecondary,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }else{
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillParentMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Set up your timetable",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = colors.accentOrangeStart
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap to pick your section & load your routine",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Parse "HH:mm:ss" string into kotlinx-datetime LocalTime
 */
fun String.toLocalTime(): LocalTime {
    val parts = this.split(":")
    return LocalTime(
        hour = parts[0].toInt(),
        minute = parts[1].toInt(),
        second = if (parts.size > 2) parts[2].toInt() else 0
    )
}

fun findOngoingAndAllUpcoming(
    schedule: List<ScheduleItem>,
    now: LocalTime
): Pair<ScheduleItem?, List<ScheduleItem>> {

    val ongoing = schedule.firstOrNull {
        val start = it.startTime.toLocalTime()
        val end = it.endTime.toLocalTime()
        now in start..end
    }

    val upcoming = schedule
        .filter { it.startTime.toLocalTime() > now }
        .sortedBy { it.startTime.toLocalTime() }

    return ongoing to upcoming
}

@Composable
fun rememberCurrentTime(enableAnimations: Boolean = true): LocalTime {
    val currentDateTime = currentLocalDateTime()
    var now by remember { mutableStateOf(LocalTime(currentDateTime.hour, currentDateTime.minute, currentDateTime.second)) }

    LaunchedEffect(Unit) {
        if (!enableAnimations) return@LaunchedEffect
        while (true) {
            val current = currentLocalDateTime()
            now = LocalTime(current.hour, current.minute, current.second)

            // ⏱ align to next minute boundary
            val delayMillis = (60 - current.second) * 1000L - current.nanosecond / 1_000_000
            delay(delayMillis.milliseconds)
        }
    }

    return now
}
