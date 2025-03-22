package com.example.wheeldeal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wheeldeal.ui.screens.LandingPage
import com.example.wheeldeal.ui.screens.MainScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Landing.route
    ) {
        // 1) Landing route
        composable(Screen.Landing.route) {
            LandingPage(
                onGetStartedClick = {
                    // Navigate to Main (which has bottom nav + sub-routes)
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Landing.route) { inclusive = true }
                    }
                }
            )
        }

        // 2) Main route (includes bottom nav + sub-screens)
        composable(Screen.Main.route) {
            MainScreen()
        }
    }
}
