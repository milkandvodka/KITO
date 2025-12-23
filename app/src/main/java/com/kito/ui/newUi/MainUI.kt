package com.kito.ui.newUi
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kito.ui.components.NoRippleInteractionSource
import com.kito.ui.components.UIColors
import com.kito.ui.navigation.BottomNavigationItems
import com.kito.ui.navigation.Destinations
import com.kito.ui.newUi.screen.AttendanceListScreen
import com.kito.ui.newUi.screen.HomeScreen
import com.kito.ui.screen.CalendarScreen
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class
)
@Composable
fun MainUI() {
    val uiColors = UIColors()
    val hazeState = rememberHazeState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    Surface {
        Scaffold(
            bottomBar = {
                FlexibleBottomAppBar(
                    containerColor = Color.Transparent,
                    modifier = Modifier
                        .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                            blurRadius = 15.dp
                            noiseFactor = 0.05f
                            inputScale = HazeInputScale.Auto
                            alpha = 0.98f
                        }
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BottomNavigationItems.forEach { item ->
                            val isSelected =
                                currentDestination?.hierarchy?.any { it.route == item.destination::class.qualifiedName } == true
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (!isSelected) {
                                        navController.navigate(item.destination) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unSelectedIcon,
                                        contentDescription = item.title
                                    )
                                },
                                interactionSource = NoRippleInteractionSource,
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = uiColors.progressAccent
                                )
                            )
                        }
                    }
                }
            },
            contentWindowInsets = WindowInsets(0),
            containerColor = Color.Transparent,
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Destinations.Home,
                    modifier = Modifier.hazeSource(hazeState)
                ) {
                    composable<Destinations.Home> {
                        HomeScreen()
                    }
                    composable<Destinations.Attendance> {
                        AttendanceListScreen()
                    }
                    composable<Destinations.Profile> {
                        CalendarScreen()
                    }
                }
            }
        }
    }
}