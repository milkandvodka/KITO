package com.kito.ui.newUi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kito.ui.newUi.screen.LoginScreen

@Composable
fun AppUI(navController: NavHostController){
    Box(
        modifier = Modifier.background(
            Brush.linearGradient(
                listOf(
                    Color(0xFF131621),
                    Color(0xFF0A0C12)
                )
            )
        )
    ){
        Scaffold(
            containerColor = Color.Transparent
        ) {innerpadding ->
            Box(
                modifier = Modifier
                    .padding(innerpadding)
                    .fillMaxSize()
            ){
                NavHost(
                    navController = navController,
                    startDestination = "Login"
                ){
                    composable("Login") {
                        LoginScreen()
                    }
                }
            }
        }
    }
}