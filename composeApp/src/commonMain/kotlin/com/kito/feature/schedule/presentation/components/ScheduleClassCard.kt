package com.kito.feature.schedule.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kito.core.common.util.formatTo12Hour
import com.kito.core.designsystem.UIColors
import com.kito.core.designsystem.meshGradient
import com.kito.feature.schedule.domain.model.ScheduleItem
import kotlinx.datetime.LocalTime

@Composable
fun ScheduleClassCard(
    item: ScheduleItem,
    index: Int,
    listSize: Int,
    page: Int,
    currentPage: Int,
    today: String,
    now: LocalTime,
    uiColors: UIColors,
    meshColorAnimators: List<Animatable<Color, AnimationVector4D>>,
    animatedPointMid: Animatable<Float, *>,
    animatedPointTop: Animatable<Float, *>,
    isClassUpcoming: (String, LocalTime) -> Boolean,
    isClassOngoing: (String, String, LocalTime) -> Boolean
) {
    val isFirstYear = item.batch.equals("batch_1", ignoreCase = true) ||
            item.batch.trim() == "1" ||
            item.section.matches(Regex("^[AB]\\d+.*", RegexOption.IGNORE_CASE))
    val isLongLocation = item.room != null && (item.room.contains("/") || item.room.length > 13)
    val showLocationBelow = isFirstYear && isLongLocation

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (showLocationBelow) 116.dp else 100.dp)
            .then(
                if (page == currentPage && isClassUpcoming(item.startTime, now) && today != "SUN") {
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
                                bottomStart = if (index == listSize - 1) 24.dp else 4.dp,
                                bottomEnd = if (index == listSize - 1) 24.dp else 4.dp
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
            bottomStart = if (index == listSize - 1) 24.dp else 4.dp,
            bottomEnd = if (index == listSize - 1) 24.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (page == currentPage && isClassOngoing(item.startTime, item.endTime, now) && today != "SUN") {
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
            val isFirstYear = item.batch.equals("batch_1", ignoreCase = true) ||
                    item.batch.trim() == "1" ||
                    item.section.matches(Regex("^[AB]\\d+.*", RegexOption.IGNORE_CASE))
            val isLongLocation = item.room != null && (item.room.contains("/") || item.room.length > 13)
            val showLocationBelow = isFirstYear && isLongLocation

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(if (showLocationBelow) 68.dp else 48.dp)
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
                Spacer(modifier = Modifier.width(10.dp))
                if (showLocationBelow) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        Text(
                            text = item.subject,
                            color = uiColors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "${formatTo12Hour(item.startTime)} - ${formatTo12Hour(item.endTime)}",
                            color = uiColors.textPrimary.copy(alpha = 0.85f),
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = item.room ?: "No Room",
                            color = uiColors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
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
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${formatTo12Hour(item.startTime)} - ${formatTo12Hour(item.endTime)}",
                                color = uiColors.textPrimary.copy(alpha = 0.85f),
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
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
