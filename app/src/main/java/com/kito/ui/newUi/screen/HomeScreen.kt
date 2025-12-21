package com.kito.ui.newUi.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kito.ui.components.*

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
) {
    val uiColors = UIColors()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = "Welcome",
                    color = uiColors.progressAccent,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.titleLargeEmphasized
                )
                Text(
                    text = "Pratyusha 👋",
                    color = uiColors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.displaySmallEmphasized
                )
            }
//            IconButton(
//                onClick = {}
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Notifications,
//                    contentDescription = "Notifications",
//                    tint = uiColors.textPrimary
//                )
//            }
        }
        LazyColumn() {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Schedule",
                        color = uiColors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                            contentDescription = "Notifications",
                            tint = uiColors.textPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            // Schedule Section
            item {
                ScheduleCard(uiColors)
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upcoming KIIT Events",
                        color = uiColors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                            contentDescription = "Notifications",
                            tint = uiColors.textPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                UpcomingEventCard()
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Overall Attendance",
                        color = uiColors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                            contentDescription = "Notifications",
                            tint = uiColors.textPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            // Horizontal Cards
            item {
                OverallAttendanceCard(
                    colors = uiColors
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
