package com.kito.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.unit.dp
import com.kito.data.local.db.studentsection.StudentSectionEntity
import com.kito.ui.components.animation.PageNotFoundAnimation
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScheduleCard(
    colors: UIColors,
    schedule: List<StudentSectionEntity>,
    onCLick: () -> Unit
) {
    val now = rememberLifecycleAwareCurrentTime()
    val (ongoing, upcomingList) = remember(schedule, now) {
        findOngoingAndAllUpcoming(schedule, now)
    }
    val meshColors = listOf(
        Color(0xFF77280F).copy(alpha = 0.82f), // burnt orange
        Color(0xFF753107).copy(alpha = 0.82f), // amber-700
        Color(0xFF62290A).copy(alpha = 0.82f), // amber-800
        Color(0xFF46180C).copy(alpha = 0.82f), // deep orange-brown

        // 🔥 new additions (subtle!)
        Color(0xFFA14B09).copy(alpha = 0.70f), // muted yellow (amber-500 toned down)
        Color(0xFF6B1414).copy(alpha = 0.75f), // brick red (not crimson)
    )
    val animatedPointMid = remember { Animatable(.8f) }
    val animatedPointTop = remember { Animatable(.8f) }
    val meshColorAnimators = remember {
        List(15) { index ->
            Animatable(meshColors[index % meshColors.size])
        }
    }
    LaunchedEffect(Unit) {
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
                .height(205.dp)
                .background(colors.cardBackground, RoundedCornerShape(22.dp))
                .padding(horizontal = 8.dp)
        ) {
            item{
                Spacer(modifier = Modifier.height(4.dp))
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
                        Box(modifier = Modifier
                            .clip(
                                RoundedCornerShape(12.dp)
                            )
                            .meshGradient(
                                points = listOf(
                                // ───── TOP ROW ─────
                                    listOf(
                                        Offset(0f, 0f) to meshColorAnimators[0].value,
                                        Offset(0.25f, 0f) to meshColorAnimators[1].value,
                                        Offset(0.5f, 0f) to meshColorAnimators[2].value,
                                        Offset(0.75f, 0f) to meshColorAnimators[3].value,
                                        Offset(1f, 0f) to meshColorAnimators[4].value,
                                    ),

                                    // ───── MIDDLE ROW (curved glow band) ─────
                                    listOf(
                                        Offset(-0.05f, 0.55f) to meshColorAnimators[5].value,
                                        Offset(0.2f, animatedPointTop.value) to meshColorAnimators[6].value,
                                        Offset(0.5f, 0.6f) to meshColorAnimators[7].value,
                                        Offset(0.8f, animatedPointMid.value) to meshColorAnimators[8].value,
                                        Offset(1.05f, 0.55f) to meshColorAnimators[9].value,
                                    ),

                                    // ───── BOTTOM ROW (independent animation per point) ─────
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
            if (ongoing == null && upcomingList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillParentMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        PageNotFoundAnimation()
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

@RequiresApi(Build.VERSION_CODES.O)
fun String.toLocalTime(): LocalTime =
    LocalTime.parse(this, timeFormatter)


@RequiresApi(Build.VERSION_CODES.O)
fun findOngoingAndAllUpcoming(
    schedule: List<StudentSectionEntity>,
    now: LocalTime
): Pair<StudentSectionEntity?, List<StudentSectionEntity>> {

    val ongoing = schedule.firstOrNull {
        val start = it.startTime.toLocalTime()
        val end = it.endTime.toLocalTime()
        now in start..end
    }

    val upcoming = schedule
        .filter { it.startTime.toLocalTime().isAfter(now) }
        .sortedBy { it.startTime.toLocalTime() }

    return ongoing to upcoming
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun rememberLifecycleAwareCurrentTime(): LocalTime {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var now by remember { mutableStateOf(LocalTime.now()) }

    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        var job: kotlinx.coroutines.Job? = null

        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_START -> {
                    job = kotlinx.coroutines.CoroutineScope(
                        kotlinx.coroutines.Dispatchers.Main.immediate
                    ).launch {
                        while (true) {
                            val current = LocalTime.now()
                            now = current

                            // ⏱ align to next minute boundary
                            val delayMillis =
                                (60 - current.second) * 1000L - current.nano / 1_000_000
                            kotlinx.coroutines.delay(delayMillis)
                        }
                    }
                }

                androidx.lifecycle.Lifecycle.Event.ON_STOP -> {
                    job?.cancel()
                    job = null
                }

                else -> Unit
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            job?.cancel()
            lifecycle.removeObserver(observer)
        }
    }

    return now
}