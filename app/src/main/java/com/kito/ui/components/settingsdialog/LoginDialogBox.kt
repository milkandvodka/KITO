package com.kito.ui.components.settingsdialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kito.ui.components.UIColors
import com.kito.ui.components.animation.LockAnimation
import com.kito.ui.components.state.SyncUiState
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalHazeMaterialsApi::class,
    ExperimentalHazeApi::class
)
@Composable
fun LoginDialogBox(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    syncState: SyncUiState,
    hazeState: HazeState
) {
    val uiColors = UIColors()
    var sapPassword by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        icon = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(100.dp)
                ) {
                    LockAnimation()
                }
                Text(
                    text = "Login To Sap",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        onDismissRequest = onDismiss,
        text = {
            Column {
                OutlinedTextField(
                    enabled = syncState !is SyncUiState.Loading,
                    value = sapPassword,
                    onValueChange = { sapPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    leadingIcon = {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFFB8B2BC))
                    },
                    label = { Text(
                        text = "SAP Password",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.titleMediumEmphasized
                    ) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8C00),
                        unfocusedBorderColor = Color(0xFF3F3942),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color.White,
                        errorTextColor = Color.White,
                        focusedLabelColor = Color(0xFFFF8C00),
                        cursorColor = Color(0xFFFF8C00)
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        val description =
                            if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                )
                if (isError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Name cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                if (syncState is SyncUiState.Error){
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = syncState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }else{
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your password is securely encrypted and never leaves your device.",
                        fontFamily = FontFamily.Monospace,
                        color = uiColors.accentOrangeStart,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            FilledTonalButton(
                enabled = syncState !is SyncUiState.Loading,
                onClick = {
                    if (sapPassword.isNotBlank()) {
                        onConfirm(sapPassword)
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
                Text("Login")
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
        containerColor = Color.Transparent,
        modifier = Modifier
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
            },
    )
}