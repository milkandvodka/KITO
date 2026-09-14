package com.kito.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.kito.core.designsystem.UIColors
import com.kito.core.platform.NotificationPermissionEffect
import com.kito.core.platform.areNotificationsEnabled
import com.kito.core.platform.canScheduleExactAlarms
import com.kito.core.platform.openAlarmSettings
import com.kito.core.platform.openAppSettings
import com.kito.core.platform.openNotificationSettings
import com.kito.core.platform.sendEmail
import com.kito.core.ui.state.SyncUiState
import com.kito.core.presentation.navigation3.TabRoutes
import com.kito.core.presentation.navigation3.navigateTab
import com.kito.feature.app.presentation.isAndroid
import com.kito.feature.settings.presentation.components.AboutAppDialogBox
import com.kito.feature.settings.presentation.components.LoginDialogBox
import com.kito.feature.settings.presentation.components.NameChangeDialogBox
import com.kito.feature.settings.presentation.components.PrivacyPolicyDialog
import com.kito.feature.settings.presentation.components.RequiredAttendanceDialogBox
import com.kito.feature.settings.presentation.components.RollChangeDialogBox
import com.kito.feature.settings.presentation.components.TermsOfServiceDialog
import com.kito.core.designsystem.YearTermChangeDialogBox
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalHazeApi::class,
    ExperimentalHazeMaterialsApi::class
)
@Composable
fun SettingsContent(
    name: String,
    roll: String,
    year: String,
    term: String,
    notificationState: Boolean,
    requiredAttendance: Int,
    isLoggedIn: Boolean,
    syncState: SyncUiState,
    pendingEnable: Boolean,
    onEvent: (SettingsEvent) -> Unit,
    tabNavBackStack: NavBackStack<NavKey>?,
    snackbarHostState: SnackbarHostState,
    kayaConnected: Boolean = false,
    onKayaConnect: suspend (String) -> String? = { null },
    onKayaDisconnect: () -> Unit = {},
) {
    val uiColors = UIColors()
    val haptic = LocalHapticFeedback.current
    val hazeState = rememberHazeState()
    val scope = rememberCoroutineScope()

    var isNameChangeDialogOpen by remember { mutableStateOf(false) }
    var isRollChangeDialogOpen by remember { mutableStateOf(false) }
    var isYearTermChangeDialogOpen by remember { mutableStateOf(false) }
    var isAttendanceChangeDialogOpen by remember { mutableStateOf(false) }
    var isLoginDialogOpen by remember { mutableStateOf(false) }
    var isKayaLoginDialogOpen by remember { mutableStateOf(false) }
    var kayaState by remember { mutableStateOf<SyncUiState>(SyncUiState.Idle) }
    var isPrivacyPolicyDialogOpen by remember { mutableStateOf(false) }
    var isTermsOfServiceDialogOpen by remember { mutableStateOf(false) }
    var isAboutAppDialogOpen by remember { mutableStateOf(false) }
    var askPermission by remember { mutableStateOf(false) }

    if (askPermission) {
        NotificationPermissionEffect { granted ->
            askPermission = false
            onEvent(SettingsEvent.RequestEnableNotifications)
        }
    }

    val settingsItems = listOfNotNull(
        SettingsItem(
            title = "Name",
            value = name,
            icon = Icons.Default.Person,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                isNameChangeDialogOpen = true
            },
            editButton = true,
        ),
        SettingsItem(
            title = "Roll No",
            value = roll,
            icon = Icons.Default.Badge,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                isRollChangeDialogOpen = true
            },
            editButton = true,
        ),
        SettingsItem(
            title = "Year & Term",
            value = "$year · $term",
            icon = Icons.Default.School,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                isYearTermChangeDialogOpen = true
            },
            editButton = true,
        ),
        SettingsItem(
            title = "Required Attendance",
            value = "$requiredAttendance%",
            icon = Icons.Default.Balance,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                isAttendanceChangeDialogOpen = true
            },
            editButton = true,
        ),
        SettingsItem(
            title = "Set Notification",
            value = if (notificationState) "Enabled" else "Disabled",
            icon = Icons.Default.Notifications,
            onClick = {
                if (notificationState) {
                    onEvent(SettingsEvent.SetNotificationState(false))
                } else {
                    askPermission = true
                }
            },
            toggle = true
        ),
        SettingsItem(
            title = "FeedBack",
            value = "Submit your feedback",
            icon = Icons.Default.Feedback,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                sendEmail(
                    to = "kiito.admin@gmail.com",
                    subject = "KIITO Feedback",
                    body = ""
                )
            }
        ),
        SettingsItem(
            title = "Privacy Policy",
            value = "Read our privacy policy",
            icon = Icons.Default.PrivacyTip,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                isPrivacyPolicyDialogOpen = true
            },
        ),
        SettingsItem(
            title = "Terms Of Service",
            value = "Read our terms of service",
            icon = Icons.Default.FilePresent,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                isTermsOfServiceDialogOpen = true
            }
        ),
        SettingsItem(
            title = "About App",
            value = "Know more about this app",
            icon = Icons.Default.Info,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                isAboutAppDialogOpen = true
            },
        ),
        SettingsItem(
            title = if (!isLoggedIn) "Login" else "Logout",
            value = if (!isLoggedIn) "Login to SAP" else "Logout of SAP",
            icon = if (!isLoggedIn) Icons.Default.Person else Icons.Default.Lock,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                if (isLoggedIn) {
                    onEvent(SettingsEvent.LogOut)
                } else {
                    isLoginDialogOpen = true
                }
            },
            editButton = false,
            isLogout = true,
        ),
        SettingsItem(
            title = if (!kayaConnected) "Login" else "Logout",
            value = if (!kayaConnected) "Login to KAYA" else "Logout of KAYA",
            icon = if (!kayaConnected) Icons.Default.Person else Icons.Default.Lock,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                if (kayaConnected) {
                    onKayaDisconnect()
                } else {
                    isKayaLoginDialogOpen = true
                }
            },
            editButton = false,
            isLogout = true,
        )
    )

    LaunchedEffect(pendingEnable) {
        if (!pendingEnable) return@LaunchedEffect
        if (!canScheduleExactAlarms()) {
            val result = snackbarHostState.showSnackbar(
                message = "Allow exact alarms to receive timely notifications",
                actionLabel = "Allow",
                withDismissAction = true
            )
            if (result == SnackbarResult.ActionPerformed) {
                openAlarmSettings()
            }
        }
        if (!areNotificationsEnabled()) {
            val result = snackbarHostState.showSnackbar(
                message = "Notification permission is required to enable alerts. Please enable in settings.",
                actionLabel = "Settings",
                withDismissAction = true
            )
            if (result == SnackbarResult.ActionPerformed) {
                if (isAndroid()) {
                    openAppSettings()
                } else {
                    openNotificationSettings()
                }
            }
        } else if (canScheduleExactAlarms()) {
            onEvent(SettingsEvent.SetNotificationState(true))
        }
        onEvent(SettingsEvent.ClearPendingNotificationEnable)
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (pendingEnable) {
            scope.launch {
                if (canScheduleExactAlarms() && areNotificationsEnabled()) {
                    onEvent(SettingsEvent.SetNotificationState(true))
                    onEvent(SettingsEvent.ClearPendingNotificationEnable)
                }
            }
        }
    }

    LaunchedEffect(syncState) {
        if (syncState is SyncUiState.Success) {
            if (isLoginDialogOpen) {
                tabNavBackStack?.navigateTab(TabRoutes.Home)
            }
            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
            isNameChangeDialogOpen = false
            isRollChangeDialogOpen = false
            isYearTermChangeDialogOpen = false
            isLoginDialogOpen = false
            isAttendanceChangeDialogOpen = false
            onEvent(SettingsEvent.SyncStateIdle)
        }
    }

    LaunchedEffect(kayaState) {
        if (kayaState is SyncUiState.Success) {
            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
            isKayaLoginDialogOpen = false
            kayaState = SyncUiState.Idle
        }
    }

    Box {
        Box(
            modifier = Modifier
                .hazeSource(hazeState)
                .background(Color(0xFF121116))
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 46.dp
                ),
                verticalArrangement = Arrangement.spacedBy(2.5.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .semantics { testTag = "settings_list" },
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                itemsIndexed(settingsItems) { index, item ->
                    Card(
                        shape = RoundedCornerShape(
                            topStart = if (index == 0) 24.dp else 4.dp,
                            topEnd = if (index == 0) 24.dp else 4.dp,
                            bottomStart = if (index == settingsItems.size - 1) 24.dp else 4.dp,
                            bottomEnd = if (index == settingsItems.size - 1) 24.dp else 4.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        uiColors.cardBackground,
                                        Color(0xFF2F222F),
                                        Color(0xFF2F222F),
                                        uiColors.cardBackgroundHigh
                                    )
                                ),
                                shape = RoundedCornerShape(
                                    topStart = if (index == 0) 24.dp else 4.dp,
                                    topEnd = if (index == 0) 24.dp else 4.dp,
                                    bottomStart = if (index == settingsItems.size - 1) 24.dp else 4.dp,
                                    bottomEnd = if (index == settingsItems.size - 1) 24.dp else 4.dp
                                )
                            ),
                        onClick = {
                            item.onClick()
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {

                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = if (!item.isLogout) uiColors.textPrimary else Color(
                                    0xFFB32727
                                )
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    color = if (!item.isLogout) uiColors.textSecondary else Color(
                                        0xFFB32727
                                    ),
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.value,
                                    color = uiColors.textPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            if (item.editButton) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = uiColors.textSecondary
                                )
                            } else if (item.toggle) {
                                Switch(
                                    checked = notificationState,
                                    onCheckedChange = {
                                        item.onClick()
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFF693E01),
                                        uncheckedThumbColor = uiColors.textSecondary,
                                        checkedTrackColor = Color(0xFFB6774C),
                                        uncheckedTrackColor = uiColors.cardBackground
                                    )
                                )
                            }
                        }
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
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Spacer(
                modifier = Modifier.height(
                    16.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                )
            )
            Text(
                text = "Settings",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = uiColors.textPrimary,
                style = MaterialTheme.typography.titleLargeEmphasized
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    if (isNameChangeDialogOpen) {
        NameChangeDialogBox(
            onDismiss = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isNameChangeDialogOpen = false
            },
            onConfirm = { nameVal ->
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                onEvent(SettingsEvent.ChangeName(nameVal))
            },
            syncState = syncState,
            hazeState = hazeState
        )
    }
    if (isRollChangeDialogOpen) {
        RollChangeDialogBox(
            onDismiss = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isRollChangeDialogOpen = false
            },
            onConfirm = { rollVal ->
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                onEvent(SettingsEvent.ChangeRoll(rollVal))
            },
            syncState = syncState,
            hazeState = hazeState
        )
    }
    if (isYearTermChangeDialogOpen) {
        YearTermChangeDialogBox(
            onDismiss = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isYearTermChangeDialogOpen = false
            },
            onConfirm = { yearVal, termVal ->
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                onEvent(SettingsEvent.ChangeYearTerm(yearVal, termVal))
            },
            year = year,
            term = term,
            syncState = syncState,
            hazeState = hazeState
        )
    }
    if (isAttendanceChangeDialogOpen) {
        RequiredAttendanceDialogBox(
            onDismiss = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isAttendanceChangeDialogOpen = false
            },
            onConfirm = { attendance ->
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                onEvent(SettingsEvent.ChangeAttendance(attendance.toInt()))
            },
            syncState = syncState,
            hazeState = hazeState
        )
    }
    if (isLoginDialogOpen) {
        LoginDialogBox(
            onDismiss = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isLoginDialogOpen = false
                onEvent(SettingsEvent.SyncStateIdle)
            },
            onConfirm = { sapPassword ->
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                onEvent(SettingsEvent.LogIn(sapPassword))
            },
            syncState = syncState,
            hazeState = hazeState
        )
    }
    if (isKayaLoginDialogOpen) {
        LoginDialogBox(
            onDismiss = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isKayaLoginDialogOpen = false
                kayaState = SyncUiState.Idle
            },
            onConfirm = { kayaPassword ->
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                scope.launch {
                    kayaState = SyncUiState.Loading
                    val err = onKayaConnect(kayaPassword)
                    kayaState = if (err == null) SyncUiState.Success else SyncUiState.Error(err)
                }
            },
            syncState = kayaState,
            hazeState = hazeState,
            title = "Login To KAYA",
            passwordLabel = "KAYA Password",
            confirmText = "Login",
        )
    }
    if (isPrivacyPolicyDialogOpen) {
        PrivacyPolicyDialog(
            onDismiss = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isPrivacyPolicyDialogOpen = false
            },
            hazeState = hazeState
        )
    }
    if (isTermsOfServiceDialogOpen) {
        TermsOfServiceDialog(
            onDismiss = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isTermsOfServiceDialogOpen = false
            },
            hazeState = hazeState
        )
    }
    if (isAboutAppDialogOpen) {
        AboutAppDialogBox(
            onDismiss = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isAboutAppDialogOpen = false
            },
            hazeState = hazeState
        )
    }
}

data class SettingsItem(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val onClick: () -> Unit = {},
    val editButton: Boolean = false,
    val isLogout: Boolean = false,
    val toggle: Boolean = false,
)

@Preview
@Composable
fun SettingsContentPreview() {
    SettingsContent(
        name = "John Doe",
        roll = "1234567",
        year = "3rd Year",
        term = "Autumn",
        notificationState = true,
        requiredAttendance = 75,
        isLoggedIn = true,
        syncState = SyncUiState.Idle,
        pendingEnable = false,
        onEvent = {},
        tabNavBackStack = null,
        snackbarHostState = remember { SnackbarHostState() }
    )
}
