package com.kito.core.presentation.navigation3

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.kito.core.designsystem.ExpressiveEasing
import com.kito.core.designsystem.LocalRootNavAnimatedContentScope
import com.kito.core.designsystem.LocalSharedTransitionScope
import com.kito.core.designsystem.sharedElementEntryMetadata
import com.kito.feature.auth.presentation.onboarding.OnBoardingScreen
import com.kito.feature.auth.presentation.usersetup.UserSetupScreen
import com.kito.feature.exam.presentation.UpcomingExamScreen
import com.kito.feature.faculty.presentation.FacultyDetailScreen
import com.kito.feature.faculty.presentation.FacultyScreen
import com.kito.feature.gpa.presentation.GPAScreen
import com.kito.feature.holiday.presentation.HolidayListScreen
import com.kito.feature.khaoogully.presentation.KhaooGullyHomeScreen
import com.kito.feature.khaoogully.presentation.RestaurantMenuScreen
import com.kito.feature.promotions.presentations.PromotionsScreen
import com.kito.feature.schedule.presentation.ScheduleScreen

@Composable
fun RootNavGraph(
    rootNavBackStack: NavBackStack<NavKey>,
    tabNavBackStack: NavBackStack<NavKey>,
    snackbarHostState: SnackbarHostState,
) {
    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavDisplay(
                backStack = rootNavBackStack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator()
                ),
                predictivePopTransitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -(fullWidth * 0.3f).toInt() },
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = ExpressiveEasing.Emphasized
                        )
                    ) togetherWith slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = ExpressiveEasing.Emphasized
                        )
                    )
                },
                popTransitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -(fullWidth * 0.3f).toInt() },
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = ExpressiveEasing.Emphasized
                        )
                    ) togetherWith slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = ExpressiveEasing.Emphasized
                        )
                    )
                },
                transitionSpec = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(
                            durationMillis = 600,
                            easing = ExpressiveEasing.Emphasized
                        )
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -(fullWidth * 0.3f).toInt() },
                        animationSpec = tween(
                            durationMillis = 600,
                            easing = ExpressiveEasing.Emphasized
                        )
                    )
                },
                entryProvider = entryProvider {
                    entry<Routes.Tabs> {
                        CompositionLocalProvider(
                            LocalRootNavAnimatedContentScope provides LocalNavAnimatedContentScope.current
                        ) {
                            TabNavGraph(
                                rootNavStack = rootNavBackStack,
                                tabNavStack = tabNavBackStack,
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }
                    entry<Routes.Schedule> {
                        ScheduleScreen(
                            onBack = { rootNavBackStack.removeAt(rootNavBackStack.lastIndex) }
                        )
                    }
                    entry<Routes.ExamSchedule>(metadata = sharedElementEntryMetadata()) {
                        UpcomingExamScreen(
                            onBack = { rootNavBackStack.removeAt(rootNavBackStack.lastIndex) }
                        )
                    }
                    entry<Routes.Faculty> {
                        FacultyScreen(rootNavBackStack = rootNavBackStack)
                    }
                    entry<Routes.FacultyDetail> {
                        FacultyDetailScreen(
                            facultyId = it.facultyId,
                            onBack = { rootNavBackStack.removeAt(rootNavBackStack.lastIndex) }
                        )
                    }
                    entry<Routes.Onboarding> {
                        OnBoardingScreen(
                            onOnboardingComplete = {
                                rootNavBackStack.clear()
                                rootNavBackStack.add(Routes.UserSetup)
                            }
                        )
                    }
                    entry<Routes.UserSetup> {
                        UserSetupScreen(
                            onSetupComplete = {
                                rootNavBackStack.clear()
                                rootNavBackStack.add(Routes.Tabs)
                            }
                        )
                    }
                    entry<Routes.Promotions> {
                        PromotionsScreen(url = it.url)
                    }
                    entry<Routes.FriendView>(metadata = sharedElementEntryMetadata()) {
                        com.kito.feature.friendview.presentation.list.FriendListScreen(
                            onBack = { rootNavBackStack.removeAt(rootNavBackStack.lastIndex) },
                            onNavigateToSchedule = { roll ->
                                rootNavBackStack.add(Routes.FriendSchedule(roll = roll))
                            }
                        )
                    }
                    entry<Routes.FriendSchedule> {
                        com.kito.feature.friendview.presentation.schedule.FriendScheduleScreen(
                            roll = it.roll,
                            onBack = { rootNavBackStack.removeAt(rootNavBackStack.lastIndex) }
                        )
                    }
                    entry<Routes.HolidayList>(metadata = sharedElementEntryMetadata()) {
                        HolidayListScreen(
                            onBack = { rootNavBackStack.removeAt(rootNavBackStack.lastIndex) }
                        )
                    }
                    entry<Routes.GPACalc>(metadata = sharedElementEntryMetadata()) {
                        GPAScreen(
                            onBack = { rootNavBackStack.removeAt(rootNavBackStack.lastIndex) }
                        )
                    }
                    entry<Routes.Calendar>(metadata = sharedElementEntryMetadata()) {
                        KhaooGullyHomeScreen(
                            onRestaurantClick = { restaurant ->
                                rootNavBackStack.add(
                                    Routes.RestaurantMenu(
                                        restaurantId = restaurant.id,
                                        restaurantName = restaurant.name,
                                        restaurantImage = restaurant.image,
                                        restaurantRating = restaurant.rating,
                                        browseOnly = restaurant.browseOnly
                                    )
                                )
                            }
                        )
                    }
                    entry<Routes.RestaurantMenu> {
                        RestaurantMenuScreen(
                            route = it,
                            onBack = { rootNavBackStack.removeAt(rootNavBackStack.lastIndex) }
                        )
                    }
                }
            )
        }
    }
}