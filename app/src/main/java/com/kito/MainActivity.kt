package com.kito

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.kito.data.local.preferences.newpreferences.PrefsRepository
import com.kito.ui.legacy.screens.AttendanceScreen
import com.kito.ui.newUi.AppUI
import com.kito.ui.newUi.screen.AttendanceListScreen
import com.kito.ui.theme.KitoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefs: PrefsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            val onboardingDone = prefs.isOnboardingDone()

            if (!onboardingDone) {
                startActivity(Intent(this@MainActivity, OnBoardingActivity::class.java))
                finish() // prevent returning to MainActivity
                return@launch
            }
            setContent {
                val navController = rememberNavController()
                KitoTheme() {
                    // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    AttendanceApp(context = this)
//                }
//                   AppUI(navController)
                    AttendanceListScreen()
                }
            }
        }
    }
}