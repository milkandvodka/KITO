package com.kito.feature.calendar.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.UIColors
import com.kito.feature.attendance.presentation.components.InstagramPullIndicator
import com.kito.feature.calendar.domain.model.CalendarEntry
import com.kito.feature.calendar.domain.model.CalendarEntryType
import com.kito.feature.calendar.presentation.components.AgendaItemCard
import com.kito.feature.calendar.presentation.components.CalendarShimmer
import com.kito.feature.calendar.presentation.components.CalendarTopBar
import com.kito.feature.calendar.presentation.components.DayEventsDialog
import com.kito.feature.calendar.presentation.components.MonthPager
import com.kito.feature.calendar.presentation.components.OfflinePill
import com.kito.feature.calendar.presentation.components.TodayHeroCard
import com.kito.feature.calendar.presentation.components.staggeredEntrance
import com.kito.feature.calendar.presentation.components.monthIndex
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalendarContent(
    state: CalendarUiState,
    onEvent: (CalendarScreenEvent) -> Unit,
    enableAnimations: Boolean = true,
) {
    val uiColors = UIColors()
    val cardHaze = rememberHazeState()
    val listHaze = rememberHazeState()
    val haptic = LocalHapticFeedback.current
    val pullState = rememberPullToRefreshState()
    val pullOffsetPx = with(LocalDensity.current) { (42.dp * pullState.distanceFraction.coerceIn(0f, 1f)).toPx() }
    val upcoming = state.upcoming
    val todayCount = state.months[state.today.monthIndex()]?.get(state.today).orEmpty().size
    val nextHighlight = upcoming.firstOrNull {
        it.date > state.today && (it.type == CalendarEntryType.EXAM || it.type == CalendarEntryType.HOLIDAY)
    }

    Box(modifier = Modifier.hazeSource(cardHaze)) {
        Box(modifier = Modifier.background(Color(0xFF121116))) {
            PullToRefreshBox(
                state = pullState,
                isRefreshing = state.isRefreshing,
                onRefresh = { onEvent(CalendarScreenEvent.Refresh) },
                indicator = {},
                modifier = Modifier.semantics { testTag = "calendar_content" },
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(
                        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 46.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(2.5.dp),
                    modifier = Modifier
                        .graphicsLayer { translationY = pullOffsetPx }
                        .hazeSource(listHaze)
                        .fillMaxSize()
                        .semantics { testTag = "calendar_list" }
                        .padding(horizontal = 16.dp),
                ) {
                    item { Spacer(Modifier.height(20.dp)) }
                    if (state.isOffline) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(bottom = 8.dp), contentAlignment = Alignment.Center) {
                                OfflinePill()
                            }
                        }
                    }
                    item(key = "today_hero") {
                        TodayHeroCard(
                            today = state.today,
                            todayCount = todayCount,
                            nextHighlight = nextHighlight,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onEvent(CalendarScreenEvent.SelectDate(state.today))
                            },
                            enableAnimations = enableAnimations,
                            modifier = Modifier
                                .padding(bottom = 10.dp)
                                .staggeredEntrance(0, enableAnimations)
                                .semantics { testTag = "calendar_today_hero" }
                        )
                    }
                    item(key = "month_pager") {
                        MonthPager(
                            today = state.today,
                            displayedMonth = state.displayedMonth,
                            months = state.months,
                            onMonthSettled = { onEvent(CalendarScreenEvent.MonthChanged(it)) },
                            onDayClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onEvent(CalendarScreenEvent.SelectDate(it))
                            },
                            enableAnimations = enableAnimations,
                            modifier = Modifier
                                .staggeredEntrance(1, enableAnimations)
                                .semantics { testTag = "calendar_grid" }
                        )
                    }
                    item {
                        Text(
                            text = "Upcoming",
                            color = uiColors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 4.dp, top = 20.dp, bottom = 8.dp)
                        )
                    }
                    when {
                        upcoming.isNotEmpty() -> itemsIndexed(upcoming, key = { _, it -> "${it.id}_${it.date}" }) { index, entry ->
                            AgendaItemCard(
                                entry = entry,
                                index = index,
                                count = upcoming.size,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                    onEvent(CalendarScreenEvent.SelectDate(entry.date))
                                },
                                highlight = index == 0 && entry.type == CalendarEntryType.EXAM &&
                                    state.today.daysUntil(entry.date) <= 7,
                                enableAnimations = enableAnimations,
                                modifier = Modifier
                                    .animateItem()
                                    .staggeredEntrance(index + 2, enableAnimations)
                                    .semantics { testTag = "calendar_agenda_item" }
                            )
                        }
                        state.months.isEmpty() -> item {
                            CalendarShimmer(Modifier.semantics { testTag = "calendar_loading" })
                        }
                        else -> item {
                            Text(
                                text = "Nothing in the next 30 days",
                                fontFamily = FontFamily.Monospace,
                                color = uiColors.textSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 4.dp).semantics { testTag = "calendar_agenda_empty" }
                            )
                        }
                    }
                    item {
                        Spacer(
                            Modifier.height(86.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                        )
                    }
                }
                InstagramPullIndicator(pullState = pullState, isRefreshing = state.isRefreshing)
            }
            CalendarTopBar(
                monthIndex = state.displayedMonth,
                hazeState = listHaze,
                onToday = { onEvent(CalendarScreenEvent.GoToToday) },
                onPrevious = { onEvent(CalendarScreenEvent.MonthChanged(state.displayedMonth - 1)) },
                onNext = { onEvent(CalendarScreenEvent.MonthChanged(state.displayedMonth + 1)) },
            )
        }
    }

    state.selectedDate?.let { date ->
        DayEventsDialog(
            date = date,
            entries = state.selectedDayEntries,
            classes = state.selectedDayClasses,
            isLoadingClasses = state.isLoadingClasses,
            hazeState = cardHaze,
            onDismiss = { onEvent(CalendarScreenEvent.DismissDay) },
            enableAnimations = enableAnimations,
        )
    }
}

@Preview
@Composable
private fun CalendarContentPreview() {
    val today = LocalDate(2026, 10, 8)
    CalendarContent(
        state = CalendarUiState(
            today = today,
            months = mapOf(
                today.monthIndex() to mapOf(
                    LocalDate(2026, 10, 12) to listOf(
                        CalendarEntry("e1", "Mid-sem: DBMS", LocalDate(2026, 10, 12), CalendarEntryType.EXAM, "09:00", "11:00")
                    ),
                    LocalDate(2026, 10, 17) to listOf(
                        CalendarEntry("h1", "Durga Puja", LocalDate(2026, 10, 17), CalendarEntryType.HOLIDAY)
                    ),
                )
            ),
        ),
        onEvent = {},
        enableAnimations = false,
    )
}
