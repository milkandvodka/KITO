package com.kito.feature.home.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import coil3.compose.AsyncImage
import com.kito.core.common.util.currentLocalDateTime
import com.kito.core.network.supabase.model.EventAndAdModel
import com.kito.core.platform.openUrl
import com.kito.core.platform.sendEmail
import com.kito.core.platform.toast
import com.kito.core.presentation.components.AboutELabsDialog
import com.kito.core.presentation.components.AttendanceBarCard
import com.kito.core.presentation.components.ScheduleCard
import com.kito.core.presentation.components.UIColors
import com.kito.core.presentation.components.UtilityCard
import com.kito.core.presentation.components.state.SyncUiState
import com.kito.core.presentation.navigation3.Routes
import com.kito.core.presentation.navigation3.TabRoutes
import com.kito.core.presentation.navigation3.isTopAsState
import com.kito.core.presentation.navigation3.navigateTab
import com.kito.feature.schedule.presentation.horizontalCarouselTransition
import com.kito.feature.settings.presentation.components.LoginDialogBox
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kito.composeapp.generated.resources.Res
import kito.composeapp.generated.resources.e_labs_logo
import kito.composeapp.generated.resources.header
import kito.composeapp.generated.resources.ic_instagram
import kotlinx.coroutines.delay
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalHazeApi::class,
    ExperimentalHazeMaterialsApi::class
)
@Composable
fun HomeScreen(
    viewmodel: HomeViewModel = koinInject(),
    rootNavBackStack: NavBackStack<NavKey>,
    tabNavBackStack: NavBackStack<NavKey>,
) {
    var showAboutDialog by remember { mutableStateOf(false) }
    val uiColors = UIColors()
    val name by viewmodel.name.collectAsState()
    val sapLoggedIn by viewmodel.sapLoggedIn.collectAsState()
    val attendance by viewmodel.attendance.collectAsState()
    val schedule by viewmodel.schedule.collectAsState()
    val nextSchedule by viewmodel.nextSchedule.collectAsState()
    val syncState by viewmodel.syncState.collectAsState()
    val hazeState = rememberHazeState()
    val haptic = LocalHapticFeedback.current
    var isLoginDialogOpen by remember { mutableStateOf(false) }
    val loginState by viewmodel.loginState.collectAsState()
    val isOnline by viewmodel.isOnline.collectAsState()
    val isTabTop by tabNavBackStack.isTopAsState(TabRoutes.Home)
    val isRootTop by rootNavBackStack.isTopAsState(Routes.Tabs)
    val isTopScreen = isTabTop && isRootTop
    val lifecycleOwner = LocalLifecycleOwner.current
    val eventsAndAds by viewmodel.ads.collectAsState()
    val isScheduleEmpty by viewmodel.isScheduleEmpty.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is SyncUiState.Success) {
            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
            isLoginDialogOpen = false
            viewmodel.setLoginStateIdle()
        }
    }

    LaunchedEffect(Unit,isTopScreen,lifecycleOwner) {
        if (isTopScreen) {
            val today = currentLocalDateTime().dayOfWeek
            val dayString = when (today) {
                DayOfWeek.MONDAY -> "MON"
                DayOfWeek.TUESDAY -> "TUE"
                DayOfWeek.WEDNESDAY -> "WED"
                DayOfWeek.THURSDAY -> "THU"
                DayOfWeek.FRIDAY -> "FRI"
                DayOfWeek.SATURDAY -> "SAT"
                DayOfWeek.SUNDAY -> "SUN"
            }
            viewmodel.updateDay(dayString)
        }
        lifecycleOwner.lifecycle.repeatOnLifecycle(
            Lifecycle.State.STARTED
        ) {
            val today = currentLocalDateTime().dayOfWeek
            val dayString = when (today) {
                DayOfWeek.MONDAY -> "MON"
                DayOfWeek.TUESDAY -> "TUE"
                DayOfWeek.WEDNESDAY -> "WED"
                DayOfWeek.THURSDAY -> "THU"
                DayOfWeek.FRIDAY -> "FRI"
                DayOfWeek.SATURDAY -> "SAT"
                DayOfWeek.SUNDAY -> "SUN"
            }
            viewmodel.updateDay(dayString)
        }
    }

    LaunchedEffect(Unit) {
        delay(1000)
        if (isOnline) {
            viewmodel.syncOnStartup()
        }else{
            toast("No Internet Connection")
        }
    }

    LaunchedEffect(Unit) {
        viewmodel.syncEvents.collect { event ->
            when (event) {
                is SyncUiState.Success -> {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                    toast("Sync completed")
                }
                is SyncUiState.Error -> {
                    haptic.performHapticFeedback(HapticFeedbackType.Reject)
                    toast(event.message)
                }
                is SyncUiState.Loading -> {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                }
                else -> {}
            }
        }
    }

    Box() {
        Box(
            modifier = Modifier
                .hazeSource(hazeState)
        ) {
            Box(
                Modifier
                    .background(Color(0xFF121116))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
//                        .padding(horizontal = 12.dp)
                ) {
                    LazyColumn() {
                        item {
                            Spacer(
                                modifier = Modifier.height(
                                    72.dp + WindowInsets.statusBars.asPaddingValues()
                                        .calculateTopPadding()
                                )
                            )
                        }

                        item {
                            AnimatedVisibility(syncState is SyncUiState.Loading) {
                                Column {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearWavyProgressIndicator(
                                        color = uiColors.accentOrangeStart,
                                        trackColor = uiColors.progressAccent,
                                        modifier = Modifier.fillMaxWidth(),
                                        waveSpeed = 5.dp,
                                        wavelength = 70.dp,
                                    )
                                }
                            }
                        }


                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = "Schedule",
                                    color = uiColors.textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                                if (false) {
                                    IconButton(
                                        onClick = {

                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (false) {
                                                Icons.Default.NotificationsActive
                                            } else {
                                                Icons.Outlined.NotificationsOff
                                            },
                                            contentDescription = "notifications",
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                IconButton(
                                    onClick = {
                                        sendEmail(
                                            to = "elabs.kiito@gmail.com",
                                            subject = "KIITO Schedule Report",
                                            body = ""
                                        )
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        contentColor = Color(0xFFB32727)
                                    ),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Report,
                                        contentDescription = "Report",
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                        rootNavBackStack.add(
                                            Routes.Schedule
                                        )
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                                        contentDescription = "Notifications",
                                        tint = uiColors.textPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }



                        item {
                            Spacer(Modifier.height(8.dp))
                        }

                        item {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                            ) {
                                ScheduleCard(
                                    colors = uiColors,
                                    schedule = schedule,
                                    nextSchedule = nextSchedule,
                                    isScheduleEmpty = isScheduleEmpty,
                                    onCLick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                        rootNavBackStack.add(Routes.Schedule)
                                    }
                                )
                            }
                        }
                        item {
                            Spacer(Modifier.height(8.dp))
                        }
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Text(
                                        text = "Utilities",
                                        color = uiColors.textPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (false) {
                                        IconButton(
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                                tabNavBackStack.navigateTab(TabRoutes.Attendance)
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                                                contentDescription = "Back",
                                                tint = uiColors.textPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                Spacer(Modifier.height(8.dp))
                            }

                            item {
                                Box(
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                ){
                                    UtilityCard(
                                        onCLick = { navKey, url ->
                                            when {
                                                url != null -> openUrl(url)
                                                navKey != null -> rootNavBackStack.add(navKey)
                                            }
                                        }
                                    )
                                }
                            }

                            item {
                                Spacer(Modifier.height(8.dp))
                            }

                        if (eventsAndAds.isNotEmpty()) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Text(
                                        text = "Events",
                                        color = uiColors.textPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier
                                            .weight(1f)
                                    )
                                }
                            }

                            item {
                                Spacer(Modifier.height(8.dp))
                            }

                            item {
                                EventAndAdBanner(
                                    eventsAndAds = eventsAndAds,
                                    onClick = {url, isAd ->
                                        if (isAd) {
                                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                            rootNavBackStack.add(
                                                Routes.Promotions(
                                                    url = url
                                                )
                                            )
                                        }else{
                                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                            openUrl(url)
                                        }
                                    }
                                )
                            }

                            item {
                                Spacer(Modifier.height(8.dp))
                            }
                        }

                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = "Attendance",
                                    color = uiColors.textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                        tabNavBackStack.navigateTab(TabRoutes.Attendance)
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                                        contentDescription = "Back",
                                        tint = uiColors.textPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(8.dp))
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(horizontal = 12.dp)
                            ) {
                                AttendanceBarCard(
                                    attendance = attendance,
                                    onNavigate = {
                                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                        tabNavBackStack.navigateTab(TabRoutes.Attendance)
                                    },
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                        isLoginDialogOpen = true
                                    },
                                    sapLoggedIn = sapLoggedIn
                                )
                            }
                        }
                        item {
                            Spacer(
                                modifier = Modifier.height(
                                    86.dp + WindowInsets.navigationBars.asPaddingValues()
                                        .calculateBottomPadding()
                                )
                            )
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                    blurRadius = 15.dp
                    noiseFactor = 0.05f
                    inputScale = HazeInputScale.Auto
                    alpha = 0.98f
                }
                .padding(horizontal = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        top = 8.dp + WindowInsets.statusBars.asPaddingValues()
                            .calculateTopPadding()
                    )
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = "Welcome",
                        color = uiColors.progressAccent,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.titleMediumEmphasized
                    )
                    Text(
                        text = name.trim().substringBefore(" "),
                        color = uiColors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.headlineLargeEmphasized,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                val infiniteTransition = rememberInfiniteTransition()
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 5000
                            1f at 0 // Wait 4.5s
                            1f at 4500
                            1.2f at 4750 // Scale up
                            1f at 5000 // Scale back down
                        },
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "InstagramScale"
                )

                val shimmerOffset by infiniteTransition.animateFloat(
                    initialValue = -100f,
                    targetValue = -100f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 5000
                            -100f at 0
                            -100f at 4500
                            150f at 5000 // Sweep across
                        },
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "InstagramShimmer"
                )

                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        openUrl("https://www.instagram.com/kiito.app?igsh=MTkzajlteWM5dzhldg==")
                    },
                    modifier = Modifier.size(52.dp).scale(scale)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_instagram),
                        contentDescription = "Instagram",
                        tint = uiColors.accentOrangeStart,
                        modifier = Modifier
                            .size(34.dp)
                            .drawWithContent {
                                drawContent()
                                val shimmerBrush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.5f),
                                        Color.White.copy(alpha = 0.8f),
                                        Color.White.copy(alpha = 0.5f),
                                        Color.Transparent
                                    ),
                                    start = Offset(shimmerOffset, shimmerOffset - 50f),
                                    end = Offset(shimmerOffset + 50f, shimmerOffset + 50f)
                                )
                                drawRect(brush = shimmerBrush)
                            }
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        showAboutDialog = !showAboutDialog
                    },
                    modifier = Modifier.size(60.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.e_labs_logo),
                        contentDescription = "Logo",
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
    if (showAboutDialog) {
        AboutELabsDialog(
            onDismiss = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                showAboutDialog = false
            },

            hazeState = hazeState
        )
    }
    if (isLoginDialogOpen){
        LoginDialogBox(
            onDismiss = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isLoginDialogOpen = false
                viewmodel.setLoginStateIdle()
            },
            onConfirm = { sapPassword->
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                viewmodel.login(sapPassword)
            },
            syncState = loginState,
            hazeState = hazeState
        )
    }
}


