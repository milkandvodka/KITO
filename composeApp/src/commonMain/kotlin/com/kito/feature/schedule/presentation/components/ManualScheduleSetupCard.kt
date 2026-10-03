package com.kito.feature.schedule.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.UIColors
import com.kito.feature.schedule.domain.model.AvailableSectionsData

@Composable
fun ManualScheduleSetupCard(
    availableData: AvailableSectionsData,
    selectedBatch: String,
    selectedBranch: String,
    selectedCoreSection: String,
    selectedElective1: String,
    selectedElective2: String,
    onSelectBatch: (String) -> Unit,
    onSelectBranch: (String) -> Unit,
    onSelectCoreSection: (String) -> Unit,
    onSelectElective1: (String) -> Unit,
    onSelectElective2: (String) -> Unit,
    onSubmit: () -> Unit,
    isSubmitting: Boolean,
    uiColors: UIColors,
    modifier: Modifier = Modifier,
    errorMessage: String? = null
) {
    val effectiveBranch = selectedBranch.ifBlank { extractBranchName(selectedCoreSection) }
    val branches = availableData.branchesByBatch[selectedBatch].orEmpty()
    val coreSections = availableData.coreSectionsByBatchAndBranch[selectedBatch]?.get(effectiveBranch)
        ?: availableData.coreSectionsByBatchAndBranch[selectedBatch]?.entries?.firstOrNull {
            it.key.equals(effectiveBranch, ignoreCase = true)
        }?.value.orEmpty()
    val electiveSlots = availableData.electiveSlotsByBatch[selectedBatch].orEmpty()
    val isDataLoading = availableData.branchesByBatch.isEmpty()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { testTag = "manual_schedule_setup_card" },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            uiColors.cardBackground,
                            uiColors.cardBackgroundHigh
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = uiColors.accentOrangeStart.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Set up your timetable",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = uiColors.textPrimary
                    )
                    if (isDataLoading) {
                        CircularProgressIndicator(
                            color = uiColors.accentOrangeStart,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = "Your roll number is not in the system yet. Select your year, branch, and section to load your schedule.",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = uiColors.textSecondary
                )

                // 1. Year / Batch Selector
                DropdownSelector(
                    label = "Year / Batch *",
                    selectedValue = selectedBatch,
                    options = availableData.availableBatches,
                    onSelect = onSelectBatch,
                    uiColors = uiColors,
                    placeholder = "Select Year...",
                    displayFormatter = ::formatBatchYear
                )

                // 2. Branch / Scheme Selector
                val isYear1 = selectedBatch.equals("batch_1", ignoreCase = true)
                val branchLabel = if (isYear1) "Scheme *" else "Branch *"
                val branchPlaceholder = if (selectedBatch.isBlank()) "Select Year first" else if (isYear1) "Select Scheme..." else "Select Branch..."
                val sectionPlaceholder = if (selectedBranch.isBlank()) {
                    if (isYear1) "Select Scheme first" else "Select Branch first"
                } else "Select Section..."

                DropdownSelector(
                    label = branchLabel,
                    selectedValue = selectedBranch,
                    options = branches,
                    onSelect = onSelectBranch,
                    uiColors = uiColors,
                    placeholder = branchPlaceholder,
                    enabled = selectedBatch.isNotBlank() && branches.isNotEmpty(),
                    isLoading = selectedBatch.isNotBlank() && branches.isEmpty() && isDataLoading
                )

                // 3. Core Section Selector
                DropdownSelector(
                    label = "Core Section *",
                    selectedValue = selectedCoreSection,
                    options = coreSections,
                    onSelect = onSelectCoreSection,
                    uiColors = uiColors,
                    placeholder = sectionPlaceholder,
                    enabled = selectedBranch.isNotBlank() && coreSections.isNotEmpty(),
                    isLoading = selectedBranch.isNotBlank() && coreSections.isEmpty() && isDataLoading
                )

                // 4. Dynamic N-Elective Selectors
                electiveSlots.forEachIndexed { index, slot ->
                    val selectedElective = if (index == 0) selectedElective1 else selectedElective2
                    val onSelectElective = if (index == 0) onSelectElective1 else onSelectElective2

                    DropdownSelector(
                        label = slot.displayName,
                        selectedValue = selectedElective,
                        options = slot.availableSections,
                        onSelect = onSelectElective,
                        uiColors = uiColors,
                        placeholder = "Select ${slot.displayName} (Optional)...",
                        enabled = slot.availableSections.isNotEmpty()
                    )
                }

                if (!errorMessage.isNullOrBlank()) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE57373),
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val isFormValid = selectedBatch.isNotBlank() &&
                        selectedBranch.isNotBlank() &&
                        selectedCoreSection.isNotBlank()

                Button(
                    onClick = onSubmit,
                    enabled = isFormValid && !isSubmitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = uiColors.progressAccent,
                        disabledContainerColor = uiColors.cardSurface.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .semantics { testTag = "manual_schedule_submit_button" }
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            color = uiColors.textPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.height(20.dp)
                        )
                    } else {
                        Text(
                            text = "Save & Load Timetable",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            color = uiColors.textPrimary
                        )
                    }
                }
            }
        }
    }
}
