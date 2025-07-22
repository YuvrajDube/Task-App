package com.example.taskapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.taskapp.R
import kotlinx.coroutines.delay

@Composable
fun Splashscreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(2500)
        navController.navigate("signup") {
            popUpTo("splash") { inclusive = true }
        }
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo", modifier = Modifier.size(120.dp))
            Spacer(modifier = Modifier.height(20.dp))
            Text("Scale Us Technologies", style = MaterialTheme.typography.headlineMedium)
        }
    }
}
