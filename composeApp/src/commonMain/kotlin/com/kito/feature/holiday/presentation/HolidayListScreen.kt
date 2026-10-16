package com.kito.feature.holiday.presentation

import com.kito.feature.holiday.domain.model.holidayList2026
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.kito.core.common.util.currentLocalDateTime
import com.kito.core.designsystem.SharedExpandContainer
import com.kito.core.designsystem.UIColors
import com.kito.core.platform.AppConfig
import com.kito.core.presentation.navigation3.Routes
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

private val currentMonthName = currentLocalDateTime().month.name
    .lowercase()
    .replaceFirstChar { it.uppercase() }

private val groupedHolidays = holidayList2026.groupBy { it.month }

private val holidayScrollIndex = run {
    var idx = 0
    for (key in groupedHolidays.keys) {
        if (key.contains(currentMonthName, ignoreCase = true)) break
        idx += 1 + (groupedHolidays[key]?.size ?: 0)
    }
    idx
}

@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalSharedTransitionApi::class)
@Composable
fun HolidayListScreen(
    onBack: () -> Unit = {}
) {
    val uiColors = remember { UIColors() }
    val listState = rememberLazyListState()
    val hazeState = rememberHazeState()
    LaunchedEffect(Unit) {
        delay(600)

        snapshotFlow {
            listState.layoutInfo.totalItemsCount > 0 &&
                    listState.layoutInfo.viewportEndOffset > 0
        }.first { it }

        if (holidayScrollIndex > 0) {
            val targetRestingOffset = 50f

            var prevScroll = 0f
            val anim = Animatable(0f)
            var exactTargetFound = false
            var finalTarget = 50000f

            try {
                anim.animateTo(
                    targetValue = finalTarget,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy, // Smooth, steady acceleration
                        stiffness = 15f
                    )
                ) {
                    val delta = value - prevScroll
                    prevScroll = value
                    listState.dispatchRawDelta(delta)

                    val item = listState.layoutInfo.visibleItemsInfo.find { it.index == holidayScrollIndex }

                    if (item != null && item.offset <= listState.layoutInfo.viewportEndOffset) {

                        val remainingPixels = item.offset - targetRestingOffset
                        finalTarget = this.value + remainingPixels
                        exactTargetFound = true

                        throw RuntimeException("TargetFound")
                    }
                }
            } catch (e: RuntimeException) {
                if (e.message == "TargetFound") {
                    anim.animateTo(
                        targetValue = finalTarget,
                        initialVelocity = anim.velocity,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) {
                        val delta = value - prevScroll
                        prevScroll = value
                        listState.dispatchRawDelta(delta)
                    }
                } else {
                    throw e
                }
            }
        }
    }

    SharedExpandContainer(
        routeKey = Routes.HolidayList,
        backgroundColor = Color(0xFF121116),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 70.dp,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 16.dp
            )
        ) {
            groupedHolidays.entries.forEachIndexed { groupIndex, (month, holidays) ->

                item(key = "header_$month") {
                    if (groupIndex > 0) Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f)
                            .then(
                                if (month.contains(currentMonthName, ignoreCase = true)) {
                                    Modifier.border(2.dp, uiColors.progressAccent, RoundedCornerShape(28.dp))
                                } else Modifier
                            ),
                        shape = RoundedCornerShape(28.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = getMonthImage(month),
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .layout { measurable, constraints ->
                                        val placeable = measurable.measure(
                                            constraints.copy(maxHeight = Int.MAX_VALUE)
                                        )
                                        layout(placeable.width, placeable.height) {
                                            placeable.place(0, 0)
                                        }
                                    }
                                    .graphicsLayer {
                                        // ✅ draw phase — zero recomposition
                                        val itemInfo = listState.layoutInfo.visibleItemsInfo
                                            .firstOrNull { it.key == "header_$month" }
                                        val viewportHeight = (
                                                listState.layoutInfo.viewportEndOffset -
                                                        listState.layoutInfo.viewportStartOffset
                                                ).toFloat()
                                        val cardOffset = itemInfo?.offset?.toFloat() ?: 0f
                                        val imageHeightPx = size.width * (21f / 9f)
                                        val translateY = (viewportHeight - imageHeightPx) - cardOffset
                                        scaleY = 1.2f
                                        translationY = translateY + 700f
                                    }
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(uiColors.backgroundBottom.copy(alpha = 0.45f))
                            )
                            Text(
                                text = month,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(20.dp),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                                color = uiColors.textPrimary,
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }
                    }
                }

                itemsIndexed(holidays) { index, holiday ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = if (index == 0) 8.dp else 2.5.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(
                            topStart    = if (index == 0)                24.dp else 4.dp,
                            topEnd      = if (index == 0)                24.dp else 4.dp,
                            bottomStart = if (index == holidays.lastIndex) 24.dp else 4.dp,
                            bottomEnd   = if (index == holidays.lastIndex) 24.dp else 4.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
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
                                .padding(horizontal = 16.dp, vertical = 18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(48.dp)
                                        .background(
                                            brush = Brush.verticalGradient(
                                                listOf(
                                                    uiColors.accentOrangeStart,
                                                    uiColors.accentOrangeEnd
                                                )
                                            ),
                                            shape = RoundedCornerShape(2.dp)
                                        )
                                )
                                Spacer(modifier = Modifier.width(12.dp))

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(70.dp)
                                ) {
                                    Text(
                                        text = holiday.startDate.take(2),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = uiColors.textPrimary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = holiday.month.take(3).uppercase(),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = uiColors.textSecondary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = holiday.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = uiColors.textPrimary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    if (holiday.numberOfDays > 1) {
                                        Text(
                                            text = "${holiday.startDate} - ${holiday.endDate}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = uiColors.textSecondary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    } else {
                                        Text(
                                            text = holiday.startDay,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = uiColors.textSecondary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    if (holiday.numberOfDays > 1) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "• ${holiday.numberOfDays} Days",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = uiColors.accentOrangeStart,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(
                    modifier = Modifier.height(
                        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                    blurRadius = 15.dp
                    noiseFactor = 0.05f
                    alpha = 0.98f
                }
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(
                modifier = Modifier.height(
                    16.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Holiday List",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = uiColors.textPrimary,
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun getMonthImage(month: String): String {
    return when {
        month.contains("January") -> "${AppConfig.cdnURL}january1_bg.webp"
        month.contains("February") -> "${AppConfig.cdnURL}feb_bg.webp"
        month.contains("March") -> "${AppConfig.cdnURL}march_bg.webp"
        month.contains("April") -> "${AppConfig.cdnURL}april_bg.webp"
        month.contains("May") -> "${AppConfig.cdnURL}may_bg.webp"
        month.contains("June") -> "${AppConfig.cdnURL}june_bg.webp"
        month.contains("July") -> "${AppConfig.cdnURL}july_bg.webp"
        month.contains("August") -> "${AppConfig.cdnURL}aug_bg.webp"
        month.contains("September") -> "${AppConfig.cdnURL}sept_bg.webp"
        month.contains("October") -> "${AppConfig.cdnURL}oct_bg.webp"
        month.contains("November") -> "${AppConfig.cdnURL}nov_bg.webp"
        month.contains("December") -> "${AppConfig.cdnURL}dec_bg.webp"
        else -> "${AppConfig.cdnURL}january1_bg.webp"
    }
}