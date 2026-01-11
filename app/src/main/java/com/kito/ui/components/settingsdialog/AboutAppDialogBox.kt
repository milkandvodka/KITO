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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kito.R
import com.kito.ui.components.UIColors
import com.kito.ui.components.openLink
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalHazeMaterialsApi::class,
    ExperimentalHazeApi::class
)
@Composable
fun AboutAppDialogBox(
    onDismiss: () -> Unit,
    hazeState: HazeState
) {
    val uiColors = UIColors()
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.Transparent,
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
                        .verticalScroll(rememberScrollState())
                ) {

                    Text(
                        text = """
                            KIITO is a student-built utility app designed to help users easily view and track their academic attendance and timetable information in one place.

                            The app is developed and maintained by members of the eLabs technical society as part of a collaborative, learning-driven initiative. KIITO is not an official university application and is intended solely for personal and informational use.
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = uiColors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Key Highlights",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = """
                            • Quick and convenient access to attendance data
                            • Clean and modern UI built with Jetpack Compose
                            • Privacy-first and minimal data usage
                            • Open-source and community-driven
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Privacy & Security",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = """
                            KIITO does not store your login credentials permanently and does not claim ownership of any personal information. Sensitive data is handled only as required for functionality and is never shared with third parties.
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Open-Source Project",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = """
                            KIITO is an open-source project. The source code is publicly available for learning, review, and contribution under the applicable open-source license.
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Devs",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Shanu",
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMediumEmphasized
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DeveloperLink(
                            SocialMedia.LinkedIn,
                            "https://www.linkedin.com/in/shanudevcodes/",
                            context
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DeveloperLink(
                            SocialMedia.Github,
                            "https://github.com/ShanuDevCodes",
                            context
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Subham Shah",
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMediumEmphasized

                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DeveloperLink(
                            SocialMedia.LinkedIn,
                            "https://www.linkedin.com/in/subham-shah-51b29a343/",
                            context
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DeveloperLink(
                            SocialMedia.Github,
                            "https://github.com/milkandvodka",
                            context
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Pratyusha Mohanty",
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMediumEmphasized
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DeveloperLink(
                            SocialMedia.LinkedIn,
                            "https://www.linkedin.com/in/pratyusha12792/",
                            context
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DeveloperLink(
                            SocialMedia.Github,
                            "https://github.com/pratyusha12792",
                            context
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Abinash Mohanty",
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMediumEmphasized
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DeveloperLink(
                            SocialMedia.LinkedIn,
                            "https://www.linkedin.com/in/abinash-mohanty-/",
                            context
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DeveloperLink(
                            SocialMedia.Github,
                            "https://github.com/abinashmohanty8059",
                            context
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Yogisha Rani",
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMediumEmphasized
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DeveloperLink(
                            SocialMedia.LinkedIn,
                            "https://www.linkedin.com/in/yogisha-rani-1382a7381/",
                            context
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DeveloperLink(
                            SocialMedia.Github,
                            "https://github.com/LostRunes",
                            context
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Harsh Singh",
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMediumEmphasized
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DeveloperLink(
                            SocialMedia.LinkedIn,
                            "https://www.linkedin.com/in/harsh-singh-60a7b432b",
                            context
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DeveloperLink(
                            SocialMedia.Github,
                            "https://github.com/harshkumarsingh12",
                            context
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Disclaimer",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = """
                            KIITO relies on external academic systems and network availability. While efforts are made to ensure accuracy, users are encouraged to verify important information through official sources.
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Contact",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
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

@Composable
private fun DeveloperLink(
    socialMedia: SocialMedia,
    url: String,
    context: android.content.Context
) {
    IconButton(
        onClick = { openLink(context, url) },
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        ),
    ) {
        Icon(
            painter = painterResource(id = if (socialMedia == SocialMedia.LinkedIn)R.drawable.linkedin else R.drawable.github),
            contentDescription = "Logo",
            modifier = Modifier.size(24.dp),
            tint = if (socialMedia == SocialMedia.LinkedIn) Color.Unspecified else Color.White
        )
    }
}
enum class SocialMedia{
    LinkedIn,
    Github
}