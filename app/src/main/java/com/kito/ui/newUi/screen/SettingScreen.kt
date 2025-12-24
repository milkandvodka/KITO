package com.kito.ui.newUi.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kito.ui.components.SettingsCard
import com.kito.ui.components.UIColors

@Composable
fun SettingsScreen() {
    val uiColors = UIColors()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        uiColors.backgroundTop,
                        uiColors.backgroundBottom
                    )
                )
            )
            .padding(
                top = WindowInsets.statusBars
                    .asPaddingValues()
                    .calculateTopPadding() + 12.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )
    ) {

        Text(
            text = "Settings",
            color = uiColors.textPrimary,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        SettingsCard(
            uiColors = uiColors,
            icon = Icons.Default.Person,
            title = "Name",
            value = "Pratyusha Mohanty"
        )

        SettingsCard(
            uiColors = uiColors,
            icon = Icons.Default.Badge,
            title = "Roll No",
            value = "23053287"
        )

        SettingsCard(
            uiColors = uiColors,
            icon = Icons.Default.School,
            title = "Year & Term",
            value = "3rd Year · 6th Sem"
        )

        SettingsCard(
            uiColors = uiColors,
            icon = Icons.Default.Info,
            title = "About App",
            value = "Know more about this app",
            showEdit = false
        )

        SettingsCard(
            uiColors = uiColors,
            icon = Icons.Default.Logout,
            title = "Logout",
            value = "Sign out from account",
            showEdit = false,
            isLogout = true
        )
    }
}
