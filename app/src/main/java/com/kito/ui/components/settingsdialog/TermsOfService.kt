package com.kito.ui.components.settingsdialog

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kito.ui.components.UIColors
import com.kito.ui.components.openLink
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class)
@Composable
fun TermsOfServiceDialog(
    onDismiss: () -> Unit,
    hazeState: HazeState
) {
    val uiColors = UIColors()
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.Transparent,
            tonalElevation = 6.dp,
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .shadow(
                    elevation = 24.dp,
                    spotColor = uiColors.progressAccent
                )
                .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                    blurRadius = 35.dp
                    noiseFactor = 0.00f
                    inputScale = HazeInputScale.Auto
                    alpha = 0.98f
                    tints = listOf(HazeTint(uiColors.cardBackground.copy(alpha = 0.15f)))
                },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Terms of Service",
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
1. Use of the App

KITO is provided for personal, non-commercial use. You agree to use the App solely for its intended purpose of accessing attendance and timetable-related information and in accordance with applicable laws, institutional guidelines, and these Terms.

You agree not to:
• Use the App for any unlawful or prohibited purpose
• Attempt to disrupt, interfere with, or misuse the App’s functionality
• Use the App in a manner that could negatively impact its availability or integrity

2. Privacy Policy

Your use of the App is governed by our Privacy Policy, which is available within the App. By using KITO, you acknowledge that you have read, understood, and agreed to the Privacy Policy.

3. User Information and Content

Any information or content you voluntarily provide while using the App remains your property. KITO does not claim ownership over your personal information or credentials.

4. App Management

KITO is an open-source project maintained under the eLabs technical society.

The App is managed as part of a collaborative, student-driven technical initiative. Any operational matters, improvements, or concerns related to the App are handled through the eLabs framework and its established processes.

5. Intellectual Property

Unless otherwise stated, all original branding, design elements, and code contributions associated with KITO are protected under applicable intellectual property laws.

As an open-source project:
• The source code is publicly available for inspection and contribution
• Any reuse or redistribution must comply with the applicable open-source license

6. Disclaimer

KITO is an unofficial utility application intended to assist users by providing convenient access to attendance and timetable-related information.

While reasonable efforts are made to ensure proper functioning and reliability, the App may rely on external systems, network connectivity, or institutional portals that are beyond direct control. Users are encouraged to verify critical or time-sensitive information through official sources when necessary.

7. Governing Law

These Terms shall be governed by and construed in accordance with the laws of India. Any matters arising in relation to these Terms shall be subject to the jurisdiction of the competent courts in Odisha, India.

8. Changes to These Terms

We believe in transparency and responsible development. Any updates to these Terms will be made thoughtfully and communicated clearly through the App or its store listing. Continued use of the App after such updates indicates acceptance of the revised Terms.

9. Contact Us

If you have any questions or concerns regarding these Terms of Service, please contact us at:

                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.2
                    )
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:elabs.kiito@gmail.com")
                                    putExtra(Intent.EXTRA_SUBJECT, "KIITO App Feedback")
                                    putExtra(Intent.EXTRA_TEXT, "")
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.White,
                                containerColor = Color.White.copy(alpha = 0.08f)
                            )
                        ) {
                            Text(
                                text = "Email",
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        TextButton(
                            onClick = {
                                openLink(context, "https://elabskiit.in/")
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.White,
                                containerColor = Color.White.copy(alpha = 0.08f)
                            )
                        ) {
                            Text(
                                text = "Website",
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}