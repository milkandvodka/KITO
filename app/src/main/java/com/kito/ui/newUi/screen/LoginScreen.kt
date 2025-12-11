package com.kito.ui.newUi.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun DropDownField(
    label: String,
    options: List<String>,
    selectedValue: String,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label, color = Color.White, fontWeight = FontWeight.Medium,fontFamily = FontFamily.Monospace )

        Spacer(Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF3F3942), RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Text(
                text = selectedValue,
                color = Color.White,
                fontSize = 15.sp
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color(0xFF262226))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color.White) },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLogin: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    // Updated session list
    val sessions = listOf(
        "2022 - 2023",
        "2023 - 2024",
        "2024 - 2025",
        "2025 - 2026",
        "2026 - 2027"
    )

    val terms = listOf("Autumn", "Spring")

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var selectedSession by rememberSaveable { mutableStateOf(sessions.last()) }
    var selectedTerm by rememberSaveable { mutableStateOf(terms.first()) }

    val isValid = username.isNotBlank() && password.isNotBlank()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Text(
                    text = "KITO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(Modifier.height(30.dp))
            }

            // Username
            item {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFFB8B2BC))
                    },
                    label = { Text(text = "Username",fontFamily = FontFamily.Monospace) },
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

            // Password
            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    leadingIcon = {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFFB8B2BC))
                    },
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(icon, contentDescription = null, tint = Color(0xFFC2BCC8))
                        }
                    },
                    label = { Text("Password",fontFamily = FontFamily.Monospace) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                Spacer(Modifier.height(20.dp))
            }

            // Session (instead of academic year)
            // SESSION DROPDOWN
            item {
                DropDownField(
                    label = "Session",
                    options = sessions,
                    selectedValue = selectedSession,
                    onValueSelected = { selectedSession = it }
                )
                Spacer(Modifier.height(24.dp))
            }

// TERM DROPDOWN
            item {
                DropDownField(
                    label = "Term",
                    options = terms,
                    selectedValue = selectedTerm,
                    onValueSelected = { selectedTerm = it },
                )
                Spacer(Modifier.height(30.dp))
            }


            // Login Button
            item {
                val loginGradient = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFF8C00), Color(0xFFFF6A00))
                )
                val disabledGradient = Brush.horizontalGradient(
                    listOf(Color(0xFF2C2830), Color(0xFF2C2830))
                )

                Button(
                    onClick = {
                        onLogin(username, password, selectedSession, selectedTerm)
                    },
                    enabled = isValid,
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
                            .background(if (isValid) loginGradient else disabledGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Login & Sync Attendance",
                            fontFamily = FontFamily.Monospace,
                            color = if (isValid) Color.White else Color(0xFFC2927F),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