@Composable
fun JoinELabsBanner(
    colors: UIColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Check if we should show the banner (before or on Feb 22, 2026)
    val currentDate = currentLocalDateTime().date
    val recruitmentStartDate = LocalDate(2026, 2, 21)
    val recruitmentEndDate = LocalDate(2026, 2, 22)
    val shouldShowBanner = currentDate <= recruitmentEndDate

    if (!shouldShowBanner) {
        return // Don't render anything after Feb 22
    }

    // Calculate countdown
    val isRecruitmentLive = currentDate >= recruitmentStartDate && currentDate <= recruitmentEndDate
    val daysUntilRecruitment = if (!isRecruitmentLive) {
        val currentInstant = currentDate.atStartOfDayIn(TimeZone.currentSystemDefault())
        val recruitmentInstant = recruitmentStartDate.atStartOfDayIn(TimeZone.currentSystemDefault())
        val diff = recruitmentInstant.minus(currentInstant).inWholeDays
        diff.toInt()
    } else {
        0
    }

    // Carousel state: 0 = Logo, 1 = Header, 2 = Countdown
    var currentSlide by remember { mutableStateOf(0) }
    val totalSlides = 3

    // Switch slides every 2.5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            currentSlide = (currentSlide + 1) % totalSlides
        }
    }

    // Infinite animations
    val infiniteTransition = rememberInfiniteTransition()

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        colors.cardBackground,
                        colors.cardBackgroundHigh,
                        colors.cardBackground
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        colors.progressAccent.copy(alpha = 0.4f + colorShift * 0.4f),
                        colors.accentOrangeStart.copy(alpha = 0.6f + colorShift * 0.4f),
                        colors.accentOrangeEnd.copy(alpha = 0.5f + colorShift * 0.3f),
                        colors.accentOrangeStart.copy(alpha = 0.6f + colorShift * 0.4f),
                        colors.progressAccent.copy(alpha = 0.4f + colorShift * 0.4f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .drawWithContent {
                drawContent()

                // Animated shimmer overlay
                val shimmerBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        colors.accentOrangeStart.copy(alpha = 0.3f),
                        colors.accentOrangeEnd.copy(alpha = 0.5f),
                        colors.accentOrangeStart.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    start = Offset(shimmerOffset - 200f, 0f),
                    end = Offset(shimmerOffset + 200f, size.height),
                    tileMode = TileMode.Clamp
                )

                drawRect(
                    brush = shimmerBrush,
                    size = size
                )
            }
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        // Animated content with fade transitions
        AnimatedContent(
            targetState = currentSlide,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                ).togetherWith(
                    fadeOut(
                        animationSpec = tween(600, easing = FastOutSlowInEasing)
                    )
                )
            },
            modifier = Modifier.align(Alignment.Center)
        ) { slide ->
            when (slide) {
                0 -> {
                    // Logo slide
                    Image(
                        painter = painterResource(Res.drawable.header),
                        contentDescription = "E-Labs Header",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                1 -> {
                    // Header slide with logo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.e_labs_logo),
                            contentDescription = "E-Labs Logo",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "JOIN E-LABS",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp,
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.Serif
                            ),
                            color = colors.accentOrangeStart
                        )
                    }
                }
                2 -> {
                    // Countdown slide
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (isRecruitmentLive) {
                            Text(
                                text = "🔴 RECRUITMENT IS LIVE",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 23.sp,
                                    letterSpacing = 1.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = colors.accentOrangeEnd
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Join us now!",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = colors.textSecondary
                            )
                        } else {
                            Text(
                                text = when {
                                    daysUntilRecruitment == 1 -> "1 DAY TO GO"
                                    daysUntilRecruitment > 1 -> "$daysUntilRecruitment DAYS TO GO"
                                    else -> "STARTING SOON"
                                },
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 25.sp,
                                    letterSpacing = 1.5.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = colors.accentOrangeStart
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Feb 21-22, 2026",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EventAndAdBanner(
    eventsAndAds: List<EventAndAdModel>,
    modifier: Modifier = Modifier,
    onClick: (
        url : String,
        isAd : Boolean
    ) -> Unit
) {
    if (eventsAndAds.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { eventsAndAds.size })

    LaunchedEffect(eventsAndAds) {
        while (true) {
            delay(3000)
            val next = (pagerState.currentPage + 1) % eventsAndAds.size
            pagerState.animateScrollToPage(next)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            contentPadding = PaddingValues(
                horizontal = if (
                    eventsAndAds.size > 1
                ){
                    24.dp
                }else{
                    12.dp
                }
            ),
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(6f)
        ) { page ->
            val eventOrAd = eventsAndAds[page]
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalCarouselTransition(
                        page = page,
                        pagerState = pagerState,
                        scale = 0.90f
                    ),
                shape = RoundedCornerShape(16.dp),
                onClick = {
                    eventOrAd.let {
                        onClick(
                            it.click_url?:"",
                            it.isAd?: false
                        )
                    }
                }
            ) {
                Box() {
                    AsyncImage(
                        model = eventOrAd.media_url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (eventOrAd.isAd == true) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(horizontal = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Gray.copy(
                                    alpha = 0.3f
                                )
                            )
                        ) {
                            Text(
                                text = "Sponsor",
                                style = MaterialTheme.typography.labelSmallEmphasized,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        // Dot indicators
        if (eventsAndAds.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(eventsAndAds.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (pagerState.currentPage == index) 8.dp else 5.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index)
                                    Color(0xFFFF6B35)
                                else
                                    Color.Gray.copy(alpha = 0.5f)
                            )
                    )
                }
            }
        }
    }
}