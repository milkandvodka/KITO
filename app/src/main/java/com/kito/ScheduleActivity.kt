package com.kito

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.kito.ui.newUi.screen.ScheduleScreen
import com.kito.ui.theme.KitoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleActivity : ComponentActivity() {

    private fun todayKey(): String =
        when (java.time.LocalDate.now().dayOfWeek) {
            java.time.DayOfWeek.MONDAY -> "MON"
            java.time.DayOfWeek.TUESDAY -> "TUE"
            java.time.DayOfWeek.WEDNESDAY -> "WED"
            java.time.DayOfWeek.THURSDAY -> "THU"
            java.time.DayOfWeek.FRIDAY -> "FRI"
            java.time.DayOfWeek.SATURDAY -> "SAT"
            else -> "MON"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val today = todayKey()
        Log.d("Today_FLOW", today)
        setContent {
            KitoTheme {
                Surface {
                    ScheduleScreen(
                        page = when (today) {
                            "MON" -> 0
                            "TUE" -> 1
                            "WED" -> 2
                            "THU" -> 3
                            "FRI" -> 4
                            "SAT" -> 5
                            else -> 5
                        }
                    )
                }
            }
        }
    }
}