package com.kito.ui.components.settingsdialog

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
                    val context = LocalContext.current

                    val annotatedText = buildAnnotatedString {
                        append("KITO is a student-built utility app designed to help users easily view and track their academic attendance and timetable information in one place.\n\n")
                        append("The app is developed and maintained by members of the eLabs technical society as part of a collaborative, learning-driven initiative. KITO is not an official university application and is intended solely for personal and informational use.\n\n")
                        append("Key Highlights\n")
                        append("• Quick and convenient access to attendance data\n")
                        append("• Clean and modern user interface built with Jetpack Compose\n")
                        append("• Focus on privacy-first and minimal data usage\n")
                        append("• Open-source and community-driven development\n\n")
                        append("Privacy & Security\n")
                        append("KITO does not store your login credentials permanently and does not claim ownership of any personal information. Sensitive data is handled only as required for functionality and is never shared with third parties.\n\n")
                        append("Open-Source Project\n")
                        append("KITO is an open-source project. The source code is publicly available for learning, review, and contribution under the applicable open-source license.\n\n")

                        // Add the Devs section with clickable links
                        append("Devs\n")
                        append("Meet the developers behind KITO:\n\n")

                        // Shanu - LinkedIn (correct approach)
                        pushStringAnnotation(tag = "URL", annotation = "https://www.linkedin.com/in/shanudevcodes/")
                        val startShanu = length
                        append("Shanu (https://www.linkedin.com/in/shanudevcodes/)\n")
                        val endShanu = startShanu + "Shanu (".length + "https://www.linkedin.com/in/shanudevcodes/".length - 1
                        addStyle(
                            style = SpanStyle(
                                color = androidx.compose.ui.graphics.Color.Blue,
                                textDecoration = TextDecoration.Underline
                            ),
                            start = startShanu + 6, // Position after "Shanu "
                            end = endShanu
                        )
                        pop()

                        // Subham Shah - LinkedIn
                        pushStringAnnotation(tag = "URL", annotation = "https://www.linkedin.com/in/subham-shah-51b29a343/")
                        val startSubham = length
                        append("Subham Shah (https://www.linkedin.com/in/subham-shah-51b29a343/)\n")
                        val endSubham = startSubham + "Subham Shah (".length + "https://www.linkedin.com/in/subham-shah-51b29a343/".length - 1
                        addStyle(
                            style = SpanStyle(
                                color = androidx.compose.ui.graphics.Color.Blue,
                                textDecoration = TextDecoration.Underline
                            ),
                            start = startSubham + 12, // Position after "Subham Shah "
                            end = endSubham
                        )
                        pop()

                        // Pratyusha Mohanty - LinkedIn
                        pushStringAnnotation(tag = "URL", annotation = "https://www.linkedin.com/in/pratyusha12792/")
                        val startPratyusha = length
                        append("Pratyusha Mohanty (https://www.linkedin.com/in/pratyusha12792/)\n")
                        val endPratyusha = startPratyusha + "Pratyusha Mohanty (".length + "https://www.linkedin.com/in/pratyusha12792/".length - 1
                        addStyle(
                            style = SpanStyle(
                                color = androidx.compose.ui.graphics.Color.Blue,
                                textDecoration = TextDecoration.Underline
                            ),
                            start = startPratyusha + 17, // Position after "Pratyusha Mohanty "
                            end = endPratyusha
                        )
                        pop()

                        // Abinash Mohanty - LinkedIn
                        pushStringAnnotation(tag = "URL", annotation = "https://www.linkedin.com/in/abinash-mohanty-/")
                        val startAbinash = length
                        append("Abinash Mohanty (https://www.linkedin.com/in/abinash-mohanty-/)\n")
                        val endAbinash = startAbinash + "Abinash Mohanty (".length + "https://www.linkedin.com/in/abinash-mohanty-/".length - 1
                        addStyle(
                            style = SpanStyle(
                                color = androidx.compose.ui.graphics.Color.Blue,
                                textDecoration = TextDecoration.Underline
                            ),
                            start = startAbinash + 16, // Position after "Abinash Mohanty "
                            end = endAbinash
                        )
                        pop()

                        // Yogisha Rani - LinkedIn
                        pushStringAnnotation(tag = "URL", annotation = "https://www.linkedin.com/in/yogisha-rani-1382a7381/")
                        val startYogisha = length
                        append("Yogisha Rani (https://www.linkedin.com/in/yogisha-rani-1382a7381/)\n")
                        val endYogisha = startYogisha + "Yogisha Rani (".length + "https://www.linkedin.com/in/yogisha-rani-1382a7381/".length - 1
                        addStyle(
                            style = SpanStyle(
                                color = androidx.compose.ui.graphics.Color.Blue,
                                textDecoration = TextDecoration.Underline
                            ),
                            start = startYogisha + 14, // Position after "Yogisha Rani "
                            end = endYogisha
                        )
                        pop()

                        append("\n")

                        append("Disclaimer\n")
                        append("KITO relies on external academic systems and network availability. While efforts are made to ensure accuracy, users are encouraged to verify important or time-sensitive information through official institutional sources.\n\n")
                        append("Contact\n")
                        append("For feedback, issues, or contributions, feel free to reach out:\n\n")
                        append("📧 elabs.kiito@gmail.com")
                    }

                    ClickableText(
                        text = annotatedText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.2,
                            color = uiColors.textPrimary
                        ),
                        onClick = { offset ->
                            annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                .firstOrNull()?.let { span ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(span.item))
                                    context.startActivity(intent)
                                }
                        }
                    )
                }
            }
        }
    }
}