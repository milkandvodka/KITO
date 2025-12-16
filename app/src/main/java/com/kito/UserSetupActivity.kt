package com.kito

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kito.data.local.preferences.newpreferences.PrefsRepository
import com.kito.ui.theme.KitoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserSetupActivity : ComponentActivity() {
    @Inject
    lateinit var prefs: PrefsRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KitoTheme {
//                val scope = rememberCoroutineScope()
//                val context = LocalContext.current
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    Button(
//                        onClick = {
//                            scope.launch {
//                                prefs.setUserSetupDone()
//                                context.startActivity(Intent(context, MainActivity::class.java))
//                                finish()
//                            }
//                        },
//                    ) {
//                        Text("Done")
//                    }
//                }
            }
        }
    }
}