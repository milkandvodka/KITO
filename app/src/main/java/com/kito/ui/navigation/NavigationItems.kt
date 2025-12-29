package com.kito.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

//data class BottomNavigationModel (
//    val title: String,
//    val selectedIcon: ImageVector,
//    val unSelectedIcon: ImageVector,
//    val destination: Destinations
//)
//
//val BottomNavigationItems = listOf<BottomNavigationModel>(
//    BottomNavigationModel(
//        title = "Home",
//        selectedIcon = Icons.Filled.Home,
//        unSelectedIcon = Icons.Outlined.Home,
//        destination = Destinations.Home
//    ),
//    BottomNavigationModel(
//        title = "Attendance",
//        selectedIcon  = Icons.Filled.CheckCircle,
//        unSelectedIcon = Icons.Outlined.CheckCircle,
//        destination = Destinations.Attendance
//    ),
//    BottomNavigationModel(
//        title = "Settings",
//        selectedIcon = Icons.Filled.Settings,
//        unSelectedIcon = Icons.Outlined.Settings,
//        destination = Destinations.Profile
//    )
//)

sealed class BottomBarTab(
    val title: String,
    val icon: ImageVector,
    val color: Color
){
    data object Home : BottomBarTab(
        title = "Home",
        icon = Icons.Filled.Home,
        color = Color(0xFFFFA574)
    )
    data object Attendance : BottomBarTab(
        title = "Attendance",
        icon = Icons.Filled.CheckCircle,
        color = Color(0xFFFFA574)
    )
    data object Settings : BottomBarTab(
        title = "Settings",
        icon = Icons.Filled.Settings,
        color = Color(0xFFFFA574)
    )
}

val tabs = listOf(
    BottomBarTab.Home,
    BottomBarTab.Attendance,
    BottomBarTab.Settings
)

