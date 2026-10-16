package com.kito.feature.calendar.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.UIColors
import com.kito.feature.calendar.domain.model.CalendarEntry
import com.kito.feature.schedule.presentation.components.horizontalCarouselTransition
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.datetime.LocalDate

/** Months reachable on either side of today. */
private const val PAGE_SPAN = 120

/** Swipeable month grid inside an app-styled gradient card with a soft passing shimmer. */
@Composable
fun MonthPager(
    today: LocalDate,
    displayedMonth: Int,
    months: Map<Int, Map<LocalDate, List<CalendarEntry>>>,
    onMonthSettled: (Int) -> Unit,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    enableAnimations: Boolean = true,
) {
    val uiColors = UIColors()
    val haptic = LocalHapticFeedback.current
    val firstIndex = today.monthIndex() - PAGE_SPAN
    val pagerState = rememberPagerState(
        initialPage = (displayedMonth - firstIndex).coerceIn(0, PAGE_SPAN * 2)
    ) { PAGE_SPAN * 2 + 1 }
    val latestOnSettled = rememberUpdatedState(onMonthSettled)
    val shape = RoundedCornerShape(24.dp)

    // Top bar arrows and the Today button move the pager; swipes report back via settledPage.
    LaunchedEffect(displayedMonth) {
        val target = (displayedMonth - firstIndex).coerceIn(0, PAGE_SPAN * 2)
        if (pagerState.currentPage != target) pagerState.animateScrollToPage(target)
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { latestOnSettled.value(firstIndex + it) }
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.drop(1).distinctUntilChanged().collect {
            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
        }
    }

    // A faint light band crosses the card every few seconds (subtle take on JoinELabsBanner's shimmer).
    val sweep = if (enableAnimations) {
        rememberInfiniteTransition(label = "gridShimmer").animateFloat(
            initialValue = -0.6f,
            targetValue = 2.2f,
            animationSpec = infiniteRepeatable(tween(4500, easing = LinearEasing), RepeatMode.Restart),
            label = "gridSweep"
        ).value
    } else null

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(uiColors.cardBackground, Color(0xFF2F222F), Color(0xFF2F222F), uiColors.cardBackgroundHigh)
                )
            )
            .border(
                Dp.Hairline,
                Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.05f))),
                shape
            )
            .drawWithContent {
                drawContent()
                if (sweep != null) {
                    val x = size.width * sweep
                    drawRect(
                        Brush.linearGradient(
                            listOf(Color.Transparent, Color.White.copy(alpha = 0.06f), Color.Transparent),
                            start = Offset(x - size.width * 0.35f, 0f),
                            end = Offset(x, size.height),
                        )
                    )
                }
            }
            .padding(horizontal = 6.dp, vertical = 12.dp)
    ) {
        WeekdayHeaderRow()
        Spacer(Modifier.height(6.dp))
        HorizontalPager(state = pagerState, beyondViewportPageCount = 1) { page ->
            val index = firstIndex + page
            MonthGrid(
                monthIndex = index,
                entriesByDate = months[index].orEmpty(),
                today = today,
                onDayClick = onDayClick,
                enableAnimations = enableAnimations,
                modifier = Modifier.horizontalCarouselTransition(page, pagerState, scale = 0.94f),
            )
        }
    }
}
