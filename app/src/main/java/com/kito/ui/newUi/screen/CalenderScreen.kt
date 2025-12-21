package com.kito.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kito.ui.components.*

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalendarScreen() {

    val colors = UIColors()

    var showDialog by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf(1) }
    var selectedDayName by remember { mutableStateOf("Monday") }
    Column() {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "KIIT Calender",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
            style = MaterialTheme.typography.titleLargeEmphasized,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        CalendarMonthUI(colors) { day, name ->
            selectedDay = day
            selectedDayName = name
            showDialog = true
        }
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
