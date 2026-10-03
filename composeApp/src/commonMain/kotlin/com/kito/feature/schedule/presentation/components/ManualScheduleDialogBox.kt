package com.kito.feature.schedule.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.UIColors
import com.kito.feature.schedule.domain.model.AvailableSectionsData
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class,
    ExperimentalHazeApi::class
)
@Composable
fun ManualScheduleDialogBox(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
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
    isSubmitting: Boolean,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    title: String = "Set up Timetable",
    errorMessage: String? = null
) {
    val uiColors = UIColors()
    val effectiveBranch = selectedBranch.ifBlank { extractBranchName(selectedCoreSection) }
    val branches = availableData.branchesByBatch[selectedBatch].orEmpty()
    val coreSections = availableData.coreSectionsByBatchAndBranch[selectedBatch]?.get(effectiveBranch)
        ?: availableData.coreSectionsByBatchAndBranch[selectedBatch]?.entries?.firstOrNull {
            it.key.equals(effectiveBranch, ignoreCase = true)
        }?.value.orEmpty()
    val electiveSlots = availableData.electiveSlotsByBatch[selectedBatch].orEmpty()
    val isDataLoading = availableData.branchesByBatch.isEmpty()

    val isFormValid = selectedBatch.isNotBlank() &&
            (selectedBranch.isNotBlank() || effectiveBranch.isNotBlank()) &&
            selectedCoreSection.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
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
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Select your year, branch, and section to load your schedule.",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = uiColors.textSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

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
                Spacer(modifier = Modifier.height(10.dp))

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
                Spacer(modifier = Modifier.height(10.dp))

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
                Spacer(modifier = Modifier.height(10.dp))

                // 4. Dynamic N-Electives
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
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE57373),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        confirmButton = {
            FilledTonalButton(
                enabled = isFormValid && !isSubmitting,
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = uiColors.progressAccent,
                    contentColor = uiColors.textPrimary
                )
            ) {
                if (isSubmitting) {
                    LoadingIndicator(
                        color = uiColors.progressAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = "Save",
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        dismissButton = {
            TextButton(
                enabled = !isSubmitting,
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = uiColors.progressAccent
                )
            ) {
                Text(
                    text = "Cancel",
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        containerColor = Color.Transparent,
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .shadow(
                elevation = 24.dp,
                spotColor = uiColors.progressAccent
            )
            .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                blurRadius = 35.dp
                noiseFactor = 0.00f
                inputScale = HazeInputScale.Auto
                alpha = 0.98f
                tints = listOf(HazeTint(uiColors.cardBackground.copy(alpha = 0.15f)))
            }
    )
}
