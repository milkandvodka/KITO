package com.kito.ui.components.settingsdialog

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kito.ui.components.UIColors

@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit,
) {
    val uiColor = UIColors()
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = uiColor.cardBackground,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(20.dp)
        ) {
            Text(
                text = "Privacy Policy",
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
Welcome to KITO. Your privacy is important to us, and we are committed to protecting it. This Privacy Policy explains how KITO collects, uses, and safeguards information when you use the app.

Information We Collect

1. Information You Provide

When you use KITO, you may voluntarily provide certain information such as:
• Name
• Roll Number
• Login credentials for the portal (optional)

Important:
Login credentials, if provided, are stored securely on your device using industry-standard encryption.
These credentials are never transmitted, logged, or shared with any external servers and remain strictly local to your device.

2. Information Automatically Collected

When you use the app, limited technical information may be collected automatically, including:
• Device type
• Operating system version
• App version

This information is used only for app stability, compatibility, and debugging purposes.

We do not collect or track:
• Usage behavior
• Activity logs
• Personal analytics

3. Third-Party Services

KITO uses Supabase exclusively for:
• Fetching and displaying timetable and student-related data

Supabase is not used for tracking, advertising, or analytics.
No login credentials or sensitive information are shared with Supabase services.

How We Use Your Information

The information handled by the app is used strictly to:
• Provide core app functionality such as attendance and timetable access
• Maintain and improve app performance
• Detect and resolve technical issues

Data Security

We take reasonable technical measures to ensure your data remains secure.
• Sensitive information is stored locally on your device using encrypted storage
• Credentials never leave your device
• No personal data is stored on external servers
• We do not maintain any user database

Open-Source & Transparency

KITO is an open-source project.
The source code is publicly available for anyone to inspect, audit, or contribute to.

This transparency ensures accountability and allows users to verify that their data never leaves their device.

Sharing of Information

We do not:
• Sell your data
• Share your data with advertisers
• Transfer your data to third parties for marketing, profiling, or analytics

Your Choices and Control

Since KITO does not maintain any server-side user data:
• Uninstalling the app removes all locally stored data
• Reinstalling the app requires re-entering credentials

Changes to This Privacy Policy

This Privacy Policy may be updated occasionally. Any changes will be reflected transparently within the app or its store listing.

Contact Us

If you have any questions or concerns regarding this Privacy Policy, please contact us at:

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