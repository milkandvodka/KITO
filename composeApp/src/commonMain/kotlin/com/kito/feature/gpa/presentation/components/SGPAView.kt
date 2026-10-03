package com.kito.feature.gpa.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.UIColors
import com.kito.feature.gpa.presentation.GPAEvent
import com.kito.feature.gpa.presentation.GPASubjectState
import com.kito.feature.gpa.presentation.GPAUiState
import kotlin.math.roundToInt

internal val GpaTopContentPadding = 128.dp

@Composable
fun SGPAScreen(
    uiState: GPAUiState,
    onEvent: (GPAEvent) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingSubject by remember { mutableStateOf<GPASubjectState?>(null) }
    val displayedSubjects = remember(uiState.subjects) {
        uiState.subjects
            .withIndex()
            .sortedWith(
                compareByDescending<IndexedValue<GPASubjectState>> { it.value.credits }
                    .thenBy { it.index }
            )
            .map { it.value }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF121116)),
        verticalArrangement = Arrangement.spacedBy(2.5.dp)
    ) {
        TopActionsRow(
            onAdd = { showAddDialog = true },
            onReset = { onEvent(GPAEvent.ResetSubjects) }
        )

        displayedSubjects.forEachIndexed { index, subject ->
            SubjectCard(
                subject = subject,
                index = index,
                lastIndex = displayedSubjects.lastIndex,
                onEdit = { editingSubject = subject },
                onDelete = { onEvent(GPAEvent.DeleteSubject(subject.id)) },
                onGradeChange = { onEvent(GPAEvent.UpdateSubjectGrade(subject.id, it)) }
            )
        }

        if (displayedSubjects.isEmpty()) {
            Text(
                text = "No subjects added",
                fontFamily = FontFamily.Monospace,
                color = UIColors().textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (showAddDialog) {
        SubjectDetailsDialog(
            title = "Add subject",
            initialName = "",
            initialCredits = "",
            confirmText = "Add",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, credits ->
                onEvent(GPAEvent.UpdateNewSubjectName(name))
                onEvent(GPAEvent.UpdateNewSubjectCredits(credits.toString()))
                onEvent(GPAEvent.UpdateNewSubjectGrade(0))
                onEvent(GPAEvent.AddSubject)
                showAddDialog = false
            }
        )
    }

    editingSubject?.let { subject ->
        SubjectDetailsDialog(
            title = "Edit subject",
            initialName = subject.name,
            initialCredits = subject.creditText,
            confirmText = "Save",
            onDismiss = { editingSubject = null },
            onConfirm = { name, credits ->
                onEvent(GPAEvent.UpdateSubjectName(subject.id, name))
                onEvent(GPAEvent.UpdateSubjectCredits(subject.id, credits.toString()))
                editingSubject = null
            }
        )
    }
}

@Composable
internal fun GpaResultHeader(
    title: String,
    value: String,
    metadata: String?,
    topPadding: Dp,
    horizontalPadding: Dp = 16.dp,
) {
    val uiColors = UIColors()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = topPadding,
                start = horizontalPadding,
                end = horizontalPadding
            )
            .heightIn(min = 88.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            uiColors.cardBackground,
                            Color(0xFF2F222F),
                            Color(0xFF2F222F),
                            uiColors.cardBackgroundHigh
                        )
                    )
                )
                .padding(horizontal = 18.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    fontFamily = FontFamily.Monospace,
                    color = uiColors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = value,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = uiColors.progressAccent,
                    style = MaterialTheme.typography.headlineMedium
                )
                if (metadata != null) {
                    Text(
                        text = metadata,
                        fontFamily = FontFamily.Monospace,
                        color = uiColors.textSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun TopActionsRow(
    onAdd: () -> Unit,
    onReset: () -> Unit,
) {
    val uiColors = UIColors()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onAdd,
            modifier = Modifier.height(36.dp),
            colors = ButtonDefaults.buttonColors(containerColor = uiColors.progressAccent),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
        ) {
            Text(
                text = "Add +",
                color = Color.Black,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.height(36.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = uiColors.progressAccent),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Reset",
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun SubjectCard(
    subject: GPASubjectState,
    index: Int,
    lastIndex: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onGradeChange: (Int) -> Unit,
) {
    val uiColors = UIColors()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 128.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(
            topStart = if (index == 0) 24.dp else 4.dp,
            topEnd = if (index == 0) 24.dp else 4.dp,
            bottomStart = if (index == lastIndex) 24.dp else 4.dp,
            bottomEnd = if (index == lastIndex) 24.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(cardBrush(uiColors))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit ${subject.name}",
                        tint = uiColors.progressAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = subject.name,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = uiColors.textPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Credits: ${subject.credits}",
                    fontFamily = FontFamily.Monospace,
                    color = uiColors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete ${subject.name}",
                        tint = Color(0xFFFF8C8C),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            GradeSlider(
                selectedIndex = subject.gradeIndex,
                onGradeChange = onGradeChange
            )
        }
    }
}

@Composable
private fun SubjectDetailsDialog(
    title: String,
    initialName: String,
    initialCredits: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit,
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var credits by remember(initialCredits) { mutableStateOf(initialCredits) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var creditError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF211923),
        title = {
            Text(
                text = title,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SubjectTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    label = "Subject name",
                    error = nameError,
                    modifier = Modifier.fillMaxWidth()
                )
                SubjectTextField(
                    value = credits,
                    onValueChange = {
                        credits = it
                        creditError = null
                    },
                    label = "Credits",
                    error = creditError,
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedCredits = parseCredits(credits)
                    nameError = if (name.isBlank()) "Subject name is required" else null
                    creditError = parsedCredits.error
                    if (nameError == null && parsedCredits.value != null) {
                        onConfirm(name.trim(), parsedCredits.value)
                    }
                }
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SubjectTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        isError = error != null,
        label = {
            Text(
                text = label,
                fontFamily = FontFamily.Monospace
            )
        },
        supportingText = {
            if (error != null) {
                Text(
                    text = error,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(18.dp),
        colors = gpaTextFieldColors()
    )
}

@Composable
internal fun gpaTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFFFF8C00),
    unfocusedBorderColor = Color(0xFF3F3942),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    disabledTextColor = Color.White,
    errorTextColor = Color.White,
    focusedLabelColor = Color(0xFFFF8C00),
    cursorColor = Color(0xFFFF8C00)
)

internal fun formatGpa(value: Double?): String {
    if (value == null || value.isNaN() || value.isInfinite()) return "—"
    val rounded = (value * 100).roundToInt() / 100.0
    return rounded.toString()
}

private fun parseCredits(value: String): CreditParseResult {
    if (value.isBlank()) return CreditParseResult(error = "Credits are required")
    if (value.any { !it.isDigit() }) return CreditParseResult(error = "Enter a whole number")
    val parsed = value.toLongOrNull() ?: return CreditParseResult(error = "Credits are too large")
    if (parsed > Int.MAX_VALUE) return CreditParseResult(error = "Credits are too large")
    if (parsed <= 0) return CreditParseResult(error = "Credits must be greater than zero")
    return CreditParseResult(value = parsed.toInt())
}

private data class CreditParseResult(
    val value: Int? = null,
    val error: String? = null,
)

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
