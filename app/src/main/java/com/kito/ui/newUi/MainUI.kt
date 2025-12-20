package com.kito.ui.newUi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.kito.ui.components.BottomNavigationItems
import com.kito.ui.components.NoRippleInteractionSource
import com.kito.ui.components.UIColors
import com.kito.ui.newUi.screen.AttendanceListScreen
import com.kito.ui.screen.CalendarScreen


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainUI(){
    val uiColors = UIColors()
    val navigationItems = BottomNavigationItems
    var selected by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier
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
                    containerColor = uiColors.backgroundTop
                ){
                    NavigationBar(
                        containerColor = uiColors.backgroundTop
                    ) {
                        BottomNavigationItems.forEachIndexed { index,item->
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
            ){
                when(selected) {
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
}