package com.kito.core.presentation.navigation3

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

data class BottomBarTab(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val destination: NavKey
)

val NavigationItems = listOf(
    BottomBarTab(
        title = "Home",
        icon = Icons.Filled.Home,
        color = Color(0xFFFFA574),
        destination = TabRoutes.Home
    ),
    BottomBarTab(
        title = "Attendance",
        icon = Icons.Filled.CheckCircle,
        color = Color(0xFFFFA574),
        destination = TabRoutes.Attendance
    ),
    BottomBarTab(
        title = "Calendar",
        icon = Icons.Filled.CalendarMonth,
        color = Color(0xFFFFA574),
        destination = TabRoutes.Calendar
    ),
    BottomBarTab(
        title = "Settings",
        icon = Icons.Filled.Settings,
        color = Color(0xFFFFA574),
        destination = TabRoutes.Profile
    ),
)