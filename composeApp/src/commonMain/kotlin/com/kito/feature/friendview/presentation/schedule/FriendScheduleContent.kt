package com.kito.feature.friendview.presentation.schedule

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kito.core.common.util.currentLocalDateTime
import com.kito.core.common.util.formatTo12Hour
import com.kito.core.designsystem.UIColors
import com.kito.core.designsystem.meshGradient
import com.kito.core.platform.sendEmail
import com.kito.core.presentation.components.animation.PandaSleepingAnimation
import com.kito.feature.friendview.domain.model.FriendScheduleItem
import com.kito.feature.friendview.domain.model.FriendSummary
import com.kito.feature.schedule.presentation.WeekDay
import com.kito.feature.schedule.presentation.components.horizontalCarouselTransition
import com.kito.feature.schedule.presentation.components.isClassOngoing
import com.kito.feature.schedule.presentation.components.isClassUpcoming
import com.kito.feature.schedule.presentation.components.todayKey
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import kotlin.random.Random

@OptIn(
    ExperimentalHazeApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class
)
@Composable
fun FriendScheduleContent(
    roll: String,
    summary: FriendSummary?,
    schedule: Map<WeekDay, List<FriendScheduleItem>>,
    isLoading: Boolean,
    onBack: () -> Unit,
    enableAnimations: Boolean = true,
    modifier: Modifier = Modifier
) {
    val hazeState = rememberHazeState()
    val uiColors = UIColors()
    val coroutineScope = rememberCoroutineScope()
    val weekDays = WeekDay.entries
    val today = todayKey()
    val currentPage = when (today) {
        "MON" -> 0
        "TUE" -> 1
        "WED" -> 2
        "THU" -> 3
        "FRI" -> 4
        "SAT" -> 5
        else -> 0
    }
    val pagerState = rememberPagerState(
        initialPage = currentPage,
        pageCount = { weekDays.size }
    )
    val haptics = LocalHapticFeedback.current

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
    var now by remember {
        val dt = currentLocalDateTime()
        mutableStateOf(LocalTime(dt.hour, dt.minute, dt.second))
    }

    LaunchedEffect(Unit) {
        val dt = currentLocalDateTime()
        now = LocalTime(dt.hour, dt.minute, dt.second)
    }

    if (enableAnimations) {
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
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .drop(1)
            .distinctUntilChanged()
            .collect {
                haptics.performHapticFeedback(HapticFeedbackType.Confirm)
            }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121116))
            .semantics { testTag = "friendschedule_content" }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121116))
                .hazeSource(hazeState)
        ) {
            if (isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        color = uiColors.progressAccent,
                        modifier = Modifier.size(36.dp)
                    )
                }
            } else {
                HorizontalPager(
                    contentPadding = PaddingValues(
                        start = 28.dp,
                        end = 28.dp,
                    ),
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val day = weekDays[page]
                    val daySchedule = schedule[day].orEmpty()
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(2.5.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .horizontalCarouselTransition(page, pagerState),
                    ) {
                        item {
                            Spacer(
                                modifier = Modifier.height(
                                    WindowInsets.statusBars.asPaddingValues()
                                        .calculateTopPadding() + 140.dp
                                )
                            )
                        }
                        if (daySchedule.isNotEmpty()) {
                            itemsIndexed(daySchedule) { index, item ->
                                val isFirstYear = item.batch.equals("batch_1", ignoreCase = true) ||
                                        item.batch.trim() == "1" ||
                                        item.section.matches(Regex("^[AB]\\d+.*", RegexOption.IGNORE_CASE))
                                val isLongLocation = item.room != null && (item.room.contains("/") || item.room.length > 13)
                                val showLocationBelow = isFirstYear && isLongLocation

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(if (showLocationBelow) 116.dp else 100.dp)
                                        .then(
                                            if (page == currentPage && isClassUpcoming(
                                                    startTime = item.startTime,
                                                    now = now
                                                ) && today != "SUN"
                                            ) {
                                                Modifier.border(
                                                    width = 2.dp,
                                                    brush = Brush.verticalGradient(
                                                        colors = listOf(
                                                            uiColors.progressAccent,
                                                            uiColors.progressAccent
                                                        )
                                                    ),
                                                    shape = RoundedCornerShape(
                                                        topStart = if (index == 0) 24.dp else 4.dp,
                                                        topEnd = if (index == 0) 24.dp else 4.dp,
                                                        bottomStart = if (index == daySchedule.size - 1) 24.dp else 4.dp,
                                                        bottomEnd = if (index == daySchedule.size - 1) 24.dp else 4.dp
                                                    )
                                                )
                                            } else {
                                                Modifier
                                            }
                                        ),
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                    shape = RoundedCornerShape(
                                        topStart = if (index == 0) 24.dp else 4.dp,
                                        topEnd = if (index == 0) 24.dp else 4.dp,
                                        bottomStart = if (index == daySchedule.size - 1) 24.dp else 4.dp,
                                        bottomEnd = if (index == daySchedule.size - 1) 24.dp else 4.dp
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .then(
                                                if (page == currentPage && isClassOngoing(
                                                        startTime = item.startTime,
                                                        endTime = item.endTime,
                                                        now = now
                                                    ) && today != "SUN"
                                                ) {
                                                    Modifier.meshGradient(
                                                        points = listOf(
                                                            listOf(
                                                                Offset(0f, 0f) to meshColorAnimators[0].value,
                                                                Offset(0.25f, 0f) to meshColorAnimators[1].value,
                                                                Offset(0.5f, 0f) to meshColorAnimators[2].value,
                                                                Offset(0.75f, 0f) to meshColorAnimators[3].value,
                                                                Offset(1f, 0f) to meshColorAnimators[4].value,
                                                            ),
                                                            listOf(
                                                                Offset(-0.05f, 0.55f) to meshColorAnimators[5].value,
                                                                Offset(0.2f, animatedPointTop.value) to meshColorAnimators[6].value,
                                                                Offset(0.5f, 0.6f) to meshColorAnimators[7].value,
                                                                Offset(0.8f, animatedPointMid.value) to meshColorAnimators[8].value,
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
                                                } else {
                                                    Modifier.background(
                                                        brush = Brush.linearGradient(
                                                            colors = listOf(
                                                                uiColors.cardBackground,
                                                                Color(0xFF2F222F),
                                                                Color(0xFF2F222F),
                                                                uiColors.cardBackgroundHigh
                                                            )
                                                        )
                                                    )
                                                }
                                            )
                                    ) {
                                        val isFirstYear = item.batch.equals("batch_1", ignoreCase = true) ||
                                                item.batch.trim() == "1" ||
                                                item.section.matches(Regex("^[AB]\\d+.*", RegexOption.IGNORE_CASE))
                                        val isLongLocation = item.room != null && (item.room.contains("/") || item.room.length > 13)
                                        val showLocationBelow = isFirstYear && isLongLocation

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .padding(horizontal = 14.dp, vertical = 12.dp)
                                                .fillMaxSize()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .width(4.dp)
                                                    .height(if (showLocationBelow) 68.dp else 48.dp)
                                                    .background(
                                                        Brush.verticalGradient(
                                                            listOf(
                                                                uiColors.accentOrangeStart,
                                                                uiColors.accentOrangeEnd
                                                            )
                                                        ),
                                                        RoundedCornerShape(2.dp)
                                                    )
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            if (showLocationBelow) {
                                                Column(
                                                    verticalArrangement = Arrangement.Center,
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .weight(1f)
                                                ) {
                                                    Text(
                                                        text = item.subject,
                                                        color = uiColors.textPrimary,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = FontFamily.Monospace,
                                                        style = MaterialTheme.typography.headlineSmallEmphasized,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Spacer(modifier = Modifier.height(3.dp))
                                                    Text(
                                                        text = "${formatTo12Hour(item.startTime)} - ${formatTo12Hour(item.endTime)}",
                                                        color = uiColors.textPrimary.copy(alpha = 0.85f),
                                                        style = MaterialTheme.typography.labelLargeEmphasized,
                                                        fontFamily = FontFamily.Monospace,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Spacer(modifier = Modifier.height(3.dp))
                                                    Text(
                                                        text = item.room ?: "No Room",
                                                        color = uiColors.textPrimary,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = FontFamily.Monospace,
                                                        style = MaterialTheme.typography.titleMediumEmphasized,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            } else {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Column(
                                                        verticalArrangement = Arrangement.Center,
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .padding(vertical = 6.dp)
                                                            .weight(1f)
                                                    ) {
                                                        Text(
                                                            text = item.subject,
                                                            color = uiColors.textPrimary,
                                                            fontWeight = FontWeight.Bold,
                                                            fontFamily = FontFamily.Monospace,
                                                            style = MaterialTheme.typography.headlineSmallEmphasized,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Text(
                                                            text = "${formatTo12Hour(item.startTime)} - ${formatTo12Hour(item.endTime)}",
                                                            color = uiColors.textPrimary.copy(alpha = 0.85f),
                                                            style = MaterialTheme.typography.labelLargeEmphasized,
                                                            fontFamily = FontFamily.Monospace,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                    Text(
                                                        text = item.room ?: "No Room",
                                                        color = uiColors.textPrimary,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = FontFamily.Monospace,
                                                        style = MaterialTheme.typography.titleMediumEmphasized,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .height(500.dp)
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
                                    ) {
                                        PandaSleepingAnimation()
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(
                                modifier = Modifier.height(
                                    86.dp + WindowInsets.navigationBars.asPaddingValues()
                                        .calculateBottomPadding()
                                )
                            )
                        }
                    }
                }
            }
        }

        // Top Bar
        Box(
            modifier = Modifier
                .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                    blurRadius = 15.dp
                    noiseFactor = 0.05f
                    inputScale = HazeInputScale.Auto
                    alpha = 0.98f
                }
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(
                    modifier = Modifier.height(
                        16.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            contentColor = uiColors.progressAccent
                        ),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        val isSectionFriend = roll.startsWith("SEC:")
                        val sectionFromRoll = if (isSectionFriend) roll.removePrefix("SEC:").substringBefore(":") else ""

                        val titleText = if (isSectionFriend) {
                            val sec = summary?.section?.ifBlank { sectionFromRoll } ?: sectionFromRoll
                            val hasCustomName = summary != null && summary.name.isNotBlank() &&
                                    !summary.name.startsWith("SEC:") && summary.name != sec
                            if (hasCustomName) summary.name else sec.ifBlank { roll }
                        } else {
                            val hasCustomName = summary != null && summary.name.isNotBlank() && summary.name != roll
                            if (hasCustomName) summary.name else roll
                        }

                        Text(
                            text = titleText,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = uiColors.textPrimary,
                            style = MaterialTheme.typography.titleMediumEmphasized,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        val subtitle = if (isSectionFriend) {
                            val sec = summary?.section?.ifBlank { sectionFromRoll } ?: sectionFromRoll
                            val hasCustomName = summary != null && summary.name.isNotBlank() &&
                                    !summary.name.startsWith("SEC:") && summary.name != sec
                            val batchStr = summary?.batch.orEmpty()
                            val batchFormatted = if (batchStr.isNotBlank()) com.kito.feature.schedule.presentation.components.formatBatchYear(batchStr) else ""
                            val electives = listOfNotNull(
                                summary?.elective1?.takeIf { it.isNotBlank() },
                                summary?.elective2?.takeIf { it.isNotBlank() }
                            ).joinToString(", ")
                            when {
                                hasCustomName && batchFormatted.isNotBlank() && electives.isNotBlank() -> "$sec • $batchFormatted • $electives"
                                hasCustomName && batchFormatted.isNotBlank() -> "$sec • $batchFormatted"
                                hasCustomName && electives.isNotBlank() -> "$sec • $electives"
                                hasCustomName -> sec
                                batchFormatted.isNotBlank() && electives.isNotBlank() -> "$batchFormatted • $electives"
                                batchFormatted.isNotBlank() -> batchFormatted
                                electives.isNotBlank() -> electives
                                else -> ""
                            }
                        } else {
                            val rollText = if (titleText != roll) "$roll • " else ""
                            val sub = summary?.subtitleText.orEmpty()
                            if (sub.isNotBlank() && !sub.equals("Details unavailable", ignoreCase = true)) {
                                "$rollText$sub"
                            } else if (titleText != roll) {
                                roll
                            } else {
                                sub
                            }
                        }

                        if (subtitle.isNotBlank()) {
                            Text(
                                text = subtitle,
                                fontFamily = FontFamily.Monospace,
                                color = uiColors.textSecondary,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            sendEmail(
                                to = "kiito.admin@gmail.com",
                                subject = "KIITO Schedule Report - Friend $roll",
                                body = ""
                            )
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            contentColor = Color(0xFFB32727)
                        ),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Report,
                            contentDescription = "Report",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = ButtonGroupDefaults.ConnectedSpaceBetween,
                        alignment = Alignment.CenterHorizontally
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(weekDays) { index, label ->
                        ToggleButton(
                            modifier = Modifier.then(
                                if (index == currentPage && pagerState.currentPage != index) {
                                    Modifier
                                        .zIndex(-2f)
                                        .dropShadow(
                                            shape = ButtonGroupDefaults.connectedMiddleButtonShapes().shape,
                                            shadow = Shadow(
                                                radius = 20.dp,
                                                color = uiColors.accentOrangeStart
                                            )
                                        )
                                } else {
                                    Modifier
                                }
                            ),
                            checked = pagerState.currentPage == index,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            shapes = when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                weekDays.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            },
                            colors = ToggleButtonDefaults.toggleButtonColors(
                                containerColor = uiColors.cardBackground,
                                checkedContainerColor = uiColors.progressAccent,
                            )
                        ) {
                            Text(
                                text = label.toString(),
                                style = MaterialTheme.typography.bodySmallEmphasized,
                                color = uiColors.textPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
