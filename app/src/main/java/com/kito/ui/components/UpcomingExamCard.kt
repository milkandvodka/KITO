package com.kito.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kito.data.remote.model.MidsemScheduleModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UpcomingExamCard(
    item: MidsemScheduleModel?,
    onClick: () -> Unit
) {
    val uiColors = UIColors()
    Card(
        colors = CardDefaults.cardColors(containerColor = uiColors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(
            24.dp
        ),
        onClick = {
            onClick()
        }
    ) {
        Box(
            modifier = Modifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(38.dp)
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
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 6.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = item?.subject?:"",
                            color = uiColors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.titleMediumEmphasized,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${formatTo12Hour(item?.start_time?:"")} - ${
                                formatTo12Hour(
                                    item?.end_time?:""
                                )
                            }",
                            color = uiColors.textPrimary.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.labelSmallEmphasized,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    val formattedDate = remember(item?.date?:"") {
                        LocalDate.parse(item?.date?:"").format(examDateFormatter)
                    }
                    Text(
                        text = formattedDate,
                        color = uiColors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.titleSmallEmphasized,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
val examDateFormatter =
    DateTimeFormatter.ofPattern("d MMM, EEE", Locale.ENGLISH)