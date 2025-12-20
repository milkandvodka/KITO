package com.kito.ui.newUi.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kito.ui.components.*

@Composable
fun HomeScreen(
    username: String,
    colors: UIColors = UIColors()
) {
    val bgGradient = Brush.verticalGradient(
        colors = listOf(colors.backgroundTop, colors.backgroundBottom)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .padding(16.dp)
    ) {

        item {
            Text(
                text = "Hi ABC 👋",
                color = colors.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Spacer(Modifier.height(20.dp))
        }
        item {
            Text(
                text = "Dashboard",
                color = colors.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Spacer(Modifier.height(20.dp))
        }

        // Schedule Section
        item {
            ScheduleCard(colors)
            Spacer(Modifier.height(24.dp))
        }
//        item {
//            Text(
//                text = "",
//                color = colors.textPrimary,
//                fontSize = 22.sp,
//                fontWeight = FontWeight.Bold,
//                fontFamily = FontFamily.Monospace
//            )
//            Spacer(Modifier.height(20.dp))
//        }
        // Horizontal Cards
        item {
            DashboardCard(
                data = DashboardCardData(
                    title = "Attendance",
                    subtitle = "Track your presence"
                ),
                colors = colors
            )

            Spacer(Modifier.height(16.dp))

            DashboardCard(
                data = DashboardCardData(
                    title = "Upcoming Events...",
                    subtitle = "Stay updated"
                ),
                colors = colors
            )
        }
    }
}
