package com.kito.ui.components

import androidx.appcompat.widget.DialogTitle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationModel (
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
)

val BottomNavigationItems = listOf<BottomNavigationModel>(
    BottomNavigationModel(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unSelectedIcon = Icons.Outlined.Home
    ),
    BottomNavigationModel(
        title = "Attendance",
        selectedIcon = Icons.Filled.CheckCircle,
        unSelectedIcon = Icons.Outlined.CheckCircle,
    ),
    BottomNavigationModel(
        title = "KIIT Calender",
        selectedIcon = Icons.Filled.CalendarMonth,
        unSelectedIcon = Icons.Outlined.CalendarMonth
    ),
    BottomNavigationModel(
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unSelectedIcon = Icons.Outlined.Person
    )
)