package com.kito.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalendarMonthUI(
    colors: UIColors,
    onDayClick: (Int, String) -> Unit
) {

    val dayNames = listOf(
        "Monday", "Tuesday", "Wednesday",
        "Thursday", "Friday", "Saturday", "Sunday"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(colors.backgroundTop, colors.backgroundBottom)
                )
            )
            .padding(12.dp)
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
            listOf("M","T","W","T","F","S","S").forEach {
                Text(it, color = colors.textSecondary)
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(42) { index ->

                val dayNumber = (index % 31) + 1
                val dayName = dayNames[index % 7]

                Column(
                    modifier = Modifier
                        .height(95.dp)
                        .clickable {
                            onDayClick(dayNumber, dayName)
                        }
                ) {

                    Text(
                        dayNumber.toString(),
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(4.dp))

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
                                "AI C25-A",
                                color = colors.textSecondary,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
