package com.kito.ui.components.settingsdialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kito.ui.components.UIColors

@Composable
fun AboutAppDialogBox(
    onDismiss:() -> Unit
) {
    val uiColors = UIColors()
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = uiColors.cardBackground,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "About App",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = """
About KITO

KITO is a student-built utility app designed to help users easily view and track their academic attendance and timetable information in one place.

The app is developed and maintained by members of the eLabs technical society as part of a collaborative, learning-driven initiative. KITO is not an official university application and is intended solely for personal and informational use.

Key Highlights
• Quick and convenient access to attendance data
• Clean and modern user interface built with Jetpack Compose
• Focus on privacy-first and minimal data usage
• Open-source and community-driven development

Privacy & Security
KITO does not store your login credentials permanently and does not claim ownership of any personal information. Sensitive data is handled only as required for functionality and is never shared with third parties.

Open-Source Project
KITO is an open-source project. The source code is publicly available for learning, review, and contribution under the applicable open-source license.

Disclaimer
KITO relies on external academic systems and network availability. While efforts are made to ensure accuracy, users are encouraged to verify important or time-sensitive information through official institutional sources.

Contact
For feedback, issues, or contributions, feel free to reach out:

📧 elabs.kiito@gmail.com
    """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.2
                    )
                }
            }
        }
    }
}