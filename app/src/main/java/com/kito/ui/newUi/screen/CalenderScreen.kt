package com.kito.ui.screen

import androidx.compose.runtime.*
import com.kito.ui.components.*

@Composable
fun CalendarScreen() {

    val colors = UIColors()

    var showDialog by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf(1) }
    var selectedDayName by remember { mutableStateOf("Monday") }

    CalendarMonthUI(colors) { day, name ->
        selectedDay = day
        selectedDayName = name
        showDialog = true
    }

    if (showDialog) {
        DayEventsDialog(
            colors = colors,
            dayNumber = selectedDay,
            dayName = selectedDayName
        ) {
            showDialog = false
        }
    }
}
