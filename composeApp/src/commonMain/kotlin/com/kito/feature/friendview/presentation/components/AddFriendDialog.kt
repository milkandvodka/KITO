package com.kito.feature.friendview.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.RopeTabRow
import com.kito.core.designsystem.UIColors
import com.kito.feature.schedule.domain.model.AvailableSectionsData
import com.kito.feature.schedule.presentation.components.DropdownSelector
import com.kito.feature.schedule.presentation.components.formatBatchYear
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class,
    ExperimentalHazeApi::class
)
@Composable
fun AddFriendDialog(
    onDismiss: () -> Unit,
    onConfirmRoll: (name: String, roll: String) -> Unit,
    onConfirmSection: (
        name: String,
        batch: String,
        branch: String,
        section: String,
        elective1: String,
        elective2: String
    ) -> Unit = { _, _, _, _, _, _ -> },
    availableData: AvailableSectionsData = AvailableSectionsData(),
    isSubmitting: Boolean = false,
    errorMessage: String? = null,
    hazeState: HazeState,
    modifier: Modifier = Modifier
) {
    val uiColors = UIColors()
    var friendName by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Roll No, 1 = Section
    var rollNumber by remember { mutableStateOf("") }
    var selectedBatch by remember { mutableStateOf("") }
    var selectedBranch by remember { mutableStateOf("") }
    var selectedCoreSection by remember { mutableStateOf("") }
    var selectedElective1 by remember { mutableStateOf("") }
    var selectedElective2 by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    val animatedTabPosition by animateFloatAsState(
        targetValue = selectedTab.toFloat(),
        animationSpec = tween(
            durationMillis = 350,
            easing = FastOutSlowInEasing
        ),
        label = "addFriendTabAnimation"
    )

    val effectiveBranch = selectedBranch.ifBlank { com.kito.feature.schedule.presentation.components.extractBranchName(selectedCoreSection) }
    val branches = availableData.branchesByBatch[selectedBatch].orEmpty()
    val coreSections = availableData.coreSectionsByBatchAndBranch[selectedBatch]?.get(effectiveBranch)
        ?: availableData.coreSectionsByBatchAndBranch[selectedBatch]?.entries?.firstOrNull {
            it.key.equals(effectiveBranch, ignoreCase = true)
        }?.value.orEmpty()
    val electiveSlots = availableData.electiveSlotsByBatch[selectedBatch].orEmpty()

    AlertDialog(
        icon = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = uiColors.progressAccent,
                    modifier = Modifier.height(44.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Add Friend",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Friend Name Field
                OutlinedTextField(
                    value = friendName,
                    onValueChange = {
                        friendName = it
                        localError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    label = {
                        Text(
                            text = "Friend's Name",
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.titleMediumEmphasized
                        )
                    },
                    placeholder = {
                        Text(
                            text = "e.g., Alex",
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8C00),
                        unfocusedBorderColor = Color(0xFF3F3942),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFFFF8C00),
                        cursorColor = Color(0xFFFF8C00),
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.3f)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Words
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Animated Lasso Selector (Roll No vs Section)
                RopeTabRow(
                    tabPosition = animatedTabPosition,
                    onTabSelected = {
                        selectedTab = it
                        localError = null
                    },
                    tabs = listOf("Roll No", "Section")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Tab Content
                if (selectedTab == 0) {
                    // Option 1: Roll No
                    OutlinedTextField(
                        value = rollNumber,
                        onValueChange = {
                            rollNumber = it
                            localError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        label = {
                            Text(
                                text = "Friend's Roll Number",
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.titleMediumEmphasized
                            )
                        },
                        placeholder = {
                            Text(
                                text = "e.g., 123456789",
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF8C00),
                            unfocusedBorderColor = Color(0xFF3F3942),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFFFF8C00),
                            cursorColor = Color(0xFFFF8C00),
                            focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Enter your friend's roll number to view their schedule.",
                        fontFamily = FontFamily.Monospace,
                        color = uiColors.accentOrangeStart,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Option 2: Section (Exact manual routine setup logic)
                    val isDataLoading = availableData.branchesByBatch.isEmpty()
                    if (isDataLoading) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = uiColors.accentOrangeStart,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownSelector(
                        label = "Year / Batch *",
                        selectedValue = selectedBatch,
                        options = availableData.availableBatches,
                        onSelect = {
                            selectedBatch = it
                            selectedBranch = ""
                            selectedCoreSection = ""
                            selectedElective1 = ""
                            selectedElective2 = ""
                            localError = null
                        },
                        uiColors = uiColors,
                        placeholder = "Select Year...",
                        displayFormatter = ::formatBatchYear
                    )
                    Spacer(modifier = Modifier.height(10.dp))

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
                        onSelect = {
                            selectedBranch = it
                            selectedCoreSection = ""
                            selectedElective1 = ""
                            selectedElective2 = ""
                            localError = null
                        },
                        uiColors = uiColors,
                        placeholder = branchPlaceholder,
                        enabled = selectedBatch.isNotBlank() && branches.isNotEmpty(),
                        isLoading = selectedBatch.isNotBlank() && branches.isEmpty() && isDataLoading
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    DropdownSelector(
                        label = "Core Section *",
                        selectedValue = selectedCoreSection,
                        options = coreSections,
                        onSelect = {
                            selectedCoreSection = it
                            selectedElective1 = ""
                            selectedElective2 = ""
                            localError = null
                        },
                        uiColors = uiColors,
                        placeholder = sectionPlaceholder,
                        enabled = selectedBranch.isNotBlank() && coreSections.isNotEmpty(),
                        isLoading = selectedBranch.isNotBlank() && coreSections.isEmpty() && isDataLoading
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    electiveSlots.forEachIndexed { index, slot ->
                        val selectedElective = if (index == 0) selectedElective1 else selectedElective2
                        val onSelectElective: (String) -> Unit = { elective ->
                            if (index == 0) selectedElective1 = elective else selectedElective2 = elective
                            localError = null
                        }

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
                }

                val activeError = localError ?: errorMessage
                if (!activeError.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = activeError,
                        color = Color(0xFFE57373),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        confirmButton = {
            FilledTonalButton(
                enabled = !isSubmitting,
                onClick = {
                    if (friendName.isBlank()) {
                        localError = "Please enter friend's name"
                        return@FilledTonalButton
                    }
                    if (selectedTab == 0) {
                        if (rollNumber.isBlank()) {
                            localError = "Please enter roll number"
                            return@FilledTonalButton
                        }
                        onConfirmRoll(friendName.trim(), rollNumber.trim())
                    } else {
                        if (selectedBatch.isBlank() || selectedBranch.isBlank() || selectedCoreSection.isBlank()) {
                            localError = "Please select year, branch, and core section"
                            return@FilledTonalButton
                        }
                        onConfirmSection(
                            friendName.trim(),
                            selectedBatch,
                            selectedBranch,
                            selectedCoreSection,
                            selectedElective1,
                            selectedElective2
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = uiColors.progressAccent,
                    contentColor = uiColors.textPrimary
                )
            ) {
                if (isSubmitting) {
                    LoadingIndicator(
                        color = uiColors.progressAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = "Add",
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
                blurRadius = 30.dp
                noiseFactor = 0.00f
                inputScale = HazeInputScale.Auto
                alpha = 0.98f
                tints = listOf(HazeTint(uiColors.cardBackground.copy(alpha = 0.15f)))
            }
    )
}
