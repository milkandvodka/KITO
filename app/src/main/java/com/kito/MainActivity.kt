package com.kito

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kito.data.local.preferences.newpreferences.PrefsRepository
import com.kito.ui.newUi.MainUI
import com.kito.ui.theme.KitoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefs: PrefsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            var keepOnScreenCondition by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                val onboardingDone = prefs.isOnboardingDone()
                val isUserSetupDone = prefs.isUserSetupDone()
                if (!onboardingDone) {
                    startActivity(Intent(this@MainActivity, OnBoardingActivity::class.java)
                        .apply {
                            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        }
                    )
                    finish()
                    keepOnScreenCondition = false
                } else if (!isUserSetupDone) {
                    startActivity(Intent(this@MainActivity, UserSetupActivity::class.java)
                        .apply {
                            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        }
                    )
                    finish()
                    keepOnScreenCondition = false
                }else {
                    keepOnScreenCondition = false
                }
            }
            splashScreen.setKeepOnScreenCondition { keepOnScreenCondition }
            KitoTheme {
                MainUI()
            }
        }
    }
}