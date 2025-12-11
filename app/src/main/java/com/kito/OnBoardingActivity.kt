package com.kito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kito.data.local.preferences.newpreferences.PrefsRepository
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
                Box(
                    contentAlignment = Alignment.Center
                ){
                    OnBoardingScreen(prefs)
                }
            }
        }
    }
}