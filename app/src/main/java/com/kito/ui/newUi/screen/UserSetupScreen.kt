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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LoadingIndicatorDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
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
import com.kito.ui.newUi.viewmodel.UserSetupViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UserSetupScreen(
    userSetupViewModel: UserSetupViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var kiitRollNumber by rememberSaveable { mutableStateOf("") }
    var sapPassword by rememberSaveable { mutableStateOf("")}
    var loading by rememberSaveable { mutableStateOf(false)}
    val uiColor = UIColors()
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
                    label = { Text(text = "Name",fontFamily = FontFamily.Monospace) },
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
            }

            // Username
            item {
                OutlinedTextField(
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
                            fontFamily = FontFamily.Monospace
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
            item {
                OutlinedTextField(
                    enabled = !loading,
                    value = sapPassword,
                    onValueChange = { sapPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    leadingIcon = {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFFB8B2BC))
                    },
                    label = {
                        Text(
                            text = "SAP Password (Optional)",
                            fontFamily = FontFamily.Monospace
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
                    )
                )
                Spacer(Modifier.height(12.dp))
            }
            item {
                val loginGradient = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFF8C00), Color(0xFFFF6A00))
                )
                val disabledGradient = Brush.horizontalGradient(
                    listOf(Color(0xFF2C2830), Color(0xFF2C2830))
                )

                Button(
                    onClick = {
                        scope.launch {
                            loading = true
                            delay(2000)
                            if (sapPassword.isNotEmpty()){
                                userSetupViewModel.setSapPassword(sapPassword)
                            }
                            userSetupViewModel.setUserName(name)
                            userSetupViewModel.setUserRoll(kiitRollNumber)
                            userSetupViewModel.setUserSetupDone()
                            userSetupViewModel.fetchAndUpsertTimetable()
                            if (sapPassword.isNotEmpty()) {
                                userSetupViewModel.fetchAndUpsertAttendance(
                                    year = "2025",
                                    term = "1"
                                )
                            }
                            userSetupViewModel.getAttendance()
                            context.startActivity(Intent(context, MainActivity::class.java))
                            (context as? UserSetupActivity)?.finish()
                        }
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
