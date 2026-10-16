package com.kito.feature.calendar.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.kito.feature.calendar.domain.model.CalendarEntry
import dev.chrisbanes.haze.HazeState
import kotlinx.datetime.LocalDate

/** Frosted dialog listing everything on [date]; same glass recipe as AttendanceDialog. */
@Composable
fun DayEventsDialog(
    date: LocalDate,
    entries: List<CalendarEntry>,
    classes: List<CalendarEntry>,
    isLoadingClasses: Boolean,
    hazeState: HazeState,
    onDismiss: () -> Unit,
    enableAnimations: Boolean = true,
) {
    Dialog(onDismissRequest = onDismiss) {
        DayEventsDialogBody(date, entries, classes, isLoadingClasses, hazeState, onDismiss, enableAnimations)
    }
}
