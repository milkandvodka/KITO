package com.kito.feature.app.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.kito.core.datastore.domain.repository.PrefsRepository
import com.kito.core.presentation.navigation.LegacyBottomNav
import com.kito.core.presentation.navigation3.NavigationItems
import com.kito.core.presentation.navigation3.RootNavGraph
import com.kito.core.presentation.navigation3.Routes
import com.kito.core.presentation.navigation3.TabRoutes
import com.kito.core.presentation.navigation3.navigateTab
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.first
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class
)
@Composable
fun MainUI(
    appViewModel: AppViewModel = koinInject(),
    deepLinkTarget: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
    initialDestination: NavKey? = null
) {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .crossfade(true)
            .build()
    }

    val prefs: PrefsRepository = koinInject()
    var startDestination by remember { mutableStateOf(initialDestination) }

    // Only fetch if initialDestination was null (mainly for iOS or fallback)
    LaunchedEffect(Unit) {
        if (startDestination == null) {
            val onboardingDone = prefs.onBoardingFlow.first()
            val isUserSetupDone = prefs.userSetupDoneFlow.first()
            startDestination = when {
                !onboardingDone -> Routes.Onboarding
                !isUserSetupDone -> Routes.UserSetup
                else -> Routes.Tabs
            }
        }
    }

    val currentStartDestination = startDestination
    // Show Splash until startDestination is determined
    if (currentStartDestination == null) {
        AppSplash()
        return
    }

    val rootBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration{
            serializersModule = SerializersModule{
                polymorphic(NavKey::class){
                    subclass(Routes.Tabs::class, Routes.Tabs.serializer())
                    subclass(Routes.Schedule::class, Routes.Schedule.serializer())
                    subclass(Routes.ExamSchedule::class, Routes.ExamSchedule.serializer())
                    subclass(Routes.FacultyDetail::class, Routes.FacultyDetail.serializer())
                    subclass(Routes.Onboarding::class, Routes.Onboarding.serializer())
                    subclass(Routes.UserSetup::class, Routes.UserSetup.serializer())
                    subclass(Routes.Promotions::class, Routes.Promotions.serializer())
                    subclass(Routes.FriendView::class, Routes.FriendView.serializer())
                    subclass(Routes.HolidayList::class, Routes.HolidayList.serializer())
                    subclass(Routes.GPACalc::class, Routes.GPACalc.serializer())
                    subclass(Routes.Calendar::class, Routes.Calendar.serializer())
                    subclass(Routes.RestaurantMenu::class, Routes.RestaurantMenu.serializer())
                }
            }
        },
        currentStartDestination
    )
    val tabBackStack =  rememberNavBackStack(
        configuration = SavedStateConfiguration{
            serializersModule = SerializersModule{
                polymorphic(NavKey::class){
                    subclass(TabRoutes.Home::class, TabRoutes.Home.serializer())
                    subclass(TabRoutes.Profile::class, TabRoutes.Profile.serializer())
                    subclass(TabRoutes.Attendance::class, TabRoutes.Attendance.serializer())
                    subclass(TabRoutes.Calendar::class, TabRoutes.Calendar.serializer())
                }
            }
        },
        TabRoutes.Home
    )
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val shouldShowBottomBar = rootBackStack.last() == Routes.Tabs
    val snackbarHostState = remember { SnackbarHostState() }
    val navigationBarType = rememberNavigationBarType()
    LaunchedEffect(deepLinkTarget) {
        if (deepLinkTarget == "schedule") {
            rootBackStack.add(Routes.Schedule)
            onDeepLinkConsumed()
        }
    }
    LaunchedEffect(Unit) {
        appViewModel.checkResetFix()
    }
    LaunchedEffect(tabBackStack.last()) {
        val index = NavigationItems.indexOfFirst { it.destination == tabBackStack.last() }
        if (index >= 0) selectedTabIndex = index
    }

    IosBottomBarBridge(
        selectedTabIndex = selectedTabIndex,
        visible = shouldShowBottomBar,
        onTabSelected = { index -> tabBackStack.navigateTab(NavigationItems[index].destination) }
    )

    val hazeState = rememberHazeState()
    Surface {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (isAndroid()) {
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
                        LegacyBottomNav(
                            tabs = NavigationItems,
                            selectedTabIndex = selectedTabIndex,
                            onTabSelected = { item ->
                                tabBackStack.navigateTab(item.destination)
                            },
                            hazeState = hazeState
                        )
                    }
                }
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .hazeSource(hazeState)
                ) {
                    RootNavGraph(
                        rootNavBackStack = rootBackStack,
                        tabNavBackStack = tabBackStack,
                        snackbarHostState = snackbarHostState
                    )
                }
                if (isAndroid() && navigationBarType == NavigationBarType.ThreeButton) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(
                                WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding()
                            )
                            .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                                blurRadius = 15.dp
                                noiseFactor = 0.05f
                                inputScale = HazeInputScale.Auto
                                alpha = 0.98f
                            }
                    )
                }
            }
        }
    }
}

expect fun isAndroid(): Boolean
@Composable
fun rememberNavigationBarType(): NavigationBarType {
    val density = LocalDensity.current
    val bottomInset = WindowInsets.navigationBars.getBottom(density)

    return if (bottomInset > with(density) { 24.dp.roundToPx() })
        NavigationBarType.ThreeButton
    else
        NavigationBarType.Gesture
}

enum class NavigationBarType {
    Gesture,
    ThreeButton
}

@Composable
private fun AppSplash() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121116))
            .hazeEffect(
                state = rememberHazeState(),
                style = HazeMaterials.regular()
            )
    )
}