package com.kito.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.kito.core.presentation.navigation3.Routes
import kito.composeapp.generated.resources.Res
import kito.composeapp.generated.resources.khaoo_gully
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


data class Utilities(
    val title : String,
    val iconVector: ImageVector? = null,
    val iconRes: DrawableResource? = null,
    val itemBoxColor: Color,
    val textColor: Color,
    val iconGradient: Brush,
    val destination: NavKey? = null,
    val url: String? = null
)

val UtilityList = listOf(
    Utilities(
        title = "Khaoo Gully",
//        iconVector = Icons.Rounded.CalendarMonth,
        iconRes = Res.drawable.khaoo_gully,
        itemBoxColor = Color(0xFF30583E),
        textColor = Color(0xFFC1F9D2),
        iconGradient = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF89C6A2),
                Color(0xFF3B684B)
            )
        ),
        destination = Routes.Calendar
    ),
    Utilities(
        title = "Study Material",
        iconVector = Icons.AutoMirrored.Rounded.Assignment,
        itemBoxColor = Color(0xFF2E3A58),
        textColor = Color(0xFFC1D4F9),
        iconGradient = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF89A8C6),
                Color(0xFF3B4F68)
            )
        ),
        url = "https://drive.google.com/drive/folders/1Ugm0zGR4A1d-mZPjCemNmxV7IsPK7Skg?usp=drive_link"
    ),
    Utilities(
        title = "Exam Schedule",
        iconVector = Icons.AutoMirrored.Rounded.Assignment,
        itemBoxColor = Color(0xFF3E3058),
        textColor = Color(0xFFE1C1F9),
        iconGradient = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFA689C6),
                Color(0xFF4B3B68)
            )
        ),
        destination = Routes.ExamSchedule
    ),
    Utilities(
        title = "GPA Calc",
        iconVector = Icons.Rounded.Calculate,
        itemBoxColor = Color(0xFF583E30),
        textColor = Color(0xFFF9E1C1),
        iconGradient = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFC69D89),
                Color(0xFF684B3B)
            )
        ),
        destination = Routes.GPACalc
    ),
    Utilities(
        title = "Friend Schedule",
        iconVector = Icons.Rounded.Group,
        itemBoxColor = Color(0xFF583030),
        textColor = Color(0xFFF9C1C1),
        iconGradient = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFC68989),
                Color(0xFF683B3B)
            )
        ),
        destination = Routes.FriendView
    ),
    Utilities(
        title = "Holiday List",
        iconVector = Icons.Rounded.EventAvailable,
        itemBoxColor = Color(0xFF304558),
        textColor = Color(0xFFC1E4F9),
        iconGradient = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF89B8C6),
                Color(0xFF3B5C68)
            )
        ),
        destination = Routes.HolidayList
    ),

    Utilities(
        title = "Coming Soon",
        iconVector = Icons.Rounded.AutoAwesome,
        itemBoxColor = Color(0xFF2D3242),
        textColor = Color(0xFFECEDFF),
        iconGradient = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF6C7293),
                Color(0xFF1C2128)
            )
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UtilityCard(
    onCLick: (
        destination: NavKey?,
        url: String?
    ) -> Unit
) {
    val colors = UIColors()
    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(22.dp)
            )
            .background(
                color = colors.cardBackground
            )
            .fillMaxWidth()
    ) {
        Column {
//            ShrinkingCarouselRow(
//                itemCount = 6,
//                itemWidth = 100.dp,
//                itemSpacing = 8.dp,
//                minScale = 0.6f
//            ) { index ->
//                GradientIcon(
//                    imageVector = Icons.Rounded.CalendarMonth,
//                    contentDescription = "Calendar",
//                    modifier = Modifier
//                        .size(64.dp)
//                        .align(Alignment.BottomEnd)
//                        .offset(x = 8.dp, y = 4.dp)
//                        .graphicsLayer { scaleX = 1.2f; scaleY = 1.2f},
//                    gradient = Brush.horizontalGradient(
//                        colors = listOf(Color(0xFFC7895F), Color(0xFF765138))
//                    )
//                )
//                Text(
//                    text = "GPA",
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis,
//                    modifier = Modifier.padding(8.dp),
//                    fontFamily = FontFamily.Monospace,
//                    style = MaterialTheme.typography.titleMediumEmphasized,
//                    fontWeight = FontWeight.SemiBold,
//                    color = Color(0xFFC7895F)
//                )
//            }
            ParallaxCarouselRow(
                itemCount = UtilityList.size,
                itemWidth = 100.dp,
                itemSpacing = 8.dp,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                parallaxFraction = 0.4f,
                itemBoxColor = { index->
                    UtilityList[index].itemBoxColor
                }
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            onClick = {
                                onCLick(
                                    UtilityList[index].destination,
                                    UtilityList[index].url
                                )
                            }
                        )
                ) {
                    val painter = when {
                        UtilityList[index].iconVector != null ->
                            rememberVectorPainter(UtilityList[index].iconVector!!)
                        UtilityList[index].iconRes != null ->
                            painterResource(UtilityList[index].iconRes!!)
                        else -> null
                    }
                    GradientIcon(
                        image = painter,
                        contentDescription = UtilityList[index].title,
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 8.dp, y = 4.dp)
                            .graphicsLayer {
                                scaleX = 1.2f;
                                scaleY = 1.2f
                            },
                        gradient = UtilityList[index].iconGradient
                    )
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        val words = UtilityList[index].title.split(" ")
                        Text(
                            text = words.getOrNull(0) ?: "",
                            fontFamily = FontFamily.Monospace,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.titleMediumEmphasized,
                            fontWeight = FontWeight.SemiBold,
                            color = UtilityList[index].textColor
                        )
                        Text(
                            text = words.getOrNull(1) ?: "",
                            fontFamily = FontFamily.Monospace,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.titleSmallEmphasized,
                            color = UtilityList[index].textColor
                        )
                    }
                }
            }
//            ShrinkingParallaxCarouselRow(
//                itemCount = 6,
//                itemWidth = 100.dp,
//                itemSpacing = 8.dp,
//                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
//                cardColor = Color(0xFF5F2B04),
//                cornerRadius = 28.dp,
//                minScale = 0.6f,
//                parallaxFraction = 0.4f
//            ) { index ->
//                GradientIcon(
//                    imageVector = Icons.Rounded.CalendarMonth,
//                    contentDescription = "Calendar",
//                    modifier = Modifier
//                        .size(64.dp)
//                        .align(Alignment.BottomEnd)
//                        .offset(x = 8.dp, y = 4.dp)
//                        .graphicsLayer {
//                            scaleX = 1.2f
//                            scaleY = 1.2f
//                        },
//                    gradient = Brush.horizontalGradient(
//                        colors = listOf(
//                            Color(0xFFC7895F),
//                            Color(0xFF765138)
//                        )
//                    )
//                )
//                Text(
//                    text = "GPA",
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis,
//                    modifier = Modifier.padding(8.dp),
//                    fontFamily = FontFamily.Monospace,
//                    style = MaterialTheme.typography.titleMediumEmphasized,
//                    fontWeight = FontWeight.SemiBold,
//                    color = Color(0xFFC7895F)
//                )
//            }
        }
    }
}