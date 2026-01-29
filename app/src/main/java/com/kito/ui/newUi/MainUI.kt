package com.kito.ui.newUi

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.kito.ui.components.ExpressiveEasing
import com.kito.ui.navigation.BottomBarTab
import com.kito.ui.navigation.BottomBarTabs
import com.kito.ui.navigation.RootDestination
import com.kito.ui.navigation.TabDestination
import com.kito.ui.navigation.tabs
import com.kito.ui.newUi.screen.AttendanceListScreen
import com.kito.ui.newUi.screen.FacultyDetailScreen
import com.kito.ui.newUi.screen.FacultyScreen
import com.kito.ui.newUi.screen.HomeScreen
import com.kito.ui.newUi.screen.ScheduleScreen
import com.kito.ui.newUi.screen.SettingsScreen
import com.kito.ui.newUi.viewmodel.AppViewModel
import com.kito.ui.newUi.viewmodel.FacultyDetailViewModel
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class
)
@Composable
fun MainUI(
    appViewModel: AppViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val shouldShowBottomBar = currentDestination?.hierarchy?.any { it.hasRoute<RootDestination.Tabs>() } == true

    LaunchedEffect(Unit) {
        appViewModel.checkResetFix()
    }
    LaunchedEffect(currentDestination) {
        selectedTabIndex = when {
            currentDestination?.hasRoute<TabDestination.Home>() == true -> 0
            currentDestination?.hasRoute<TabDestination.Attendance>() == true -> 1
            currentDestination?.hasRoute<TabDestination.Faculty>() == true -> 2
            currentDestination?.hasRoute<TabDestination.Profile>() == true -> 3
            else -> selectedTabIndex
        }
    }

    val hazeState = rememberHazeState()

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            AnimatedVisibility(
                visible = shouldShowBottomBar,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
            ) {
                Box(
                    modifier = Modifier
                        .padding(
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding()
                        )
                        .padding(vertical = 10.dp, horizontal = 64.dp)
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(CircleShape)
                        .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                            blurRadius = 15.dp
                            noiseFactor = 0.05f
                            inputScale = HazeInputScale.Auto
                            alpha = 0.98f
                        }
                        .border(
                            width = Dp.Hairline,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.5f),
                                    Color.White.copy(alpha = 0.1f),
                                )
                            ),
                            shape = CircleShape
                        )
                ) {
                    val animatedSelectedTabIndex by animateFloatAsState(
                        targetValue = selectedTabIndex.toFloat(),
                        label = "animatedSelectedTabIndex",
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioLowBouncy,
                        )
                    )

                    val animatedColor by animateColorAsState(
                        targetValue = if (selectedTabIndex in tabs.indices) tabs[selectedTabIndex].color else Color.White,
                        label = "animatedColor",
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                        )
                    )

                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val tabWidth = size.width / tabs.size
                        val centerOffset = tabWidth * animatedSelectedTabIndex + tabWidth / 2

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    animatedColor.copy(alpha = 0.3f),
                                    Color.Transparent
                                ),
                                center = Offset(centerOffset, size.height * 0.55f),
                                radius = tabWidth * 0.7f
                            ),
                            radius = tabWidth * 0.7f,
                            center = Offset(centerOffset, size.height * 0.55f)
                        )

                        val path = Path().apply {
                            addRoundRect(RoundRect(size.toRect(), CornerRadius(size.height / 2f)))
                        }
                        val measure = PathMeasure()
                        measure.setPath(path, false)
                        drawPath(
                            path = path,
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    animatedColor.copy(alpha = 0.5f),
                                    animatedColor,
                                    animatedColor.copy(alpha = 0.5f),
                                    Color.Transparent,
                                ),
                                startX = centerOffset - (tabWidth * 0.6f),
                                endX = centerOffset + (tabWidth * 0.6f),
                            ),
                            style = Stroke(width = 5f)
                        )
                    }

                    BottomBarTabs(
                        tabs = tabs,
                        selectedTab = selectedTabIndex,
                        onTabSelected = { tab ->
                            val destination = when (tab) {
                                BottomBarTab.Home -> TabDestination.Home
                                BottomBarTab.Attendance -> TabDestination.Attendance
                                BottomBarTab.Faculty -> TabDestination.Faculty
                                BottomBarTab.Settings -> TabDestination.Profile
                            }
                            navController.navigate(destination) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = RootDestination.Tabs,
            modifier = Modifier.hazeSource(hazeState)
        ) {

            // 🔹 Bottom bar graph
            navigation<RootDestination.Tabs>(
                startDestination = TabDestination.Home
            ) {
                composable<TabDestination.Home> {
                    HomeScreen(
                        navController = navController
                    )
                }
                composable<TabDestination.Attendance> {
                    AttendanceListScreen(navController = navController)
                }
                composable<TabDestination.Faculty> {
                    FacultyScreen(navController)
                }
                composable<TabDestination.Profile> {
                    SettingsScreen(navController = navController)
                }
            }

            // 🔹 Fullscreen: Schedule
            composable<RootDestination.Schedule>(
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(
                            durationMillis = 600,
                            easing = ExpressiveEasing.Emphasized
                        )
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -(fullWidth * 0.3f).toInt() },
                        animationSpec = tween(
                            durationMillis = 600,
                            easing = ExpressiveEasing.Emphasized
                        )
                    )
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -(fullWidth * 0.3f).toInt() },
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = ExpressiveEasing.Emphasized
                        )
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = ExpressiveEasing.Emphasized
                        )
                    )
                }
            ) {
                ScheduleScreen()
            }

            // 🔹 Fullscreen: Faculty detail
            composable<RootDestination.FacultyDetail>(
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(
                            durationMillis = 600,
                            easing = ExpressiveEasing.Emphasized
                        )
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -(fullWidth * 0.3f).toInt() },
                        animationSpec = tween(
                            durationMillis = 600,
                            easing = ExpressiveEasing.Emphasized
                        )
                    )
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -(fullWidth * 0.3f).toInt() },
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = ExpressiveEasing.Emphasized
                        )
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = ExpressiveEasing.Emphasized
                        )
                    )
                }
            ) {
                val viewModel: FacultyDetailViewModel = hiltViewModel()

                val faculty by viewModel.faculty.collectAsState()
                val schedule by viewModel.schedule.collectAsState()

                LaunchedEffect(Unit) {
                    val facultyId =
                        navController.currentBackStackEntry
                            ?.toRoute<RootDestination.FacultyDetail>()
                            ?.facultyId
                            ?: return@LaunchedEffect
                    viewModel.loadFacultyDetail(facultyId)
                }

                if (faculty == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    FacultyDetailScreen(
                        facultyName = faculty!!.name,
                        facultyRoom = faculty!!.office_room,
                        facultyEmail = faculty!!.email,
                        schedule = schedule
                    )
                }
            }
        }
    }
}