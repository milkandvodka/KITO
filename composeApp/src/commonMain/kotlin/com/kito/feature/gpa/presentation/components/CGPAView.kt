package com.kito.feature.gpa.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.UIColors
import com.kito.feature.gpa.presentation.GPAEvent
import com.kito.feature.gpa.presentation.GPAUiState

@Composable
fun CGPAScreen(
    uiState: GPAUiState,
    onEvent: (GPAEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF121116)),
        verticalArrangement = Arrangement.spacedBy(2.5.dp)
    ) {
        CalculatedSgpaToggleCard(
            uiState = uiState,
            onEvent = onEvent,
        )

        cgpaInputs.forEachIndexed { index, input ->
            CgpaInputCard(
                input = input,
                index = index,
                lastIndex = cgpaInputs.lastIndex,
                uiState = uiState,
                onEvent = onEvent,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

private enum class CgpaInput(
    val label: String,
    val keyboardType: KeyboardType,
) {
    PreviousCgpa("Previous CGPA", KeyboardType.Decimal),
    CompletedSemesters("Semesters Completed", KeyboardType.Number),
    CurrentSgpa("Current Expected SGPA", KeyboardType.Decimal),
}

private val cgpaInputs = listOf(
    CgpaInput.PreviousCgpa,
    CgpaInput.CompletedSemesters,
    CgpaInput.CurrentSgpa,
)

@Composable
private fun CalculatedSgpaToggleCard(
    uiState: GPAUiState,
    onEvent: (GPAEvent) -> Unit,
) {
    val uiColors = UIColors()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBrush(uiColors))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Use calculated SGPA",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = uiColors.textPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Switch(
                checked = uiState.useCalculatedSgpa,
                onCheckedChange = { onEvent(GPAEvent.UseCalculatedSgpa(it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = uiColors.progressAccent,
                )
            )
        }
    }
}

@Composable
private fun CgpaInputCard(
    input: CgpaInput,
    index: Int,
    lastIndex: Int,
    uiState: GPAUiState,
    onEvent: (GPAEvent) -> Unit,
) {
    val uiColors = UIColors()
    val enabled = input != CgpaInput.CurrentSgpa || !uiState.useCalculatedSgpa

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(
            topStart = if (index == 0) 24.dp else 4.dp,
            topEnd = if (index == 0) 24.dp else 4.dp,
            bottomStart = if (index == lastIndex) 24.dp else 4.dp,
            bottomEnd = if (index == lastIndex) 24.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardBrush(uiColors))
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = when (input) {
                    CgpaInput.PreviousCgpa -> uiState.previousCgpa
                    CgpaInput.CompletedSemesters -> uiState.completedSemesters
                    CgpaInput.CurrentSgpa -> if (uiState.useCalculatedSgpa) {
                        formatGpa(uiState.sgpa)
                    } else {
                        uiState.manualCurrentSgpa
                    }
                },
                onValueChange = {
                    when (input) {
                        CgpaInput.PreviousCgpa -> onEvent(GPAEvent.UpdatePreviousCgpa(it))
                        CgpaInput.CompletedSemesters -> onEvent(GPAEvent.UpdateCompletedSemesters(it))
                        CgpaInput.CurrentSgpa -> onEvent(GPAEvent.UpdateManualCurrentSgpa(it))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = enabled,
                shape = RoundedCornerShape(18.dp),
                leadingIcon = {
                    Icon(
                        imageVector = when (input) {
                            CgpaInput.PreviousCgpa -> Icons.Filled.BarChart
                            CgpaInput.CompletedSemesters -> Icons.Filled.CalendarMonth
                            CgpaInput.CurrentSgpa -> Icons.Filled.Calculate
                        },
                        contentDescription = null,
                        tint = Color(0xFFB8B2BC)
                    )
                },
                label = {
                    Text(
                        text = input.label,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                colors = gpaTextFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = input.keyboardType),
            )
        }
    }
}

private fun cardBrush(uiColors: UIColors): Brush {
    return Brush.linearGradient(
        colors = listOf(
            uiColors.cardBackground,
            Color(0xFF2F222F),
            Color(0xFF2F222F),
            uiColors.cardBackgroundHigh
        )
    )
}
