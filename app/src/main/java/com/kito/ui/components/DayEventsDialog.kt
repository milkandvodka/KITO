package com.kito.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog

@Composable
fun DayEventsDialog(
    colors: UIColors,
    dayNumber: Int,
    dayName: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
                .background(colors.cardSurface, RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            Column {

                Text(
                    "$dayNumber  $dayName",
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                repeat(4) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .width(4.dp)
                                .height(40.dp)
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

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text("AI C25-A-008", color = colors.textPrimary)
                            Text(
                                "08:00 - 09:00",
                                color = colors.textSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Add on $dayNumber Dec",
                        color = colors.textSecondary,
                        modifier = Modifier
                            .background(colors.cardBackground, RoundedCornerShape(30.dp))
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    )

                    FloatingActionButton(
                        onClick = {},
                        containerColor = colors.accentOrangeEnd
                    ) {
                        Text("+", fontSize = 24.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
