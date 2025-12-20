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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            val weekDays = listOf("M","T","W","T","F","S","S")
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(weekDays){weekDay->
                    Text(
                        text = weekDay,
                        color = colors.textSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(42) { index ->

                val dayNumber = (index % 31) + 1
                val dayName = dayNames[index % 7]

                Card(
                    modifier = Modifier
                        .heightIn(97.dp),
                    onClick = {
                        onDayClick(dayNumber, dayName)
                    },
                    shape = RoundedCornerShape(
                        topStart = if (index == 0) 24.dp else 4.dp,
                        topEnd = if (index  == 6) 24.dp else 4.dp,
                        bottomStart = if (index == 35) 24.dp else 4.dp,
                        bottomEnd = if (index == 41) 24.dp else 4.dp
                    ),
                    colors = CardDefaults.cardColors(containerColor = uiColors.cardBackground)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            dayNumber.toString(),
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(2.dp))

                        repeat(2) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 1.dp)
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

                                Spacer(Modifier.width(4.dp))

                                Text(
                                    "AI",
                                    color = colors.textSecondary,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
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
            }
        }
    }
}
