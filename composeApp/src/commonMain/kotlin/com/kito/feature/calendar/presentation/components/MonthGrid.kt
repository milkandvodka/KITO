package com.kito.feature.calendar.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.kito.feature.calendar.domain.model.CalendarEntry
import kotlinx.datetime.LocalDate

@Composable
fun MonthGrid(
    monthIndex: Int,
    entriesByDate: Map<LocalDate, List<CalendarEntry>>,
    today: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    enableAnimations: Boolean = true,
) {
    val cells = remember(monthIndex) { buildMonthCells(monthIndex) }
    Column(modifier = modifier.fillMaxWidth()) {
        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    DayCell(
                        date = date,
                        entries = entriesByDate[date].orEmpty(),
                        isToday = date == today,
                        inMonth = date.monthIndex() == monthIndex,
                        onClick = { onDayClick(date) },
                        modifier = Modifier.weight(1f),
                        enableAnimations = enableAnimations,
                    )
                }
            }
        }
    }
}
