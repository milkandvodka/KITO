package com.kito.ui.newUi.screen

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.glance.appwidget.updateAll
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.kito.R
import com.kito.ScheduleActivity
import com.kito.ui.components.AboutELabsDialog
import com.kito.ui.components.OverallAttendanceCard
import com.kito.ui.components.ScheduleCard
import com.kito.ui.components.UIColors
import com.kito.ui.components.UpcomingEventCard
import com.kito.ui.components.settingsdialog.LoginDialogBox
import com.kito.ui.components.state.SyncUiState
import com.kito.ui.navigation.Destinations
import com.kito.ui.newUi.viewmodel.HomeViewmodel
import com.kito.widget.TimeTableAppWidget
import com.kito.widget.TimetableWidget
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalHazeApi::class,
    ExperimentalHazeMaterialsApi::class
)
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
    val highestAttendancePercentage by viewmodel.highestAttendancePercentage.collectAsState()
    val lowestAttendancePercentage by viewmodel.lowestAttendancePercentage.collectAsState()
    val schedule by viewmodel.schedule.collectAsState()
    val syncState by viewmodel.syncState.collectAsState()
    val context = LocalContext.current
    val hazeState = rememberHazeState()
    val haptic = LocalHapticFeedback.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isLoginDialogOpen by remember { mutableStateOf(false) }
    val loginState by viewmodel.loginState.collectAsState()
    LaunchedEffect(loginState) {
        if (loginState is SyncUiState.Success) {
            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
            isLoginDialogOpen = false
            viewmodel.setLoginStateIdle()
        }
    }
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(
            Lifecycle.State.STARTED
        ) {
            val today = when (LocalDate.now().dayOfWeek) {
                DayOfWeek.MONDAY -> "MON"
                DayOfWeek.TUESDAY -> "TUE"
                DayOfWeek.WEDNESDAY -> "WED"
                DayOfWeek.THURSDAY -> "THU"
                DayOfWeek.FRIDAY -> "FRI"
                DayOfWeek.SATURDAY -> "SAT"
                DayOfWeek.SUNDAY -> "SUN"
            }

            viewmodel.updateDay(today)
        }
    }
    LaunchedEffect(Unit) {
        delay(1000)
        viewmodel.syncOnStartup()
    }

    LaunchedEffect(Unit) {
        viewmodel.syncEvents.collect { event ->
            when (event) {
                is SyncUiState.Success -> {
                    AppWidgetManager.getInstance(context)
                        .getAppWidgetIds(
                            ComponentName(context, TimeTableAppWidget::class.java)
                        )
                        .takeIf { it.isNotEmpty() }
                        ?.let {
                            TimetableWidget().updateAll(context)
                        }
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

    Box() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .padding(horizontal = 12.dp)
        ) {
            LazyColumn() {
                item {
                    Spacer(
                        modifier = Modifier.height(
                            72.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                        )
                    )
                }

                item {
                    AnimatedVisibility(syncState is SyncUiState.Loading) {
                        Column {
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
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                    ) {
                        Text(
                            text = "Today's Schedule",
                            color = uiColors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .weight(1f)
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
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
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
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
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
                        verticalAlignment = Alignment.CenterVertically,
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
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Attendance",
                            color = uiColors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        OverallAttendanceCard(
                            colors = uiColors,
                            sapLoggedIn = sapLoggedIn,
                            percentageOverall = averageAttendancePercentage,
                            percentageHighest = highestAttendancePercentage,
                            percentageLowest = lowestAttendancePercentage,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                isLoginDialogOpen = true
                            },
                            onNavigate = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                navController.navigate(Destinations.Attendance) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
                item {
                    Spacer(
                        modifier = Modifier.height(
                            86.dp + WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding()
                        )
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                    blurRadius = 15.dp
                    noiseFactor = 0.05f
                    inputScale = HazeInputScale.Auto
                    alpha = 0.98f
                }
                .padding(horizontal = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        top = 8.dp + WindowInsets.statusBars.asPaddingValues()
                            .calculateTopPadding()
                    )
                    .fillMaxWidth()
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
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
    if (showAboutDialog) {
        AboutELabsDialog(
            onDismiss = { showAboutDialog = false },
            context = LocalContext.current,
            hazeState = hazeState
        )
    }
    if (isLoginDialogOpen){
        LoginDialogBox(
            onDismiss = {
                isLoginDialogOpen = false
            },
            onConfirm = { sapPassword->
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                viewmodel.login(sapPassword)
            },
            syncState = loginState,
            hazeState = hazeState
        )
    }
}
