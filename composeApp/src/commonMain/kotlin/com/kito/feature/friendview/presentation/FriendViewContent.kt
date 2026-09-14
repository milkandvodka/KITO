package com.kito.feature.friendview.presentation

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp.Companion.Hairline
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kito.core.common.util.currentLocalDateTime
import com.kito.core.common.util.formatTo12Hour
import com.kito.core.designsystem.ExpressiveEasing
import com.kito.core.designsystem.UIColors
import com.kito.core.designsystem.meshGradient
import com.kito.core.platform.sendEmail
import com.kito.core.presentation.components.animation.PageNotFoundAnimation
import com.kito.core.presentation.components.animation.PandaSleepingAnimation
import com.kito.feature.friendview.domain.model.FriendScheduleItem
import com.kito.feature.friendview.presentation.components.AddFriendDialog
import com.kito.feature.friendview.presentation.components.DropdownItem
import com.kito.feature.schedule.presentation.WeekDay
import com.kito.feature.schedule.presentation.components.horizontalCarouselTransition
import com.kito.feature.schedule.presentation.components.isClassOngoing
import com.kito.feature.schedule.presentation.components.isClassUpcoming
import com.kito.feature.schedule.presentation.components.todayKey
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import kotlin.random.Random

@OptIn(ExperimentalHazeApi::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class
)
@Composable
fun FriendViewContent(
    selectedRoll: String,
    friendRolls: List<String>,
    schedule: Map<WeekDay, List<FriendScheduleItem>>,
    onBack: () -> Unit,
    onSelectFriend: (String) -> Unit,
    onRemoveFriend: (String) -> Unit,
    onAddFriend: (String) -> Unit,
    enableAnimations: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showDropdown by remember { mutableStateOf(false) }
    var showAddFriendDialog by remember { mutableStateOf(false) }
    val hazeState = rememberHazeState()
    val uiColors = UIColors()
    val coroutineScope = rememberCoroutineScope()
    val weekDays = WeekDay.entries
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {
            weekDays.size
        }
    )
    val today = todayKey()
    val currentPage = when (today) {
        "MON" -> 0
        "TUE" -> 1
        "WED" -> 2
        "THU" -> 3
        "FRI" -> 4
        "SAT" -> 5
        else -> 0
    }
    val haptics = LocalHapticFeedback.current

    val meshColors = listOf(
        Color(0xFF77280F).copy(alpha = 0.82f), // burnt orange
        Color(0xFF753107).copy(alpha = 0.82f), // amber-700
        Color(0xFF62290A).copy(alpha = 0.82f), // amber-800
        Color(0xFF46180C).copy(alpha = 0.82f), // deep orange-brown

        // 🔥 new additions (subtle!)
        Color(0xFFA14B09).copy(alpha = 0.70f), // muted yellow (amber-500 toned down)
        Color(0xFF6B1414).copy(alpha = 0.75f), // brick red (not crimson)
    )
    val animatedPointMid = remember { Animatable(.8f) }
    val animatedPointTop = remember { Animatable(.8f) }
    val meshColorAnimators = remember {
        List(15) { index ->
            Animatable(meshColors[index % meshColors.size])
        }
    }
    var now by remember {
        val dt = currentLocalDateTime()
        mutableStateOf(LocalTime(dt.hour, dt.minute, dt.second))
    }
    LaunchedEffect(Unit) {
        val dt = currentLocalDateTime()
        now = LocalTime(dt.hour, dt.minute, dt.second)
    }
    if (enableAnimations) {
        LaunchedEffect(Unit) {
            meshColorAnimators.forEachIndexed { i, anim ->
                launch {
                    val random = Random(i * 97)
                    while (true) {
                        val nextColor = meshColors[random.nextInt(meshColors.size)]
                        anim.animateTo(
                            targetValue = nextColor,
                            animationSpec = tween(
                                durationMillis = random.nextInt(1800, 4200),
                                easing = LinearOutSlowInEasing
                            )
                        )
                    }
                }
            }
        }
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .drop(1) // skip initial emission
            .distinctUntilChanged()
            .collect {
                haptics.performHapticFeedback(
                    HapticFeedbackType.Confirm
                )
            }
    }
    LaunchedEffect(Unit) {
        delay(100)
        pagerState.animateScrollToPage(
            page = currentPage,
            animationSpec = tween(
                durationMillis = 800,
                easing = ExpressiveEasing.Emphasized
            )
        )
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121116))
            .semantics { testTag = "friendview_content" }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121116))
                .hazeSource(hazeState)
        ) {
            HorizontalPager(
                contentPadding = PaddingValues(
                    start = 28.dp,
                    end = 28.dp,
                ),
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
            ) { page ->
                val day = weekDays[page]
                val daySchedule = schedule[day].orEmpty()
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(2.5.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalCarouselTransition(page, pagerState),
                ) {
                    item {
                        Spacer(
                            modifier = Modifier.height(
                                WindowInsets.statusBars.asPaddingValues()
                                    .calculateTopPadding() + 132.dp
                            )
                        )
                    }
                    if (daySchedule.isNotEmpty()) {
                        itemsIndexed(daySchedule) { index, item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .then(
                                        if (page == currentPage && isClassUpcoming(
                                                startTime = item.startTime,
                                                now = now
                                            ) && today != "SUN"
                                        ) {
                                            Modifier
                                                .border(
                                                    width = 2.dp,
                                                    brush = Brush.verticalGradient(
                                                        colors = listOf(
                                                            uiColors.progressAccent,
                                                            uiColors.progressAccent
                                                        )
                                                    ),
                                                    shape = RoundedCornerShape(
                                                        topStart = if (index == 0) 24.dp else 4.dp,
                                                        topEnd = if (index == 0) 24.dp else 4.dp,
                                                        bottomStart = if (index == daySchedule.size - 1) 24.dp else 4.dp,
                                                        bottomEnd = if (index == daySchedule.size - 1) 24.dp else 4.dp
                                                    )
                                                )
                                        } else {
                                            Modifier
                                        }
                                    ),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                shape = RoundedCornerShape(
                                    topStart = if (index == 0) 24.dp else 4.dp,
                                    topEnd = if (index == 0) 24.dp else 4.dp,
                                    bottomStart = if (index == daySchedule.size - 1) 24.dp else 4.dp,
                                    bottomEnd = if (index == daySchedule.size - 1) 24.dp else 4.dp
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .then(
                                            if (page == currentPage && isClassOngoing(
                                                    startTime = item.startTime,
                                                    endTime = item.endTime,
                                                    now = now
                                                ) && today != "SUN"
                                            ) {
                                                Modifier.meshGradient(
                                                    points = listOf(
                                                        listOf(
                                                            Offset(0f, 0f) to meshColorAnimators[0].value,
                                                            Offset(0.25f, 0f) to meshColorAnimators[1].value,
                                                            Offset(0.5f, 0f) to meshColorAnimators[2].value,
                                                            Offset(0.75f, 0f) to meshColorAnimators[3].value,
                                                            Offset(1f, 0f) to meshColorAnimators[4].value,
                                                        ),
                                                        listOf(
                                                            Offset(-0.05f, 0.55f) to meshColorAnimators[5].value,
                                                            Offset(0.2f, animatedPointTop.value) to meshColorAnimators[6].value,
                                                            Offset(0.5f, 0.6f) to meshColorAnimators[7].value,
                                                            Offset(0.8f, animatedPointMid.value) to meshColorAnimators[8].value,
                                                            Offset(1.05f, 0.55f) to meshColorAnimators[9].value,
                                                        ),
                                                        listOf(
                                                            Offset(0f, 1f) to meshColorAnimators[10].value,
                                                            Offset(0.25f, 1f) to meshColorAnimators[11].value,
                                                            Offset(0.5f, 1f) to meshColorAnimators[12].value,
                                                            Offset(0.75f, 1f) to meshColorAnimators[13].value,
                                                            Offset(1f, 1f) to meshColorAnimators[14].value,
                                                        ),
                                                    ),
                                                    resolutionX = 30
                                                )
                                            } else {
                                                Modifier.background(
                                                    brush = Brush.linearGradient(
                                                        colors = listOf(
                                                            uiColors.cardBackground,
                                                            Color(0xFF2F222F),
                                                            Color(0xFF2F222F),
                                                            uiColors.cardBackgroundHigh
                                                        )
                                                    )
                                                )
                                            }
                                        )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxSize()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(4.dp)
                                                .height(48.dp)
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(
                                                            uiColors.accentOrangeStart,
                                                            uiColors.accentOrangeEnd
                                                        )
                                                    ),
                                                    RoundedCornerShape(2.dp)
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Column(
                                                verticalArrangement = Arrangement.Center,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(vertical = 6.dp)
                                                    .weight(1f)
                                            ) {
                                                Text(
                                                    text = item.subject,
                                                    color = uiColors.textPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.Monospace,
                                                    style = MaterialTheme.typography.headlineSmallEmphasized,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${formatTo12Hour(item.startTime)} - ${formatTo12Hour(item.endTime)}",
                                                    color = uiColors.textPrimary.copy(alpha = 0.85f),
                                                    style = MaterialTheme.typography.labelLargeEmphasized,
                                                    fontFamily = FontFamily.Monospace,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Text(
                                                text = item.room ?: "No Room",
                                                color = uiColors.textPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                style = MaterialTheme.typography.titleMediumEmphasized,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .height(600.dp)
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    uiColors.cardBackground,
                                                    Color(0xFF2F222F),
                                                    Color(0xFF2F222F),
                                                    uiColors.cardBackgroundHigh
                                                )
                                            )
                                        )
                                ) {
                                    if (friendRolls.isEmpty()){
                                        PageNotFoundAnimation()
                                    }else{
                                        PandaSleepingAnimation()
                                    }
                                }
                            }
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
        if (friendRolls.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                        blurRadius = 15.dp
                        noiseFactor = 0.05f
                        inputScale = HazeInputScale.Auto
                        alpha = 0.98f
                    }
                    .semantics { testTag = "friendview_empty" },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent().changes.forEach {
                                        it.consume()
                                    }
                                }
                            }
                        }
                )
                Button(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                        showAddFriendDialog = true
                    },
                    modifier = Modifier.align(Alignment.Center),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = uiColors.progressAccent,
                        contentColor = uiColors.textPrimary
                    )
                ) {
                    Text(
                        text = " + Add Friend",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelMediumEmphasized
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                    blurRadius = 15.dp
                    noiseFactor = 0.05f
                    inputScale = HazeInputScale.Auto
                    alpha = 0.98f
                }
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(
                    modifier = Modifier.height(
                        16.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            contentColor = uiColors.progressAccent
                        ),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "pop back stack",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Friend View",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = uiColors.textPrimary,
                        style = MaterialTheme.typography.titleLargeEmphasized,
                        modifier = Modifier
                            .weight(1f),
                        overflow = TextOverflow.Ellipsis
                    )
                    if (friendRolls.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = {
                                    showDropdown = !showDropdown
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = Color.White.copy(alpha = 0.08f),
                                ),
                                modifier = Modifier.height(32.dp),
                                contentPadding = PaddingValues(start = 8.dp, end = 4.dp),
                            ) {
                                Text(
                                    text = selectedRoll,
                                    fontFamily = FontFamily.Monospace,
                                    color = uiColors.textPrimary,
                                    style = MaterialTheme.typography.bodySmallEmphasized
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (!showDropdown) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropUp,
                                    contentDescription = "Drop Down",
                                    modifier = Modifier
                                        .size(20.dp),
                                    tint = uiColors.progressAccent
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    sendEmail(
                                        to = "kiito.admin@gmail.com",
                                        subject = "KIITO Schedule Report",
                                        body = ""
                                    )
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color.White.copy(alpha = 0.08f),
                                    contentColor = Color(0xFFB32727)
                                ),
                                modifier = Modifier.size(32.dp)
                              ) {
                                Icon(
                                    imageVector = Icons.Default.Report,
                                    contentDescription = "Report",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = ButtonGroupDefaults.ConnectedSpaceBetween,
                        alignment = Alignment.CenterHorizontally
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(weekDays) { index, label ->
                        ToggleButton(
                            modifier = Modifier
                                .then(
                                    if (index == currentPage && pagerState.currentPage != index) {
                                        Modifier
                                            .zIndex(
                                                -2f
                                            )
                                            .dropShadow(
                                                shape = ButtonGroupDefaults.connectedMiddleButtonShapes().shape,
                                                shadow = Shadow(
                                                    radius = 20.dp,
                                                    color = uiColors.accentOrangeStart
                                                )
                                            )
                                    } else {
                                        Modifier
                                    }
                                ),
                            checked = pagerState.currentPage == index,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            shapes =
                                when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    weekDays.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                },
                            colors = ToggleButtonDefaults.toggleButtonColors(
                                containerColor = uiColors.cardBackground,
                                checkedContainerColor = uiColors.progressAccent,
                            )
                        ) {
                            Text(
                                text = label.toString(),
                                style = MaterialTheme.typography.bodySmallEmphasized,
                                color = uiColors.textPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        AnimatedVisibility(
            visible = showDropdown,
            enter =
                fadeIn() +
                        expandVertically(
                            expandFrom = Alignment.Top,
                            animationSpec = tween()
                        ),
            exit =
                fadeOut() +
                        shrinkVertically(
                            shrinkTowards = Alignment.Top
                        ),
            modifier = Modifier
                .clip(
                    RoundedCornerShape(16.dp)
                )
                .padding(
                    top = WindowInsets().asPaddingValues().calculateTopPadding() + 90.dp,
                    end = 16.dp
                )
                .align(Alignment.TopEnd)
                .zIndex(10f)
        ) {
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .clip(
                        RoundedCornerShape(16.dp)
                    )
                    .hazeEffect(
                        state = hazeState,
                        style = HazeMaterials.ultraThin()
                    ) {
                        blurRadius = 15.dp
                        noiseFactor = 0.05f
                        inputScale = HazeInputScale.Auto
                        alpha = 0.98f
                    }
                    .border(
                        Hairline,
                        Color.White.copy(alpha = 0.15f),
                        RoundedCornerShape(16.dp),
                    )
            ) {
                Column {
                    friendRolls.forEach {
                        DropdownItem(
                            text = it,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onSelectFriend(it)
                                showDropdown = false
                            },
                            onRemove = {
                                haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onRemoveFriend(it)
                                showDropdown = false
                            },
                            isRemove = true
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier
                    )
                    DropdownItem(
                        text ="Add Friend",
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                            showDropdown = false
                            showAddFriendDialog = true
                        }
                    )
                }
            }
        }
    }
    if (showAddFriendDialog) {
        AddFriendDialog(
            onDismiss = {
                haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                showAddFriendDialog = false
            },
            onConfirm = { rollNumber ->
                haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                onAddFriend(rollNumber)
                showAddFriendDialog = false
            },
            hazeState = hazeState
        )
    }
}

@Preview
@Composable
private fun FriendViewContentPreview() {
    FriendViewContent(
        selectedRoll = "2205001",
        friendRolls = listOf("2205001", "2205002"),
        schedule = emptyMap(),
        onBack = {},
        onSelectFriend = {},
        onRemoveFriend = {},
        onAddFriend = {}
    )
}
