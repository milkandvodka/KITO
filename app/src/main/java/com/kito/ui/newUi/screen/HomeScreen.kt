package com.kito.ui.newUi.screen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.kito.R
import com.kito.ScheduleActivity
import com.kito.ui.components.AboutELabsDialog
import com.kito.ui.components.OverallAttendanceCard
import com.kito.ui.components.ScheduleCard
import com.kito.ui.components.UIColors
import com.kito.ui.components.UpcomingEventCard
import com.kito.ui.components.state.SyncUiState
import com.kito.ui.navigation.Destinations
import com.kito.ui.newUi.viewmodel.HomeViewmodel
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    viewmodel: HomeViewmodel = hiltViewModel(),
    navController: NavHostController
) {
    var showAboutDialog by remember { mutableStateOf(false) }
    val uiColors = UIColors()
    val name by viewmodel.name.collectAsState()
    val sapLoggedIn by viewmodel.sapLoggedIn.collectAsState()
    val averageAttendancePercentage by viewmodel.averageAttendancePercentage.collectAsState()
    val schedule by viewmodel.schedule.collectAsState()
    val syncState by viewmodel.syncState.collectAsState()
    val context = LocalContext.current
    val hazeState = rememberHazeState()
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(Unit) {
        delay(1000)
        viewmodel.syncOnStartup()
    }

    LaunchedEffect(Unit) {
        viewmodel.syncEvents.collect { event ->
            when (event) {
                is SyncUiState.Success -> {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                    Toast.makeText(
                        context,
                        "Sync completed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is SyncUiState.Error -> {
                    haptic.performHapticFeedback(HapticFeedbackType.Reject)
                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
                is SyncUiState.Loading -> {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .hazeSource(hazeState)
    ) {
        LazyColumn() {
            item {
                Spacer(
                    modifier = Modifier.height(
                        8.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                    )
                )
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
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
                            style = MaterialTheme.typography.titleMediumEmphasized
                        )
                        Text(
                            text = "${name.trim().substringBefore(" ")} 👋",
                            color = uiColors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.headlineLargeEmphasized,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = { showAboutDialog = !showAboutDialog },
                        modifier = Modifier.size(60.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.e_labs_logo),
                            contentDescription = "Logo",
                        )
                    }
                }
            }

                item {
                    AnimatedVisibility(syncState is SyncUiState.Loading) {
                        Column{
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearWavyProgressIndicator(
                                color = uiColors.accentOrangeStart,
                                trackColor = uiColors.progressAccent,
                                modifier = Modifier.fillMaxWidth(),
                                waveSpeed = 5.dp,
                                wavelength = 70.dp,
                            )
                        }
                    }
                }


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
                        onClick = {
                            val subject = Uri.encode("KIITO Schedule Report")
                            val body = Uri.encode("")
                            val intent = Intent(
                                Intent.ACTION_SENDTO,
                                Uri.parse("mailto:elabs.kiito@gmail.com?subject=$subject&body=$body")
                            )

                            context.startActivity(intent)
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color(0xFFB32727)
                        ),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Report,
                            contentDescription = "Report",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            val intent = Intent(context, ScheduleActivity::class.java)
                            context.startActivity(intent)
                        },
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
                ScheduleCard(
                    colors = uiColors,
                    schedule = schedule,
                    onCLick = {
                        val intent = Intent(context, ScheduleActivity::class.java)
                        context.startActivity(intent)
                    }
                )
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
                        onClick = {
                            navController.navigate(Destinations.Attendance) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
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
                    colors = uiColors,
                    sapLoggedIn = sapLoggedIn,
                    percentage = averageAttendancePercentage,
                    onClick = {
                        navController.navigate(Destinations.Profile) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigate = {
                        navController.navigate(Destinations.Attendance){
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            if (false) {
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
                item{
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically()
                    ) {
                        Text("Updating")
                    }
                }

                item {
                    Spacer(Modifier.height(8.dp))
                }

                item {
                    UpcomingEventCard()
                }
            }

            item {
                Spacer(
                    modifier = Modifier.height(
                        86.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    )
                )
            }
        }
    }
    if (showAboutDialog) {
        AboutELabsDialog(
            onDismiss = { showAboutDialog = false },
            context = LocalContext.current,
            hazeState = hazeState
        )
    }
}
