package com.kito.feature.calendar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.UIColors
import com.kito.core.presentation.components.animation.RelaxAnimation
import com.kito.feature.calendar.domain.model.CalendarEntry
import com.kito.feature.calendar.domain.model.CalendarEntryType
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.datetime.LocalDate

/** Dialog content without the window, so it can be rendered directly in UI tests. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class)
@Composable
fun DayEventsDialogBody(
    date: LocalDate,
    entries: List<CalendarEntry>,
    classes: List<CalendarEntry>,
    isLoadingClasses: Boolean,
    hazeState: HazeState,
    onDismiss: () -> Unit,
    enableAnimations: Boolean = true,
) {
    val uiColors = UIColors()
    val sections = listOf(
        "HOLIDAY" to entries.filter { it.type == CalendarEntryType.HOLIDAY },
        "EXAMS" to entries.filter { it.type == CalendarEntryType.EXAM },
        "EVENTS" to entries.filter { it.type == CalendarEntryType.EVENT },
        "CLASSES" to classes,
    ).filter { it.second.isNotEmpty() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { testTag = "day_dialog" }
            .staggeredEntrance(enabled = enableAnimations, fromScale = 0.92f, offset = 24.dp)
            .shadow(elevation = 24.dp, spotColor = uiColors.progressAccent)
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = Dp.Hairline,
                brush = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f))),
                shape = RoundedCornerShape(24.dp)
            )
            .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                blurRadius = 30.dp
                noiseFactor = 0.05f
                inputScale = HazeInputScale.Auto
                alpha = 0.98f
                tints = listOf(HazeTint(Color(0xFF86431D).copy(alpha = 0.15f)))
            }
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Text(
            text = formatLongDate(date),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = uiColors.textPrimary,
            style = MaterialTheme.typography.titleMediumEmphasized,
        )
        if (sections.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                sections.forEach { (_, items) ->
                    Box(
                        Modifier
                            .size(width = 18.dp, height = 4.dp)
                            .clip(CircleShape)
                            .background(entryTypeColor(items.first()))
                    )
                }
            }
        }
        Text(
            text = when (val total = entries.size + classes.size) {
                0 -> if (isLoadingClasses) "Loading…" else "Nothing scheduled"
                1 -> "1 item"
                else -> "$total items"
            },
            fontFamily = FontFamily.Monospace,
            color = uiColors.textSecondary,
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(Modifier.height(12.dp))

        if (sections.isEmpty() && !isLoadingClasses) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().semantics { testTag = "day_dialog_empty" }
            ) {
                if (enableAnimations) Box(Modifier.size(160.dp)) { RelaxAnimation() }
                else Spacer(Modifier.height(40.dp))
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.heightIn(max = 420.dp).semantics { testTag = "day_dialog_list" }
            ) {
                var rowIndex = 0
                sections.forEach { (title, items) ->
                    val start = rowIndex
                    rowIndex += items.size
                    item(key = "header_$title") {
                        Text(
                            text = title,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = uiColors.progressAccent,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                        )
                    }
                    itemsIndexed(items, key = { _, it -> it.id }) { i, entry ->
                        DayEntryRow(entry, Modifier.staggeredEntrance(start + i + 1, enableAnimations))
                    }
                }
                if (isLoadingClasses) {
                    item(key = "loading_classes") {
                        Text(
                            text = "Loading classes…",
                            fontFamily = FontFamily.Monospace,
                            color = uiColors.textSecondary,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }

        TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
            Text("Close", fontFamily = FontFamily.Monospace, color = uiColors.progressAccent)
        }
        Spacer(Modifier.height(8.dp))
    }
}
