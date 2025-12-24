package com.kito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kito.data.local.preferences.PrefsRepository
import com.kito.ui.newUi.screen.OnBoardingScreen
import com.kito.ui.theme.KitoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingActivity : ComponentActivity() {
    @Inject
    lateinit var prefs: PrefsRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KitoTheme {
                Surface {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF131621),
                                    Color(0xFF0A0C12)
                                )
                            )
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        OnBoardingScreen(prefs)
                    }
                }
            }
        }
    }
}