package com.kito.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.lazy.LazyColumn
import com.kito.data.local.db.studentsection.StudentSectionEntity

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScheduleCard(
    colors: UIColors,
    schedule: List<StudentSectionEntity>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(205.dp)
            .background(
                colors.cardBackground,
                RoundedCornerShape(22.dp)
            )
            .padding( horizontal = 16.dp, vertical = 8.dp)
    ) {
        if(schedule.isNotEmpty()) {
            items(schedule) { schedule ->
                ScheduleItem(
                    schedule.subject,
                    "${schedule.startTime} - ${schedule.endTime}",
                    colors
                )
            }
        }else{
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth().fillParentMaxHeight()
                ){
                    Text(
                        text = "No Class Today",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelMediumEmphasized
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScheduleItem(
    title: String,
    time: String,
    colors: UIColors
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(32.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(colors.accentOrangeStart, colors.accentOrangeEnd)
                    ),
                    RoundedCornerShape(2.dp)
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.padding(vertical = 6.dp)) {
            Text(
                text = title,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.labelLargeEmphasized
            )
            Text(
                text = time,
                color = colors.textPrimary.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelSmallEmphasized,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
