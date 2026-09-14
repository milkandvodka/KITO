package com.kito.feature.schedule.presentation

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kito.core.common.util.currentLocalDateTime
import com.kito.core.designsystem.ExpressiveEasing
import com.kito.core.designsystem.UIColors
import com.kito.core.platform.sendEmail
import com.kito.core.presentation.components.animation.PandaSleepingAnimation
import com.kito.feature.schedule.domain.model.ScheduleItem
import com.kito.feature.schedule.presentation.components.ScheduleClassCard
import com.kito.feature.schedule.presentation.components.todayKey
import com.kito.feature.schedule.presentation.components.isClassUpcoming
import com.kito.feature.schedule.presentation.components.isClassOngoing
import com.kito.feature.schedule.presentation.components.horizontalCarouselTransition
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import kotlin.random.Random

@OptIn(ExperimentalHazeApi::class, ExperimentalHazeMaterialsApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun ScheduleContent(
    schedule: Map<WeekDay, List<ScheduleItem>>,
    onBack: () -> Unit,
    enableAnimations: Boolean = true,
    modifier: Modifier = Modifier
) {
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
    val uiColors = UIColors()
    val coroutineScope = rememberCoroutineScope()
    val hazeState = rememberHazeState()
    val weekDays = WeekDay.entries
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {
            weekDays.size
        }
    )
    val haptics = LocalHapticFeedback.current

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
            .drop(1) // skip initial emission
            .distinctUntilChanged()
            .collect {
                haptics.performHapticFeedback(
                    HapticFeedbackType.Confirm
                )
            }
    }
    LaunchedEffect(Unit) {
        delay(100)
        pagerState.animateScrollToPage(
            page = currentPage,
            animationSpec = tween(
                durationMillis = 800,
                easing = ExpressiveEasing.Emphasized
            )
        )
    }
    Box(
        modifier = modifier
            .background(Color(0xFF121116))
            .hazeSource(hazeState)
            .semantics { testTag = "schedule_content" }
    ) {
        HorizontalPager(
            contentPadding = PaddingValues(
                start = 28.dp,
                end = 28.dp,
            ),
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
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
                                .calculateTopPadding() + 132.dp
                        )
                    )
                }
                if (daySchedule.isNotEmpty()) {
                    itemsIndexed(daySchedule) { index, item ->
                        ScheduleClassCard(
                            item = item,
                            index = index,
                            listSize = daySchedule.size,
                            page = page,
                            currentPage = currentPage,
                            today = today,
                            now = now,
                            uiColors = uiColors,
                            meshColorAnimators = meshColorAnimators,
                            animatedPointMid = animatedPointMid,
                            animatedPointTop = animatedPointTop,
                            isClassUpcoming = { start, n -> isClassUpcoming(start, now = n) },
                            isClassOngoing = ::isClassOngoing
                        )
                    }
                } else {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { testTag = "schedule_empty" },
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .height(600.dp)
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
                item{
                    Spacer(
                        modifier = Modifier.height(
                            86.dp + WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding()
                        )
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                    blurRadius = 15.dp
                    noiseFactor = 0.05f
                    inputScale = HazeInputScale.Auto
                    alpha = 0.98f
                }
                .fillMaxWidth(),
        ) {
            Spacer(
                modifier = Modifier.height(
                    16.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                IconButton(
                    onClick = {
                        onBack()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.08f),
                        contentColor = uiColors.progressAccent
                    ),
                    modifier = Modifier.size(32.dp)
                ){
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Report",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Schedule",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = uiColors.textPrimary,
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    modifier = Modifier
                        .weight(1f)
                )
                if (false) {
                    IconButton(
                        onClick = {

                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            contentColor = uiColors.progressAccent
                        ),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (true) {
                                Icons.Default.NotificationsActive
                            } else {
                                Icons.Outlined.NotificationsOff
                            },
                            contentDescription = "notifications",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                IconButton(
                    onClick = {
                        sendEmail(
                            to = "kiito.admin@gmail.com",
                            subject = "KIITO Schedule Report",
                            body = ""
                        )
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.08f),
                        contentColor = Color(0xFFB32727)
                    ),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Report,
                        contentDescription = "Report",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
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
                        modifier = Modifier
                            .then(
                                if (index == currentPage && pagerState.currentPage != index){
                                    Modifier
                                        .zIndex(
                                            -2f
                                        )
                                        .dropShadow(
                                            shape = ButtonGroupDefaults.connectedMiddleButtonShapes().shape,
                                            shadow = Shadow(
                                                radius = 20.dp,
                                                color = uiColors.accentOrangeStart
                                            )
                                        )
                                }else{
                                    Modifier
                                }
                            ),
                        checked = pagerState.currentPage == index,
                        onCheckedChange = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        shapes =
                        when (index) {
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
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
private fun ScheduleContentPreview() {
    ScheduleContent(
        schedule = emptyMap(),
        onBack = {}
    )
}
