package com.kito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kito.ui.newUi.screen.ScheduleScreen
import com.kito.ui.theme.KitoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val today = intent.getStringExtra("TODAY")
        setContent {
            KitoTheme {
                ScheduleScreen(
                    page = when(today){
                        "MON" -> 0
                        "TUE" -> 1
                        "WED" -> 2
                        "THU" -> 3
                        "FRI" -> 4
                        "SAT" -> 5
                        else-> 0
                    }
                )
            }
        }
    }
}