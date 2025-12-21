package com.kito.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UpcomingEventCard(){
    val colors = UIColors()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                colors.cardBackground,
                RoundedCornerShape(22.dp)
            )
            .padding( horizontal = 16.dp, vertical = 8.dp)
    ) {
        ScheduleItem("IOtronics", "9 May", colors)
        ScheduleItem("Kiit Fest", "12 Jan", colors)
    }
}