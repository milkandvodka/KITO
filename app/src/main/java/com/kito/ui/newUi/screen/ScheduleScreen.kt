package com.kito.ui.newUi.screen

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kito.ui.components.ExpressiveEasing
import com.kito.ui.components.UIColors
import com.kito.ui.components.animation.PandaSleepingAnimation
import com.kito.ui.components.formatTo12Hour
import com.kito.ui.components.meshGradient
import com.kito.ui.newUi.viewmodel.ScheduleScreenViewModel
import com.kito.ui.newUi.viewmodel.WeekDay
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalHazeApi::class, ExperimentalHazeMaterialsApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleScreenViewModel = hiltViewModel(),
) {
    val today = todayKey()
    val currentPage = when (today) {
        "MON" -> 0
        "TUE" -> 1
        "WED" -> 2
        "THU" -> 3
        "FRI" -> 4
        "SAT" -> 5
        else -> 5
    }
    val uiColors = UIColors()
    val coroutineScope = rememberCoroutineScope()
    val hazeState = rememberHazeState()
    val weekDays = WeekDay.entries
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {
            weekDays.size
        }
    )
    val schedule by viewModel.weeklySchedule.collectAsState()
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current
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
    var now by remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
            now = LocalTime.now()
    }
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
        modifier = Modifier.background(Color(0xFF121116))
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
                                    if(page == currentPage && isClassUpcoming(startTime = item.startTime,now = now)) {
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
                                    }else{
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
                                        if (page == currentPage && isClassOngoing(startTime = item.startTime, endTime = item.endTime, now = now)){
                                            Modifier.meshGradient(
                                                    points = listOf(

                                                        // ───── TOP ROW ─────
                                                        listOf(
                                                            Offset(0f, 0f) to meshColorAnimators[0].value,
                                                            Offset(0.25f, 0f) to meshColorAnimators[1].value,
                                                            Offset(0.5f, 0f) to meshColorAnimators[2].value,
                                                            Offset(0.75f, 0f) to meshColorAnimators[3].value,
                                                            Offset(1f, 0f) to meshColorAnimators[4].value,
                                                        ),

                                                        // ───── MIDDLE ROW (curved glow band) ─────
                                                        listOf(
                                                            Offset(-0.05f, 0.55f) to meshColorAnimators[5].value,
                                                            Offset(0.2f, animatedPointTop.value) to meshColorAnimators[6].value,
                                                            Offset(0.5f, 0.6f) to meshColorAnimators[7].value,
                                                            Offset(0.8f, animatedPointMid.value) to meshColorAnimators[8].value,
                                                            Offset(1.05f, 0.55f) to meshColorAnimators[9].value,
                                                        ),

                                                        // ───── BOTTOM ROW (independent animation per point) ─────
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
                                        }else{
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
                                                text = "${formatTo12Hour(item.startTime)} - ${
                                                    formatTo12Hour(
                                                        item.endTime
                                                    )
                                                }",
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
                                PandaSleepingAnimation()
                            }
                        }
                    }
                }
                item{
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
    Column(
        modifier = Modifier
            .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                blurRadius = 15.dp
                noiseFactor = 0.05f
                inputScale = HazeInputScale.Auto
                alpha = 0.98f
            }
            .fillMaxWidth(),
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
            Text(
                text = "Schedule",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = uiColors.textPrimary,
                style = MaterialTheme.typography.titleLargeEmphasized,
                modifier = Modifier
                    .weight(1f)
            )
            IconButton(
                onClick = {
                    val subject = Uri.encode("KIITO Schedule Report")
                    val body = Uri.encode("")

                    val intent = Intent(
                        Intent.ACTION_SENDTO,
                        "mailto:elabs.kiito@gmail.com?subject=$subject&body=$body".toUri()
                    )

                    context.startActivity(intent)
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.08f),
                    contentColor = Color(0xFFB32727)
                ),
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Report,
                    contentDescription = "Report",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        ) {
            itemsIndexed(weekDays) { index, label ->
                ToggleButton(
                    modifier = Modifier
                        .then(
                            if (index == currentPage && pagerState.currentPage != index){
                                    Modifier
                                        .zIndex(
                                            -1f
                                        )
                                        .dropShadow(
                                        shape = ButtonGroupDefaults.connectedMiddleButtonShapes().shape,
                                        shadow = Shadow(
                                        radius = 20.dp,
                                        color = uiColors.accentOrangeStart
                                    )
                                )
                            }else{
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

//                ToggleButton(
//                    checked = pagerState.currentPage == index,
//                    onCheckedChange = {
//                        coroutineScope.launch {
//                            pagerState.animateScrollToPage(index)
//                        }
//                    },
//                    colors = ToggleButtonDefaults.toggleButtonColors(
//                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
//                        checkedContainerColor = MaterialTheme.colorScheme.primary,
//                    )
//                ) {
//                    Text(label)
//                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
fun Modifier.horizontalCarouselTransition(page: Int, pagerState: PagerState) =
    graphicsLayer {
        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
        val scale = lerp(
            start = 0.92f,
            stop = 1f,
            fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
        )
        scaleX = scale
        scaleY = scale
        alpha = lerp(
            start = 0.5f,
            stop = 1f,
            fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
        )
    }

@RequiresApi(Build.VERSION_CODES.O)
private fun todayKey(): String =
    when (LocalDate.now().dayOfWeek) {
        DayOfWeek.MONDAY -> "MON"
        DayOfWeek.TUESDAY -> "TUE"
        DayOfWeek.WEDNESDAY -> "WED"
        DayOfWeek.THURSDAY -> "THU"
        DayOfWeek.FRIDAY -> "FRI"
        DayOfWeek.SATURDAY -> "SAT"
        else -> "MON"
    }

@RequiresApi(Build.VERSION_CODES.O)
private fun isClassOngoing(
    startTime: String,
    endTime: String,
    now: LocalTime
): Boolean {
    return try {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

        val start = LocalTime.parse(startTime, formatter)
        val end = LocalTime.parse(endTime, formatter)

        !now.isBefore(start) && !now.isAfter(end)
    } catch (_: Exception) {
        false
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun isClassUpcoming(
    startTime: String,
    windowMinutes: Long = 15,
    now: LocalTime
): Boolean {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val start = LocalTime.parse(startTime, formatter)

    val windowStart = start.minusMinutes(windowMinutes)

    return !now.isBefore(windowStart) && now.isBefore(start)
}