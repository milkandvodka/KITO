package com.kito.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun CalendarMonthUI(
    colors: UIColors,
    onDayClick: (Int, String) -> Unit
) {
    val uiColors = UIColors()
    val dayNames = listOf(
        "Monday", "Tuesday", "Wednesday",
        "Thursday", "Friday", "Saturday", "Sunday"
    )

    Box(
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {

            Text(
                "DEC",
                color = colors.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val weekDays = listOf("S", "M", "T", "W", "T", "F", "S")
                repeat(7) { index ->
                    Text(
                        text = weekDays[index],
                        color = colors.textSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                repeat(6) { row ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        repeat(7) { col ->
                            Spacer(modifier = Modifier.width(2.dp))
                            val index = row * 7 + col
                            val dayNumber = (index % 31) + 1
                            val dayName = dayNames[dayNumber % 7]

                            Card(
                                modifier = Modifier.weight(1f).fillMaxSize(),
                                onClick = {
                                    onDayClick(dayNumber, dayName)
                                },
                                shape = RoundedCornerShape(
                                    topStart = if (index == 0) 24.dp else 4.dp,
                                    topEnd = if (index == 6) 24.dp else 4.dp,
                                    bottomStart = if (index == 35) 24.dp else 4.dp,
                                    bottomEnd = if (index == 41) 24.dp else 4.dp
                                ),
                                colors = CardDefaults.cardColors(containerColor = uiColors.cardBackground)
                            ) {
                                Column(
                                ) {
                                    Text(
                                        text = dayNumber.toString(),
                                        color = colors.textPrimary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelLargeEmphasized,
                                    )
                                    if (index % 7 != 0 && index % 7 != 6) {
                                        repeat(4) {
                                            Card(
                                                shape = RoundedCornerShape(
                                                    topStart = if (it == 0) {
                                                        6.dp
                                                    } else {
                                                        2.dp
                                                    },
                                                    topEnd = if (it == 0) {
                                                        6.dp
                                                    } else {
                                                        2.dp
                                                    },
                                                    bottomStart = if (it == 4 - 1) {
                                                        6.dp
                                                    } else {
                                                        2.dp
                                                    },
                                                    bottomEnd = if (it == 4 - 1) {
                                                        6.dp
                                                    } else {
                                                        2.dp
                                                    }
                                                ),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = uiColors.cardBackgroundHigh
                                                ),
                                                modifier = Modifier.padding(horizontal = 2.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .width(3.dp)
                                                            .height(10.dp)
                                                            .background(
                                                                Brush.verticalGradient(
                                                                    listOf(
                                                                        colors.accentOrangeStart,
                                                                        colors.accentOrangeEnd
                                                                    )
                                                                ),
                                                                RoundedCornerShape(2.dp)
                                                            )
                                                    )
                                                    Text(
                                                        text = "AI",
                                                        color = colors.textSecondary,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(start = 2.dp),
                                                        textAlign = TextAlign.Center,
                                                        style = MaterialTheme.typography.bodySmallEmphasized,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(1.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
//        LazyVerticalGrid(
//            modifier = Modifier.fillMaxSize(),
//            columns = GridCells.Fixed(7),
//            verticalArrangement = Arrangement.spacedBy(2.dp),
//            horizontalArrangement = Arrangement.spacedBy(2.dp)
//        ) {
//            items(42) { index ->
//
//                val dayNumber = (index % 31) + 1
//                val dayName = dayNames[index % 7]
//
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .aspectRatio(0.55f) ,
//                    onClick = {
//                        onDayClick(dayNumber, dayName)
//                    },
//                    shape = RoundedCornerShape(
//                        topStart = if (index == 0) 24.dp else 4.dp,
//                        topEnd = if (index  == 6) 24.dp else 4.dp,
//                        bottomStart = if (index == 35) 24.dp else 4.dp,
//                        bottomEnd = if (index == 41) 24.dp else 4.dp
//                    ),
//                    colors = CardDefaults.cardColors(containerColor = uiColors.cardBackground)
//                ) {
//                    Column(
//                    ) {
//                        Text(
//                            text = dayNumber.toString(),
//                            color = colors.textPrimary,
//                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier.fillMaxWidth(),
//                            textAlign = TextAlign.Center,
//                            style = MaterialTheme.typography.labelLargeEmphasized,
//                        )
//                        if (index % 7 != 0 && index % 7 != 6) {
//                            repeat(4) {
//                                Card(
//                                    shape = RoundedCornerShape(
//                                        topStart = if (it == 0) {
//                                            6.dp
//                                        } else {
//                                            2.dp
//                                        },
//                                        topEnd = if (it == 0) {
//                                            6.dp
//                                        } else {
//                                            2.dp
//                                        },
//                                        bottomStart = if (it == 4 - 1) {
//                                            6.dp
//                                        } else {
//                                            2.dp
//                                        },
//                                        bottomEnd = if (it == 4 - 1) {
//                                            6.dp
//                                        } else {
//                                            2.dp
//                                        }
//                                    ),
//                                    colors = CardDefaults.cardColors(
//                                        containerColor = uiColors.cardBackgroundHigh
//                                    ),
//                                    modifier = Modifier.padding(horizontal = 2.dp)
//                                ) {
//                                    Row(
//                                        modifier = Modifier.padding(horizontal = 4.dp),
//                                        verticalAlignment = Alignment.CenterVertically,
//                                    ) {
//                                        Box(
//                                            modifier = Modifier
//                                                .width(3.dp)
//                                                .height(10.dp)
//                                                .background(
//                                                    Brush.verticalGradient(
//                                                        listOf(
//                                                            colors.accentOrangeStart,
//                                                            colors.accentOrangeEnd
//                                                        )
//                                                    ),
//                                                    RoundedCornerShape(2.dp)
//                                                )
//                                        )
//                                        Text(
//                                            text = "AI",
//                                            color = colors.textSecondary,
//                                            modifier = Modifier
//                                                .fillMaxWidth()
//                                                .padding(start = 2.dp),
//                                            textAlign = TextAlign.Center,
//                                            style = MaterialTheme.typography.bodySmallEmphasized,
//                                            maxLines = 1,
//                                            overflow = TextOverflow.Ellipsis
//                                        )
//                                    }
//                                }
//                                Spacer(modifier = Modifier.height(1.dp))
//                            }
//                        }
//                    }
//                }
//                Column(
//                    modifier = Modifier
//                        .height(95.dp)
//                        .clickable {
//                            onDayClick(dayNumber, dayName)
//                        }
//                ) {
//
//                    Text(
//                        dayNumber.toString(),
//                        color = colors.textPrimary,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 14.sp
//                    )
//
//                    Spacer(Modifier.height(4.dp))
//
//                    repeat(2) {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier.padding(vertical = 1.dp)
//                        ) {
//                            Box(
//                                modifier = Modifier
//                                    .width(3.dp)
//                                    .height(10.dp)
//                                    .background(
//                                        Brush.verticalGradient(
//                                            listOf(
//                                                colors.accentOrangeStart,
//                                                colors.accentOrangeEnd
//                                            )
//                                        ),
//                                        RoundedCornerShape(2.dp)
//                                    )
//                            )
//
//                            Spacer(Modifier.width(4.dp))
//
//                            Text(
//                                "AI C25-A",
//                                color = colors.textSecondary,
//                                fontSize = 10.sp,
//                                maxLines = 1
//                            )
//                        }
//                    }
//                }
//            }
//        }
        }
    }
}
