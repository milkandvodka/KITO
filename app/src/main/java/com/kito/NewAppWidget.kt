package com.kito

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.room.Room
import com.kito.data.local.db.AppDB
import com.kito.data.local.db.studentsection.StudentSectionEntity
import java.util.Calendar

class NewAppWidget : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TimetableWidget()
}

class TimetableWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = Room.databaseBuilder(context, AppDB::class.java, "kito_db").build()
        val studentSectionDao = db.studentSectionDao()

        val rollNo = context.getSharedPreferences("androidx.datastore.preferences.app_prefs", Context.MODE_PRIVATE)
            .getString("User_Password", "") ?: ""

        val dayOfWeek = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "MON"
            Calendar.TUESDAY -> "TUE"
            Calendar.WEDNESDAY -> "WED"
            Calendar.THURSDAY -> "THU"
            Calendar.FRIDAY -> "FRI"
            Calendar.SATURDAY -> "SAT"
            else -> "SUN"
        }

        provideContent {
            val schedule by studentSectionDao.getScheduleForStudent(rollNo, dayOfWeek).collectAsState(initial = emptyList())
            TimetableWidgetContent(schedule, dayOfWeek)
        }
    }

    @Composable
    private fun TimetableWidgetContent(schedule: List<StudentSectionEntity>, day: String) {
        val bgTop = Color(0xFF1A1423)
        val cardBg = Color(0xFF261E26)
        val textPrimary = Color(0xFFF3EFF3)
        val textSecondary = Color(0xFFCECAD0)
        val accentOrange = Color(0xFFEA850A)

        val calendar = Calendar.getInstance()
        val nowInMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

        // Filter and find current/next classes
        // Rule: 15 minutes before current class ends, move to next.
        val activeIndex = schedule.indexOfFirst { item ->
            val endTimeMinutes = timeToMinutes(item.endTime)
            nowInMinutes < (endTimeMinutes - 15)
        }

        val currentClass = if (activeIndex != -1) schedule[activeIndex] else null
        val nextClass = if (activeIndex != -1 && activeIndex + 1 < schedule.size) schedule[activeIndex + 1] else null

        GlanceTheme {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(bgTop)
                    .cornerRadius(24.dp)
                    .padding(12.dp)
            ) {
                Column(modifier = GlanceModifier.fillMaxSize()) {
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = GlanceModifier.defaultWeight()) {
                            Text(
                                text = day,
                                style = TextStyle(color = ColorProvider(accentOrange), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            )
                        }
                    }

                    Spacer(modifier = GlanceModifier.height(12.dp))

                    if (currentClass == null) {
                        Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (day == "SUN") "Happy Sunday! 🏖️" else "No more classes today",
                                style = TextStyle(color = ColorProvider(textSecondary), fontSize = 14.sp)
                            )
                        }
                    } else {
                        Column(modifier = GlanceModifier.fillMaxWidth()) {
                            Text(
                                text = "CURRENT",
                                style = TextStyle(color = ColorProvider(accentOrange), fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                modifier = GlanceModifier.padding(start = 4.dp, bottom = 2.dp)
                            )
                            TimetableItem(currentClass, cardBg, textPrimary, textSecondary, accentOrange)

                            if (nextClass != null) {
                                Spacer(modifier = GlanceModifier.height(8.dp))
                                Text(
                                    text = "NEXT",
                                    style = TextStyle(color = ColorProvider(textSecondary), fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    modifier = GlanceModifier.padding(start = 4.dp, bottom = 2.dp)
                                )
                                TimetableItem(nextClass, cardBg, textPrimary, textSecondary, Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun TimetableItem(
        item: StudentSectionEntity,
        cardBg: Color,
        textPrimary: Color,
        textSecondary: Color,
        accent: Color
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(cardBg)
                .cornerRadius(16.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = GlanceModifier.width(60.dp)) {
                Text(
                    text = item.startTime,
                    style = TextStyle(color = ColorProvider(textPrimary), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = item.endTime,
                    style = TextStyle(color = ColorProvider(textSecondary), fontSize = 10.sp)
                )
            }

            Box(
                modifier = GlanceModifier
                    .width(2.dp)
                    .height(30.dp)
                    .background(accent)
            ) {}

            Spacer(modifier = GlanceModifier.width(8.dp))

            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = item.subject,
                    style = TextStyle(
                        color = ColorProvider(textPrimary),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
                if (item.room != null) {
                    Text(
                        text = "Room: ${item.room}",
                        style = TextStyle(color = ColorProvider(textSecondary), fontSize = 11.sp)
                    )
                }
            }
        }
    }

    private fun timeToMinutes(timeStr: String): Int {
        return try {
            val parts = timeStr.split(":")
            parts[0].toInt() * 60 + parts[1].toInt()
        } catch (e: Exception) {
            0
        }
    }
}
