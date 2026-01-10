package com.kito.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UpcomingEventCard(){
    val colors = UIColors()
    val hazeState = rememberHazeState()
    Box {
        Column(
            modifier = Modifier
                .heightIn(max = 205.dp)
                .hazeSource(hazeState)
                .fillMaxWidth()
                .background(
                    colors.cardBackground,
                    RoundedCornerShape(22.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            UpcomingItem(
                title = "KIIT Fest",
                society = "KSAC",
                date = "14 - 16 Feb",
                colors = colors
            )
            UpcomingItem(
                title = "IOtronics",
                society = "E-Labs",
                date = "1 Nov",
                colors = colors
            )
        }
//        Box(
//            modifier = Modifier
//                .matchParentSize()
//                .clip(RoundedCornerShape(22.dp))
//                .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()){
//                    blurRadius = 5.dp
//                },
//            contentAlignment = Alignment.Center
//        ){
//            Text(
//                text = "KIIT Events Coming Soon.....",
//                fontFamily = FontFamily.Monospace,
//                color = Color.White,
//                style = MaterialTheme.typography.titleSmallEmphasized,
//                modifier = Modifier.padding(12.dp),
//                fontWeight = FontWeight.Bold
//            )
//        }
    }
}