package com.kito.core.presentation.navigation3

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.kito.core.designsystem.ExpressiveEasing
import com.kito.feature.attendance.presentation.AttendanceListScreen
import com.kito.feature.calendar.presentation.CalendarScreen
import com.kito.feature.home.presentation.HomeScreen
import com.kito.feature.settings.presentation.SettingsScreen

@Composable
fun TabNavGraph(
    rootNavStack: NavBackStack<NavKey>,
    tabNavStack: NavBackStack<NavKey>,
    snackbarHostState: SnackbarHostState
) {
    NavDisplay(
        backStack = tabNavStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator()
        ),
        transitionSpec = {
            fadeIn(animationSpec = tween(400, easing = ExpressiveEasing.Emphasized)) togetherWith
                    fadeOut(animationSpec = tween(400, easing = ExpressiveEasing.Emphasized))
        },
        popTransitionSpec = {
            fadeIn(animationSpec = tween(400, easing = ExpressiveEasing.Emphasized)) togetherWith
                    fadeOut(animationSpec = tween(400, easing = ExpressiveEasing.Emphasized))
        },
        predictivePopTransitionSpec = {
            fadeIn(animationSpec = tween(400, easing = ExpressiveEasing.Emphasized)) togetherWith
                    fadeOut(animationSpec = tween(400, easing = ExpressiveEasing.Emphasized))
        },
        entryProvider = entryProvider {
            entry<TabRoutes.Home> {
                HomeScreen(
                    rootNavBackStack = rootNavStack,
                    tabNavBackStack = tabNavStack
                )
            }
            entry<TabRoutes.Attendance> {
                AttendanceListScreen()
            }
            entry<TabRoutes.Calendar> {
                CalendarScreen()
            }
            entry<TabRoutes.Profile> {
                SettingsScreen(
                    tabNavBackStack = tabNavStack,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    )
}