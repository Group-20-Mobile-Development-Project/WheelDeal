package com.example.wheeldeal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import com.example.wheeldeal.ui.screens.LandingPage
import com.example.wheeldeal.ui.screens.HomeScreen

// Define the app's routes
sealed class Screen(val route: String) {
    object Landing : Screen("landing")
    object Home : Screen("home")
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Landing.route
    ) {
        composable(Screen.Landing.route) {
            LandingPage(onGetStartedClick = {
                navController.navigate(Screen.Home.route)
            })
        }

        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}
