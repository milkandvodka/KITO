package com.kito

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.color.ColorProvider
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
import androidx.room.Room
import com.kito.data.local.db.AppDB
import com.kito.data.local.db.studentsection.StudentSectionEntity
import com.kito.di.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NewAppWidget : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TimetableWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        // Listen for system time changes to update the clock every minute
        if (intent.action == Intent.ACTION_TIME_TICK || 
            intent.action == Intent.ACTION_TIME_CHANGED || 
            intent.action == Intent.ACTION_TIMEZONE_CHANGED ||
            intent.action == Intent.ACTION_BOOT_COMPLETED) {
            runBlocking {
                TimetableWidget().updateAll(context)
            }
        }
    }
}

class TimetableWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = Room.databaseBuilder(context, AppDB::class.java, "kito_db").build()
        val studentSectionDao = db.studentSectionDao()

        val rollNo = try {
            context.dataStore.data.map { prefs ->
                prefs[stringPreferencesKey("User_Password")] ?: ""
            }.first()
        } catch (e: Exception) {
            ""
        }

        // Get current day of the week
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
            TimetableWidgetContent(schedule, dayOfWeek, rollNo)
        }
    }

    @Composable
    private fun TimetableWidgetContent(schedule: List<StudentSectionEntity>, day: String, rollNo: String) {
        val bgTop = Color(0xFF1A1423)
        val cardBg = Color(0xFF261E26)
        val textPrimary = Color(0xFFF3EFF3)
        val textSecondary = Color(0xFFCECAD0)
        val accentOrange = Color(0xFFEA850A)

        // Restored real time calculation
        val calendar = Calendar.getInstance()
        val nowInMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
        
        // SimpleDateFormat can sometimes be slightly off due to caching if not careful, 
        // using calendar for maximum precision with the system clock
        val currentTime = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

        // Filter and find current/next classes
        // Rule: 15 minutes before current class ends, move to next.
        val activeIndex = schedule.indexOfFirst { item ->
            val endTimeMinutes = timeToMinutes(item.endTime)
            nowInMinutes < (endTimeMinutes - 15)
        }

        val isDayOver = activeIndex == -1

        GlanceTheme {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(bgTop)
                    .cornerRadius(24.dp)
                    .padding(12.dp)
                    .clickable(actionStartActivity<ScheduleActivity>()) // Redirect to schedule screen
            ) {
                Column(modifier = GlanceModifier.fillMaxSize()) {
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = GlanceModifier.defaultWeight()) {
                            Text(
                                text = day,
                                style = TextStyle(
                                    color = ColorProvider(day = accentOrange, night = accentOrange), 
                                    fontSize = 12.sp, 
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        
                        // Digital Clock
                        Text(
                            text = currentTime,
                            style = TextStyle(
                                color = ColorProvider(day = textSecondary, night = textSecondary),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = GlanceModifier.padding(end = 4.dp)
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(12.dp))

                    when {
                        rollNo.isEmpty() -> {
                            Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Please login in the app",
                                    style = TextStyle(color = ColorProvider(day = textSecondary, night = textSecondary), fontSize = 14.sp)
                                )
                            }
                        }
                        schedule.isEmpty() -> {
                            Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (day == "SUN") "Happy Sunday! 🏖️" else "No classes found",
                                    style = TextStyle(color = ColorProvider(day = textSecondary, night = textSecondary), fontSize = 12.sp)
                                )
                            }
                        }
                        isDayOver -> {
                            Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "No more classes today",
                                    style = TextStyle(color = ColorProvider(day = textSecondary, night = textSecondary), fontSize = 14.sp)
                                )
                            }
                        }
                        else -> {
                            val upcomingSchedule = schedule.subList(activeIndex, schedule.size)
                            
                            LazyColumn(modifier = GlanceModifier.fillMaxWidth()) {
                                itemsIndexed(upcomingSchedule) { index, item ->
                                    Column(modifier = GlanceModifier.fillMaxWidth().padding(bottom = 8.dp)) {
                                        if (index == 0) {
                                            Text(
                                                text = "CURRENT",
                                                style = TextStyle(
                                                    color = ColorProvider(day = accentOrange, night = accentOrange), 
                                                    fontSize = 10.sp, 
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = GlanceModifier.padding(start = 4.dp, bottom = 2.dp)
                                            )
                                        } else if (index == 1) {
                                            Text(
                                                text = "NEXT",
                                                style = TextStyle(
                                                    color = ColorProvider(day = textSecondary, night = textSecondary), 
                                                    fontSize = 10.sp, 
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = GlanceModifier.padding(start = 4.dp, bottom = 2.dp, top = 4.dp)
                                            )
                                        }
                                        
                                        TimetableItem(
                                            item = item,
                                            cardBg = cardBg,
                                            textPrimary = textPrimary,
                                            textSecondary = textSecondary,
                                            accent = if (index == 0) accentOrange else Color.Gray
                                        )
                                    }
                                }
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
        val startTime = formatTime(item.startTime)
        val endTime = formatTime(item.endTime)
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
                    text = startTime,
                    style = TextStyle(
                        color = ColorProvider(day = textPrimary, night = textPrimary), 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = endTime,
                    style = TextStyle(
                        color = ColorProvider(day = textSecondary, night = textSecondary), 
                        fontSize = 10.sp
                    )
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
                        color = ColorProvider(day = textPrimary, night = textPrimary),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
                if (item.room != null) {
                    Text(
                        text = "Room: ${item.room}",
                        style = TextStyle(
                            color = ColorProvider(day = textSecondary, night = textSecondary), 
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }

    private fun formatTime(timeStr: String): String {
        return try {
            val parts = timeStr.split(":")
            if (parts.size >= 2) {
                "${parts[0].trim()}:${parts[1].trim().take(2)}"
            } else {
                timeStr
            }
        } catch (e: Exception) {
            timeStr
        }
    }

    private fun timeToMinutes(timeStr: String): Int {
        return try {
            val parts = timeStr.split(":")
            parts[0].trim().toInt() * 60 + parts[1].trim().take(2).toInt()
        } catch (e: Exception) {
            0
        }
    }
}
