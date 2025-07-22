package com.example.taskapp.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskapp.ui.screens.*

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { Splashscreen(navController) }
        composable("signup") { SignUp(navController) }
        composable("signin") { SignIn(navController) }
        composable("otp/{from}") { backStackEntry ->
            val from = backStackEntry.arguments?.getString("from")
            OtpScreen(navController, from)
        }
        composable("dashboard") { DashboardScreen() }
    }
}