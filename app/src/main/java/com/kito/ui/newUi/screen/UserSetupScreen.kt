package com.kito.ui.newUi.screen

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kito.MainActivity
import com.kito.UserSetupActivity
import com.kito.data.local.preferences.newpreferences.PrefsRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSetupScreen(
    prefsRepository: PrefsRepository
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var Name by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }

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
            //Name
            item {
                OutlinedTextField(
                    value = Name,
                    onValueChange = { Name = it },
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
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    leadingIcon = {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFFB8B2BC))
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
                            prefsRepository.setUserSetupDone()
                            context.startActivity(Intent(context, MainActivity::class.java))
                            (context as? UserSetupActivity)?.finish()
                        }
                    },
                    enabled = if (username.isNotBlank() && Name.isNotBlank()) true else false,
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
                            .background(if (username.isNotBlank() && Name.isNotBlank()) loginGradient else disabledGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Login & Sync Attendance",
                            fontFamily = FontFamily.Monospace,
                            color = if (username.isNotBlank() && Name.isNotBlank()) Color.White else Color(0xFFC2927F),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
