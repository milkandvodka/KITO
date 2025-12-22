package com.kito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kito.ui.newUi.screen.UserSetupScreen
import com.kito.ui.theme.KitoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserSetupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KitoTheme {
                Box(
                    modifier = Modifier.background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF131621),
                                Color(0xFF0A0C12)
                            )
                        )
                    )
                ) {
                    UserSetupScreen()
                }
            }
        }
    }
}