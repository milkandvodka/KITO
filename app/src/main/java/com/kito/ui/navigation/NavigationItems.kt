package com.kito.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationModel (
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val destination: Destinations
)

val BottomNavigationItems = listOf<BottomNavigationModel>(
    BottomNavigationModel(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unSelectedIcon = Icons.Outlined.Home,
        destination = Destinations.Home
    ),
    BottomNavigationModel(
        title = "Attendance",
        selectedIcon = Icons.Filled.CheckCircle,
        unSelectedIcon = Icons.Outlined.CheckCircle,
        destination = Destinations.Attendance
    ),
    BottomNavigationModel(
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unSelectedIcon = Icons.Outlined.Person,
        destination = Destinations.Profile
    )
)