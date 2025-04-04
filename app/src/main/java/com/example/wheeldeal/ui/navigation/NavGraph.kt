package com.example.wheeldeal.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wheeldeal.ui.screens.*
import com.example.wheeldeal.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: AuthViewModel = viewModel()
) {
    val auth = FirebaseAuth.getInstance()
    var startDestination by remember { mutableStateOf<String?>(null) }

    if (startDestination == null) {
        SplashScreen()
        LaunchedEffect(Unit) {
            delay(1200)
            startDestination = if (auth.currentUser != null) {
                Screen.Main.route
            } else {
                Screen.Landing.route
            }
        }
        return
    }

    NavHost(navController = navController, startDestination = startDestination!!) {
        composable(Screen.Landing.route) {
            LandingPage {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Landing.route) { inclusive = true }
                }
            }
        }
        composable(Screen.Main.route) {
            MainScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }
    }
}
