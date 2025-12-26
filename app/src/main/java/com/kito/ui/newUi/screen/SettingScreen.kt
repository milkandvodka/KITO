package com.kito.ui.newUi.screen

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
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kito.ui.components.UIColors
import com.kito.ui.components.settingsdialog.AboutAppDialogBox
import com.kito.ui.components.settingsdialog.LoginDialogBox
import com.kito.ui.components.settingsdialog.NameChangeDialogBox
import com.kito.ui.components.settingsdialog.PrivacyPolicyDialog
import com.kito.ui.components.settingsdialog.RollChangeDialogBox
import com.kito.ui.components.settingsdialog.TermsOfServiceDialog
import com.kito.ui.components.settingsdialog.YearTermChangeDialogBox
import com.kito.ui.components.state.SyncUiState
import com.kito.ui.newUi.viewmodel.SettingsViewModel
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalHazeApi::class,
    ExperimentalHazeMaterialsApi::class
)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiColors = UIColors()
    val hazeState = rememberHazeState()
    val name by viewModel.name.collectAsState()
    val roll by viewModel.rollNumber.collectAsState()
    val year by viewModel.year.collectAsState()
    val term by viewModel.term.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    var isNameChangeDialogOpen by remember { mutableStateOf(false) }
    var isRollChangeDialogOpen by remember { mutableStateOf(false) }
    var isYearTermChangeDialogOpen by remember { mutableStateOf(false) }
    var isLoginDialogOpen by remember { mutableStateOf(false) }
    var isPrivacyPolicyDialogOpen by remember { mutableStateOf(false) }
    var isTermsOfServiceDialogOpen by remember { mutableStateOf(false) }
    var isAboutAppDialogOpen by remember { mutableStateOf(false) }
    val syncState by viewModel.syncState.collectAsState()
    val settingsItems = listOf(
        SettingsItem(
            title = "Name",
            value = name,
            icon = Icons.Default.Person,
            onClick = {
                isNameChangeDialogOpen = true
            },
            editButton = true,
        ),
        SettingsItem(
            title = "Roll No",
            value = roll,
            icon = Icons.Default.Badge,
            onClick = {
                isRollChangeDialogOpen = true
            },
            editButton = true,
        ),
        SettingsItem(
            title = "Year & Term",
            value = "$year · $term",
            icon = Icons.Default.School,
            onClick = {
                isYearTermChangeDialogOpen = true
            },
            editButton = true,
        ),
        SettingsItem(
            title = "Privacy Policy",
            value = "Read our privacy policy",
            icon = Icons.Default.PrivacyTip,
            onClick = {
                isPrivacyPolicyDialogOpen = true
            },
        ),
        SettingsItem(
            title = "Terms Of Service",
            value = "Read our terms of service",
            icon = Icons.Default.FilePresent,
            onClick = {
                isTermsOfServiceDialogOpen = true
            }
        ),
        SettingsItem(
            title = "About App",
            value = "Know more about this app",
            icon = Icons.Default.Info,
            onClick = {
                isAboutAppDialogOpen = true
            },
        ),
        SettingsItem(
            title = if (!isLoggedIn) "Login" else "Logout",
            value = if(!isLoggedIn) "Login to SAP" else "Logout of SAP",
            icon = if (!isLoggedIn) Icons.AutoMirrored.Filled.Login else Icons.AutoMirrored.Filled.Logout,
            onClick = {
                if (isLoggedIn){
                    viewModel.logOut()
                }else{
                    isLoginDialogOpen = true
                }
            },
            editButton = false,
            isLogout = true,
        )
    )
    LaunchedEffect(syncState) {
        if (syncState is SyncUiState.Success) {
            isNameChangeDialogOpen = false
            isRollChangeDialogOpen = false
            isYearTermChangeDialogOpen = false
            isLoginDialogOpen = false
            viewModel.syncStateIdle()
        }
    }
    Box {
        LazyColumn(
            contentPadding = PaddingValues(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 46.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            itemsIndexed(settingsItems){index,item->
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
                            tint = if (!item.isLogout) uiColors.textPrimary else Color(0xFFB32727)
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                color = if (!item.isLogout) uiColors.textSecondary else Color(0xFFB32727),
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
                        }
                    }
                }
            }
            item{
                Spacer(
                    modifier = Modifier.height(
                        106.dp + WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    )
                )
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
    if (isNameChangeDialogOpen){
        NameChangeDialogBox(
            onDismiss = {
                isNameChangeDialogOpen = false
            },
            onConfirm = {name->
                viewModel.changeName(name)

            },
            syncState = syncState,
            hazeState = hazeState
        )
    }
    if (isRollChangeDialogOpen){
        RollChangeDialogBox(
            onDismiss = {
                isRollChangeDialogOpen = false
            },
            onConfirm = {roll->
                viewModel.changeRoll(roll)
            },
            syncState = syncState,
            hazeState = hazeState
        )
    }
    if (isYearTermChangeDialogOpen){
        YearTermChangeDialogBox(
            onDismiss = {
                isYearTermChangeDialogOpen = false
            },
            onConfirm = { year, term ->
                viewModel.changeYearTerm(year = year,term = term)
            },
            year = year,
            term = term,
            syncState = syncState,
            hazeState = hazeState
        )
    }
    if(isLoginDialogOpen){
        LoginDialogBox(
            onDismiss = {
                isLoginDialogOpen = false
            },
            onConfirm = {sapPassword->
                viewModel.logIn(password = sapPassword)
            },
            syncState = syncState,
            hazeState = hazeState
        )
    }
    if(isPrivacyPolicyDialogOpen){
        PrivacyPolicyDialog(
            onDismiss = {
                isPrivacyPolicyDialogOpen = false
            },
            hazeState = hazeState
        )
    }
    if (isTermsOfServiceDialogOpen){
        TermsOfServiceDialog(
            onDismiss = {
                isTermsOfServiceDialogOpen = false
            },
            hazeState = hazeState
        )
    }
    if (isAboutAppDialogOpen){
        AboutAppDialogBox(
            onDismiss = {
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
    val onClick: () -> Unit ={},
    val editButton: Boolean = false,
    val isLogout: Boolean = false,
)