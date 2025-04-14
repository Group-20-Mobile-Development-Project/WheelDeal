package com.example.wheeldeal.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.wheeldeal.ui.screens.*
import com.example.wheeldeal.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import com.example.wheeldeal.model.CarListing
import com.google.gson.Gson
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

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

        // ✅ This is your working CarDetails navigation
        composable(
            route = Screen.CarDetails.route,
            arguments = listOf(
                navArgument("carJson") {
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val encodedJson = backStackEntry.arguments?.getString("carJson") ?: ""
            val decodedJson = URLDecoder.decode(encodedJson, StandardCharsets.UTF_8.toString())
            val listing = Gson().fromJson(decodedJson, CarListing::class.java)
            CarDetailsScreen(listing = listing, navController = navController)
        }
    }
}
