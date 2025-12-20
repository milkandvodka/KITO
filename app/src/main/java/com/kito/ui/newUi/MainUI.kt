package com.kito.ui.newUi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kito.data.local.preferences.newpreferences.PrefsRepository
import com.kito.ui.components.BottomNavigationItems
import com.kito.ui.components.NoRippleInteractionSource
import com.kito.ui.components.UIColors
import com.kito.ui.newUi.screen.AttendanceListScreen
import com.kito.ui.newUi.screen.HomeScreen
import com.kito.ui.screen.CalendarScreen
import androidx.compose.runtime.collectAsState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class
)
@Composable
fun MainUI() {
    val uiColors = UIColors()
    val navigationItems = BottomNavigationItems
    var selected by remember { mutableStateOf(0) }
    val hazeState = rememberHazeState()
    Box() {
        Box(
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(uiColors.backgroundTop, uiColors.backgroundBottom)
                    )
                )
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                navigationItems[selected].title,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                color = uiColors.textPrimary
                            )
                        },
                        actions = {
                            if (selected == 1) {
                                IconButton(onClick = {}) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh",
                                        tint = uiColors.textPrimary
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                },
                bottomBar = {
                    FlexibleBottomAppBar(
                        containerColor = uiColors.bottomBarColor
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent
                        ) {
                            BottomNavigationItems.forEachIndexed { index, item ->
                                val isSelected = index == selected
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        selected = index
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
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    when (selected) {
                        0 -> {

                        }

                        1 -> {
                            AttendanceListScreen()
                        }

                        2 -> {
                            CalendarScreen()
                        }
                    }
                }
            }
        }
        if (selected == 1){
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding() + 63.dp,
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 65.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin())
                )
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = uiColors.progressAccent,
                        contentColor = uiColors.textPrimary
                    )
                ) {
                    Text(
                        text = "Login to SAP",
                    )
                }
            }
        }
        if (selected == 2)
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding() + 63.dp
                        )
                        .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin())
                )
                Text(
                    text = "KIIT Calendar Coming Soon.....",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Bold
                )
            }
    }
}