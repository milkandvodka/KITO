package com.kito.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DashboardCardData(
    val title: String,
    val subtitle: String
)

@Composable
fun DashboardCard(
    data: DashboardCardData,
    colors: UIColors
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            colors.accentOrangeStart,
            colors.accentOrangeEnd
        )
    )

    Column(
        modifier = Modifier
            .width(395.dp)
            .height(150.dp)
            .background(gradient, RoundedCornerShape(26.dp))
            .padding(16.dp)
    ) {
        Text(
            text = data.title,
            color = colors.textPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = data.subtitle,
            color = colors.textPrimary.copy(alpha = 0.85f),
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
