package com.kito.feature.calendar.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.UIColors
import com.kito.core.designsystem.shimmer

/** Placeholder cards for the upcoming list while the first month loads. */
@Composable
fun CalendarShimmer(modifier: Modifier = Modifier) {
    val uiColors = UIColors()
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.5.dp)) {
        repeat(3) { index ->
            val top = if (index == 0) 24.dp else 4.dp
            val bottom = if (index == 2) 24.dp else 4.dp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .clip(RoundedCornerShape(topStart = top, topEnd = top, bottomStart = bottom, bottomEnd = bottom))
                    .shimmer(uiColors.cardBackground, uiColors.cardBackgroundHigh)
            )
        }
    }
}
