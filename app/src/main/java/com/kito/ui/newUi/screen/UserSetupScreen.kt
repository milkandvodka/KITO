package com.kito.ui.newUi.screen

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kito.MainActivity
import com.kito.R
import com.kito.UserSetupActivity
import com.kito.ui.components.UIColors
import com.kito.ui.newUi.viewmodel.SetupState
import com.kito.ui.newUi.viewmodel.UserSetupViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UserSetupScreen(
    userSetupViewModel: UserSetupViewModel = hiltViewModel()
) {
//    val years = (currentYear - 5..currentYear).map { it.toString() }.reversed()
//    val terms = listOf(
//        "Autumn",
//        "Spring"
//    )
//    var selectedTerm by rememberSaveable { mutableStateOf("Autumn") }
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var kiitRollNumber by rememberSaveable { mutableStateOf("") }
    var sapPassword by rememberSaveable { mutableStateOf("")}
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
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
    val setupState by userSetupViewModel.setupState.collectAsState()
    val loading = setupState is SetupState.Loading
//    var passwordVisible by remember { mutableStateOf(false) }
//    var yearExpanded by remember { mutableStateOf(false) }
//    val yearState = rememberTextFieldState(sapYear)
//    var termExpanded by remember { mutableStateOf(false) }
//    val termState = rememberTextFieldState(selectedTerm)
    val loginGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFF8C00), Color(0xFFFF6A00))
    )
    val disabledGradient = Brush.horizontalGradient(
        listOf(Color(0xFF2C2830), Color(0xFF2C2830))
    )
    LaunchedEffect(setupState) {
        if (setupState is SetupState.Success) {
            context.startActivity(Intent(context, MainActivity::class.java))
            (context as? UserSetupActivity)?.finish()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item{
                Image(
                    painter = painterResource(
                        R.drawable.e_labs_logo
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
//            item {
//                OutlinedTextField(
//                    textStyle = MaterialTheme.typography.titleSmallEmphasized.copy(
//                        fontFamily = FontFamily.Monospace,
//                        color = Color.White
//                    ),
//                    enabled = !loading,
//                    value = sapPassword,
//                    onValueChange = { sapPassword = it },
//                    modifier = Modifier.fillMaxWidth(),
//                    singleLine = true,
//                    shape = RoundedCornerShape(18.dp),
//                    leadingIcon = {
//                        Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFFB8B2BC))
//                    },
//                    label = {
//                        Text(
//                            text = "SAP Password (Optional)",
//                            fontFamily = FontFamily.Monospace,
//                            style = MaterialTheme.typography.titleSmallEmphasized
//                        )
//                    },
//                    colors = OutlinedTextFieldDefaults.colors(
//                        focusedBorderColor = Color(0xFFFF8C00),
//                        unfocusedBorderColor = Color(0xFF3F3942),
//                        focusedTextColor = Color.White,
//                        unfocusedTextColor = Color.White,
//                        disabledTextColor = Color.White,
//                        errorTextColor = Color.White,
//                        focusedLabelColor = Color(0xFFFF8C00),
//                        cursorColor = Color(0xFFFF8C00)
//                    ),
//                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                    trailingIcon = {
//                        val image =
//                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
//                        val description =
//                            if (passwordVisible) "Hide password" else "Show password"
//
//                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                            Icon(imageVector = image, contentDescription = description)
//                        }
//                    },
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//                )
//                Spacer(Modifier.height(8.dp))
//            }
            if (setupState is SetupState.Error) {
                item {
                    Text(
                        text = (setupState as SetupState.Error).message,
                        color = Color.Red,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 2
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

//            item{
//                Row {
//                    ExposedDropdownMenuBox(
//                        expanded = yearExpanded,
//                        onExpandedChange = { yearExpanded = !yearExpanded },
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        OutlinedTextField(
//                            textStyle = MaterialTheme.typography.titleSmallEmphasized.copy(
//                                fontFamily = FontFamily.Monospace,
//                                color = Color.White
//                            ),
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
//                            readOnly = true,
//                            value = yearState.text.toString(),
//                            onValueChange = {},
//                            label = {
//                                Text(
//                                    text ="Year",
//                                    fontFamily = FontFamily.Monospace,
//                                    style = MaterialTheme.typography.titleSmallEmphasized
//                                )
//                            },
//                            trailingIcon = {
//                                ExposedDropdownMenuDefaults.TrailingIcon(
//                                    expanded = yearExpanded,
//                                )
//                            },
//                            shape = RoundedCornerShape(16.dp),
//                            colors = OutlinedTextFieldDefaults.colors(
//                                focusedBorderColor = Color(0xFFFF8C00),
//                                unfocusedBorderColor = Color(0xFF3F3942),
//                                focusedTextColor = Color.White,
//                                unfocusedTextColor = Color.White,
//                                disabledTextColor = Color.White,
//                                errorTextColor = Color.White,
//                                focusedLabelColor = Color(0xFFFF8C00),
//                                cursorColor = Color(0xFFFF8C00)
//                            )
//                        )
//
//                        ExposedDropdownMenu(
//                            expanded = yearExpanded,
//                            onDismissRequest = { yearExpanded = false },
//                            modifier = Modifier.height(140.dp),
//                            shape = RoundedCornerShape(16.dp),
//                        ) {
//                            years.forEach { year ->
//                                DropdownMenuItem(
//                                    text = {
//                                        Text(
//                                            text = year,
//                                            fontFamily = FontFamily.Monospace,
//                                            style = MaterialTheme.typography.titleSmallEmphasized
//                                        )
//                                    },
//                                    onClick = {
//                                        sapYear = year
//                                        yearState.setTextAndPlaceCursorAtEnd(year)
//                                        yearExpanded = false
//                                    },
//                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
//                                )
//                            }
//                        }
//                    }
//                    Spacer(modifier = Modifier.padding(8.dp))
//                    ExposedDropdownMenuBox(
//                        expanded = termExpanded,
//                        onExpandedChange = { termExpanded = !termExpanded },
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        OutlinedTextField(
//                            textStyle = MaterialTheme.typography.titleSmallEmphasized.copy(
//                                fontFamily = FontFamily.Monospace,
//                                color = Color.White
//                            ),
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
//                            readOnly = true,
//                            value = termState.text.toString(),
//                            onValueChange = {},
//                            label = {
//                                Text(
//                                    text = "Term",
//                                    fontFamily = FontFamily.Monospace,
//                                    style = MaterialTheme.typography.titleSmallEmphasized
//                                )
//                            },
//                            trailingIcon = {
//                                ExposedDropdownMenuDefaults.TrailingIcon(
//                                    expanded = termExpanded,
//                                )
//                            },
//                            shape = RoundedCornerShape(16.dp),
//                            colors = OutlinedTextFieldDefaults.colors(
//                                focusedBorderColor = Color(0xFFFF8C00),
//                                unfocusedBorderColor = Color(0xFF3F3942),
//                                focusedTextColor = Color.White,
//                                unfocusedTextColor = Color.White,
//                                disabledTextColor = Color.White,
//                                errorTextColor = Color.White,
//                                focusedLabelColor = Color(0xFFFF8C00),
//                                cursorColor = Color(0xFFFF8C00)
//                            )
//                        )
//
//                        ExposedDropdownMenu(
//                            expanded = termExpanded,
//                            onDismissRequest = { termExpanded = false },
//                            shape = RoundedCornerShape(16.dp)
//                        ) {
//                            terms.forEach { term ->
//                                DropdownMenuItem(
//                                    text = {
//                                        Text(
//                                            text = term,
//                                            fontFamily = FontFamily.Monospace,
//                                            style = MaterialTheme.typography.titleSmallEmphasized
//                                        )
//                                    },
//                                    onClick = {
//                                        selectedTerm = term
//                                        sapTerm = if (term == "Autumn") "010" else "020"
//                                        termState.setTextAndPlaceCursorAtEnd(term)
//                                        termExpanded = false
//                                    },
//                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
//                                )
//                            }
//                        }
//                    }
//                }
//            }
            item {
                if (setupState !is SetupState.Error) {
                    Spacer(Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        userSetupViewModel.completeSetup(
                            name = name,
                            roll = kiitRollNumber,
                            sapPassword = sapPassword,
                            year = sapYear,
                            term = sapTerm
                        )
                    },
                    enabled = if (name.isNotBlank() && kiitRollNumber.isNotBlank() && kiitRollNumber.length > 6 && !loading) true else false,
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
                            if (loading) {
                                LoadingIndicator(
                                    color = uiColor.progressAccent
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = if (loading) {
                                    "Loading..."
                                } else if (sapPassword.isEmpty()) {
                                    "Get Started"
                                } else {
                                    "Sync Attendance and Get Started"
                                },
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
        }
    }
}
