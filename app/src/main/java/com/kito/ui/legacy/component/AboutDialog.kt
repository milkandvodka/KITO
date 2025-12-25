package com.kito.ui.legacy.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val aboutText = """I built this app because the SAP portal experience was honestly frustrating — logging in every single time, selecting the year and session, and dealing with a cluttered UI. So I wanted to create something simple, fast, and genuinely helpful for students.

This app is a lightweight wrapper around the SAP portal. It logs in automatically, extracts the required tokens, fetches your attendance data, parses everything cleanly, and shows it in a way that actually feels usable.

Key goals:

Make attendance tracking effortless

Clearly show who marked your attendance and who didn't

Help you maintain your target attendance

Provide a clean, friendly UI instead of the default portal mess

Privacy first:
All your credentials are stored locally on your device only. Nothing is uploaded anywhere. The app simply automates what you normally do manually on the SAP website.

I'll keep improving it and adding features over time.

If you want to reach out, feel free to mail me at maiatrangihoon@gmail.com.

And if something suddenly stops working — blame the ICT Cell, not me; they keep changing things. 😄

LinkedIn: https://www.linkedin.com/in/subham-shah-51b29a343/"""

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("About This App") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(text = aboutText)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}