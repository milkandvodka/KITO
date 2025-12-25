package com.kito.ui.components.settingsdialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kito.ui.components.UIColors
import com.kito.ui.components.state.SyncUiState
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun YearTermChangeDialogBox(
    onDismiss: () -> Unit,
    onConfirm: (String,String) -> Unit,
    syncState: SyncUiState,
    year: String,
    term: String
) {
    val uiColors = UIColors()
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear - 5..currentYear).map { it.toString() }.reversed()
    val terms = listOf(
        "Autumn",
        "Spring"
    )
    var selectedTerm by rememberSaveable { mutableStateOf(term) }
    var sapYear by rememberSaveable { mutableStateOf(year)}
    var sapTerm by rememberSaveable { mutableStateOf(if (term == "Autumn") "010" else "020") }
    var yearExpanded by remember { mutableStateOf(false) }
    val yearState = rememberTextFieldState(sapYear)
    var termExpanded by remember { mutableStateOf(false) }
    val termState = rememberTextFieldState(selectedTerm)
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Change Year & Term",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                Row {
                    ExposedDropdownMenuBox(
                        expanded = yearExpanded,
                        onExpandedChange = {
                            if (syncState !is SyncUiState.Loading) {
                                yearExpanded = !yearExpanded
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            enabled = syncState !is SyncUiState.Loading,
                            textStyle = MaterialTheme.typography.titleSmallEmphasized.copy(
                                fontFamily = FontFamily.Monospace,
                                color = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            readOnly = true,
                            value = yearState.text.toString(),
                            onValueChange = {},
                            label = {
                                Text(
                                    text ="Year",
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.titleSmallEmphasized
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = yearExpanded,
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFF8C00),
                                unfocusedBorderColor = Color(0xFF3F3942),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.White,
                                errorTextColor = Color.White,
                                focusedLabelColor = Color(0xFFFF8C00),
                                cursorColor = Color(0xFFFF8C00)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = yearExpanded,
                            onDismissRequest = { yearExpanded = false },
                            modifier = Modifier.height(140.dp),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            years.forEach { year ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = year,
                                            fontFamily = FontFamily.Monospace,
                                            style = MaterialTheme.typography.titleSmallEmphasized
                                        )
                                    },
                                    onClick = {
                                        sapYear = year
                                        yearState.setTextAndPlaceCursorAtEnd(year)
                                        yearExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = termExpanded,
                        onExpandedChange = {
                            if (syncState !is SyncUiState.Loading) {
                                termExpanded = !termExpanded
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            enabled = syncState !is SyncUiState.Loading,
                            textStyle = MaterialTheme.typography.titleSmallEmphasized.copy(
                                fontFamily = FontFamily.Monospace,
                                color = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            readOnly = true,
                            value = termState.text.toString(),
                            onValueChange = {},
                            label = {
                                Text(
                                    text = "Term",
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.titleSmallEmphasized
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = termExpanded,
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFF8C00),
                                unfocusedBorderColor = Color(0xFF3F3942),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.White,
                                errorTextColor = Color.White,
                                focusedLabelColor = Color(0xFFFF8C00),
                                cursorColor = Color(0xFFFF8C00)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = termExpanded,
                            onDismissRequest = { termExpanded = false },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            terms.forEach { term ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = term,
                                            fontFamily = FontFamily.Monospace,
                                            style = MaterialTheme.typography.titleSmallEmphasized
                                        )
                                    },
                                    onClick = {
                                        selectedTerm = term
                                        sapTerm = if (term == "Autumn") "010" else "020"
                                        termState.setTextAndPlaceCursorAtEnd(term)
                                        termExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }

                if (isError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Name cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        confirmButton = {
            FilledTonalButton(
                enabled = syncState !is SyncUiState.Loading,
                onClick = {
                    if (sapTerm.isNotBlank() && sapYear.isNotBlank()) {
                        onConfirm(sapYear,sapTerm)
                    } else {
                        isError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = uiColors.progressAccent,
                    contentColor = uiColors.textPrimary
                )
            ) {
                if (syncState is SyncUiState.Loading){
                    LoadingIndicator(
                        color = uiColors.progressAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                enabled = syncState !is SyncUiState.Loading,
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = uiColors.progressAccent
                )
            ) {
                Text("Cancel")
            }
        },
        containerColor = uiColors.cardBackground
    )
}