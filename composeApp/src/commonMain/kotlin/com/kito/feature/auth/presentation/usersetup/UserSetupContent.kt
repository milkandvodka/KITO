package com.kito.feature.auth.presentation.usersetup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kito.core.common.util.currentLocalDateTime
import com.kito.core.designsystem.UIColors
import kito.composeapp.generated.resources.Res
import kito.composeapp.generated.resources.custom_icon
import kito.composeapp.generated.resources.google
import kotlinx.datetime.number
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UserSetupContent(
    setupState: SetupState,
    loadingSource: LoadingSource,
    onSubmit: (name: String, roll: String, year: String, term: String) -> Unit,
    onGoogleSignIn: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var kiitRollNumber by rememberSaveable { mutableStateOf("") }
    val now = currentLocalDateTime()
    val currentYear = now.year
    val month = now.month.number
    val derivedYear = if (month < 5) {
        currentYear - 1
    } else {
        currentYear
    }
    val derivedTerm = when (month) {
        12, 1, 2, 3, 4 -> "020"
        in 7..11 -> "010"
        else -> "020"
    }
    var sapYear by rememberSaveable { mutableStateOf(derivedYear.toString()) }
    var sapTerm by rememberSaveable { mutableStateOf(derivedTerm) }
    val uiColor = UIColors()
    val loading = setupState is SetupState.Loading
    val manualLoading = loadingSource == LoadingSource.Manual
    val googleLoading = loadingSource == LoadingSource.Google
    val loginGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFAA5E03),
            Color(0xFF9C4502)
        )
    )
    val disabledGradient = Brush.horizontalGradient(
        listOf(Color(0xFF2C2830), Color(0xFF2C2830))
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121116))
            .padding(horizontal = 24.dp)
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Image(
                    painter = painterResource(
                        Res.drawable.custom_icon
                    ),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(120.dp)
                )
                Spacer(Modifier.height(16.dp))
            }
            //Name
            item {
                OutlinedTextField(
                    enabled = !loading,
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFFB8B2BC))
                    },
                    label = { Text(
                        text = "Name",
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
                    )
                )
                Spacer(Modifier.height(12.dp))
            }

            // Username
            item {
                OutlinedTextField(
                    textStyle = MaterialTheme.typography.titleSmallEmphasized.copy(
                        fontFamily = FontFamily.Monospace,
                        color = Color.White
                    ),
                    enabled = !loading,
                    value = kiitRollNumber,
                    onValueChange = { input ->
                        kiitRollNumber = input.filter { it.isDigit() }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Badge,
                            contentDescription = null,
                            tint = Color(0xFFB8B2BC)
                        )
                    },
                    label = {
                        Text(
                            text = "KIIT Roll Number",
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.titleMediumEmphasized
                        )
                    },
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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                Spacer(Modifier.height(12.dp))
            }
            if (setupState is SetupState.Error) {
                item {
                    Text(
                        text = setupState.message,
                        color = Color.Red,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 2
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
            item {
                if (setupState !is SetupState.Error) {
                    Spacer(Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        onSubmit(name, kiitRollNumber, sapYear, sapTerm)
                    },
                    enabled = name.isNotBlank() && kiitRollNumber.isNotBlank() && kiitRollNumber.length > 6 && !loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .shadow(
                            elevation = 15.dp,
                            shape = RoundedCornerShape(25.dp),
                            spotColor = Color(0xFFFF6A00)
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(if (kiitRollNumber.isNotBlank() && name.isNotBlank() && kiitRollNumber.length > 6 && !loading) loginGradient else disabledGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (manualLoading) {
                                LoadingIndicator(
                                    color = uiColor.progressAccent
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = if (manualLoading) "Loading..." else "Get Started",
                                fontFamily = FontFamily.Monospace,
                                color = if (kiitRollNumber.isNotBlank() && name.isNotBlank() && kiitRollNumber.length > 6) Color.White else Color(
                                    0xFFC2927F
                                ),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            if(false) {
                item {
                    Spacer(Modifier.height(24.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "OR",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.titleMediumEmphasized
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = onGoogleSignIn,
                        enabled = !loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .shadow(
                                elevation = 15.dp,
                                shape = RoundedCornerShape(25.dp),
                                spotColor = Color(0xFFFF6A00)
                            ),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(if (!loading) loginGradient else disabledGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (googleLoading) {
                                    LoadingIndicator(color = uiColor.progressAccent)
                                    Spacer(modifier = Modifier.width(8.dp))
                                } else {
                                    Image(
                                        painter = painterResource(Res.drawable.google),
                                        contentDescription = "Google",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Text(
                                    text = "@kiit.ac.in",
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.titleMediumEmphasized,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun UserSetupContentPreview() {
    UserSetupContent(
        setupState = SetupState.Idle,
        loadingSource = LoadingSource.None,
        onSubmit = { _, _, _, _ -> },
        onGoogleSignIn = {}
    )
}

@Preview
@Composable
private fun UserSetupContentErrorPreview() {
    UserSetupContent(
        setupState = SetupState.Error("Invalid roll number"),
        loadingSource = LoadingSource.None,
        onSubmit = { _, _, _, _ -> },
        onGoogleSignIn = {}
    )
}