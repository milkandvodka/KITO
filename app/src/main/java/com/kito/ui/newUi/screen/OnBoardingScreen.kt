package com.kito.ui.newUi.screen

import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kito.OnBoardingActivity
import com.kito.R
import com.kito.UserSetupActivity
import com.kito.data.local.preferences.newpreferences.PrefsRepository
import com.kito.ui.components.UIColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnBoardingScreen(prefRepository: PrefsRepository) {
    val uiColor = UIColors()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = {3}, initialPage = 0)
    val fabScale by animateFloatAsState(
        targetValue = if(pagerState.currentPage == 0) 0f else 1f
    )
    val pageOneAlpha by animateFloatAsState(
        targetValue = if (pagerState.currentPage == 0) 1f else 0f
    )
    val pageTwoAlpha by animateFloatAsState(
        targetValue = if (pagerState.currentPage == 1) 1f else 0f
    )
    val pageThreeAlpha by animateFloatAsState(
        targetValue = if (pagerState.currentPage == 2) 1f else 0f
    )
    val pageOneScale = remember { Animatable(0f) }
    val pageTwoScale = remember { Animatable(0f) }
    val pageThreeScale = remember { Animatable(0f) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        when (pagerState.currentPage) {
            0 -> {
                pageOneScale.animateTo(
                    targetValue = 1f,
                )
                pageTwoScale.animateTo(
                    targetValue = 0f
                )
                pageThreeScale.animateTo(
                    targetValue = 0f,
                )
            }
            1 -> {
                pageOneScale.animateTo(
                    targetValue = 0f,
                )
                pageTwoScale.animateTo(
                    targetValue = 1f,
                )
                pageThreeScale.animateTo(
                    targetValue = 0f,
                )
            }
            else -> {
                pageOneScale.animateTo(
                    targetValue = 0f,
                )
                pageTwoScale.animateTo(
                    targetValue = 0f,
                )
                pageThreeScale.animateTo(
                    targetValue = 1f,
                )
            }
        }
    }
    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0)
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ){
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    )
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    userScrollEnabled = false,
                ) {
                    if (it == 0) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .scale(pageOneScale.value),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(
                                    R.drawable.onboarding_elabs
                                ),
                                contentDescription = "Onboarding",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            )
                        }
                    }else if (it == 1){
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .scale(pageTwoScale.value),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(
                                    R.drawable.onboarding_schedule
                                ),
                                contentDescription = "Onboarding",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            )
                        }
                    }else{
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .scale(pageThreeScale.value),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(
                                    R.drawable.onboarding_elabs
                                ),
                                contentDescription = "Onboarding",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            )
                        }
                    }
                }
                Box() {
                    Column(
                        modifier = Modifier.alpha(pageOneAlpha)
                    ) {
                        Text(
                            text = "Your Daily College Checkpoint",
                            style = MaterialTheme.typography.headlineLarge.copy( // Slightly refined
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = MaterialTheme.typography.headlineLarge.lineHeight * 1.1f
                            ),
                            color = uiColor.progressAccent,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Check attendance, view your timetable, and stay updated all at your fingertips!",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3f,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.alpha(pageTwoAlpha)
                    ) {
                        Text(
                            text = "No More “Kitna Attendance Hai?”",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = MaterialTheme.typography.headlineLarge.lineHeight * 1.1f
                            ),
                            color =  uiColor.progressAccent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Connect your SAP and instantly track subject-wise attendance and know where you stand before it’s too late.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3f,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.alpha(pageThreeAlpha)
                    ) {
                        Text(
                            text = "Never Miss a Lecture or Event Again!",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = MaterialTheme.typography.headlineLarge.lineHeight * 1.1f
                            ),
                            color = uiColor.progressAccent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Access your daily and weekly timetable and events anytime. No confusion, no last-minute panic.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3f,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FloatingActionButton(
                        elevation = FloatingActionButtonDefaults.elevation(
                            0.dp,
                            pressedElevation = 0.dp
                        ),
                        modifier = Modifier
                            .padding(28.dp)
                            .scale(fabScale),
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                            isLoading = false
                        },
                        containerColor = uiColor.cardBackground
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Get Started",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    ExtendedFloatingActionButton(
                        containerColor = Color.Transparent,
                        expanded = if (pagerState.currentPage != 2) false else true,
                        onClick = {
                            if (pagerState.currentPage != 2) {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }else{
                                scope.launch {
                                    isLoading = true
                                    delay(500L)
                                    prefRepository.setOnboardingDone()
                                    context.startActivity(
                                        Intent(
                                            context,
                                            UserSetupActivity::class.java
                                        )
                                    )
                                    (context as? OnBoardingActivity)?.finish()
                                }
                            }
                        },
                        modifier = Modifier
                            .padding(24.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(
                                        uiColor.accentOrangeStart,
                                        uiColor.accentOrangeEnd
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        elevation = FloatingActionButtonDefaults.elevation(
                            0.dp,
                            pressedElevation = 0.dp
                        ),
                        icon = {
                            if (!isLoading) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = "Get Started",
                                    modifier = Modifier.size(24.dp)
                                )
                            }else{
                                LoadingIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        },
                        text = {
                            Text(
                                text = "Get Started"
                            )
                        }
                    )
                }
            }
            TextButton(
                onClick = {
                    scope.launch {
                        prefRepository.setOnboardingDone()
                        context.startActivity(Intent(context, UserSetupActivity::class.java))
                        (context as? OnBoardingActivity)?.finish()
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = uiColor.progressAccent
                ),
                modifier = Modifier
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    text = "Skip",
                )
            }
        }
    }
}