package com.kito

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.kito.data.local.preferences.newpreferences.PrefsRepository
import com.kito.ui.newUi.MainUI
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
            val isUserSetupDone = prefs.isUserSetupDone()
            if (!onboardingDone) {
                startActivity(Intent(this@MainActivity, OnBoardingActivity::class.java))
                finish() // prevent returning to MainActivity
                return@launch
            }else if (!isUserSetupDone){
                startActivity(Intent(this@MainActivity, UserSetupActivity::class.java))
                finish() // prevent returning to MainActivity
                return@launch
            }
            setContent {
                KitoTheme() {
                    MainUI(prefsRepository = prefs)
                }
            }
        }
    }
}