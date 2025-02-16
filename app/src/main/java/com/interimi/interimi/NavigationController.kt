package com.interimi.interimi

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.interimi.interimi.screens.MainScreen
import com.interimi.interimi.screens.StartScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "start") {
        composable("start") {
            StartScreen(navigateToMain = { navController.navigate("main") })
        }
        composable("main") {
            MainScreen()
        }
    }
}
